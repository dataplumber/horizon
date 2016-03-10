/*
Significant Event Viewer System 3.1
Author: Christian Alarcon (christian.alarcon@jpl.nasa.gov)
*/
dojo.require("config");
dojo.require("dojo.parser");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dijit.dijit");
dojo.require("dijit.form.Button");
dojo.require("dijit.Menu");
dojo.require("dijit.Tree");
dojo.require("dijit.Tooltip");
dojo.require("dijit.Dialog");
dojo.require("dijit.Toolbar");
dojo.require("dijit.ProgressBar");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.Textarea");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.FilteringSelect");
dojo.require("dijit.form.Form")
dojo.require("dijit.layout.BorderContainer");
dojo.require("dijit.layout.AccordionContainer");
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require('dojox.validate');
dojo.require('dojox.validate.us');
dojo.require('dojox.validate.web');
dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.io.xhrPlugins");
dojo.require("dojo.cookie");

// GLOBAL VARS
var currentEvent;
var eventTimer = "TRUE";
var loginString = "";

var pageState = {
    base: BASE_URL+EVENTS+LIST,
    order: "desc",
    sortColumn: "lastReceived",
    type: ["ERROR","WARN", "INFO"],
    category: "",
    source: "",
    computer: "",
    description: "",
    page: 0,
    maxPage: 1,
    label: "Main Tab"
}

function stateToURL(state) {
    var typeString = "";
    
    dojo.forEach(state.type, function(item, index) {
         typeString += "&type="+item;
    });
    // Add type, source, computer, description
    
    var returnString = state.base+"&page="+state.page+"&category="+state.category+typeString;
    /*if(state.source != "" || state.computer != "" || state.description) {
        returnString = returnString + "&source="+state.source;    
        returnString = returnString + "&source="+state.computer;
        returnString = returnString + "&source="+state.description;
    }*/
    if(state.sortColumn != "")
        returnString = returnString + "&sort="+state.sortColumn + "&order="+state.order;
  
    if(state.source != "" )
        returnString = returnString + "&source="+state.source;
    if(state.computer != "")
        returnString = returnString + "&computer="+state.computer;
    if(state.description != "")
        returnString = returnString + "&description="+state.description;
    if(state.exact != "")
        returnString = returnString + "&exact="+state.exact;
    if(state.resolved != null && state.resolved == false)
        returnString = returnString + "&resolved="+state.resolved;
    return returnString;
    
}



var tabNum = 0;
var grids = new Array();

var customPage = new Array();
var customMaxPage = new Array();
var customStore = new Array();
var customTitle = new Array();
var refreshChecks = new Array();
var checkFlags = new Array();
var workingTab = 0;

//Global Objects
var mytree = null;

Array.prototype.has = function(value) {
	var i;
	for (var i = 0, loopCnt = this.length; i < loopCnt; i++) {
		if (this[i] === value) {
			return true;
		}
	}
	return false;
};

dojo.addOnLoad(function(){
	//dojox.io.xhrPlugins.addCrossSiteXhr("http://localhost:9090/"); 
	//dojox.io.xhrPlugins.addCrossSiteXhr("http://lanina:8100/"); //RIGHT
	//dojox.io.xhrPlugins.addCrossSiteXhr("http://lanina.jpl.nasa.gov:8100/"); 
	//dojox.io.xhrPlugins.addProxy("/projects/sigevent/Proxy.php?url="); //RIGHT
	
	dojo.parser.parse();
	dijit.setWaiRole(dojo.body(), "application");
		
	var n = dojo.byId("preLoader");
	dojo.fadeOut({
		node:n,
		duration:720,
		onEnd:function(){
			// dojo._destroyElement(n); 
			dojo.style(n,"display","none");
		}
	}).play();

    

	var eventStore = new dojo.data.ItemFileWriteStore({url: stateToURL(pageState)});
	//eventStore._jsonFileUrl=currentURL+sortParams+typeParams+"&page="+page;
	eventStore.fetch({
      	 urlPreventCache: true,
     	 onComplete: processEvents
  	});
	dojo.subscribe("tabs-selectChild", function(child){
		var title = child.title;
    		child.controlButton.containerNode.innerHTML = title || ""; 
	});

	//eventGrid._refresh();
	//Create buttons in event page menu
	updatePageMenu();
	filterCategoryMenu();
	
	//Login logic
	var loginText = dojo.byId("login-text");
	if(isLoggedIn()){
		var username = dojo.cookie("sigevent-username");
		var password = dojo.cookie("sigevent-password");
		var role = dojo.cookie("sigevent-role");
		loginString = "&username="+username+"&password="+password;
		loginText.innerHTML = "<span>Logged in as: "+username+"</span><br/>\
							       <span>Role: "+role+"</span><br/>\
							       <span class='login-link' onclick='logout();'>Logout</span><br/>\
							       <button dojoType='dijit.form.Button' onClick='saveSettings();'>Save Settings</button>";
        loadSettings();
	}
	else {
		loginString = "";
		loginText.innerHTML = "<span class='login-link' onclick=\"dijit.byId('loginPrompt').show();\">Login</span>";
	}
});

function processEvents(items, request){
	dojo.forEach(items, function(item){
		if(item['type'] == "Type") {
			if(item['Type'] != "OK") {
				showGenericMessage("Server returned ERROR response: "+item['Content']);
			}
		}
		if(item['type'] == "AvailablePages") {
			pageState.maxPage = item['AvailablePages'];
			if(pageState.maxPage == 0) pageState.maxPage = 1;			
			updatePageMenu();
		}
	});

	eventGrid._refresh();
}

function filterCategoryMenu(){
	categoryMenuStore.comparatorMap = {};
        categoryMenuStore.comparatorMap["Value"] = function(a, b) {
            var ret = 0;
            // We want to map these by what the priority of these items are, not by alphabetical.
            // So, custom comparator.
            if (a > b) {
                ret = -1;
            }
            if (a < b) {
                ret = 1;
            }
            return ret;
        };
	var sortAttributes = [{
                attribute: "Value",
                descending: true
            }];

	
	categoryMenuStore.fetch({
		sort: sortAttributes,
		onComplete:function(items){
			var newOrder = {};
			newOrder['items'] = new Array();
			newOrder['identifier'] = "Value";
			newOrder['label'] = "Value";
			var size = items.length;
			var newItems = new Array();
			for (var x = 0; x<size; x++){
				if (items[x].Value!="ALL" && items[x].Value!="" ) {
					if(items[x].Value == "UNCATEGORIZED" || items[x].Value == "DMAS")
						{newOrder['items'].unshift({Value:items[x].Value});}
					else 	{newOrder['items'].push({Value:items[x].Value});}
				}
			}	
			createCategoryTree(newOrder);
		}
	});
	
}

function createCategoryTree(items){
	var treeSection = dijit.byId("treeSection");

	//treeSection.destroyDescendants();

	var treeStore = new dojo.data.ItemFileWriteStore({data: items});
	var eventTree = new dijit.Tree({id:"mytree", jsId:"mytree", store:treeStore, getIconClass:getTreeIcon, showRoot:true, label:"All Events", labelAttr:"Value",
				onClick:function(item){
						if(item.root) {
							pageState.page = 0;
							pageState.sortColumn = "";
							pageState.order = "";
							pageState.base = BASE_URL+EVENTS+LIST;
							pageState.category="";
							pageState.source="";
							pageState.computer="";
							pageState.description="";
							pageState.exact="";
							eventStore._jsonFileUrl=stateToURL(pageState);
							refreshEvent();
						}
						else {
							pageState.page = 0;
							pageState.sortColumn = "";
							pageState.order = "";
							pageState.base = BASE_URL+EVENTS+LIST;
							pageState.category = item.Value;
							pageState.source="";
							pageState.computer="";
							pageState.description="";
							pageState.exact="true"
							eventStore._jsonFileUrl=stateToURL(pageState);
							refreshEvent();
						}
					}

	});

	//treeSection.addChild(eventTree);
	mytree = eventTree;
	treeSection.domNode.appendChild(eventTree.domNode);
}

function getTreeIcon(item){
	//if(item.Value == "UNCATEGORIZED") {
	//	return "mailIconMailbox";}
	//else {
		return "mailIconFolderDocuments";
}

// Check Box click functions!

function typeSelectClick(value) {
	var typeArray = [];
	if(dijit.byId("errorCheckBox").checked)
		typeArray.push("ERROR");
	if(dijit.byId("warnCheckBox").checked)
		typeArray.push("WARN");
	if(dijit.byId("infoCheckBox").checked)
		typeArray.push("INFO");

	pageState.type = typeArray;
	pageState.page = 0;
	/*pageState.source="";
	pageState.computer="";
	pageState.description="";*/
	eventStore._jsonFileUrl = stateToURL(pageState);	
	refreshEvent();
	updatePageMenu();
}

function filterResolvedClick(value) {
	pageState.resolved = value;
	pageState.page = 0;
	eventStore._jsonFileUrl = stateToURL(pageState);
	refreshEvent();
	updatePageMenu();
			
}


// OLDER RADIO BUTTON FUNCTION
/*function typeSelectClick_OLD(value) {
	if (value == "ALL"){
		typeParams="";
	}
	else typeParams="&type="+value;
	page = 0;
	eventStore._jsonFileUrl=currentURL+sortParams+typeParams;	
	refreshEvent();
	updatePageMenu();
}
*/

// Extra tab functions!
function copyIntoNewTab() {
	var categoryTitle = "";
	var typeTitle = "";
	
	if(pageState.category == "")
		categoryTitle = "All";
	else categoryTitle = pageState.category;

	typeTitle =  pageState.type.join(", ");

	var tabTitle = categoryTitle + ":" + typeTitle;
	//if(categoryTitle.match(/=/))
	//	tabTitle = "Custom Search";

	dijit.byId("tabTitleInput").attr("value", tabTitle);
	newTabDialog.show();
}


function createNewTab(title, state) {
	var currentTab = tabNum;
	tabNum++;
	
	var newState;
	if(state) 
	    newState = state;
	else 
	    newState = dojo.clone(pageState);
	newState.tabNum = currentTab;
	var newStore = new dojo.data.ItemFileWriteStore({url: stateToURL(newState)});
	newStore.tab = currentTab;
	
	workingTab = currentTab;
	newStore.fetch({ urlPreventCache: true, onComplete: processEvents_custom});

	if(title.length>20)
		title = title.substr(0,16)+"...";
		
	newState.label = title;

	var newTab = new dijit.layout.BorderContainer({id:"custom"+currentTab,title:title, closable:true, design:"sidebar", tabNum:currentTab});
	var pageMenu = new dijit.Toolbar({id:"customPageMenu"+currentTab, region:"top", height:"20px", style:"text-align:center"});
	//var refreshButton = new dijit.form.Button({type:"submit", onClick:"refreshCustom(currentTab)", style:"float:right", label:"Refresh"});
	

	var gridLayout = [{
            field: 'Resolution',
            name: ' ',
            width: '5px',
	    formatter: formatResolve
        },
	{
            field: 'Category',
            name: 'Category',
            width: '65px'
        },
	{
            field: 'Type',
            name: 'Type',
            width: '45px'
        },
	{
            field: 'Source',
            name: 'Source',
            width: '90px'
        },  
	{
            field: 'Computer',
            name: 'Computer',
            width: '95px'
        },     
	{
            field: 'Description',
            name: 'Description',
            width: 'auto',
	    formatter: formatDescriptionLong
        },
	{
            field: 'LastReceived',
            name: 'Last Received',
            width: '130px',
	    formatter: formatDate
        }];
	var eventTable = new dojox.grid.DataGrid({ region:"center", minSize:"20", 
					  id:"customGrid"+currentTab,
					  store: newStore, query:{ Id: '*' },
					  onRowClick:onEventClick,
					  onHeaderCellClick:eventColumnSort_custom,
					  style:"height: auto;",
					  structure:gridLayout,
					  tab: currentTab,
					  state: newState});
					  
	grids.push(eventTable);	

	var messageContent = new dijit.layout.ContentPane({id:"customContent"+currentTab, region:"bottom", role:"region", minSize:"20", style:"height: 200px", splitter:"true"});

	//pageMenu.addChild(refreshButton);
	newTab.addChild(pageMenu);	
	newTab.addChild(eventTable);
	newTab.addChild(messageContent);
	tabs.addChild(newTab);
	refreshPageMenu_custom(currentTab);
	refreshTimer_custom(currentTab);	
}

function refreshTimer_custom(tab){
	
	var tabWidget = dijit.byId("custom"+tab);
	var grid  = dijit.byId("customGrid"+tab);
	
	if(tabWidget) {
		if(!tabWidget.selected && grid.getItem(0)){
			if(refreshChecks[tab] != grid.getItem(0).LastReceived[0]) {
				var title = tabWidget.title;
				tabWidget.controlButton.containerNode.innerHTML = "<span style='color: red;'>"+title+"</span>" || "";
			}
			refreshChecks[tab] = grid.getItem(0).LastReceived;
			refreshEvent_custom(tab);
		}
	
		setTimeout("refreshTimer_custom("+tab+")", 5000);
	}
}

function onEventClick_custom(cell)
{
	var item = cell.grid.getItem(cell.rowIndex),
		id = this.store.getValue(item, "Id"),
		category = this.store.getValue(item, "Category"),
		type = this.store.getValue(item, "Type"),
		source = this.store.getValue(item, "Source"),
		description = this.store.getValue(item, "Description"),
		firstReceived = new Date(parseInt(this.store.getValue(item, "FirstReceived"))).toString(),
		lastReceived = new Date(parseInt(this.store.getValue(item, "LastReceived"))).toString(),
		provider = this.store.getValue(item, "Provider"),
		computer = this.store.getValue(item, "Computer"),
		pid = this.store.getValue(item, "Pid"),
		dataURL = this.store.getValue(item, "DataUrl");
		//data = this.store.getValue(item, "Data"),
		resolution = this.store.getValue(item, "Resolution"),
		resolvedAt = new Date(parseInt(this.store.getValue(item, "Resolved_At"))).toString(),
		messageInner = "<span class='messageHeader'>Event ID: " + id + "<br>" +
		"Category: "+ category + "<br>" +
		"Type: "+ type + "<br>" +
		"Source: "+ source + "<br>" +
		"First Received: "+ firstReceived + "<br>" +
		"Last Received: "+ lastReceived + "<br>" +
		"Provider: "+ provider + "<br>" +
		"Computer: "+ computer + "<br>" +
		"PID: "+ pid + "<br>";
		if (dataURL) {
			messageInner = messageInner + "Data Link: <a href='"+ dataURL + "' target='_blank'>"+ dataURL +"</a><br>";
		}
		messageInner = messageInner + "<br><br></span>" +
		"Description:<br>"+description + "<br> <br>";//Data Link: <a href='" + data + "'>Download</a>";
	if (resolution!="N/A") {
		messageInner = "<span style='color: red'>This event has been resolved at " + resolvedAt +"<br>Resolution: " + resolution + "</span<br><br>" + messageInner;
	}
	else {
		//TOTAL HACK MUST FIX
		messageInner = "<input dojoType='dijit.form.TextBox' id='resolutionInput"+this.tab+"' size='40'/><button dojoType='dijit.form.Button' onClick='resolveEvent'>Resolve!</button><br><br>" + messageInner;	
	}
	currentEvent = this.store.getValue(item, "Id");
	dijit.byId("customContent"+this.tab).setContent(messageInner);	
	
	//color the selected row (default onRowClick actions)
	this.edit.rowClick(cell);   // <- from default onRowClick method
   	this.selection.clickSelectEvent(cell);  // <- from default onRowClick method



}

function eventColumnSort_custom(event){
	var columnLabel = event.cell.name;
	if(this.state.order == null || this.state.order == "") {
		this.state.order = "desc";
	}
	else {
		if (this.state.order=="desc") {
			this.state.order="asc";
		}
		else if (this.state.order=="asc") {
			this.state.order="desc";
		}
	}
	columnLabel=columnLabel.toLowerCase();
	if(columnLabel=="last received")
		columnLabel="lastReceived";
	if(columnLabel!="resolution"){
		this.state.sortColumn = columnLabel;
		eventStore._jsonFileUrl=stateToURL(this.state);
		refreshEvent_custom(this.tab);
		//refreshPageMenu_custom(this.tab);
	}
}

function processEvents_custom(items, request){
	var currentTab = request.store.tab;
	var currentGrid = dijit.byId("customGrid"+currentTab);

	dojo.forEach(items, function(item){
		if(item['type'] == "Type") {
			if(item['Type'] != "OK") {
				showGenericMessage("Server returned ERROR response: "+item['Content']);
			}
		}
		if(item['type'] == "AvailablePages") {
			currentGrid.state.maxPage = item['AvailablePages'];
			if(currentGrid.state.maxPage == 0) currentGrid.state.maxPage= 1;			
			refreshPageMenu_custom(currentTab);
		}
	});

	currentGrid._refresh();
}

function resolveEvent_custom(){
	var resolution = dijit.byId("resolutionInput").attr("value");
	var resolveURL = BASE_URL+EVENTS+UPDATE+"id="+currentEvent+"&resolution="+resolution;
	dojo.xhrGet( {
   		url: resolveURL,
       		handleAs: "json",
       		load: function(responseObject, ioArgs) {
			if(responseObject.Response.Type!="OK"){
				showGenericMessage("Error occured when trying to resolve: "+responseObject.Response.Content);
			}
			else {;
				showGenericMessage("Event Resolved!");
				refreshEvent();
			}				
       			return responseObject;
       		}
	});
}

function refreshPageMenu_custom(tab) {

	var customMenu = dijit.byId("customPageMenu"+tab);	
	var currentGrid = dijit.byId("customGrid"+tab);
	
	customMenu.destroyDescendants();
	var refresh = new dijit.form.Button({label:"Refresh", id:"refreshButton"+tab, onClick:refreshEvent_custom, tab:tab , style:"float:right;"});
	var select = new dijit.form.Button({label:"Select All", onClick:selectAll, tab:tab , grid: "customGrid"+tab, style:"float:right;"});
	var previous = new dijit.form.Button({label:"&lt;", onClick:previousPage_custom, tab:tab, style:""});
	var next = new dijit.form.Button({label:"&gt;", onClick:nextPage_custom, style:"", tab:tab});
	var first = new dijit.form.Button({label:"|&lt;", onClick:firstPage_custom, style:"", tab:tab});
	var last = new dijit.form.Button({label:"&gt;|", onClick:lastPage_custom, style:"", tab:tab});
	var pageText = new dojo.create("div", { innerHTML:"Page "+(currentGrid.state.page+1)+" of "+currentGrid.state.maxPage, style:"float:left; margin: 2px 6px;", id:"pageRangeId"+tab}); 
	
	if(currentGrid.state.page==0) {
		first.disabled = true;
		previous.disabled = true;
	}
	if(currentGrid.state.page+1==currentGrid.state.maxPage) {
		last.disabled = true;
		next.disabled = true;
	}
	if(dojo.byId("pageRangeId"+tab))
		customMenu.domNode.removeChild(dojo.byId("pageRangeId"+tab));
	customMenu.domNode.appendChild(pageText);

	refresh.disabled = true;
	customMenu.addChild(refresh);
	customMenu.addChild(select);
	customMenu.addChild(first);
	customMenu.addChild(previous);
	customMenu.addChild(next);
	customMenu.addChild(last);	
	setTimeout ( "dijit.byId('refreshButton'+"+tab+").disabled = false;", 500 );
}

function nextPage_custom(){
	var tab = this.tab;
	var currentGrid = dijit.byId("customGrid"+tab);
	
	currentGrid.state.page = currentGrid.state.page + 1;
	currentGrid.store._jsonFileUrl=stateToURL(currentGrid.state);
	refreshEvent_custom(tab);
	refreshPageMenu_custom(tab);
}

function previousPage_custom(){
	var tab = this.tab;
	var currentGrid = dijit.byId("customGrid"+tab);
	
	currentGrid.state.page = currentGrid.state.page - 1;
	currentGrid.store._jsonFileUrl=stateToURL(currentGrid.state);
	refreshEvent_custom(tab);
	refreshPageMenu_custom(tab);
}

function firstPage_custom(){
	var tab = this.tab;
	var currentGrid = dijit.byId("customGrid"+tab);
	
	currentGrid.state.page = 0;
	currentGrid.store._jsonFileUrl=stateToURL(currentGrid.state);
	refreshEvent_custom(tab);
	refreshPageMenu_custom(tab);
}

function lastPage_custom(){
	var tab = this.tab;
	var currentGrid = dijit.byId("customGrid"+tab);
	
	currentGrid.state.page = currentGrid.state.maxPage - 1;
	currentGrid.store._jsonFileUrl=stateToURL(currentGrid.state);
	refreshEvent_custom(tab);
	refreshPageMenu_custom(tab);
}

function refreshEvent_custom(tab){
	if (!(/^\d*$/.test(tab)))
		tab = this.tab;
	var currentGrid = dijit.byId("customGrid"+tab);
	if(eventTimer == "TRUE") {
		
		currentGrid.store.save();
		currentGrid.store.close();
		workingTab = tab;
		var newStore = new dojo.data.ItemFileWriteStore({url: stateToURL(currentGrid.state)});
		newStore.tab = tab;
		currentGrid.store = newStore;
		//currentGrid._jsonFileUrl = stateToURL(currentGrid.state);
		
		currentGrid._setStore(newStore);
		newStore.fetch({ urlPreventCache: true, onComplete: processEvents_custom});
		
		currentGrid._refresh();
		/*eventTimer="FALSE";
		setTimeout('eventTimer=\'TRUE\'', 500)
		refreshPageMenu_custom(tab);*/
		
		
		/*
		currentGrid.store.save();
		currentGrid.store.close();
		currentGrid.store.fetch();
	
		currentGrid._refresh();
		eventTimer="FALSE";
		setTimeout('eventTimer=\'TRUE\'', 500)
		refreshPageMenu_custom(tab);
		*/
	}
}

// Refresh Functions!
function refreshNotification() {
	if(eventTimer == "TRUE") {	
	notificationStore.save();
	notificationStore.close();
	notificationStore.fetch();
	notificationGrid._refresh();
	eventTimer="FALSE";
	setTimeout('eventTimer=\'TRUE\'', 500)
	}
}

function refreshEvent(){
	if(eventTimer == "TRUE") {
		eventStore.save();
		eventStore.close();
		eventStore.fetch({
      		 urlPreventCache: true,
     		 onComplete: processEvents
  		});
  		var category = pageState.category;
  		if(pageState.category == "")
  		    category = "All"
	    dojo.byId("current-category").innerHTML = "Category: "+category;
		//eventGrid._refresh();
		eventTimer="FALSE";
		setTimeout('eventTimer=\'TRUE\'', 500)
		//updatePageMenu();
	}
}


// Display generic message in a popup (usually error or success messages)
function showGenericMessage(text){
	dojo.byId("gmessage").innerHTML=text;
	dijit.byId("gmessageDialog").show();
}


// NOTIFICATION MANAGEMENT

function addNewNotification(){
	if(notificationForm.validate()) {
		var newCategory = dijit.byId("categoryEntry").attr("value");
		var newType = dijit.byId("typeEntry").attr("value");
		var newMethod = dijit.byId("methodEntry").attr("value");
		var newContact = dijit.byId("contactEntry").attr("value");
		var newRate = dijit.byId("rateEntry").attr("value");
		var newNote = dijit.byId("noteEntry").attr("value");
		var remindRate = dijit.byId("remindRateEntry").attr("value");
		var newMessageFormat = dijit.byId("messageFormatEntry").attr("value");
		var newContent = dijit.byId("contentEntry").attr("value");
		var URL = BASE_URL+NOTIFIES+CREATE
				+"type="+newType
				+"&category="+newCategory
				+"&method="+newMethod
				+"&contact="+newContact
				+"&rate="+newRate
				+"&messageFormat="+newMessageFormat
				+"&content="+newContent
				+"&note="+newNote
				+"&remindRate="+remindRate
				+loginString;
				
		newNotification.hide();

		dojo.xhrGet( {
       			url: URL,
        		handleAs: "json",
        		load: function(responseObject, ioArgs) {
				if(responseObject.Response.Type!="OK"){
					showGenericMessage("Error occured when creating notification: \n"+responseObject.Response.Content);
				}
				else {
					showGenericMessage("Notification Added!");	
					refreshNotification();
				}
					
        			return responseObject;
        		}
		});
		newNotification.reset();
      	} else {
        	alert('Form contains invalid data.  Please correct first');
	}
}

// Called by clicking cancel in the newNotification pop up
function closeNewNotification(){
	newNotification.hide();
	newNotification.reset();
}

function closeEditNotification(){
	editNotificationDisplay.hide();
	editNotificationDisplay.reset();
}

// Called by clicking "Delete Selected" button in the notification tab
function deleteNotification(){
        var items = notificationGrid.selection.getSelected();
        if(items.length){
            // Iterate through the list of selected items.
            // The current item is available in the variable
            // "selectedItem" within the following function:
	    if(confirm("Are you sure you want to delete the selected row?")) {
             dojo.forEach(items, function(selectedItem) {
                if(selectedItem !== null) {
                   	// Delete the item from the data store:
		   	var ID = notificationStore.getValue(selectedItem, "Id");
                    	deleteURL = BASE_URL+NOTIFIES+DELETE+"id="+ID+loginString;
		    	dojo.xhrGet( {
       				url: deleteURL,
        			handleAs: "json",
        			load: function(responseObject, ioArgs) {
					if(responseObject.Response.Type!="OK"){
						showGenericMessage(""+responseObject.Response.Content);
					}
					else if(responseObject.Response.Type=="OK") {
						refreshNotification();
						showGenericMessage("Notification(s) Deleted");
					}
				   	return responseObject;
        			}
			});
                }
             }); 
	    }
        }
}

// Initialize edit notification form with clicked notification
function initEditNotification(cell) {
	var item = cell.grid.getItem(cell.rowIndex);
	var category = this.store.getValue(item, "Category");
	var type = this.store.getValue(item, "Type");
	var method = this.store.getValue(item, "Method");
	var contact = this.store.getValue(item, "Contact");
	var rate = this.store.getValue(item, "Rate");
	var messageFormat = this.store.getValue(item, "MessageFormat");
	var content = this.store.getValue(item, "Content");
	var note = this.store.getValue(item, "Note");
	var ID = this.store.getValue(item, "Id");
	var remindRate = this.store.getValue(item, "RemindRate");
	dojo.byId("ENidValue").innerHTML=ID;
	dojo.byId("ENcategoryValue").innerHTML=category;
	dojo.byId("ENtypeValue").innerHTML=type;
	dijit.byId("ENmethodEntry").attr("value",method);
	dijit.byId("ENcontactEntry").attr("value",contact);
	dijit.byId("ENrateEntry").attr("value",rate);
	dijit.byId("ENmessageFormatEntry").attr("value",messageFormat);
	dijit.byId("ENcontentEntry").attr("value",content);
	dijit.byId("ENnoteEntry").attr("value",note);
	dijit.byId("ENremindRateEntry").attr("value",remindRate);
	dijit.byId("editNotificationDisplay").show();
}

// Server call to edit notification with changed values
function editNotification(){
	if(editNotificationForm.validate()) {
		var ID = dojo.byId("ENidValue").innerHTML;
		var newMethod = dijit.byId("ENmethodEntry").attr("value");
		var newContact = dijit.byId("ENcontactEntry").attr("value");
		var newRate = dijit.byId("ENrateEntry").attr("value");
		var newNote = dijit.byId("ENnoteEntry").attr("value");
		var newMessageFormat = dijit.byId("ENmessageFormatEntry").attr("value");
		var newContent = dijit.byId("ENcontentEntry").attr("value");
		var remindRate = dijit.byId("ENremindRateEntry").attr("value");
		var URL = BASE_URL+NOTIFIES+UPDATE
				+"id="+ID
				+"&method="+newMethod
				+"&contact="+newContact
				+"&rate="+newRate
				+"&messageFormat="+newMessageFormat
				+"&content="+newContent
				+"&note="+newNote
				+"&remindRate="+remindRate
				+loginString;
				
		editNotificationDisplay.hide();

		dojo.xhrGet( {
       			url: URL,
        		handleAs: "json",
        		load: function(responseObject, ioArgs) {
				if(responseObject.Response.Type!="OK"){
					showGenericMessage("Error occured when editing notification: "+responseObject.Response.Content);
				}
				else {
					showGenericMessage("Notification Edited!");	
					refreshNotification();
				}
					
        			return responseObject;
        		}
		});
		newNotification.reset();
      	} else {
        	alert('Form contains invalid data.  Please correct first');
	}
}

//EVENT GROUP MANAGEMENT

function addNewEventGroup(){
	if(eventGroupForm.validate()) {
		var newCategory = dijit.byId("EGcategoryEntry").attr("value");
		var newType = dijit.byId("EGtypeEntry").attr("value");
		var newRate = dijit.byId("EGrateEntry").attr("value");
		var newItem = {
				type:'SysEventGroup', 
				Category:newCategory, 
				Type:newType, 
				PurgeRate:newRate
			      };	
		var URL = BASE_URL+GROUPS+CREATE+"type="+newType+"&category="+newCategory+"&purgeRate="+newRate+loginString;
		newEventGroup.hide();
		
		dojo.xhrGet( {
       			url: URL,
        		handleAs: "json",
        		load: function(responseObject, ioArgs) {
				if(responseObject.Response.Type!="OK"){
					showGenericMessage("Error occured when generating message: "+responseObject.Response.Content);
				}
				else {
					categoryStore.newItem(newItem);
					showGenericMessage("Event Group Added!");
				}
					
        			return responseObject;
        		}
		});
		newEventGroup.reset();
      	} else {
        	alert('Form contains invalid data.  Please correct first');
	}
}

function closeNewEventGroup(){
	newEventGroup.hide();
	newEventGroup.reset();
}

//EDIT event group
function editEventGroup(){
	if(eventGroupEditForm.validate()) {
		var category = dijit.byId("EGEditCategoryEntry").attr("value");
		var type = dijit.byId("EGEditCategoryTypeEntry").attr("value");
		var purgeRate = dijit.byId("EGEditPurgeRateEntry").attr("value");
		var ID_URL = BASE_URL+GROUPS+CATEGORY_SHOW+"&category="+category;
		var categoryId = null;
		dojo.xhrGet( {
   			url: ID_URL,
    		handleAs: "json",
    		load: function(responseObject, ioArgs) {
    			//var categoryId = null;
    			dojo.forEach(responseObject.Response.Content.SysEventGroups, function(item, index){
    				if(item.Type == type) {
    					categoryId = item.Id;
    				}
    			});
    			if(categoryId != null) {
    				var URL = BASE_URL+GROUPS+UPDATE+"id="+categoryId+"&purgeRate="+purgeRate;
    				dojo.xhrGet( {
   						url: URL,
   						handleAs: "json",
   						load: function(responseObject, ioArgs) {
   							if(responseObject.Response.Type == "OK") {
   								
									editEventGroup.hide();
   								showGenericMessage("Purge rate updated!");
   							}
   							else {
   								showGenericMessage("Server error occured");
   							}
   						}
   					});
    			}
    			else {
    				showGenericMessage("Category/Type combo does not exist.");
    			}
    		}
    });
		
		//var URL = BASE_URL+GROUPS+UPDATE+"id="+categoryId+"&purgeRate="+newRate+loginString;
	}
}

function closeEditEventGroup(){
	editEventGroup.hide();
	editEventGroup.reset();	
}


function eventColumnSort(event){
	var columnLabel = event.cell.name;
	if(pageState.order == null || pageState.order == "") {
		pageState.order = "desc";
	}
	else {
		if (pageState.order=="desc") {
			pageState.order="asc";
		}
		else if (pageState.order=="asc") {
			pageState.order="desc";
		}		
	}
	columnLabel=columnLabel.toLowerCase();
	if(columnLabel=="last received")
		columnLabel="lastReceived";
	if(columnLabel!="resolution"){
	    pageState.sortColumn = columnLabel;
		eventStore._jsonFileUrl=stateToURL(pageState);
		refreshEvent();
		updatePageMenu();
	}
	
}

// Populate event window with clicked event row in the eventGrid
function onEventClick(cell, gridString){

	var grid;
	if(gridString) {
		grid = dijit.byId(gridString);
	}
	else grid = this;
	
	//color the selected row (default onRowClick actions)
	if(cell) {
		grid.edit.rowClick(cell);   // <- from default onRowClick method
   		grid.selection.clickSelectEvent(cell);  // <- from default onRowClick method
    }
    
    var message = "message";
    var inputID = "resolutionInput";
    if(grid.tab != null) {
    	message = "customContent"+grid.tab;
    	inputID = inputID+grid.tab;
    }
	var selection = grid.selection.getSelected();
	if(selection.length <= 1) {
		var item = grid.getItem(selection[0]._0-1),
			id = grid.store.getValue(item, "Id"),
			category = grid.store.getValue(item, "Category"),
			type = grid.store.getValue(item, "Type"),
			source = grid.store.getValue(item, "Source"),
			description = grid.store.getValue(item, "Description"),
			firstReceived = new Date(parseInt(grid.store.getValue(item, "FirstReceived"))).toString(),
			lastReceived = new Date(parseInt(grid.store.getValue(item, "LastReceived"))).toString(),
			provider = grid.store.getValue(item, "Provider"),
			computer = grid.store.getValue(item, "Computer"),
			pid = grid.store.getValue(item, "Pid"),
			//data = this.store.getValue(item, "Data"),
			resolution = grid.store.getValue(item, "Resolution"),
			dataURL = grid.store.getValue(item, "DataUrl"),
			resolvedAt = new Date(parseInt(grid.store.getValue(item, "ResolvedAt"))).toString(),
			messageInner = "<span class='messageHeader'>Event ID: " + id + "<br>" +
			"Category: "+ category + "<br>" +
			"Type: "+ type + "<br>" +
			"Source: "+ source + "<br>" +
			"First Received: "+ firstReceived + "<br>" +
			"Last Received: "+ lastReceived + "<br>" +
			"Provider: "+ provider + "<br>" +
			"Computer: "+ computer + "<br>" +
			"PID: "+ pid + "<br>";
			if (dataURL) {
				messageInner = messageInner + "Data Link: <a href='"+ dataURL + "' target='_blank'>"+ dataURL +"</a><br>";
			}
			messageInner = messageInner +"<br><br></span>" +
			"Description:<br>"+description + "<br> <br>";//Data Link: <a href='" + data + "'>Download</a>";
		if (resolution!="N/A") {
			messageInner = "<span style='color: red'>This event has been resolved at " + resolvedAt +"<br/>Resolution: " + resolution + "</span><br><br>" + messageInner;
			dijit.byId(message).setContent(messageInner);	
		}
		else {
			dijit.byId(message).setContent("<br/><br/>"+messageInner);
			var resoltionButton = new dijit.form.Button({onClick: resolveEvent, grid: grid.id, resInput: inputID, label: "Resolve!"}).placeAt(dojo.byId(message), "first");
			var resolutionInput = new dijit.form.TextBox({id:inputID, size:40}).placeAt(dojo.byId(message), "first");
			//messageInner = "<input dojoType='dijit.form.TextBox' id='resolutionInput' size='40'/><button dojoType='dijit.form.Button' onClick='resolveEvent'>Resolve!</button><br><br>" + messageInner;	
		}		
	}
	else {
		dijit.byId(message).setContent("");
		var resoltionButton = new dijit.form.Button({onClick: resolveEvent, grid: grid.id, resInput: "resolutionInput", label: "Resolve!"}).placeAt(dojo.byId(message), "first");
		var resolutionInput = new dijit.form.TextBox({id:'resolutionInput', size:40}).placeAt(dojo.byId(message), "first");

	}

	
}

function resolveEvent(){
	var resolution = dijit.byId(this.resInput).value;
	var selection = dijit.byId(this.grid).selection.getSelected();
	var ids = [];
	var selectionSize = selection.length;
	for(var count = 0; count <selectionSize;count++){
		if(selection[count] != null) {
			ids.push(selection[count].Id[0]);
		}
	}
	var grid = dijit.byId(this.grid);
	var resolveURL = BASE_URL+EVENTS+UPDATE+"id="+ids.join(",")+"&resolution="+escape(resolution)+loginString;
	dojo.xhrGet( {
   		url: resolveURL,
   		handleAs: "json",
   		load: function(responseObject, ioArgs) {
		if(responseObject.Response.Type!="OK"){
			showGenericMessage("Error occured when trying to resolve: "+responseObject.Response.Content);
		}
		else {
			showGenericMessage("Event Resolved!");
			if(grid.tab == null)
				refreshEvent();
			else {
				refreshEvent_custom(grid.tab);
			}
		}				
   			return responseObject;
   		}
	});
}

// paging code
function nextPage(){
	pageState.page++;
	eventStore._jsonFileUrl=stateToURL(pageState);
	refreshEvent();
	updatePageMenu();
}

function previousPage(){
	pageState.page--;
	eventStore._jsonFileUrl=stateToURL(pageState);	
	refreshEvent();
	updatePageMenu();
}

function firstPage(){
	pageState.page = 0
	eventStore._jsonFileUrl=stateToURL(pageState);	
	refreshEvent();
	updatePageMenu();
}

function lastPage(){
	pageState.page = pageState.maxPage-1;
	eventStore._jsonFileUrl=stateToURL(pageState);	
	refreshEvent();
	updatePageMenu();
}

/*
function toPage(event){
	page = event.originalTarget.textContent;
	eventStore._jsonFileUrl=currentURL+"&page="+page;
	refreshEvent();
	updatePageMenu();
}*/

function selectAll() {
	var grid = dijit.byId(this.grid);
	grid.selection.selectRange(0,100);
	onEventClick(null, this.grid);
}
	
function updatePageMenu(){
	var menu = dijit.byId("eventPageMenu");	
	menu.destroyDescendants();
	var refresh = new dijit.form.Button({label:"Refresh", id:"refreshButton", onClick:refreshEvent, style:"float:right;"});
	var select = new dijit.form.Button({label:"Select All", id:"selectAllButton", onClick:selectAll, style:"float:right;", grid: "eventGrid"});
	/*var filter = new dijit.form.CheckBox({id:"filterResolvedCheckbox", onChange:filterResolvedClick, checked: false, style:"float:right; margin-left:4px;", grid: "eventGrid"});
	var filterLabel = new dojo.create("span", { innerHTML:"Filter Resolved", id: "filterCheckbox", style:"float:right; margin: 2px 6px;"});
	filterLabel.appendChild(filter.domNode);*/
	var copy = new dijit.form.Button({label:"Copy to New Tab", id:"newTabButton", onClick:copyIntoNewTab, style:"float:right;"});
	var previous = new dijit.form.Button({label:"&lt;", onClick:previousPage, style:""});
	var next = new dijit.form.Button({label:"&gt;", onClick:nextPage, style:""});
	var first = new dijit.form.Button({label:"|&lt;", onClick:firstPage, style:""});
	var last = new dijit.form.Button({label:"&gt;|", onClick:lastPage, style:""});
	var pageText = new dojo.create("div", { innerHTML:"Page "+(pageState.page+1)+" of "+pageState.maxPage, style:"float:left; margin: 2px 6px;", id:"pageRangeId"}); 
	
	/*var pageMenu = new dijit.Menu({
            style: "display: none;"
        });
	for(p=1;p<=maxPage;p++){
        	var menuItem = new dijit.MenuItem({
           		label: p,
			value: "BLAHBLAHBLAH",
           		onClick: function(event) {
         		       toPage(event);
        	 	}
        	});
        	pageMenu.addChild(menuItem);
	}
        var pageButton = new dijit.form.ComboButton({
            label: "Select Page",
            dropDown: pageMenu,
	    style:"float:left"
        });
*/

	if(pageState.page==0) {
		first.disabled = true;
		previous.disabled = true;
	}
	if(pageState.page+1==pageState.maxPage) {
		last.disabled = true;
		next.disabled = true;
	}
	var eventPageMenu = dojo.byId("eventPageMenu");	
	if(dojo.byId("pageRangeId"))
		eventPageMenu.removeChild(dojo.byId("pageRangeId"));
	eventPageMenu.appendChild(pageText);
	//eventPageMenu.appendChild(pageButton.domNode);

	refresh.disabled = true;
	menu.addChild(refresh);
	menu.addChild(copy);
	menu.addChild(select);
	//menu.addChild(filter);
	/*if(dojo.byId("filterCheckbox"))
		eventPageMenu.removeChild(dojo.byId("filterCheckbox"));
	dojo.byId("eventPageMenu").appendChild(filterLabel)*/
	menu.addChild(first);
	menu.addChild(previous);
	menu.addChild(next);
	menu.addChild(last);	
	setTimeout ( "dijit.byId('refreshButton').disabled = false;", 500 );
}

// Color row depending on type (DISABLED)
function eventRowColor(row){
         //The row object has 4 parameters, and you can set two others to provide your own styling
         //These parameters are :
         // -- index : the row index
         // -- selected: whether the row is selected
         // -- over : whether the mouse is over this row
         // -- odd : whether this row index is odd.
         
	 var item = eventGrid.getItem(row.index);
         if(item){
            var type = eventStore.getValue(item,"groupType",null);
            if(type == "ERROR"){
                row.customStyles += "background-color:#dda1a1;";
            }
	    if(type == "WARN"){
                row.customStyles += "background-color:yellow;";
            }
	    if(type == "INFO"){
                row.customStyles += "background-color:white;";
            }
         }
         eventGrid.focus.styleRow(row);
         eventGrid.edit.styleRow(row);
	
}

// Called when search form has been filled and submitted
function searchEvents(){
	pageState.category = dijit.byId("categorySearch").attr("value");
	pageState.type = [dijit.byId("typeSearch").attr("value")];
	pageState.source = dijit.byId("sourceSearch").attr("value");
	pageState.computer = dijit.byId("computerSearch").attr("value");
	pageState.description = dijit.byId("descriptionSearch").attr("value");
	pageState.exact = "";
	eventStore._jsonFileUrl=stateToURL(pageState);
	refreshEvent();
	updatePageMenu();
}

function resetSearch() {
	pageState.sortColumn="";
	pageState.order="desc";

	eventStore._jsonFileUrl=stateToURL(pageState);
	refreshEvent();
	searchForm.reset();
}


//Formatter functions for grid display
var formatFlag = function(inDatum) {
	if (inDatum == "ERROR")
		var img = "<img src='images/redflagsmall.png'/>";
	else if(inDatum == "WARN")
		var img = "<img src='images/yellowflagsmall.png'/>";
	else var img = "";
	return img;

	return inDatum;
}

var formatResolve = function(inDatum){
	if(inDatum != "N/A")
		var greenBox = "<img src='images/greenbox.png'/>";
	else greenBox = "";
	return greenBox;
}

function formatDate(input) {
	var d = new Date(parseInt(input));
	var dateString = d.getFullYear() + "-"
    if (d.getMonth() < 9) {
        dateString += "0";
    }
    dateString += (d.getMonth() + 1) + "-";       
    if (d.getDate() < 10) {
        dateString += "0";
    }
    dateString += d.getDate();
    
    dateString += " "; 
    if (d.getHours() < 9) {
        dateString += "0";
    }
    dateString += d.getHours() + ":";    
    if (d.getMinutes() < 9) {
        dateString += "0";
    }
    dateString += d.getMinutes() + ":";    
    if (d.getSeconds() < 9) {
        dateString += "0";
    }
    dateString += d.getSeconds();    
    
    return dateString;
}

/*var formatDate2 = function(inDatum){
   	var day = inDatum.substring(0, 10);
	var time = inDatum.substring(11, 19);
	return day+" "+time;
}*/

var formatDescription = function(inDatum){
    //if (inDatum.length >=36)
    //return inDatum.substring(0,36)+"...";
    return inDatum;
}
var formatDescriptionLong = function(inDatum){
    if (inDatum.length >=60)
    return inDatum.substring(0,60)+"...";
    else return inDatum;
}



// LOGIN MANAGEMENT
function processLogin(){
	if(loginForm.validate()) {
		var user = dijit.byId("loginUser").attr("value");
		var pw = dijit.byId("loginPw").attr("value");
		var URL = BASE_URL+AUTH
				+"username="+user
				+"&password="+pw
				
		loginPrompt.hide();

		dojo.xhrGet( {
       			url: URL,
        		handleAs: "json",
        		load: function(responseObject, ioArgs) {
				if(responseObject.Response.Type=="ERROR"){
					showGenericMessage(responseObject.Response.Content);
				}
				else if(responseObject.Response.Type=="OK"){
					dojo.cookie("sigevent-username", user, {expires:5});
					dojo.cookie("sigevent-password", pw, {expires:5});
					dojo.cookie("sigevent-role", responseObject.Response.Content.role, {expires:5});
					var loginText = dojo.byId("login-text");
					loginString = "&username="+user+"&password="+pw;
					loginText.innerHTML = "<span>Logged in as: "+user+"</span><br/>\
							       <span>Role: "+responseObject.Response.Content.role+"</span><br/>\
							       <span class='login-link' onclick='logout();'>Logout</span><br/>\
							       <button dojoType='dijit.form.Button' onClick='saveSettings();'>Save Settings</button>";
			        loadSettings();
					showGenericMessage("Logged In!");
					setTimeout("dijit.byId('gmessageDialog').hide()", 1000);
				}
				else {
					showGenericMessage("Unknown Error");	
				}
					
        			return responseObject;
        		}
		});
		loginPrompt.reset();
      	} else {
        	alert('Form contains invalid data.  Please correct first.');
	}
}

function logout() {
	//delete cookies
	dojo.cookie("sigevent-username", "", {expires:-1});
	dojo.cookie("sigevent-password", "", {expires:-1});
	dojo.cookie("sigevent-role", "", {expires:-1});
	
	//Update login text
	loginString = "";
	var loginText = dojo.byId("login-text");
	loginText.innerHTML = "<span class='login-link' onclick=\"dijit.byId('loginPrompt').show();\">Login</span>";
	showGenericMessage("Logged Out!");	
	setTimeout("dijit.byId('gmessageDialog').hide()", 1000);
}

function isLoggedIn() {
	var username = dojo.cookie("sigevent-username");
	var pw = dojo.cookie("sigevent-password");
	loginString = "&username="+username+"&password="+pw;
	if(username && pw) {
		return true;
	}
	else return false;
}

// Settings functions
function saveSettings() {
	//Object to hold config values
	var settings = {};
	settings.tabs = [];

    var tabChildren = tabs.getChildren();
    var grids = [];
	for(var i=2;i<tabChildren.length;i++) {
	    var id = dojo.query(".dojoxGrid", tabChildren[i].domNode)[0].id;
	    grids.push(id.substr(10, id.length ));
	}
	for(var i=0;i<grids.length;i++) {
	    settings.tabs.push(dijit.byId("customGrid"+grids[i]).state);
	}
	settings.frontSettings = pageState;

	settingString = JSON.stringify(settings);
	settingString = encodeURIComponent(settingString);	
	var URL = BASE_URL+USERS+USER_UPDATE+loginString+"&setting="+settingString;
	dojo.xhrGet( {
		url: URL,
		handleAs: "json",
        load: function(responseObject, ioArgs) {
          showGenericMessage("Settings Saved!");   
        }
    });
	
}

function loadSettings() {
    var URL = BASE_URL+USERS+USER_SHOW+loginString;
    dojo.xhrGet( {
		url: URL,
		handleAs: "json",
        load: function(responseObject, ioArgs) {
            if(responseObject.Response) {
                var settings = JSON.parse(responseObject.Response.Content.SysUserSettings[0].Setting);
                for(var x=0;x< settings.tabs.length;x++) {
                    createNewTab(settings.tabs[x].label, settings.tabs[x]);
                }
                pageState = settings.frontSettings;
                
                dijit.byId("infoCheckBox").set("checked", false);
                dijit.byId("warnCheckBox").set("checked", false);
                dijit.byId("errorCheckBox").set("checked", false);
                dojo.forEach(pageState.type, function(item, index) {
                    if(item == "INFO")
                        dijit.byId("infoCheckBox").set("checked", true);
                    if(item == "WARN")
                        dijit.byId("warnCheckBox").set("checked", true);
                    if(item == "ERROR")
                        dijit.byId("errorCheckBox").set("checked", true);
                       
                });
                      
	            eventStore._jsonFileUrl=stateToURL(pageState);
                setTimeout(function(){refreshEvent();}, 1000);

            } 
        }
    });
}

