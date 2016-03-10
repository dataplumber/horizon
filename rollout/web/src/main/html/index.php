<?php
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
session_start();

if(!isset($_SESSION['dmt-username'])) {
	header('Location: login.php' ) ;
}
else {
	include_once("config.php");

?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
	"http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Dataset Manager Tool</title>

<!--<link rel="stylesheet" href="css/dataset.css"></link>-->
<style type="text/css">
@import "css/dataset.css";
@import "js/dojo/resources/dojo.css";
@import "js/dijit/themes/tundra/tundra.css";
#borderContainer { width:100%; height:100% }

</style>
<script type="text/javascript" src="js/dojo/dojo.js" charset="utf-8" djConfig="parseOnLoad: true"></script>
<script type="text/javascript" src="js/header.js" charset="utf-8" djConfig="parseOnLoad: true"></script>
<script type="text/javascript" charset="utf-8">
dojo.require("dojo.parser");
dojo.require("dijit.form.ValidationTextBox");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.layout.BorderContainer");
dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dojo.data.ItemFileWriteStore");
dojo.require("dojox.grid.DataGrid");

function updateDatasetGrid(data) {
	var nameFilter = dijit.byId("datasetGridNameFilter").get("value");
	var typeFilter = dijit.byId("datasetGridTypeFilter").get("value");
	/*if(data == "") {
		data = data.toUpperCase();
		datasetGrid.setQuery({dataset_shortName:'*'});
	}*/
	datasetRegex = new RegExp(nameFilter, "i");
	typeRegex = new RegExp(typeFilter, "i");
	datasetGrid.setQuery({dataset_shortName:datasetRegex, datasetPolicy_accessType: typeRegex});

	var count = datasetGrid.rowCount;
	dojo.byId("datasetGridCount").innerHTML = "Found "+count+" dataset(s)";
}

function updateCopyGrid(data) {
	var nameFilter = dijit.byId("datasetCopyGridNameFilter").get("value");
	var typeFilter = dijit.byId("datasetCopyGridTypeFilter").get("value");

	datasetRegex = new RegExp(nameFilter, "i");
	typeRegex = new RegExp(typeFilter, "i");
	datasetCopyGrid.setQuery({dataset_shortName:datasetRegex, datasetPolicy_accessType: typeRegex});

	var count = datasetCopyGrid.rowCount;
	dojo.byId("datasetCopyGridCount").innerHTML = "Found "+count+" dataset(s)";
}

function loadDatasetEdit(cell) {
	//console.log(item);
	var item = cell.grid.getItem(cell.rowIndex);
	id = this.store.getValue(item, "dataset_id");
	window.location = "main.php?requestType=edit&dataset_datasetId="+id;
	
}

function loadDatasetCopy(cell) {
	//console.log(item);
	var item = cell.grid.getItem(cell.rowIndex);
	id = this.store.getValue(item, "dataset_id");
	window.location = "main.php?requestType=copy&dataset_datasetId="+id;
	
}

function updateLoadGrid(data) {
	if(data == "") {
		data = data.toUpperCase();
		loadGrid.setQuery({file_dataset:'*'});
	}
	else {
		regex = new RegExp(data, "i");
		loadGrid.setQuery({file_dataset:regex});
	}		
	
	var count = loadGrid.rowCount;
	dojo.byId("datasetContinueCount").innerHTML = "Found "+count+" sessions(s)";
	
}

function loadDatasetContinue(cell) {
	var item = cell.grid.getItem(cell.rowIndex);
	file = this.store.getValue(item, "file_name");
	window.location = "main.php?requestType=continue&file="+file;
}

function loadDatasetNew() {
	window.location = "main.php?requestType=new";
}

function formatDate(value) {
	var dateObj = new Date(parseInt(value)*1000);
	return dateObj.toString();
}

dojo.addOnLoad(function(){
	//datasetGrid.setSortIndex(1);
	//console.log();
	datasetStore.fetch({
		onComplete: function(items){
			updateDatasetGrid();
			updateCopyGrid();
		}
	});
	loadStore.fetch({
		onComplete: function(items){
			updateLoadGrid();
		}
	})
});
	</script>
</head>
<body class="claro" role="application">

<div dojoType="dojo.data.ItemFileWriteStore" jsId="datasetStore"
		url="controllers/listing-controller.php?type=dataset" clearOnClose="true" urlPreventCache="true"></div>
<div dojoType="dojo.data.ItemFileWriteStore" jsId="loadStore"
		url="controllers/file-controller.php" clearOnClose="true" urlPreventCache="true"></div>
<div dojoType="dijit.layout.BorderContainer" id="main" design="sidebar" gutters="false">
<?php 
# Include shared header code
include("header.php"); 
?>
	<div dojoType="dijit.layout.TabContainer" id="tabs" style="width: 100%; height: 100%;"region="center">
		<div dojoType="dijit.layout.ContentPane" id="edit" title="Modify Existing">
			<span style="font-weight:bold; font-size: 1.5em">Perform edits on existing datasets and related information <br/></span>Double click a dataset to edit<br/><br/>			
			<input id="datasetGridNameFilter" dojoType="dijit.form.TextBox" onChange="updateDatasetGrid" intermediateChanges="true"/> <span style="margin-right:20px;">Filter Name </span>
			<input id="datasetGridTypeFilter" dojoType="dijit.form.TextBox" onChange="updateDatasetGrid" intermediateChanges="true"/> <span>Filter Access Type</span><br/><br/>
			<div id="datasetGridCount"></div>
			<div id="datasetListContainer" style="width: 100%; height: 76%;">
			<table dojoType="dojox.grid.DataGrid"
				  jsId="datasetGrid"
				  minHeight="20"
				  id="datasetGrid"
				  store="datasetStore" query="{ dataset_shortName: '*' }"
				  onRowDblClick="loadDatasetEdit"
				  style="width: 100%; height: 100%;">
				  <thead>
					<tr>
						<th field="dataset_id" width="45px" sortable="true">ID</th>
						<th field="dataset_shortName" width="auto" sortable="true">Short Name</th>
						<th field="datasetPolicy_accessType" width="110px" sortable="true">Access Type</th>
					</tr>
				  </thead>
			</table> 
			</div>
		</div>
		<div dojoType="dijit.layout.ContentPane" id="continue" title="Continue Previous">
			<span style="font-weight:bold; font-size: 1.5em">Continue a previously saved session <br/></span>Double click a file to select<br/><br/>
			<input dojoType="dijit.form.TextBox" onChange="updateLoadGrid" intermediateChanges="true"/> Search Datasets <br/><br/>
			<div id="datasetContinueCount"></div>
			<div id="datasetListContainer" style="width: 100%; height: 75%;">
			<table dojoType="dojox.grid.DataGrid"
				  jsId="loadGrid"
				  minHeight="20"
				  id="loadGrid"
				  store="loadStore" query="{ file_dataset: '*' }"
				  onRowDblClick="loadDatasetContinue"
				  style="width: 100%; height: 100%;">
				  <thead>
					<tr>
						<th field="file_type" width="100px" sortable="true">Type</th>
						<th field="file_dataset" width="auto" sortable="true">Dataset</th>
						<th field="file_dateModified" width="200px" sortable="true" formatter="formatDate">Date Modified</th>
					</tr>
				  </thead>
			</table> 
			</div>
		</div>
		<div dojoType="dijit.layout.ContentPane" id="copy" title="Copy From Existing">
			<span style="font-weight:bold; font-size: 1.5em">Create a new dataset from existing information<br/></span>Double click a file to select<br/><br/>
			<input id="datasetCopyGridNameFilter" dojoType="dijit.form.TextBox" onChange="updateCopyGrid" intermediateChanges="true"/> <span style="margin-right:20px;">Filter Name </span>
			<input id="datasetCopyGridTypeFilter" dojoType="dijit.form.TextBox" onChange="updateCopyGrid" intermediateChanges="true"/> <span>Filter Access Type</span><br/><br/>
			<div id="datasetCopyGridCount"></div>
			<div id="datasetListContainer" style="width: 100%; height: 75%;">
			<table dojoType="dojox.grid.DataGrid"
				  jsId="datasetCopyGrid"
				  minHeight="20"
				  id="datasetCopyGrid"
				  store="datasetStore" query="{ dataset_shortName: '*' }"
				  onRowDblClick="loadDatasetCopy"
				  style="width: 100%; height: 100%;">
				  <thead>
					<tr>
						<th field="dataset_id" width="45px" sortable="true">ID</th>
						<th field="dataset_shortName" width="auto" sortable="true">Short Name</th>
						<th field="datasetPolicy_accessType" width="110px" sortable="true">Access Type</th>
					</tr>
				  </thead>
			</table> 
			</div>
		</div>
		<div dojoType="dijit.layout.ContentPane" id="new" title="Create New">
			<button dojoType="dijit.form.Button" onClick="loadDatasetNew">Create New Dataset</button>
		</div>
	</div>
</div>

</body>
</html>
<?php
}
?>
