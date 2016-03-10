/*
operator.js

Javascript functions for Manager Operator Tool
Dependencies: jquery, jquery-ui, jqgrid

*/

function displayDialog(inputMessage) {
	$("#operator-dialog").html(inputMessage);
	$("#operator-dialog").dialog('open');
}

function formatNote(input, options, rowObject) {
	return "<button row='"+options.rowId+"' note='"+input+"'class='operator-noteButton' onclick=\"noteClick($(this));\">View</button>";
}

function formatDate(input, options, rowObject) {
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
  
  if(isNaN(d.getDate()))
  	dateString = "";
  
  return dateString;
	//return dateObject.format("Y-mm-dd HH-mm-ss");
	//return "<button row='"+options.rowId+"' note='"+input+"'class='operator-noteButton' onclick=\"noteClick($(this));\">View</button>";
}

function noteClick(param) {
	var row = param.attr("row");
	var note = param.attr("note")
	note = note.replace(/\n/g, "<br/>");
	displayDialog(note);
}

function editClick(param) {

  var index = $("#operator-granuleTabs").tabs('option', 'selected');
	var ids = $("#operator-granuleGrid"+index).jqGrid('getGridParam','selarrrow');
	if(ids.length <= 0) {
		displayDialog("Please select at least one granule");
	}
	else {
		var currentState = $("#operator-granuleGrid"+index).jqGrid('getCell', ids[0], "currentState");
		var currentLock = $("#operator-granuleGrid"+index).jqGrid('getCell', ids[0], "currentLock");
		var currentRetries = $("#operator-granuleGrid"+index).jqGrid('getCell', ids[0], "currentRetries");
		$("#operator-editGranuleStateSelect").val(currentState);
		$("#operator-editGranuleStateSelect").change();
		$("#operator-editGranuleLockSelect").val(currentLock);
		$("#operator-editGranuleRetries").val(currentRetries);
		$("#operator-editGranuleInfo").text("Editing granules: "+ids);
		$("#operator-editGranuleDialog").dialog("open");
	}
}

function editProductClick(param) {
	var index = $("#operator-granuleTabs").tabs('option', 'selected');
	var ids = $("#operator-productGrid"+index).jqGrid('getGridParam','selarrrow');
	if(ids.length <= 0) {
		displayDialog("Please select at least one product");
	}
	else {
		var currentLatency = $("#operator-productGrid"+index).jqGrid('getCell', ids[0], "deliveryRate");
		var currentPriority = $("#operator-productGrid"+index).jqGrid('getCell', ids[0], "priority");
		$("#operator-editProductLatency").val(currentLatency);
		$("#operator-editProductPriority").val(currentPriority);
		$("#operator-editProductInfo").text("Editing products: "+ids);
		$("#operator-editProductDialog").dialog("open");
	}
}

function refreshStatus() {
	$(".operator-status-container").empty();
	$(".operator-status-container").append("<img src='images/spinner.gif'/>");
	$.ajax({
		url: "controllers/statusController.php?oper=summary",
		type: "GET",
		success: function(responseObject) {
			if(responseObject.length > 0) {
				$(".operator-status-container").empty();
				$.each(responseObject, function(index, item){
					var type = item.type;
					var status = item.status;
					var url = item.url;
					var label = (type == "manager") ? item.type.toUpperCase()+" ("+item.name+")" : item.type.toUpperCase();
					var imageURL = (status == "online") ? "images/greenlight.png" : "images/redlight.png";
					var newStatusContainer = $("<div class='operator-status-item'>\
													<span class='operator-status-image'><img src='"+imageURL+"'/></span>\
													<div class='operator-status-text'>\
														<span style='font-weight:bold; font-size: 12px;'>"+label+"</span><br/>\
														"+url+"<br/>\
													</div>\
												</div>");
					$(".operator-status-container").append(newStatusContainer);
				});
				var refresh = $("<button class='operator-refresh-status'>Refresh Status</button>");
				refresh.click(function() {
					refreshStatus();
				});
				$(".operator-status-container").append(refresh);
			}
			else {
				$(".operator-status-container").text("Error occurred when querying status.");
			}						
		},
		error: function(responseObject) {displayDialog("A server error has occured.");}
	});
}

function refreshSummary(index) {
    if(index == null)
        index=0;
	$.ajax({
		url: "controllers/summaryListing.php?manager_id="+index,
		type: "GET",
		success: function(responseObject) {
			if(responseObject.rows.length > 0) {
				var table = $("<table class='operator-summaryTable' cellspacing='0' cellpadding='0' border='0'><tr><th class='ui-th-column ui-state-default'>State</th><th class='ui-th-column ui-state-default'>Lock</th><th class='ui-th-column ui-state-default'>Count</th></tr></table>");
				for(var x=0; x<responseObject.rows.length;x++) {
					var state=responseObject.rows[x].currentState;
					var lock=responseObject.rows[x].currentLock;
					var count=responseObject.rows[x].count;
					var row = $("<tr><td><a href='javascript:void(0)' onclick=\"setStateCombo('"+state+"', '"+lock+"', '"+index+"');\">"+state+"</a></td><td><a href='javascript:void(0)' onclick=\"setStateCombo('"+state+"', '"+lock+"', '"+index+"');\">"+lock+"</a></td><td style='text-align:right;'>"+count+"</td></tr>");
					table.append(row);
				}
				$("#operator-summaryInfoArea"+index).html(table);
			}
			else {
				$("#operator-summaryInfoArea"+index).text("No Granules Found");
			}						
		},
		error: function(responseObject) {displayDialog("A server error has occured.");}
	});
}

function secondsToTime(secs)
{
    var hours = Math.floor(secs / (60 * 60));
   
    var divisor_for_minutes = secs % (60 * 60);
    var minutes = Math.floor(divisor_for_minutes / 60);
 
    var divisor_for_seconds = divisor_for_minutes % 60;
    var seconds = Math.ceil(divisor_for_seconds);
   
    var obj = {
        "h": hours,
        "m": minutes,
        "s": seconds
    };
    return obj;
}

function refreshPriority(index){
	if(index == null)
		index=0;
	var container = $("#operator-priority-tab-"+index);
	container.empty();
	container.append("<img src='images/spinner.gif'/>");
	$.ajax({
		url: "controllers/priorityListing.php",
		data: {manager_id: index},
		success: function(responseObject) {
			container.empty();
			//code for granule count by date
			var granuleCountContainer = $("<div class='operator-priority-granuleCount-container operator-priority-container'>\
				<h2>Granule Count By Priority (Since 12AM Today)</h2>\
				<div class='operator-priority-granuleCount'></div>\
			</div>");
			if(responseObject.granuleCount && responseObject.granuleCount.rows.length > 0) {
				granuleCountContainer.find(".operator-priority-granuleCount").append("<table cellspacing='0' cellpadding='0' border='0'><tr><th class='ui-th-column ui-state-default'>Priority</th><th class='ui-th-column ui-state-default'>Count</th></tr></table>");
				$.each(responseObject.granuleCount.rows, function(key, value){
					granuleCountContainer.find("table").append("<tr><td>"+value.priority+"</td><td style='text-align:right;'>"+value.count+"</td></tr>");
				});
			}
			else granuleCountContainer.find(".operator-priority-granuleCount").append("No granules archived today.");
			
			//code for average arhive time by priority
			var averageContainer = $("<div class='operator-priority-average-container operator-priority-container'>\
				<h2>Average Time to Archive by Priority (All Time)</h2>\
				<div class='operator-priority-average'></div>\
			</div>");
			if(responseObject.averageTime!="null") {
				averageContainer.find(".operator-priority-average").append("<table cellspacing='0' cellpadding='0' border='0'><tr><th class='ui-th-column ui-state-default'>Priority</th><th class='ui-th-column ui-state-default'>Time (h:m:s)</th></tr></table>");
				$.each(responseObject.averageTime, function(key, value) {
					var timeObj = secondsToTime(value);
					var timeString = timeObj.h+":"+timeObj.m+":"+timeObj.s;
					averageContainer.find("table").append("<tr><td>"+key+"</td><td style='text-align:right;'>"+timeString+"</td></tr>");
				});
			}
			else averageContainer.find(".operator-priority-average").append("No granules found.");

			
			//Code for storage job count
			var storageCountContainer = $("<div class='operator-priority-storageCount-container operator-priority-container'>\
				<h2>Current Job Count by Storage</h2>\
				<div class='operator-priority-storageCount'></div>\
			</div>");
			if(responseObject.jobCount && responseObject.jobCount.rows.length > 0) {
				$.each(responseObject.jobCount.rows, function(key, storage){
					var storageContainer = $("<div class='operator-priority-storage-container'><h3>"+storage.name+"</h3><table cellspacing='0' cellpadding='0' border='0'><tr><th class='ui-th-column ui-state-default'>Priority</th><th class='ui-th-column ui-state-default'>Count</th></tr></table></div>");
					$.each(storage.counts, function(key, priority) {
						storageContainer.find("table").append("<tr><td>"+priority.priority+"</td><td style='text-align:right;'>"+priority.count+"</td></tr>");
					});
					
					storageCountContainer.find(".operator-priority-storageCount").append(storageContainer);
				});
			}
			else storageCountContainer.find(".operator-priority-storageCount").append("No current jobs found.");
			
			var priorityRefreshButton = $("<button style='clear:both; display:block;'>Refresh</button>");
			priorityRefreshButton.click(function(){
				refreshPriority(index);
			});
			container.append(granuleCountContainer, averageContainer, storageCountContainer, priorityRefreshButton);
		},
		error: function(responseObject) {displayDialog("A server error has occured.");}
	});
	
}


function refreshEngine(){
	$.ajax({
		url: "controllers/engineListing.php",
		type: "GET",
		success: function(responseObject) {
				for(var x=0; x<responseObject.rows.length;x++) {
					var id = responseObject.rows[x].cell[0];
					var name = responseObject.rows[x].cell[1];
					var active = responseObject.rows[x].cell[2];
					var isOnline = responseObject.rows[x].cell[3];
					var hostname = responseObject.rows[x].cell[4];
					var stereotype = responseObject.rows[x].cell[5];
					var startedAtLong = responseObject.rows[x].cell[6];
					startedAt = new Date(startedAtLong);
					var stoppedAt = responseObject.rows[x].cell[7];
					var storageId = responseObject.rows[x].cell[8];
					var note = responseObject.rows[x].cell[9];
					var imageURL = "";
					var status = ""
					if(active) {
						imageURL = "images/greenlight.png";
						status = "Online";
					}
					/*else if(!isOnline && active) {
						imageURL = "images/yellowlight.png";
						status = "Deactivated";
					}*/
					else {
						imageURL = "images/redlight.png";
						status = "Offline";
					}
					var newEngineContainer = $("<div class='operator-engine-container'>\
											<img class='operator-engineIcon' src='"+imageURL+"'/>\
											<div class='operator-engineSummaryInfo'>\
												<span style='font-weight:bold; display:block;'>"+name+"<br/></span>Hostname: "+hostname+"\
												<div class='operator-engine-status-container'>\
													<span>Status:</span>\
													<span class='operator-engine-status operator-engine-online'>"+status+"</span>\
												</div>\
												<span>Started At: "+startedAt.toDateString()+" "+startedAt.getHours()+":"+startedAt.getMinutes()+":"+startedAt.getSeconds()+"\
											</div>\
										   </div>");
					if(stereotype == "INGEST")
						$(".operator-engineCategory-ingest").append(newEngineContainer);
					else if(stereotype == "ARCHIVE")
						$(".operator-engineCategory-archive").append(newEngineContainer);
					else if(stereotype == "PURGE")
						$(".operator-engineCategory-purge").append(newEngineContainer);
				}
			},
		error: function(responseObject) {
				displayDialog("A server error has occured");
			}
		
	});
}

function refreshStorage() {
	$("#operator-storageManagement").empty();
	$("#operator-storageManagement").append("<img src='images/spinner.gif'/>");
	$.ajax({
		url: "controllers/storageListing.php",
		type: "GET",
		success: function(responseObject) {
				$("#operator-storageManagement").empty();
				var refreshButton = $("<button class='operator-refresh-storage' style='clear:both; display:block; margin-bottom: 15px; margin-left: 800px;'>Refresh</button>");
				$("#operator-storageManagement").append(refreshButton);
				refreshButton.click(refreshStorage);
				for(var x=0; x<responseObject.rows.length;x++) {
					var id = responseObject.rows[x].cell[0];
					var path = responseObject.rows[x].cell[1];
					var protocol = responseObject.rows[x].cell[2];
					var used = responseObject.rows[x].cell[3];
					var reserved = responseObject.rows[x].cell[4];
					var threshold = responseObject.rows[x].cell[5];
					var active = responseObject.rows[x].cell[6];
					var storageArray = responseObject.rows[x].cell[7];
					var percent = Math.floor(parseInt(used)/parseInt(reserved) * 100);
					var freeDisplay = Math.floor((parseInt(reserved) - parseInt(used)) / 1073741824);
					var totalDisplay =  Math.floor(parseInt(reserved) / 1073741824);
					
					for(var y=0; y<storageArray.length;y++) {
						var storageId = storageArray[y].id;
						var name = storageArray[y].name;
						if(!active || active == "false")
							name = name+"<span style='color:red;'> (Inactive)</span>";
						var priority = storageArray[y].priority;
						
						var summaryContainer = $("<div class='operator-storageContainer'><div class='operator-roundAll operator-storageSummaryContainer'>"+
									"<div class='operator-storageSummaryInfo'>"+
									"	<div class='operator-storageName'><span style='font-weight:bold; font-size:100%;padding: 0 5px 0 0;'>"+name+" </span>("+percent+"%)</div>"+
									"	<div class='operator-storageBar'></div>"+
									"	<div class='operator-storageFree'>"+freeDisplay+" GB free of "+totalDisplay+" GB</div>"+
									"</div>"+
								"</div>");
					
						$(".operator-storage-summary-container").append(summaryContainer)
						$(summaryContainer).find(".operator-storageBar").progressbar({value:percent});
						$(summaryContainer).click(function(){
							$("#operator-tabs").tabs("select", 2);
						});
						
						var newStorageContainer = $("<div class='operator-section operator-section-half operator-storage-container'>\
								<h1>Storage - <span class='operator-storage-label'>"+name+"</span> <button class='clean-gray operator-storage-detail-button'>View/Edit Details</button><div style='clear:both;'></div></h1>\
								<div class='operator-content'>\
									<div class='operator-storage-progress'>\
										<h2>Summary</h2>\
										<div class='operator-sub-content'>\
											<div class='operator-storage-bar'></div>\
											<div class='operator-storage-percent'>"+freeDisplay+" GB free of "+totalDisplay+" GB ("+percent+"% used)</div>\
										</div>\
									</div>\
									<div class='operator-storage-engine-container'>\
										<h2>Engines</h2>\
									</div>\
								</div>\
							</div>\
						");
						$("#operator-storageManagement").append(newStorageContainer);
						$(newStorageContainer).find(".operator-storage-bar").progressbar({value:percent});
						
						var detailsContainer = "<div class='operator-storageDetails operator-roundAll'>"+
										"<span style='margin:0 5px; font-size:1.1em;'>Storage Details</span><div class='ui-icon ui-icon-circle-close operator-storageDetailsCloseButton'></div>"+
										"<table style='clear:both;'>"+
										"	<tr><td><span style='font-weight:bold;'>Path: </td><td><span class='operator-storageConstant' label='path'>"+path+"</span></td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Storage ID: </td><td><span class='operator-storageConstant' label='storageId'>"+storageId+"</span></td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Location ID: </td><td><span class='operator-storageConstant' label='id'>"+id+"</span></td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Protocol: </td><td>"+protocol+"</td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Active: </td><td><span class='operator-storageValue' label='active'>"+active+"</span><select class='operator-storageInput' label='active' value='"+active+"'><option>true</option><option>false</option></select></td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Priority: </td><td><span class='operator-storageValue' label='priority'>"+priority+"</span><select class='operator-storageInput' label='priority' value='"+priority+"'><option>HIGH</option><option>NORMAL</option><option>LOW</option></select></td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Space Used: </td><td><span class='operator-storageValue' label='used'>"+used+"</span><input type='text' class='operator-storageInput' label='used' value='"+used+"'></input></td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Space Reserved: </td><td><span class='operator-storageValue' label='reserved'>"+reserved+"</span><input type='text' class='operator-storageInput' label='reserved' value='"+reserved+"'></input></td></tr>"+
										"	<tr><td><span style='font-weight:bold;'>Space Threshold: </td><td><span class='operator-storageValue' label='threshold'>"+threshold+"</span><input type='text' class='operator-storageInput' label='threshold' value='"+threshold+"'></input></td></tr>"+
										"</table>"+
										"<button class='operator-editStorageButton' style='font-size:75%'>Edit</button><button class='operator-confirmEditStorageButton' style='font-size:75%; display:none;'>Confirm</button> <button class='operator-cancelEditStorageButton' style='font-size:75%; display:none;'>Cancel</button>"+
									"</div>";
						$(newStorageContainer).append(detailsContainer);
						$(newStorageContainer).data("name", name);
						
						if(active) {
						// Engine code
						$.ajax({
							url:"controllers/zkController.php",
							data:{oper:"storage_summary", storage:name},
							type:"GET",
							storageContainer: newStorageContainer,
							success:function(responseObject){
								var currentContainer = this.storageContainer;
								$.each(responseObject.engines, function(index, engineObject){
									if(engineObject.timeStarted) 
										var lastUpdatedString = formatDate(parseInt(engineObject.timeStarted));//lastUpdated.toDateString()+" "+lastUpdated.getHours()+":"+lastUpdated.getMinutes()+":"+lastUpdated.getSeconds()
									else var lastUpdatedString = "N/A";
									var imageURL = "";
		
									if(engineObject.status == "online") {
										imageURL = "images/greenlight.png";
										status = "Online";
									}
									else {
										imageURL = "images/redlight.png";
										status = "Offline";
									}
	
									//startedAt.toDateString()+" "+startedAt.getHours()+":"+startedAt.getMinutes()+":"+startedAt.getSeconds()
									var newEngineContainer = $("<div class='operator-engine-container'>\
																	<div class='operator-engine-button-container'>\
																		<button class='operator-engine-pause clean-gray'>\
																			<span class='operator-button-icon ui-icon ui-icon-pause' alt='Pause Engine'></span>\
																			<span class='operator-button-text'>Pause</span>\
																		</button>\
																		<button class='operator-engine-resume clean-gray'>\
																			<span class='operator-button-icon ui-icon ui-icon-play'></span>\
																			<span class='operator-button-text'>Resume</span>\
																		</button>\
																		<button class='operator-engine-stop clean-gray'>\
																			<span class='operator-button-icon ui-icon ui-icon-closethick'></span>\
																			<span class='operator-button-text'>Stop</span>\
																		</button>\
																	</div>\
																	<img src='"+imageURL+"' class='operator-engineIcon'/>\
																	<div class='operator-engineSummaryInfo'>\
																		<span style='font-weight:bold; display:block;'>"+engineObject.name+"<br></span>\
																		<div class='operator-engine-status-container'>\
																			<span>Status: </span><span class='operator-engine-status operator-engine-online'>"+engineObject.status+"</span>\
																		</div>\
																		<span>Last Updated: "+lastUpdatedString+"</span>\
																	</div>\
																</div>");
									$(currentContainer).find(".operator-storage-engine-container").append(newEngineContainer);
									if(engineObject.status == "online") {
										newEngineContainer.find(".operator-engine-resume").hide();
									}
									else newEngineContainer.find(".operator-engine-pause").hide();
									newEngineContainer.data("status", {engine:engineObject.name, storage:$(currentContainer).data("name")});
									
										
									$(".operator-engine-pause", newEngineContainer).click(function() {
										var storage = newEngineContainer.data("status").storage;
										var engine = newEngineContainer.data("status").engine;
										var input = confirm("Are you certain you would like to pause this engine?");
										if(input) {
											$.ajax({
												url: "controllers/zkController.php",
												data: {oper:"pause", storage:storage, engine:engine},
												type: "GET",
												success:function(responseObject) {
													if(responseObject.status == "OK") {
														refreshStorage();
													}
													else {
														displayDialog(responseObject.content);
													}
												},
												error: function(){
													
												}
											});
										}	
									});
									$(".operator-engine-resume", newEngineContainer).click(function() {
										var storage = newEngineContainer.data("status").storage;
										var engine = newEngineContainer.data("status").engine;
										var input = confirm("Are you certain you would like to resume this engine?");
										if(input) {
											$.ajax({
												url: "controllers/zkController.php",
												data: {oper:"resume", storage:storage, engine:engine},
												type: "GET",
												success:function(responseObject) {
													if(responseObject.status == "OK") {
														refreshStorage();
													}
													else {
														displayDialog(responseObject.content);
													}
												},
												error: function(){
													
												}
											});
										}
									});
									$(".operator-engine-stop", newEngineContainer).click(function() {
										var storage = newEngineContainer.data("status").storage;
										var engine = newEngineContainer.data("status").engine;
										var input = confirm("Are you certain you would like to shut down (kill) this engine?");
										if(input) {
											$.ajax({
												url: "controllers/zkController.php",
												data: {oper:"shutdown", storage:storage, engine:engine},
												type: "GET",
												success:function(responseObject) {
													if(responseObject.status == "OK") {
														refreshStorage();
													}
													else {
														displayDialog(responseObject.content);
													}
												},
												error: function(){
													
												}
											});
										}
									});
								});
	
							},
							error:function(){
								displayDialog("A server error has occurred. Could not connect to ZooKeeper")
							}
						});
						}
					}
				}
				$("#operator-storageManagement").append("<div style='clear:both;'>");
				$(".operator-storage-summary-container").append("<div style='clear:both;'>");
	
				$(".operator-storage-detail-button").click(function() {
					$(this).parents(".operator-section").find(".operator-storageDetails:hidden").show("drop", "fast");
					//$(this).parents(".operator-section").find(".operator-storageDetails:visible").hide("drop", "fast");
				});
				$(".operator-storageDetailsCloseButton").click(function(){
					$(this).parent(":visible").hide("drop", "fast");
					$(this).siblings("table").find(".operator-storageValue").show();
					$(this).siblings("table").find(".operator-storageInput").hide();
					$(this).parent().find("button").hide();
					$(this).siblings("button.operator-editStorageButton").show();
				});				
				$(".operator-editStorageButton").click(function(){
					// set input vals
					var used = $(this).siblings("table").find(".operator-storageValue[label=used]").text();
					var reserved = $(this).siblings("table").find(".operator-storageValue[label=reserved]").text();
					var threshold = $(this).siblings("table").find(".operator-storageValue[label=threshold]").text();
					var priority = $(this).siblings("table").find(".operator-storageValue[label=priority]").text();
					var active = $(this).siblings("table").find(".operator-storageValue[label=active]").text();
					$(this).siblings("table").find(".operator-storageInput[label=used]").attr("value", used);
					$(this).siblings("table").find(".operator-storageInput[label=reserved]").attr("value", reserved);
					$(this).siblings("table").find(".operator-storageInput[label=threshold]").attr("value", threshold);
					$(this).siblings("table").find(".operator-storageInput[label=priority]").attr("value", priority);
					$(this).siblings("table").find(".operator-storageInput[label=active]").attr("value", active);
	
					$(this).siblings("table").find(".operator-storageValue").hide();
					$(this).siblings("table").find(".operator-storageInput").show();
					$(this).siblings("button").show();
					$(this).hide();
				});

				$(".operator-confirmEditStorageButton").click(function(){
					var currentContainer = this;
					var id = $(this).siblings("table").find(".operator-storageConstant[label=id]").text();
					var storageId = $(this).siblings("table").find(".operator-storageConstant[label=storageId]").text();
					var used = $(this).siblings("table").find(".operator-storageInput[label=used]").attr("value");
					var reserved = $(this).siblings("table").find(".operator-storageInput[label=reserved]").attr("value");
					var threshold = $(this).siblings("table").find(".operator-storageInput[label=threshold]").attr("value");
					var priority = $(this).siblings("table").find(".operator-storageInput[label=priority]").attr("value");
					var active = $(this).siblings("table").find(".operator-storageInput[label=active]").attr("value");
					if(!isNaN(used) && !isNaN(reserved) && !isNaN(threshold) && (Number(threshold) < Number(reserved))) {
						$.ajax({
							url: "controllers/editStorageController.php",
							data: {oper:"edit", id:id, used:used, reserved:reserved, threshold:threshold, priority:priority, storageId: storageId, active:active},
							type: "GET",
							success: function(responseObject) {
								if(responseObject.response == "OK") {
									var percent = Math.floor(parseInt(used)/parseInt(reserved) * 100);
									var freeDisplay = Math.floor((parseInt(reserved) - parseInt(used)) / 1073741824);
									var totalDisplay =  Math.floor(parseInt(reserved) / 1073741824);
									$(currentContainer).siblings("table").find(".operator-storageValue[label=used]").text(used)
									$(currentContainer).siblings("table").find(".operator-storageValue[label=reserved]").text(reserved);
									$(currentContainer).siblings("table").find(".operator-storageValue[label=threshold]").text(threshold);
									$(currentContainer).siblings("table").find(".operator-storageValue").show();
									$(currentContainer).siblings("table").find(".operator-storageInput").hide();
									$(currentContainer).parent().find("button").hide();
									$(currentContainer).siblings("button.operator-editStorageButton").show();
									$(currentContainer).parent().siblings(".operator-storageSummaryContainer").find(".operator-storageBar").progressbar( "value" , percent);
									$(currentContainer).parent().siblings(".operator-storageSummaryContainer").find(".operator-storageFree").text(freeDisplay+" GB free of "+totalDisplay+" GB");
									refreshStorage();
								}
								else {
									displayDialog(responseObject.content);
								}						
							},
							error: function(responseObject) {displayDialog("A server error has occured.");}
						});
					}
					else { displayDialog("Please enter only valid values (size in bytes) with threshold not exceeding reserved."); }
				});
				$(".operator-cancelEditStorageButton").click(function(){
					// swap inputs
					$(this).siblings("table").find(".operator-storageValue").show();
					$(this).siblings("table").find(".operator-storageInput").hide();
					$(this).parent().find("button").hide();
					$(this).siblings("button.operator-editStorageButton").show();
				});
				return 1;
			},
		error: function(responseObject) {
				displayDialog("A server error has occured");
			}
	});
	
}

function setStateCombo(state, lock, index) {
	$("#managerTab"+index+" #gs_currentState").attr('value', state); 
	$("#managerTab"+index+" #gs_currentLock").attr('value', lock); 
	$("#managerTab"+index+" #gs_currentLock").trigger('keydown');
	
	$("#operator-tabs").tabs("select", 1);
	$("#operator-granuleTabs").tabs("select", index);
	
	//$('#operator-granuleGrid').trigger('reloadGrid');
}

$(function() {

	$("#operator-tabs").tabs({ fx: { opacity: 'toggle', duration: "fast" } });
	$("#operator-granuleTabs").tabs();
	$("#operator-dialog").dialog({ autoOpen: false , modal: true});
	
	// Resize enabling
	//$(".operator-sidebar-section .operator-content").resizable({handles:'s', minHeight:100});
	
	$.each(managers, function(index, manager) {
	    $("#operator-granuleTabs").tabs("add", "#managerTab"+index, manager.name);
	    var newTab = $("#managerTab"+index);
	    var granuleStats = $("<div class='operator-section' style='margin-top:10px;'>\
					<h1>Granule Status (Times in GMT)</h1>\
					<div class='operator-granuleList'>\
						<table class='operator-granuleGrid' id='operator-granuleGrid"+index+"'></table>\
						<div class='operator-granulePager' id='operator-granulePager"+index+"'></div>\
					</div>\
				</div>");
		var productManagement = $("<div class='operator-section'>\
					<h1>Product Management</h1>\
					<div class='operator-productList'>\
						<table class='operator-productGrid' id='operator-productGrid"+index+"'></table>\
						<div class='operator-productPager' id='operator-productPager"+index+"'></div>\
					</div>\
				</div>");
		newTab.append(granuleStats);
	    newTab.append(productManagement);
	    
    	newTab.find(".operator-granuleGrid").jqGrid({
    		url: "controllers/granuleListing.php?manager_id="+index,
    		editurl: "controllers/editGranuleController.php?manager_id="+index,
    		datatype: "json", 
    		colNames:['ID','Name', 'Current State','Current Lock', 'Retries','Created','Last Updated','Archived At','Contributor','Notes', 'Priority'],
    		colModel :[ 
    		      {name:'id', index:'id', width:35}, 
    		      {name:'name', index:'name'}, 
    		      {name:'currentState', index:'currentState', width:85}, 
    		      {name:'currentLock', index:'currentLock', width:60, align:'left'}, 
    		      {name:'currentRetries', index:'currentRetries', width:40, align:'right'}, 
    		      {name:'created', index:'created', width:92, align:'right', search:false, formatter:formatDate},//"date", formatoptions: {srcformat:"u", newformat:"Y-m-d H:i:s"}}, 
    		      {name:'updated', index:'updated', width:92, align:'right', search:false, formatter:formatDate},//"date", formatoptions: {srcformat:"u", newformat:"Y-m-d H:i:s"}}, 
    		      {name:'archivedAt', index:'archivedAt', width:92, align:'right', search:false, formatter:formatDate},//"date", formatoptions: {srcformat:"u", newformat:"Y-m-d H:i:s"}}, 
    		      {name:'contributor', index:'contributor', width:55, align:'right'}, 
    		      {name:'note', index:'note', width:45, sortable:false, align:'center', formatter:formatNote},
    		      {name:'priority', index:'priority', width:60, align:'left'}, 
    		    ],
    		 pager: '#operator-granulePager'+index,
    		 rowNum:20,
    		 rowList:[20,50,100],
    		 sortname: 'id',
    		 sortorder: 'desc',
    		 multiselect: true, 
    		 viewrecords: true,
    		 width: 900,
    		 shrinkToFit: true,
    		 height: 290
      	});
    	newTab.find(".operator-granuleGrid").jqGrid('navGrid','#operator-granulePager'+index,{edit:false,search:false,add:false,del:true,deltext:"Delete",refreshtext:"Refresh",searchtext:"Search"}); 
    	newTab.find(".operator-granuleGrid").jqGrid('filterToolbar',{searchOnEnter:false});
    	newTab.find(".operator-granuleGrid").jqGrid('navButtonAdd',"#operator-granulePager"+index,{
    		 caption:"Edit", buttonicon:"ui-icon-newwin", onClickButton:editClick, position: "first", title:"Edit Selected Rows", cursor: "pointer"});	    
	    
        newTab.find(".operator-productGrid").jqGrid({
    		url: "controllers/datasetListing.php?manager_id="+index,
    		editurl: "controllers/editDatasetController.php?manager_id="+index,
    		mtype: "GET",
    		datatype: "json", 
    		colNames:['ID','Name', 'Federation Name','Locked', 'Locked At','Locked By','Ingest Only','Relative Path','Purge Rate','Updated', 'Updated By', 'Event Category', 'Note', 'Delivery Rate', 'Priority'],
    		colModel :[ 
    		      {name:'id', index:'id', width:15}, 
    		      {name:'name', index:'name', autoWidth: true}, 
    		      {name:'federationName', index:'federationName', hidden:true}, 
    		      {name:'locked', index:'locked', hidden:true}, 
    		      {name:'lockedAt', index:'lockedAt', hidden:true}, 
    		      {name:'lockedBy', index:'lockedBy', hidden:true}, 
    		      {name:'ingestOnly', index:'ingestOnly', hidden:true}, 
    		      {name:'relativePath', index:'relativePath', hidden:true}, 
    		      {name:'purgeRate', index:'purgeRate', width:15,align:'right', search:false}, 
    		      {name:'updated', index:'updated', hidden:true}, 
    		      {name:'updatedBy', index:'updatedBy', hidden:true}, 
    		      {name:'eventCategory', index:'eventCategory', search:false, hidden:true}, 
    		      {name:'note', index:'note', search:false, hidden:true}, 
    		      {name:'deliveryRate', index:'deliveryRate', align:'right',search:false, width:15},
    		      {name:'priority', index:'priority', align:'left',search:true, width:15}
    		    ],
    		 pager: '#operator-productPager'+index,
    		 rowNum:20,
    		 rowList:[20,50, 100],
    		 sortname: 'id',
    		 sortorder: 'asc',
    		 multiselect: true, 
    		 viewrecords: true,
    		 autowidth: false, 
    		 //scroll:true,
    		 width: 900,
    		 height: 290
      	}); 
    	newTab.find(".operator-productGrid").jqGrid('navGrid','#operator-productPager'+index,{edit:false,search:false,add:false,del:false,deltext:"Delete",refreshtext:"Refresh",searchtext:"Search"}); 
    	newTab.find(".operator-productGrid").jqGrid('filterToolbar',{searchOnEnter:false});
    	newTab.find(".operator-productGrid").jqGrid('navButtonAdd',"#operator-productPager"+index,{
    		 caption:"Edit", buttonicon:"ui-icon-newwin", onClickButton:editProductClick, position: "first", title:"Edit Selected Rows", cursor: "pointer"});
    	//Create summary listings
    	
    	$(".operator-granuleSummary-Tabs").tabs();
    	$(".operator-granuleSummary-Tabs").tabs("add", "#managerSummaryTab"+index, manager.name);
    	var newSummaryTab = $("#managerSummaryTab"+index);
    	newSummaryTab.addClass("operator-summaryTab");
    	var newSummary = $("<div>\
						<div class='operator-summaryInfoArea' id='operator-summaryInfoArea"+index+"'></div>\
						<button class='operator-refreshSummary' id='operator-refreshSummary"+index+"'>Refresh Summary</button>\
					</div>");
			newSummaryTab.append(newSummary);
			refreshSummary(index);
			
			//Create priority views
			$(".operator-priority-tabs").tabs();
			$(".operator-priority-tabs").tabs("add", "#operator-priority-tab-"+index, manager.name);
			var newPriorityTab = $("#operator-priority-tab-"+index);
			newPriorityTab.addClass("operator-priority-tab");
			
			refreshPriority(index);
			
			
	});
	
	
	$("#operator-editGranuleDialog").dialog(
	      { autoOpen: false , 
    		modal: true, 
    		minWidth: 300,
    		buttons: {
    			'Confirm Edit': function() {
    			    var index = $("#operator-granuleTabs").tabs('option', 'selected');
    				var ids = $("#operator-granuleGrid"+index).jqGrid('getGridParam','selarrrow').join(",");
    				var state = $("#operator-editGranuleStateSelect").val();	
    				var lock = $("#operator-editGranuleLockSelect").val();
    				var retries = $("#operator-editGranuleRetries").val();
    				if(!isNaN(retries) && retries != "") {
    					$.ajax({
    						url: "controllers/editGranuleController.php?manager_id="+index,
    						data: {oper:"edit", id:ids, current_state:state, current_lock:lock, current_retries:retries},
    						type: "GET",
    						success: function(responseObject) {
    							if(responseObject.response == "OK") {
                    			    var index = $("#operator-granuleTabs").tabs('option', 'selected');
    								$("#operator-editGranuleDialog").dialog('close');
    								$("#operator-granuleGrid"+index).trigger("reloadGrid");
    							}
    							else {
    								displayDialog(responseObject.content);
    							}						
    						},
    						error: function(responseObject) {displayDialog("A server error has occured.");}
    					});
    				}
    				else {
    					displayDialog("Please enter a positive integer for Retries");
    				}
    			},
    			'Cancel': function() {
    				$(this).dialog('close');
    			}
		}
	});
	
	$("#operator-editProductDialog").dialog(
	      { autoOpen: false , 
    		modal: true, 
    		minWidth: 400,
    		buttons: {
    			'Confirm Edit': function() {
    			  var index = $("#operator-granuleTabs").tabs('option', 'selected');
    				var ids = $("#operator-productGrid"+index).jqGrid('getGridParam','selarrrow').join(",");
    				var rate = $("#operator-editProductLatency").val();
						var priority = $("#operator-editProductPriority").val();
    				$.ajax({
    					url: "controllers/editDatasetController.php?manager_id="+index,
    					data: {oper:"edit", id:ids, delivery_rate:rate, priority: priority},
    					type: "GET",
    					success: function(responseObject) {
    						if(responseObject.response == "OK") {
                			    var index = $("#operator-granuleTabs").tabs('option', 'selected');
    							$("#operator-editProductDialog").dialog('close');
    							$("#operator-productGrid"+index).trigger("reloadGrid");
    						}
    						else {
    							displayDialog(responseObject.content);
    						}						
    					},
    					error: function(responseObject) {displayDialog("A server error has occured.");}
    				});

    			},
    			'Cancel': function() {
    				$(this).dialog('close');
    			}
		}
	});

	var stateLockCombos = {	"ABORTED":["RESERVED"], 
			"PENDING_STORAGE":["REPLACE", "ADD"],
			"PENDING_ARCHIVE_STORAGE":["ARCHIVE"],
			"PENDING":["REPLACE", "ADD"],
			"ASSIGNED":["REPLACE", "ADD", "INVENTORY", "ARCHIVE"],
			"STAGED":["REPLACE", "ADD"],
			"INVENTORIED":["INVENTORY"],
			"PENDING_ARCHIVE":["ARCHIVE", "INVENTORY"],
			"ARCHIVED": ["DELETE","TRASH"]
		};
	$("#operator-editGranuleStateSelect").change(function(){
		var state = $(this).val();
		var options = '';
		var vals = stateLockCombos[state];
		for (var i = 0; i < vals.length; i++) {
			options += '<option value="' + vals[i] + '">' + vals[i] + '</option>';
		}
		$("#operator-editGranuleLockSelect").html(options);
		$('#operator-editGranuleLockSelect option:first').attr('selected', 'selected');
	});
	$("#operator-editGranuleRetries").change(function(){
		 this.value = this.value.replace(/[^0-9]/g,'');
	});
	$("#operator-editGranuleRetries").keyup(function(){
		 this.value = this.value.replace(/[^0-9]/g,'');
	});


//Storage Code
	refreshStorage();
    
//Summary Code
	$(".operator-refreshSummary").click(function(){
		var index = $(".operator-granuleSummary-Tabs").tabs('option', 'selected');
	    refreshSummary(index);
	});
	
//Priority code
	$(".operator-refreshPriority").click(function(){
		var index = $(".operator-priority-tabs").tabs('option', 'selected');
	    refreshPriority(index);
	});
//Engine Code
	//refreshEngine();
	
//Utility Code
	$(".operator-recreate-domain-button").click(function() {
		$.ajax({
			url: "controllers/domainController.php",
			data: {oper:"refresh"},
			success: function(responseObject) {
				if(responseObject.status == "OK") {
					displayDialog("Domain file successfuly recreated!");
					setTimeout("window.location.replace('index.php'); ", 1000);	
				}
				else {
					displayDialog("Error occured when writing domain file.");
				}
			},
			error: function() {
				displayDialog("Error communicating with manager.");
			}
		});
	});

//Hack to get the scrolling to work on product grids	
	setTimeout(function(){$(".operator-productGrid").trigger("reloadGrid")}, 2000);

//Status call
	refreshStatus();

//Metrics calls
	//refreshIngestByDataset();
		
	//refreshMonthlyIngestion();
});
