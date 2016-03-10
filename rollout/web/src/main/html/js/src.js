/*
Dataset Template Form
Author: Christian Alarcon (calarcon@sdsio.jpl.nasa.gov)
*/

/* KNOWN BUGS

 - Can add multiple of selected items (example contact drop down list)
 - Can't reuse created sensor for another source
 
*/



//dojo.require("dojo.parser");
dojo.require("dojo.data.ItemFileWriteStore");
//dojo.require("dijit.dijit");
dojo.require("dijit.form.Button");
dojo.require("dijit.Menu");
//dojo.require("dijit.Tree");
dojo.require("dijit.Tooltip");
dojo.require("dijit.Dialog");
dojo.require("dijit.Toolbar");
//dojo.require("dijit.ProgressBar");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.Textarea");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.DateTextBox");
dojo.require("dijit.form.TimeTextBox");
dojo.require("dijit.form.TextBox");
//dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.FilteringSelect");
dojo.require("dijit.form.Form")
dojo.require("dijit.layout.BorderContainer");
//dojo.require("dijit.layout.AccordionContainer");
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.form.NumberSpinner");
dojo.require("dijit.form.NumberTextBox");
//dojo.require("dojox.grid.DataGrid");
//dojo.require("dojox.io.xhrPlugins");


// GLOBAL VARS
var sourceSensorMax = 0; // "0" meaning infinite amount

var contactNum = 1;
var projectNum = 1;
var sensorNum = 1;
var paramNum = 1;
var citationNum = 1;
var locationPolicyNum = 1;
var versionNum = 1;
var resourceNum = 1;
var providerPathNum = 1;
var collectionNum = 1;
var elementNum = 1;
var regionNum = 1;

var infoToggle = true;

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
	
	dojo.parser.parse();
	dijit.setWaiRole(dojo.body(), "application");
	
	dojo.query("body").onclick(disableInfoPopup);
		
	/*var n = dojo.byId("preLoader");
	dojo.fadeOut({
		node:n,
		duration:720,
		onEnd:function(){
			// dojo._destroyElement(n); 
			dojo.style(n,"display","none");
		}
	}).play();*/
	//datasetForm.validate();


<?php 

printExistingDatasetParameterJS($datasetManifest); 
printExistingDatasetLocationPolicyJS($datasetManifest); 
printExistingDatasetProjectJS($datasetManifest);
printExistingDatasetSourceSensorJS($datasetManifest);
printExistingDatasetResourceJS($datasetManifest);
printExistingDatasetProviderJS($datasetManifest);
printExistingDatasetCitationJS($datasetManifest);
printExistingDatasetContactJS($datasetManifest);
printExistingDatasetCollectionJS($datasetManifest);
printExistingDatasetElementJS($datasetManifest);
printExistingDatasetRegionJS($datasetManifest);

if($requestType == "update")
	print "processStatus();";

?>
	

});

function inputModified(input){
	//console.log(input);
	this.modified = 'TRUE';
	dojo.byId(this.id+"Modified").innerHTML="Modified";
	//console.log(this);
}



// Display generic message in a popup (usually error or success messages)
function showGenericMessage(text){
	//console.log(text);
	dojo.byId("gmessage").innerHTML=text;
	dijit.byId("gmessageDialog").show();
}

function removeChildrenFromNode(node) {
	if(node.hasChildNodes()) {
		while(node.childNodes.length >= 1 ) {
			node.removeChild(node.firstChild);
		}
	} 
}

function saveLocal(){
	if(dijit.byId("dataset_shortName").attr("value") != "") {
		dojo.xhrPost( {
       			form:dojo.byId("datasetForm"),
			url:"controllers/save-controller.php",
        		handleAs: "json",
        		load: function(responseObject, ioArgs) {
				//dijit.byId("localID").attr("value", responseObject.id);
				if(responseObject.response == "OK" || responseObject.response == "201")
					showGenericMessage("Saved!");
				else showGenericMessage("Error encountered while saving!");
				return responseObject;
        		},
			error: function(responseObject, ioArgs) {
				//console.log(ioArgs);
				showGenericMessage("Error encountered while saving!");
				return responseObject;
        		}
			
		});
	}
	else {
		showGenericMessage("Must have a Dataset Short Name before saving!");
	}
}


// Submit final dataset information to generate SQL
function submitFinal(){
	if(dijit.byId("dataset_shortName").attr("value") != "") {
		if(dojo.indexOf(dojo.query(".error-message").style("display"), "inline") == -1){
			dojo.xhrPost( {
	       		form:dojo.byId("datasetForm"),
				url:"controllers/save-controller.php",
	        		handleAs: "json",
	        		load: function(responseObject, ioArgs) {
					if(responseObject.response != "OK" && responseObject.response != "201")
						showGenericMessage("Error encountered while saving!");
					if(datasetForm.validate()) {
						dojo.byId("datasetForm").submit();
					}
					else { showGenericMessage("Form not complete!"); }
					return responseObject;
	        		},
				error: function(responseObject, ioArgs) {
						return responseObject;
	        		}
				
			});
		}
		else {
			showGenericMessage("Dataset Short Name and Persistent ID must be unique!");
		}
	}
	else {
		showGenericMessage("Must have a Dataset Short Name before saving!");
	}
}

function removeChildrenFromNode(node) {
	if(node.hasChildNodes()) {
		while(node.childNodes.length >= 1 ) {
			node.removeChild(node.firstChild);
		}
	} 
}


/////////////////////////  DATSET PARAMETER ///////////////////////////////////////////////////////

function createNewParameter() {
	var num = paramNum;
	var paramTable = dojo.byId("parameterTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"parameterForm"+num});	
		
	var title = dojo.create("div", {innerHTML:"Parameter "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){ paramTable.removeChild(dojo.byId("parameterForm"+num)); }})
	title.appendChild(deleteButton.domNode);
	//labels.push(title);
	//inputs.push(deleteButton.domNode);	

	var tail = dojo.create("div", {innerHTML:"<br/>"});

<?php
	printDatasetParameterJS($templateManifest);
?>	

	container.appendChild(title);
	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	container.appendChild(tail);

	paramTable.appendChild(container);
	
	//increaseCount("paramCount");	
	paramNum++;
}

/////////////////////////  DATSET REGION ///////////////////////////////////////////////////////

function createNewRegion() {
	var num = regionNum;
	var regionTable = dojo.byId("regionTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"regionForm"+num});	
		
	var title = dojo.create("div", {innerHTML:"Region "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){ regionTable.removeChild(dojo.byId("regionForm"+num)); }})
	title.appendChild(deleteButton.domNode);
	//labels.push(title);
	//inputs.push(deleteButton.domNode);	

	var tail = dojo.create("div", {innerHTML:"<br/>"});

<?php
	printDatasetRegionJS($templateManifest);
?>	

	container.appendChild(title);
	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	container.appendChild(tail);

	regionTable.appendChild(container);
	
	//increaseCount("regionCount");	
	regionNum++;
}


///////////////////////// SOURCE / SENSOR /////////////////////////////////////////////////////////

function newSource() {
	dijit.byId("sourceButtonOK").setLabel("Confirm New Source");
	dojo.byId("sourceForm").reset();
	dijit.byId("sourceAction").attr("value", "create");
	dojo.byId("source_sourceId").innerHTML="";
	dijit.byId("sourceDialog").show();
}

function editSource() {
	sourceId = dijit.byId(this.sourceForm).attr("value");
	if(sourceId !="") {
	dijit.byId("sourceButtonOK").setLabel("Confirm Edit");
	dijit.byId("sourceAction").attr("value", "update");
	
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=source&source_sourceId="+sourceId,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			idRegEx = new RegExp(".*Id$");	
			for(x=0; x<responseObject.items.length; x++) {
				if(idRegEx.test(responseObject.items[x].field)) {
					dojo.byId(responseObject.items[x].field).innerHTML = responseObject.items[x].value;
				}
				else {
					dijit.byId(responseObject.items[x].field).attr("value", responseObject.items[x].value);
				}
			}
			//console.log(responseObject.items.length);
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	dijit.byId("sourceDialog").show();
	}
	else showGenericMessage("Must select an item before editing!");
}

function processSource() {
	id = dojo.byId("source_sourceId").innerHTML;

	if(dijit.byId("sourceForm").validate()) {

	dijit.byId("sourceButtonOK").attr("disabled", true);
	dojo.xhrPost({
		form:dojo.byId("sourceForm"),
		url:"controllers/submit-controller.php?type=source&source_sourceId="+id,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				sourceStore.close();
				dijit.byId("sourceDialog").hide();
				showGenericMessage("Source Added!");
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			dijit.byId("sourceButtonOK").attr("disabled", false);
			return responseObject;
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			dijit.byId("sourceButtonOK").attr("disabled", false);
			return responseObject;
		}
	});

	}
	else { showGenericMessage("Form not complete!"); }
}


function newSensor() {
	dijit.byId("sensorButtonOK").setLabel("Confirm New Sensor");
	dojo.byId("sensorForm").reset();
	dijit.byId("sensorAction").attr("value", "create");
	dojo.byId("sensor_sensorId").innerHTML="";
	dijit.byId("sensorDialog").show();
}

function editSensor() {
	sensorId = dijit.byId(this.sensorForm).attr("value");

	if(sensorId != "") {
	dijit.byId("sensorButtonOK").setLabel("Confirm Edit");
	dijit.byId("sensorAction").attr("value", "update");
	
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=sensor&sensor_sensorId="+sensorId,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			idRegEx = new RegExp(".*Id$");	
			for(x=0; x<responseObject.items.length; x++) {
				if(idRegEx.test(responseObject.items[x].field)) {
					dojo.byId(responseObject.items[x].field).innerHTML = responseObject.items[x].value;
				}
				else {
					dijit.byId(responseObject.items[x].field).attr("value", responseObject.items[x].value);
				}
			}
			//console.log(responseObject.items.length);
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	dijit.byId("sensorDialog").show();
	}
	else showGenericMessage("Must select an item before editing!");
}

function processSensor() {
	id = dojo.byId("sensor_sensorId").innerHTML;

	if(dijit.byId("sensorForm").validate()) {

	dijit.byId("sensorButtonOK").attr("disabled", true);
	dojo.xhrPost({
		form:dojo.byId("sensorForm"),
		url:"controllers/submit-controller.php?type=sensor&sensor_sensorId="+id,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				sensorStore.close();
				dijit.byId("sensorDialog").hide();
				showGenericMessage("Sensor Processed!");
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			dijit.byId("sourceButtonOK").attr("disabled", false);
			return responseObject;
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			dijit.byId("sourceButtonOK").attr("disabled", false);
			return responseObject;
		}
	});
	}
	else { showGenericMessage("Form not complete!"); }
}

function addNewSourceSensorRow(){
	var num = sensorNum;
	var sensorTable = dojo.byId("sensorTable");
	var labels = new Array();
	var inputs = new Array();
	var labels2 = new Array();
	var inputs2 = new Array();
	var currentRows = sensorTable.children.length;
	
	if(currentRows < sourceSensorMax || sourceSensorMax == 0) {

	var container = dojo.create("div", {id:"sensorForm"+num});	
	
	
	var title = dojo.create("div", {innerHTML:"Source/Sensor"+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){sensorTable.removeChild(dojo.byId("sensorForm"+num)); }})
	title.appendChild(deleteButton.domNode);
		
	var tail = dojo.create("div", {innerHTML:"<br/>"});

	var sourceContainer = dojo.create("table", {style:"float:none;"});

	var sourceContainerLabel = new dojo.create("div", {innerHTML:"<b>Source</b>", style:"float:none;"});
	var sourceDropDown = new dijit.form.FilteringSelect({
						name:"datasetSource_source"+num ,
						store:sourceStore,
						required:true,
						searchAttr:"source_shortName", 
						labelAttr:"source_shortName", 
						id:"datasetSource_source"+num });
	var sourceEditButton = new dijit.form.Button({
						label:"View/Edit Details", 
						sourceForm:"datasetSource_source"+num, 
						onClick:editSource});
	var sourceNewButton = new dijit.form.Button({
						label:"Create New Entry", 
						iconClass:"mailIconNewMessage", 
						sourceForm:"datasetSource_source"+num, 
						onClick:newSource});
	sourceContainerLabel.appendChild(sourceDropDown.domNode);
	sourceContainerLabel.appendChild(sourceEditButton.domNode);


	var sensorContainer = dojo.create("table", {style:"float:none;"});
	var sensorContainerLabel = dojo.create("div", {innerHTML:"<b>Sensor</b>"})
	var sensorDropDown = new dijit.form.FilteringSelect({
						name:"datasetSource_sensor"+num ,
						store:sensorStore,
						required:true,
						searchAttr:"sensor_shortName", 
						labelAttr:"sensor_shortName", 
						id:"datasetSource_sensor"+num });
	var sensorEditButton = new dijit.form.Button({
						label:"View/Edit Details", 
						sensorForm:"datasetSource_sensor"+num, 
						onClick:editSensor});
	var sensorNewButton = new dijit.form.Button({
						label:"Create New Entry", 
						iconClass:"mailIconNewMessage", 
						sensorForm:"datasetSource_sensor"+num, 
						onClick:newSensor});
	sensorContainerLabel.appendChild(sensorDropDown.domNode);
	sensorContainerLabel.appendChild(sensorEditButton.domNode);


	sensorContainer.appendChild(sensorContainerLabel);
	sourceContainer.appendChild(sourceContainerLabel);
	sourceContainer.appendChild(tail);

	container.appendChild(title);
	container.appendChild(sourceContainer);
	container.appendChild(tail);
	container.appendChild(sensorContainer);
	container.appendChild(tail);

	sensorTable.appendChild(container);

	sensorNum++;
	}
	
	else {
		showGenericMessage("Reached max source/sensor pairs ("+sourceSensorMax+")");
	}
}



////////////////////////////////////////// Contact  ///////////////////////////////////////////////////////////////////


function newContact() {
	dijit.byId("contactButtonOK").setLabel("Confirm New Contact");
	dojo.byId("contactForm").reset();
	dijit.byId("contactAction").attr("value", "create");
	dojo.byId("contact_contactId").innerHTML="";
	dijit.byId("contactDialog").show();
}

function editContact() {
	contactId = dijit.byId(this.contactForm).attr("value");

	if(contactId != "") {
	dijit.byId("contactButtonOK").setLabel("Confirm Edit");
	dijit.byId("contactAction").attr("value", "update");
	
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=contact&contact_contactId="+contactId,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			idRegEx = new RegExp(".*contactId$");	
			for(x=0; x<responseObject.items.length; x++) {
				if(idRegEx.test(responseObject.items[x].field)) {
					dojo.byId(responseObject.items[x].field).innerHTML = responseObject.items[x].value;
				}
				else { 
					//console.log(responseObject.items[x].field+" "+responseObject.items[x].value);
					dijit.byId(responseObject.items[x].field).attr("value", responseObject.items[x].value);
				}
			}
			//console.log(responseObject.items.length);
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	dijit.byId("contactDialog").show();
	}
	else showGenericMessage("Must select an item before editing!");
}

function processContact() {
	id = dojo.byId("contact_contactId").innerHTML;

	if(dijit.byId("contactForm").validate()) {

	dijit.byId("contactButtonOK").attr("disabled", true);
	dojo.xhrPost({
		form:dojo.byId("contactForm"),
		url:"controllers/submit-controller.php?type=contact&contact_contactId="+id,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				contactStore.close();
				dijit.byId("contactDialog").hide();
				showGenericMessage("Contact Processed!");
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			dijit.byId("contactButtonOK").attr("disabled", false);
			return responseObject;
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			dijit.byId("contactButtonOK").attr("disabled", false);
			return responseObject;
		}
	});
	}
	else { showGenericMessage("Form not complete!"); }
}


function createNewContact(){
	
	var num = contactNum;
	var contactTable = dojo.byId("contactTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"contactForm"+num});	
	
	var title = dojo.create("div", {innerHTML:"Contact "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){contactTable.removeChild(dojo.byId("contactForm"+num)); }})
	title.appendChild(deleteButton.domNode);
	
	var tail = dojo.create("div", {innerHTML:"<br/>"});

	var contactContainerLabel = new dojo.create("div", {style:"float:none;"});
	var contactDropDown = new dijit.form.FilteringSelect({
						name:"datasetContact_contactId"+num ,
						store:contactStore,
						required:true,
						searchAttr:"contact_name", 
						labelAttr:"contact_name", 
						id:"datasetContact_contactId"+num });
	var contactEditButton = new dijit.form.Button({
						label:"View/Edit Details", 
						//iconClass:"mailIconNewMessage", 
						contactForm:"datasetContact_contactId"+num, 
						onClick:editContact});
	contactContainerLabel.appendChild(contactDropDown.domNode);
	contactContainerLabel.appendChild(contactEditButton.domNode);

	contactNum++;
	
	var newRow;
	var newCell1;
	var newCell2;
	
	container.appendChild(title);
	container.appendChild(contactContainerLabel);
	container.appendChild(tail);
	
	contactTable.appendChild(container);

}

////////////////////////////////////////// Project  ///////////////////////////////////////////////////////////////////

function loadProjectFromManifest(id) {
	
	var selectionID = id;
	//console.log("project id to load is "+selectionID[0]);
	projectStore.fetch({query: {PROJECT_ID:selectionID[0]}, test: 123, onItem: loadProject});
}

function addProject() {
	var num = projectNum-1;
	var selection = dijit.byId("projectEntry");	
	var selectionID = selection.attr("value");
	projectStore.fetch({query: {project_id:selectionID}, onItem: loadProject});
}


function loadProject(item) {
	//console.log("return from ops query");
	//console.log(item);
	generateNewProjectRow(item.project_id, item.project_shortName);

}

function newProject() {
	dijit.byId("projectButtonOK").setLabel("Confirm New Project");
	dojo.byId("projectForm").reset();
	dijit.byId("projectAction").attr("value", "create");
	dojo.byId("project_projectId").innerHTML="";
	dijit.byId("projectDialog").show();
}

function editProject() {
	//console.log(this);
	//projectId = dijit.byId(this.projectForm).attr("value");
	projectId = dijit.byId("datasetProject_projectId").attr("value");
	if(projectId != "") {
	dijit.byId("projectButtonOK").setLabel("Confirm Edit");
	dijit.byId("projectAction").attr("value", "update");
	
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=project&project_projectId="+projectId,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			idRegEx = new RegExp(".*Id$");	
			for(x=0; x<responseObject.items.length; x++) {
				if(idRegEx.test(responseObject.items[x].field)) {
					dojo.byId(responseObject.items[x].field).innerHTML = responseObject.items[x].value;
				}
				else {
					dijit.byId(responseObject.items[x].field).attr("value", responseObject.items[x].value);
				}
			}
			//console.log(responseObject.items.length);
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	dijit.byId("projectDialog").show();
	}
	else showGenericMessage("Must select an item before editing!");
}

function processProject() {
	id = dojo.byId("project_projectId").innerHTML;

	if(dijit.byId("projectForm").validate()) {

	dijit.byId("projectButtonOK").attr("disabled", true);
	dojo.xhrPost({
		form:dojo.byId("projectForm"),
		url:"controllers/submit-controller.php?type=project&project_projectId="+id,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				projectStore.close();
				dijit.byId("projectDialog").hide();
				showGenericMessage("Project Processed!");
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			dijit.byId("projectButtonOK").attr("disabled", false);
			return responseObject;
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			dijit.byId("projectButtonOK").attr("disabled", false);
			return responseObject;
		}
	});
	}
	else { showGenericMessage("Form not complete!"); }
}



function generateNewProjectRow(project_id, project_shortName){
	
	var num = projectNum;
	 var projectTable = dojo.byId("projectTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"projectForm"+num});	
	
	var title = dojo.create("div", {innerHTML:"Project "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){projectTable.removeChild(dojo.byId("projectForm"+num)); }})
	var editButton = new dijit.form.Button({label:"View/Edit Details", iconClass:"mailIconNewMessage", projectForm:"datasetProject_projectId"+num, onClick:editProject})
	title.appendChild(deleteButton.domNode);
	title.appendChild(editButton.domNode);
	//labels.push(title);
	//inputs.push(deleteButton.domNode);

	var tail = dojo.create("div", {innerHTML:"<br/>"});
	
	var dropDown = new dijit.form.FilteringSelect({id:"datasetProject_projectId"+num, name:"datasetProject_projectId"+num, store:projectStore, searchAttr:"project_shortName", labelAttr:"project_shortName", required:true});
	//dropDown.domNode.appendChild(deleteButton.domNode);
	//dropDown.domNode.appendChild(editButton.domNode);	
	labels.push(dropDown.domNode);
	inputs.push(title);
	
	projectNum++;
	
	var newRow;
	var newCell1;
	var newCell2;
	
//	container.appendChild(title);
//	container.appendChild(id.domNode);
	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	container.appendChild(tail);
	
	projectTable.appendChild(container);

}


////////////////////////////////////////// Provider  ///////////////////////////////////////////////////////////////////

function newProvider() {
	dijit.byId("providerButtonOK").setLabel("Confirm New provider");
	dojo.byId("providerForm").reset();
	dijit.byId("providerAction").attr("value", "create");
	dojo.byId("provider_providerId").innerHTML="";
	clearPaths();
	dijit.byId("providerDialog").show();
}

function editProvider() {
	//console.log(this);
	providerId = dijit.byId("dataset_providerId").attr("value");
	if(providerId != "") {
	dijit.byId("providerButtonOK").setLabel("Confirm Edit");
	dijit.byId("providerAction").attr("value", "update");
	clearPaths();
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=provider&provider_providerId="+providerId,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			idRegEx = new RegExp(".*Id$");
			pathRegEx = new RegExp(".*path.*");
			for(x=0; x<responseObject.items.length; x++) {
				if(idRegEx.test(responseObject.items[x].field)) {
					dojo.byId(responseObject.items[x].field).innerHTML = responseObject.items[x].value;
				}
				else if(pathRegEx.test(responseObject.items[x].field)) {
					var num = providerPathNum;
					addNewProviderPath();
					dijit.byId("provider_path"+num).attr("value", responseObject.items[x].value);
				}
				else {
					dijit.byId(responseObject.items[x].field).attr("value", responseObject.items[x].value);
				}
			}
			//console.log(responseObject.items.length);
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	dijit.byId("providerDialog").show();
	}
	else showGenericMessage("Must select an item before editing!");
}

function processProvider() {
	id = dojo.byId("provider_providerId").innerHTML;

	if(dijit.byId("providerForm").validate()) {

	dijit.byId("providerButtonOK").attr("disabled", true);
	dojo.xhrPost({
		form:dojo.byId("providerForm"),
		url:"controllers/submit-controller.php?type=provider&provider_providerId="+id,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				providerStore.close();
				dijit.byId("providerDialog").hide();
				showGenericMessage("Provider Processed!");
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			dijit.byId("providerButtonOK").attr("disabled", false);
			return responseObject;
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			dijit.byId("providerButtonOK").attr("disabled", false);
			return responseObject;
		}
	});
	}
	else { showGenericMessage("Form not complete!"); }
}

function addExistingProvider() {
	var selection = dijit.byId("providerEntry");	
	var selectionID = selection.attr("value");
	providerStore.fetch({query: {PROVIDER_ID:selectionID}, onItem: loadProvider});
}

function loadExistingProvider(id) {
	var selectionID = id;
	providerStore.fetch({query: {PROVIDER_ID:selectionID}, onItem: loadProvider});
}

function loadProvider(item) {
	//console.log(item);
	dijit.byId("providerID").attr("value", item.PROVIDER_ID);
	dijit.byId("providerShortName").attr("value", item.SHORT_NAME);
	dijit.byId("providerShortName").attr("disabled", "true");
	dijit.byId("providerLongName").attr("value", item.LONG_NAME);
	dijit.byId("providerLongName").attr("disabled", "true");
	dijit.byId("providerType").attr("value", item.TYPE);
	dijit.byId("providerType").attr("disabled", "true");
	dijit.byId("providerPathButton").attr("disabled", true);
	if ( dojo.byId("providerTable").hasChildNodes() )
	{
    		while ( dojo.byId("providerTable").childNodes.length >= 1 )
    		{
        	dojo.byId("providerTable").removeChild( dojo.byId("providerTable").firstChild );       
   		} 
	}

}

function clearProvider(){
	dijit.byId("providerID").attr("value", "");
	dijit.byId("providerShortName").attr("value", "");
	dijit.byId("providerShortName").attr("disabled", false);
	dijit.byId("providerLongName").attr("value", "");
	dijit.byId("providerLongName").attr("disabled", false);
	dijit.byId("providerType").attr("value", "");
	dijit.byId("providerType").attr("disabled", false);
	dijit.byId("providerPathButton").attr("disabled",false);
}

function clearPaths() {
	removeChildrenFromNode(dojo.byId("providerPathTable"));
	//providerPathNum = 1;
}

function addNewProviderPath(){
	var num = providerPathNum;
	providerPathNum++;
	var table = dojo.byId("providerPathTable");
	var label = new dojo.create("div", {innerHTML:"Path: "});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){dojo.byId("providerPathTable").removeChild(dojo.byId("providerPathRow"+num)); }})
	label.appendChild(deleteButton.domNode);
	var input = new dijit.form.ValidationTextBox({maxLength:"255", style:"float:none;", id:"provider_path"+num, name:"provider_path"+num, required:true});
	
	var newRow = dojo.create("tr", {id:"providerPathRow"+num});
	var newCell1 = dojo.create("td");
	newCell1.appendChild(label);
	var newCell2 = dojo.create("td");
	newCell2.appendChild(input.domNode);
	newRow.appendChild(newCell1);
	newRow.appendChild(newCell2);
	table.appendChild(newRow);
}


////////////////////////////////////////// Citation  ///////////////////////////////////////////////////////////////////

function toggleCitation() {
	<?php printToggleCitationJS($templateManifest); ?>
	for(x=0;x<citationField.length;x++) {
		//if (dijit.byId(citationField[x]).attr("disabled") == "false") {
			dijit.byId(citationField[x]).attr("disabled", "true");
		//}
		//else if(dijit.byId(citationField[x]).attr("disabled") == "true") {
		//	dijit.byId(citationField[x]).attr("disabled", "false");
		//}
	}
}

function createNewCitation(){
	
	var num = citationNum;
	var citationTable = dojo.byId("citationTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"citationForm"+num});	

	var entryTitle = dojo.create("div", {innerHTML:"Citation "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){citationTable.removeChild(dojo.byId("citationForm"+num));}})
	entryTitle.appendChild(deleteButton.domNode);
	
	var tail = dojo.create("div", {innerHTML:"<br/>"});

<?php
	printDatasetCitationJS($templateManifest);
?>

	citationNum++;
	
	var newRow;
	var newCell1;
	var newCell2;
	
	container.appendChild(entryTitle);

	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	//container.appendChild(tail);
	
	citationTable.appendChild(container);

}



////////////////////////////////////////// Version  ///////////////////////////////////////////////////////////////////

function createNewVersion(){
	
	var num = versionNum;
	var versionTable = dojo.byId("versionTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"versionForm"+num});	

	var entryTitle = dojo.create("div", {innerHTML:"Version "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){ versionTable.removeChild(dojo.byId("versionForm"+num));}})
	entryTitle.appendChild(deleteButton.domNode);
	
	var tail = dojo.create("div", {innerHTML:"<br/>"});

	var titleLabel = new dojo.create("div", {innerHTML:"Title:  ", style:"float:none;"});
	var title = new dijit.form.ValidationTextBox({maxLength:"255", style:"float:none;", id:"versionTitle"+num, name:"versionTitle"+num, required:true});
	labels.push(titleLabel);
	inputs.push(title.domNode);	
	
	//increaseCount("versionCount");

	versionNum++;
	
	var newRow;
	var newCell1;
	var newCell2;
	
	container.appendChild(entryTitle);

	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	//container.appendChild(tail);
	
	versionTable.appendChild(container);

}

////////////////////////////////////////// Location Policy  ///////////////////////////////////////////////////////////////////

function createNewLocationPolicy(){
	
	var num = locationPolicyNum;
	var locationPolicyTable = dojo.byId("locationPolicyTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"locationPolicyForm"+num});	

	var entryTitle = dojo.create("div", {innerHTML:"LocationPolicy "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel",  <?php if(!$_SESSION['dmt-admin'] && $requestType != "create") print "disabled:true, "; ?> onClick:function(){locationPolicyTable.removeChild(dojo.byId("locationPolicyForm"+num));}})
	entryTitle.appendChild(deleteButton.domNode);
	
	var tail = dojo.create("div", {innerHTML:"<br/>"});

<?php
printDatasetLocationPolicyJS($templateManifest);
?>
	locationPolicyNum++;
	
	var newRow;
	var newCell1;
	var newCell2;
	
	container.appendChild(entryTitle);

	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	//container.appendChild(tail);
	
	locationPolicyTable.appendChild(container);

}

////////////////////////////////////////// Resource  ///////////////////////////////////////////////////////////////////

function createNewResource(){
	
	var num = resourceNum;
	var resourceTable = dojo.byId("resourceTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"resourceForm"+num});	

	var entryTitle = dojo.create("div", {innerHTML:"Resource "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){resourceTable.removeChild(dojo.byId("resourceForm"+num));}})
	entryTitle.appendChild(deleteButton.domNode);
	
	var tail = dojo.create("div", {innerHTML:"<br/>"});

	var nameLabel = new dojo.create("div", {innerHTML:"Name:  ", style:"float:none;"});

<?php
printDatasetResourceJS($templateManifest);
?>
	resourceNum++;
	
	var newRow;
	var newCell1;
	var newCell2;
	
	container.appendChild(entryTitle);

	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	//container.appendChild(tail);
	
	resourceTable.appendChild(container);

}

////////////////////////////////////////// Collection  ///////////////////////////////////////////////////////////////////

function newCollection() {
	dijit.byId("collectionButtonOK").setLabel("Confirm New Collection");
	dojo.byId("collectionForm").reset();
	dijit.byId("collectionAction").attr("value", "create");
	dojo.byId("collection_collectionId").innerHTML="";
	clearPaths();
	dijit.byId("collectionDialog").show();
}

function editCollection() {
	//console.log(this);
	collectionId = dijit.byId(this.collectionForm).attr("value");
	if(collectionId != "") {
	dijit.byId("collectionButtonOK").setLabel("Confirm Edit");
	dijit.byId("collectionAction").attr("value", "update");
	clearPaths();
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=collection&collection_collectionId="+collectionId,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			idRegEx = new RegExp(".*collectionId$");
			pathRegEx = new RegExp(".*path.*");
			datasetRegEx = new RegExp("^collectionDataset.*");
			for(x=0; x<responseObject.items.length; x++) {
				if(idRegEx.test(responseObject.items[x].field)) {
					dojo.byId(responseObject.items[x].field).innerHTML = responseObject.items[x].value;
				}
				else {
					if(!datasetRegEx.test(responseObject.items[x].field)) {
						//console.log("Setting "+responseObject.items[x].field+" to "+responseObject.items[x].value);
						dijit.byId(responseObject.items[x].field).attr("value", responseObject.items[x].value);
					}
				}
			}
			//console.log(responseObject.items.length);
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	dijit.byId("collectionDialog").show();
	}
	else showGenericMessage("Must select an item before editing!");
}

function processCollection() {
	id = dojo.byId("collection_collectionId").innerHTML;

	if(dijit.byId("collectionForm").validate()) {

	dijit.byId("collectionButtonOK").attr("disabled", true);		
	dojo.xhrPost({
		form:dojo.byId("collectionForm"),
		url:"controllers/submit-controller.php?type=collection&collection_collectionId="+id,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				collectionStore.close();
				dijit.byId("collectionDialog").hide();
				showGenericMessage("Collection Processed!");
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			dijit.byId("collectionButtonOK").attr("disabled", false);
			return responseObject;
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			dijit.byId("collectionButtonOK").attr("disabled", false);
			return responseObject;
		}
	});
	}
	else { showGenericMessage("Form not complete!"); }
}


function createNewCollection() {
	var num = collectionNum;
	var collectionTable = dojo.byId("collectionTable");
		var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"collectionForm"+num});	

	var entryTitle = dojo.create("div", {innerHTML:"Collection "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){collectionTable.removeChild(dojo.byId("collectionForm"+num));}})
	entryTitle.appendChild(deleteButton.domNode);
	
	var tail = dojo.create("div", {innerHTML:"<br/>"});

	var nameLabel = new dojo.create("div", {innerHTML:"Name:  ", style:"float:none;"});

<?php
printDatasetCollectionJS($templateManifest);
?>
	collectionNum++;
	
	var newRow;
	var newCell1;
	var newCell2;
	
	container.appendChild(entryTitle);

	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	//container.appendChild(tail);
	
	collectionTable.appendChild(container);

}


///////////////////////////////// Element ///////////////////////////////////

function newElement() {
	dijit.byId("elementButtonOK").setLabel("Confirm New Element");
	dojo.byId("elementForm").reset();
	dijit.byId("elementAction").attr("value", "create");
	dojo.byId("element_elementId").innerHTML="";
	clearPaths();
	dijit.byId("elementDialog").show();
}

function editElement() {
	//console.log(this);
	elementId = dijit.byId(this.elementForm).attr("value");
	if(elementId != "") {
	dijit.byId("elementButtonOK").setLabel("Confirm Edit");
	dijit.byId("elementAction").attr("value", "update");
	clearPaths();
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=element&element_elementId="+elementId,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			idRegEx = new RegExp(".*Id$");
			pathRegEx = new RegExp(".*path.*");
			for(x=0; x<responseObject.items.length; x++) {
				if(idRegEx.test(responseObject.items[x].field)) {
					dojo.byId(responseObject.items[x].field).innerHTML = responseObject.items[x].value;
				}
				else {
					dijit.byId(responseObject.items[x].field).attr("value", responseObject.items[x].value);
				}
			}
			//console.log(responseObject.items.length);
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	dijit.byId("elementDialog").show();
	}
	else showGenericMessage("Must select an item before editing!");
}

function processElement() {
	id = dojo.byId("element_elementId").innerHTML;
	
	if(dijit.byId("elementForm").validate()) {

	dijit.byId("elementButtonOK").attr("disabled", true);
	dojo.xhrPost({
		form:dojo.byId("elementForm"),
		url:"controllers/submit-controller.php?type=element&element_elementId="+id,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				elementStore.close();
				dijit.byId("elementDialog").hide();
				showGenericMessage("Element Processed!");
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			dijit.byId("elementButtonOK").attr("disabled", false);
			return responseObject;
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			dijit.byId("elementButtonOK").attr("disabled", false);
			return responseObject;
		}
	});

	}
	else { showGenericMessage("Form not complete!"); }
}

function createNewElement(existingId) {
	if(existingId == null) {
		var id = dijit.byId("elementTypeSelect").attr("value");
		//console.log("no id specified!");
	}
	else {
		var id = existingId;
	}
	if(id !="") {
	dojo.xhrGet({
		url:"controllers/listing-controller.php?type=element&element_elementId="+id,
		handleAs: "json",
		load: handleNewElement,
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
	}
	else showGenericMessage("Must select an element type first!");

}

function onScopeSelect() {
	//console.log(this.value);
	//console.log(this.num);
	var num = this.num;
	var elementType = this.elementType;
	if(this.value == "DATASET" || this.value == "BOTH") {
		if(dijit.byId("datasetElement_deId"+num).attr("value") == "") {
			dijit.byId("dataset"+elementType+"_elementId"+num).attr("disabled", false)
		}
		else {
			dijit.byId("dataset"+elementType+"_deId"+num).attr("disabled", false);
			dijit.byId("dataset"+elementType+"_deId"+num).attr("value", dijit.byId("datasetElement_deId"+num).attr("value"));
		}
		dojo.byId("elementValueLabel"+num).style.display = 'block';
		if(elementType == "DateTime") {
			dijit.byId("dataset"+elementType+"_value"+num+"DATE").domNode.style.display = 'block';
			dijit.byId("dataset"+elementType+"_value"+num+"DATE").attr("disabled", false);
			dijit.byId("dataset"+elementType+"_value"+num+"TIME").domNode.style.display = 'block';
			dijit.byId("dataset"+elementType+"_value"+num+"TIME").attr("disabled", false);
		}	
		else {
			dijit.byId("dataset"+elementType+"_value"+num).domNode.style.display = 'block';
			dijit.byId("dataset"+elementType+"_value"+num).attr("disabled", false);
		}
		if(elementType == "Integer" || elementType == "Real") {
			dojo.byId("elementUnitsLabel"+num).style.display = 'block';
			dijit.byId("dataset"+elementType+"_units"+num).domNode.style.display = 'block';
			dijit.byId("dataset"+elementType+"_units"+num).attr("disabled", false);
		}
	}
	else if (this.value == "GRANULE") {
		dojo.byId("elementValueLabel"+num).style.display = 'none';
		if(elementType == "DateTime") {
			dijit.byId("dataset"+elementType+"_value"+num+"DATE").domNode.style.display = 'none';
			dijit.byId("dataset"+elementType+"_value"+num+"DATE").attr("disabled", true);
			dijit.byId("dataset"+elementType+"_value"+num+"TIME").domNode.style.display = 'none';
			dijit.byId("dataset"+elementType+"_value"+num+"TIME").attr("disabled", true);
		}	
		else {
			dijit.byId("dataset"+elementType+"_value"+num).domNode.style.display = 'none';
			dijit.byId("dataset"+elementType+"_value"+num).attr("disabled", true);
		}
		if(elementType == "Integer" || elementType == "Real") {
			dojo.byId("elementUnitsLabel"+num).style.display = 'none';
			dijit.byId("dataset"+elementType+"_units"+num).domNode.style.display = 'none';
			dijit.byId("dataset"+elementType+"_units"+num).attr("disabled", true);
		}
	}
}

function handleNewElement(responseObject, ioArgs, adminFlag) {
	for(x=0; x<responseObject.items.length; x++) {
		if(responseObject.items[x].field == "element_type")
			var elementType = responseObject.items[x].value;
		if(responseObject.items[x].field == "element_elementId")
			var elementId = responseObject.items[x].value;
		if(responseObject.items[x].field == "element_longName")
			var longName = responseObject.items[x].value;
	}
	
	if(elementType == "character") {
		elementType = "Character";
	}
	else if(elementType == "integer") {
		elementType = "Integer";
	}
	else if(elementType == "real") {
		elementType = "Real";
	}
	else if(elementType == "date" || elementType == "datetime" || elementType == "time") {
		elementType = "DateTime";	
	}
	else if(elementType == "spatial") {
	    elementType = "Spatial";
	}
	
	var num = elementNum;
	elementNum++;
	var elementTable = dojo.byId("elementTable");
	var labels = new Array();
	var inputs = new Array();
	
	var container = dojo.create("table", {id:"elementForm"+num});	

	var entryTitle = dojo.create("div", {innerHTML:"Element "+num+ "    ", style:"font-weight: bold;"});
	var deleteButton = new dijit.form.Button({label:"Remove", iconClass:"mailIconCancel", onClick:function(){elementTable.removeChild(dojo.byId("elementForm"+num));}})
	if(adminFlag == true || adminFlag == null) 
		entryTitle.appendChild(deleteButton.domNode);
	
	var tail = dojo.create("div", {innerHTML:"<br/>"});

	var label = dojo.create("div", {innerHTML:"Element Field"});
	var input = dojo.create("div", {innerHTML:longName});
	var elementIdField = new dijit.form.TextBox({id:"datasetElement_elementId"+num, name:"datasetElement_elementId"+num, type:"hidden", value:elementId});
	var elementDeIdField = new dijit.form.TextBox({id:"datasetElement_deId"+num, name:"datasetElement_deId"+num, disabled:true, type:"hidden"});
	var customDeIdField = new dijit.form.TextBox({id:"dataset"+elementType+"_deId"+num, name:"dataset"+elementType+"_deId"+num, disabled:true, type:"hidden"});
	var customElementIdField = new dijit.form.TextBox({id:"dataset"+elementType+"_elementId"+num, name:"dataset"+elementType+"_elementId"+num, disabled:true, type:"hidden"});
	customElementIdField.attr("value", elementId);
	input.appendChild(elementIdField.domNode);
	input.appendChild(elementDeIdField.domNode);
	input.appendChild(customDeIdField.domNode);
	input.appendChild(customElementIdField.domNode);
	labels.push(label);
	inputs.push(input);

	var label = dojo.create("div", {innerHTML:"Scope: "});
	var scopeData = {identifier:"value",
							label:"value",
							items:[{value:"DATASET"},
								{value:"GRANULE"},
								{value:"BOTH"}
							]};
	if(elementType == "Spatial" || elementType == "spatial") {
	    scopeData.items = [{value:"GRANULE"}];
	}
	var scopeTypeStore = new dojo.data.ItemFileWriteStore({data:scopeData});
	var field = new dijit.form.FilteringSelect({maxLength:"15", id:"datasetElement_scope"+num, name:"datasetElement_scope"+num, required:true, store: scopeTypeStore, searchAttr:"value", labelAttr:"value", num:num, onChange:onScopeSelect, elementType:elementType});
	labels.push(label);
	inputs.push(field.domNode);

	var label = dojo.create("div", {innerHTML:"Obligation Flag: "});
	var obligationData = {identifier:"value",
							label:"value",
							items:[{value:"M"},
								{value:"O"}
							]};
	var obligationTypeStore = new dojo.data.ItemFileWriteStore({data:obligationData});
	var field = new dijit.form.FilteringSelect({maxLength:"15", id:"datasetElement_obligationFlag"+num, name:"datasetElement_obligationFlag"+num, required:true, store: obligationTypeStore, searchAttr:"value", labelAttr:"value"});
	labels.push(label);
	inputs.push(field.domNode);
	
	var label = dojo.create("div", {innerHTML:"Value: ", id:"elementValueLabel"+num, style:"display:none"});
	if(elementType == "Character" || elementType == "Spatial") {
		var field = new dijit.form.ValidationTextBox({maxLength:"255", style:"float:none;", 
					id:"dataset"+elementType+"_value"+num, 
					name:"dataset"+elementType+"_value"+num, required:true,
					disabled:true,	
					style:"display: none"});
	}
	else if(elementType == "Integer" || elementType == "Real") {
		var field = new dijit.form.NumberTextBox({maxLength:"255", style:"float:none;", 
					id:"dataset"+elementType+"_value"+num, 
					name:"dataset"+elementType+"_value"+num, required:true,
					disabled:true,	
					style:"display:none"});

	}
	else if(elementType == "DateTime") {
		var field = dojo.create("div");
		var fieldDate = new dijit.form.DateTextBox({maxLength:"255", style:"float:none;", id:"dataset"+elementType+"_value"+num+"DATE", name:"dataset"+elementType+"_value"+num+"DATE", required:true,
					disabled:true,	
					style:"display:none"});
		var fieldTime = new dijit.form.TimeTextBox({maxLength:"255", style:"float:none;", id:"dataset"+elementType+"_value"+num+"TIME", name:"dataset"+elementType+"_value"+num+"TIME", required:true,
					disabled:true,	
					style:"display:none"});
		field.appendChild(fieldDate.domNode);
		field.appendChild(fieldTime.domNode);
	}
	labels.push(label);
	if(elementType == "DateTime") {
		inputs.push(field);
	}
	else {
		inputs.push(field.domNode);
	}
	
	var label = dojo.create("div", {innerHTML:"Units: ", id:"elementUnitsLabel"+num, style:"display:none"});
	var field = new dijit.form.ValidationTextBox({maxLength:"255", style:"float:none;", 
					id:"dataset"+elementType+"_units"+num, 
					name:"dataset"+elementType+"_units"+num, required:true,
					disabled:true,	
					style:"display: none"});
	labels.push(label);
	inputs.push(field.domNode);

	var newRow;
	var newCell1;
	var newCell2;
	
	container.appendChild(entryTitle);
	
	for (var x = 0;x<labels.length;x++) {
		newRow = dojo.create("tr");
		newCell1 = dojo.create("td");
		newCell1.appendChild(labels[x]);
		newCell2 = dojo.create("td");
		newCell2.appendChild(inputs[x]);
		newRow.appendChild(newCell1);
		newRow.appendChild(newCell2);
		container.appendChild(newRow);
	}
	//container.appendChild(tail);
	
	elementTable.appendChild(container);

}

function toggleUserInfo() {
	if(infoToggle) {
		dojo.query(".user-info").style("display", "block");
		dojo.query("#banner .user-name").style("background", "#fff");
		dojo.query("#banner .user-name").style("color", "#000");
		infoToggle = false;
	}
	else {
		dojo.query(".user-info").style("display", "none");
		dojo.query("#banner .user-name").style("background", "#333");
		dojo.query("#banner .user-name").style("color", "#fff");
		infoToggle = true;	
	}
	return false;
}

function disableInfoPopup(event) {
	if(!infoToggle && event.target.className != "user-name") {
		dojo.query(".user-info").style("display", "none");
		dojo.query("#banner .user-name").style("background", "#333");
		dojo.query("#banner .user-name").style("color", "#fff");
		infoToggle = true;	
	}
}

//Validation functions
var persistentErrorBox =null;
function validatePersistentID(newVal) {
	if(dojo.byId("requestType").value != "update"){    
	    var inputBox = this.domNode;
	    var loadingIcon = dojo.place("<img src='images/loading.gif' style='margin-left:10px'/>", inputBox, "after");
	    dojo.xhrGet({
			url:"controllers/listing-controller.php?type=ajax_persistent",
			handleAs: "json",
			load: function(responseObject, ioArgs) {
				dojo.destroy(loadingIcon);
			    var match = false;
				for(x=0; x<responseObject.items.length; x++) {
	                if(responseObject.items[x] == newVal)
	                    match = true;
				}
				if(match) {
	                if(persistentErrorBox == null)
	                    persistentErrorBox = dojo.place("<span class='error-message dijitTooltipContainer'>Persistent ID is already used!</span>", inputBox, "after");
	                else persistentErrorBox.style.display= "inline";
				}
				else if(persistentErrorBox != null){
				    persistentErrorBox.style.display = "none";
				}
			},
			error: function(responseObject, ioArgs) {
				showGenericMessage("A server error has occured");
				return responseObject;
			}
		});
	}
}

var shortNameErrorBox = null;
function validateShortName(newVal) {
    if(dojo.byId("requestType").value != "update"){    
	    var inputBox = this.domNode;
	    var loadingIcon = dojo.place("<img src='images/loading.gif' style='margin-left:10px'/>", inputBox, "after");
	    dojo.xhrGet({
			url:"controllers/listing-controller.php?type=ajax_shortName",
			handleAs: "json",
			load: function(responseObject, ioArgs) {
				dojo.destroy(loadingIcon);
			    var match = false;
				for(x=0; x<responseObject.items.length; x++) {
	                if(responseObject.items[x] == newVal)
	                    match = true;
				}
				if(match) {
	                if(shortNameErrorBox == null)
	                    shortNameErrorBox = dojo.place("<span class='error-message dijitTooltipContainer'>Short name is already used!</span>", inputBox, "after");
	                else shortNameErrorBox.style.display= "inline";
				}
				else if(shortNameErrorBox != null){
				    shortNameErrorBox.style.display = "none";
				}
			},
			error: function(responseObject, ioArgs) {
				showGenericMessage("A server error has occured");
				return responseObject;
			}
		});
	} 
}

function processStatus() {
	var manager_div = dojo.byId("manager-status");
	var sigevent_div = dojo.byId("sigevent-status");
	var shortName = dijit.byId("dataset_shortName").attr("value");
	dojo.xhrGet({
		url:"controllers/creation-controller.php?type=check&dataset="+shortName,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.manager_status) {
				manager_div.innerHTML = "<span style='color:green;'>Created</span>";
			}
			else {
				var manager_button = new dijit.form.Button({label: "Create Manager Entry", onClick: managerCreate}, manager_div);
			}
			if(responseObject.sigevent_status) {
				sigevent_div.innerHTML = "<span style='color:green;'>Created</span>";
			}
			else {
				var sigevent_button = new dijit.form.Button({label: "Create Sigevent Entry", onClick: sigeventCreate}, sigevent_div, "after");
			}
				
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});
}

function managerCreate(){
	var manager_div = dojo.byId("manager-status");
	var shortName = dijit.byId("dataset_shortName").attr("value");
	dojo.xhrGet({
		url:"controllers/creation-controller.php?type=create&source=manager&dataset="+shortName,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				while (manager_div.hasChildNodes()) {
    				manager_div.removeChild(manager_div.lastChild);
				}
				manager_div.innerHTML = "<span style='color:green;'>Created</span>";
			}
			else {
				showGenericMessage("A server error has occured. Name already exists or server not responding!");			
			}		
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});	
}

function sigeventCreate(){
	var sigevent_div = dojo.byId("sigevent-status");
	var shortName = dijit.byId("dataset_shortName").attr("value");
	dojo.xhrGet({
		url:"controllers/creation-controller.php?type=create&source=sigevent&dataset="+shortName,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			if(responseObject.response == "OK" || responseObject.response == "201") {
				while (sigevent_div.hasChildNodes()) {
    				sigevent_div.removeChild(sigevent_div.lastChild);
				}
				sigevent_div.innerHTML = "<span style='color:green;'>Created</span>";
			}
			else {
				showGenericMessage("A server error has occured. Name already exists or server not responding!");			
			}		
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});	
}
