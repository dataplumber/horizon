<?php
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
date_default_timezone_set('GMT');
session_start();

if(!isset($_SESSION['dmt-username'])) {
	header('Location: login.php' ) ;
	
}
else {
		//header("Content-Type: text/xml");
	include_once("controllers/datasetToolLibrary.php");
	include_once("config.php");
	if($_GET['requestType'] == "edit") {
		if(isset($_GET['dataset_datasetId']) )
			$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<dataset type=\"list\"><field name=\"object_id\" type=\"int\">".$_GET['dataset_datasetId']."</field></dataset>";
		else die("Couldn't load dataset from inventory");
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$responseContent = (simplexml_load_string($response)->Content);
		$datasetManifest = simplexml_load_string($responseContent);
		$requestType = "update";
	}
	elseif($_GET['requestType'] == "new") {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<dataset type=\"template\"></dataset>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$datasetManifest = simplexml_load_string(simplexml_load_string($response)->Content);
		//echo simplexml_load_string($response)->Content."<br/>";
		$requestType = "create";
	}
	elseif($_GET['requestType'] == "copy") {
		if(isset($_GET['dataset_datasetId']) )
			$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<dataset type=\"list\"><field name=\"object_id\" type=\"int\">".$_GET['dataset_datasetId']."</field></dataset>";
		else die("Couldn't load dataset from INVENTORY");
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$responseContent = (simplexml_load_string($response)->Content);
		$datasetManifest = simplexml_load_string($responseContent);
		$requestType = "create";
	}
	elseif($_GET['requestType'] == "continue") {
		try {
			$datasetManifest = simplexml_load_file("controllers/$OUTPUT_DIR/".$_GET['file']);
		}
		catch(Exception $e) {
			print "Error occured when loading file";
		}
		//print_r($datasetManifest);
		$requestType = $datasetManifest['type'];
	}

//Template XML
$templateManifest = getTemplate("dataset");
$projectManifest = getTemplate("project");
$providerManifest = getTemplate("provider");
$collectionManifest = getTemplate("collection");
$sourceManifest = getTemplate("source");
$sensorManifest = getTemplate("sensor");
$contactManifest = getTemplate("contact");
$elementManifest = getTemplate("element");
?>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
	"http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<title>Dataset Template Form</title>

	<link rel="stylesheet" href="css/dataset.css"></link>
	<script type="text/javascript" src="js/dojo/dojo.js" charset="utf-8"></script>
	<script type="text/javascript" src="js/header.js" charset="utf-8" djConfig="parseOnLoad: true"></script>
	<!--<script type="text/javascript" src="js/config.js" charset="utf-8"></script> -->
	<!--<script type="text/javascript" src="js/src.js" charset="utf-8"></script> -->
	<script type="text/javascript" charset="utf-8"><?php include_once("js/src.js"); ?></script>
</head>
<body class="claro" role="application">
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="collectionStore"
		url="controllers/listing-controller.php?type=collection" clearOnClose="true" urlPreventCache="true"></div> 
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="providerStore"
		url="controllers/listing-controller.php?type=provider" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="contactStore"
		url="controllers/listing-controller.php?type=contact" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="projectStore"
		url="controllers/listing-controller.php?type=project" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="sourceStore"
		url="controllers/listing-controller.php?type=source" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="sensorStore"
		url="controllers/listing-controller.php?type=sensor" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="elementStore"
		url="controllers/listing-controller.php?type=element" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="GCMDCategoryStore"
		url="GCMDParse.php?type=category" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="GCMDTopicStore"
		url="GCMDParse.php?type=topic" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="GCMDTermStore"
		url="GCMDParse.php?type=term" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="GCMDVariableStore"
		url="GCMDParse.php?type=variable" clearOnClose="true" urlPreventCache="true"></div>
	<div dojoType="dojo.data.ItemFileWriteStore" jsId="GCMDVariableDetailStore"
		url="GCMDParse.php?type=variable_detail" clearOnClose="true" urlPreventCache="true"></div>
<form dojoType="dijit.form.Form" id=datasetForm jsId="datasetForm" method="POST" action="finalize.php" style="height:100%">
<input dojoType="dijit.form.TextBox" type="hidden" value="<?php print $requestType; ?>" id="requestType" name="requestType"/>
<div dojoType="dijit.layout.BorderContainer" id="main" gutters="false">
<?php 
# Include shared header code
include("header.php"); 
?>
	<div dojoType="dijit.layout.TabContainer" id="tabs" jsId="tabs" region="center" tabPosition="left">
	<div dojoType="dijit.layout.BorderContainer" id="dataset" jsId="dataset" region="center" title="Dataset" design="sidebar">
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20;">
			<table margin="0 auto">
			<?php printDatasetTable($datasetManifest); ?>
			</table>
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="datasetSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="datasetRegion" region="center" title="Dataset Region" design="sidebar">
		<div dojoType="dijit.Toolbar" id="regionMenu" region="top" height="20px" style="text-align:left;">
				<div dojoType="dijit.form.Button" id="createRegionButton" onClick="createNewRegion">Create New Region</div>
		</div>	
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
			Add new dataset regions using the button above!<br/>			
			<div dojoType="dijit.layout.ContentPane" id="regionTable" style="align: left; margin: 0 0 0 20px;"></div>
		</div>	
		<div dojoType="dijit.form.Button" region="bottom" id="regionSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="datasetPolicy" region="center" title="Dataset Policy" design="sidebar">
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
		<table>	
			<?php printDatasetPolicyTable($datasetManifest); ?>
		</table>
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="datasetPolicySaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="datasetCoverage" region="center" title="Dataset Coverage" design="sidebar">
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
		<table>	
			<?php printDatasetCoverageTable($datasetManifest); ?>
		</table>
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="datasetCoverageSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>	
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="datasetElement" region="center" title="Dataset Element" design="sidebar">
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
		<table>	
			<?php /*printDatasetElementTable($datasetManifest);*/ ?>
		</table>
		<select dojoType="dijit.form.FilteringSelect" 
						style="width: 15em;"
						id="elementTypeSelect"
						store="elementStore"
						searchAttr="elementDD_shortName"
						labelAttr="elementDD_shortName" 
						required="false">
		</select> 
		 <div dojoType="dijit.form.Button" onClick="createNewElement()">Add New Element Value</div>
		<div dojoType="dijit.form.Button" onClick="newElement">Create New Element Category</div><br/>
		<table margin="0 auto" id=elementTable>
		</table>
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="datasetElementSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>	
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="datasetParameter" region="center" title="Dataset Parameter" design="sidebar">
		<div dojoType="dijit.Toolbar" id="parameterMenu" region="top" height="20px" style="text-align:left;">
				<div dojoType="dijit.form.Button" id="createParameterButton" onClick="createNewParameter">Create New Parameter</div>
		</div>	
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
			Add new dataset parameters using the button above!<br/>			
			<div dojoType="dijit.layout.ContentPane" id="parameterTable" style="align: left; margin: 0 0 0 20px;"></div>
		</div>	
		<div dojoType="dijit.form.Button" region="bottom" id="parameterSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="project" region="center" title="Project" design="sidebar">
		<div dojoType="dijit.Toolbar" id="projectMenu" region="top" height="20px" style="text-align:left;">
			<!--<div dojoType="dijit.form.Button" id="addProjectButton" onClick="generateNewProjectRow">Add Project</div> -->
			<div dojoType="dijit.form.Button" id="createProjectButton" onClick="newProject">Create New Project</div>			
		</div>
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">
			<!-- Add new dataset projects using the menu above!<br/> 
			<div dojoType="dijit.layout.ContentPane" id="projectTable" style="align: left; margin: 0 0 0 20px;"></div> -->
			Project Short Name: <select dojoType="dijit.form.FilteringSelect" 
						style="width: 10em;"
						id="datasetProject_projectId" 
						name="datasetProject_projectId" 
						store="projectStore"
						searchAttr="project_shortName" 
						labelAttr="project_shortName"
						required="true"></select> 
			<button dojoType="dijit.form.Button" label="View/Edit Details" iconClass="mailIconNewMessage" projectForm="datasetProject_projectId" onClick="editProject"></button>
			
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="projectSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="provider" region="center" title="Provider" design="sidebar">
		<div dojoType="dijit.Toolbar" id="providerMenu" region="top" height="20px" style="text-align:left;">
			
			<div dojoType="dijit.form.Button" id="addProviderButton" onClick="newProvider">Create New Provider</div>
		</div>
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">
			Provider Short Name: <select dojoType="dijit.form.FilteringSelect" 
						style="width: 10em;"
						id="dataset_providerId" 
						name="dataset_providerId" 
						store="providerStore"
						searchAttr="provider_shortName" 
						labelAttr="provider_shortName"
						required="true"></select>
			<button dojoType="dijit.form.Button" label="View/Edit Details" iconClass="mailIconNewMessage" projectForm="dataset_providerId" onClick="editProvider"></button>
			<!--<div dojoType="dijit.layout.ContentPane" id="providerTable" style="align: left; margin: 0 0 0 20px;"></div>-->
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="providerSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>	
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="collection" region="center" title="Collection" design="sidebar">
		<div dojoType="dijit.Toolbar" id="collectionMenu" region="top" height="20px" style="text-align:left;">
			<div dojoType="dijit.form.Button" onClick="createNewCollection">Add Collection</div>			
		</div>		
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">
			If  a specific collection is not found in the "Collection" drop down, create a new one with the following button<br/><br/>

			<div dojoType="dijit.form.Button" onClick="newCollection" label="Create New Collection"></div><br/>
			Notes: <br/>Granule Range is a flag to indicate whether its 0-360 or -180-180<br/><br/>
			Granule Flag indicates whether this collection includes all granules or partial, which is then detailed by the start/stop granule id's<br/><br/><br/>
			<div dojoType="dijit.form.TextBox" id="collectionID" name="collectionID" type="hidden"></div>
			<table margin="0 auto" id=collectionTable>
			</table>
			<!--<div dojoType="dijit.layout.ContentPane" id="providerTable" style="align: left; margin: 0 0 0 20px;"></div>-->
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="collectionSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>	
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="sourcesensor" region="center" title="Source/Sensor" design="sidebar">
		<div dojoType="dijit.Toolbar" id="sourcesensorMenu" region="top" height="20px" style="text-align:left;">
			<!--<select dojoType="dijit.form.FilteringSelect" 
						style="width: 10em;"
						id="sensorEntry" 
						name="sensor" 
						store="sensorStore"
						searchAttr="LABEL" 
						labelAttr="LABEL"
						required="false"></select> -->
				<div dojoType="dijit.form.Button" onClick="addNewSourceSensorRow">Add Source/Sensor Pair</div>
		</div>
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">
			Add new source/sensor combos using the menu above!<br/><br/>
			If the desired source or sensor does not appear in the drop down menus, create new entries here:<br/>
			<div dojoType="dijit.form.Button" onClick="newSource">Create New Source</div>
			<div dojoType="dijit.form.Button" onClick="newSensor">Create New Sensor</div><br/><br/>
			<div dojoType="dijit.layout.ContentPane" id="sensorTable" style="align: left; margin: 0 0 0 20px;"></div>
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="sensorSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>	
		
	</div>
	<div dojoType="dijit.layout.BorderContainer" id="contact" region="center" title="Contact" design="sidebar">
		<div dojoType="dijit.Toolbar" id="contactMenu" region="top" height="20px" style="text-align:left;">
			<!--<select dojoType="dijit.form.FilteringSelect" 
						style="width: 10em;"
						id="contactEntry" 
						name="contact" 
						store="contactStore"
						searchAttr="LAST_NAME" 
						labelAttr="LAST_NAME"
						required="false"></select> -->
			<div dojoType="dijit.form.Button" id="createContactButton" onClick="createNewContact">Add Contact</div>			
		</div>
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">
			If the desired contact does not appear in the drop down menus, create a new contact here:<br/>
			<div dojoType="dijit.form.Button" onClick="newContact">Create New Contact</div><br/><br/>
			<div dojoType="dijit.layout.ContentPane" id="contactTable" style="align: left; margin: 0 0 0 20px;"></div>
		</div>
		<div dojoType="dijit.form.Button" region="bottom" id="contactSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>




	<div dojoType="dijit.layout.BorderContainer" id="citation" region="center" title="Dataset Citation" design="sidebar">
		<div dojoType="dijit.Toolbar" id="citationMenu" region="top" height="20px" style="text-align:left;">
				<div dojoType="dijit.form.Button" onClick="toggleCitation">Disable Citation</div>
		</div>	
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
			<!--Add new dataset citations using the button above!<br/> 	
			<div dojoType="dijit.layout.ContentPane" id="citationTable" style="align: left; margin: 0 0 0 20px;"></div> -->
			If citation entries do not apply to this dataset, please disable the citation fields with the button above!<br/><br/>
			<table>
			<?php printDatasetCitationTable($templateManifest); ?>
			</table>
		</div>	
		<div dojoType="dijit.form.Button" region="bottom" id="citationSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>

	</div>

	<div dojoType="dijit.layout.BorderContainer" id="location" region="center" title="Location Policy" design="sidebar">
		<div dojoType="dijit.Toolbar" id="locationPolicyMenu" region="top" height="20px" style="text-align:left;">
				<div dojoType="dijit.form.Button" <?php if(!$_SESSION['dmt-admin'] && $requestType != "create") print "disabled='true' " ?>id="createLocationPolicyButton" onClick="createNewLocationPolicy">Create New Location Policy Entry</div>
		</div>	
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
			Add new dataset location policies using the button above!<br/>			
			<div dojoType="dijit.layout.ContentPane" id="locationPolicyTable" style="align: left; margin: 0 0 0 20px;"></div>
		</div>	
		<div dojoType="dijit.form.Button" region="bottom" id="locationPolicySaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>
	
	<div dojoType="dijit.layout.BorderContainer" id="resource" region="center" title="Dataset Resource" design="sidebar">
		<div dojoType="dijit.Toolbar" id="resourceMenu" region="top" height="20px" style="text-align:left;">
				<div dojoType="dijit.form.Button" id="createResourceButton" onClick="createNewResource">Create New Resource Entry</div>
		</div>	
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20">		
			Add new dataset resources using the button above!<br/>			
			<div dojoType="dijit.layout.ContentPane" id="resourceTable" style="align: left; margin: 0 0 0 20px;"></div>
		</div>	
		<div dojoType="dijit.form.Button" region="bottom" id="resourceSaveButton" onClick="saveLocal" label="Save Progress" iconClass="mailIconOk"></div>
	</div>
<?php
	if($requestType == "update") :
?>
	<div dojoType="dijit.layout.BorderContainer" id="creation" jsId="creation" region="center" title="Creation Status" design="sidebar">
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 20;">
			<table margin="0 auto">
				<tr>
					<td>Manager Product Creation Status: </td>
					<td><div id="manager-status"></div></td>
				</tr>
				<tr>
					<td>Sigevent Product Creation Status: </td>
					<td><div id="sigevent-status"></div></td>
				</tr>
			</table>
		</div>
		
	</div>
<?php
	endif;
?>	

	<div dojoType="dijit.layout.BorderContainer" id="submit" region="center" title="Submit" design="sidebar">
		<div dojoType="dijit.layout.ContentPane" region="center" style="min-size: 200px">
		Are you certain you want to submit? A summary page will outline the form fields on the following page.<br/>
		<div dojoType="dijit.form.Button" id="finalSubmitButton" onClick="submitFinal">Submit</div>
		</div>
	</div>


	</div>
</div>
</form>

<!-- Generic Message Popup -->	
<div dojoType="dijit.Dialog" id="gmessageDialog" title="Attention:" minWidth="50px">
		<div id="gmessage">
		</div>
</div>

<div dojoType="dijit.Dialog" id="projectDialog" title="Project Information:" minWidth="50px">
		<div dojoType="dijit.form.Form" id="projectForm" onSubmit="return false;"">
			<input dojoType="dijit.form.TextBox" type="hidden" name="requestType" id="projectAction"/>
			<table width="400px" margin="0 auto">

<?php 
printProjectTable($projectManifest);
?>
			<tr>
				<td colspan=2 align=center>
					<button dojoType="dijit.form.Button" id="projectButtonOK" label="Add" onClick="processProject" iconClass="mailIconOk"></button>
					<button dojoType="dijit.form.Button" id="projectButtonCancel" label="Cancel" onClick="dijit.byId('projectDialog').hide(); dijit.byId('projectDialog').reset();" iconClass="mailIconCancel"></button>
				</td>
			</tr>
			</table>
		</div>
</div>
<div dojoType="dijit.Dialog" id="providerDialog" title="Provider Information:" minWidth="50px">
		<div dojoType="dijit.form.Form" id="providerForm" onSubmit="return false;">
			<input dojoType="dijit.form.TextBox" type="hidden" name="requestType" id="providerAction"/>
			<table width="400px" margin="0 auto">

<?php 
printProviderTable($providerManifest);
?>
			<tr>	
				<td><div dojoType="dijit.form.Button" 
						id="providerPathButton" 
						onClick="addNewProviderPath">Create New Path</div>
				</td>	
			</tr>	
			<tr>
				<td colspan=2><table id=providerPathTable>
				</table>
				</td>
			</tr>			
			<tr>
				<td colspan=2 align=center>
					<button dojoType="dijit.form.Button" id="providerButtonOK" label="Add" onClick="processProvider" iconClass="mailIconOk"></button>
					<button dojoType="dijit.form.Button" id="providerButtonCancel" label="Cancel" onClick="dijit.byId('providerDialog').hide(); dijit.byId('providerDialog').reset();" iconClass="mailIconCancel"></button>
				</td>
			</tr>
			</table>
		</div>
</div>
<div dojoType="dijit.Dialog" id="collectionDialog" title="Collection Information:" minWidth="50px">
		<div dojoType="dijit.form.Form" id="collectionForm" onSubmit="return false;">
			<input dojoType="dijit.form.TextBox" type="hidden" name="requestType" id="collectionAction"/>
			<table width="400px" margin="0 auto">

<?php 
printCollectionTable($collectionManifest);
?>
			<tr>
				<td colspan=2 align=center>
					<button dojoType="dijit.form.Button" id="collectionButtonOK" label="Add" onClick="processCollection" iconClass="mailIconOk"></button>
					<button dojoType="dijit.form.Button" id="collectionButtonCancel" label="Cancel" onClick="dijit.byId('collectionDialog').hide(); dijit.byId('collectionDialog').reset();" iconClass="mailIconCancel"></button>
				</td>
			</tr>
			</table>
		</div>
</div>
<div dojoType="dijit.Dialog" id="sourceDialog" title="Source(Platform) Information:" minWidth="50px">
		<div dojoType="dijit.form.Form" id="sourceForm" onSubmit="return false;">
			<input dojoType="dijit.form.TextBox" type="hidden" name="requestType" id="sourceAction"/>
			<table width="400px" margin="0 auto">

<?php 
printSourceTable($sourceManifest);
?>
			<tr>
				<td colspan=2 align=center>
					<button dojoType="dijit.form.Button" id="sourceButtonOK" label="Add" onClick="processSource" iconClass="mailIconOk"></button>
					<button dojoType="dijit.form.Button" id="sourceButtonCancel" label="Cancel" onClick="dijit.byId('sourceDialog').hide(); dijit.byId('sourceDialog').reset();" iconClass="mailIconCancel"></button>
				</td>
			</tr>
			</table>
		</div>
</div>
<div dojoType="dijit.Dialog" id="sensorDialog" title="Sensor Information:" minWidth="50px">
		<div dojoType="dijit.form.Form" id="sensorForm" onSubmit="return false;">
			<input dojoType="dijit.form.TextBox" type="hidden" name="requestType" id="sensorAction"/>
			<table width="400px" margin="0 auto">

<?php 
printSensorTable($sensorManifest);
?>
			<tr>
				<td colspan=2 align=center>
					<button dojoType="dijit.form.Button" id="sensorButtonOK" label="Add" onClick="processSensor" iconClass="mailIconOk"></button>
					<button dojoType="dijit.form.Button" id="sensorButtonCancel" label="Cancel" onClick="dijit.byId('sensorDialog').hide(); dijit.byId('sensorDialog').reset();" iconClass="mailIconCancel"></button>
				</td>
			</tr>
			</table>
		</div>
</div>
<div dojoType="dijit.Dialog" id="contactDialog" title="Contact Information:" minWidth="50px">
		<div dojoType="dijit.form.Form" id="contactForm" onSubmit="return false'">
			<input dojoType="dijit.form.TextBox" type="hidden" name="requestType" id="contactAction"/>
			<table width="400px" margin="0 auto">

<?php 
printContactTable($contactManifest);
?>
			<tr>
				<td colspan=2 align=center>
					<button dojoType="dijit.form.Button" id="contactButtonOK" label="Add" onClick="processContact" iconClass="mailIconOk"></button>
					<button dojoType="dijit.form.Button" id="contactButtonCancel" label="Cancel" onClick="dijit.byId('contactDialog').hide(); dijit.byId('contactDialog').reset();" iconClass="mailIconCancel"></button>
				</td>
			</tr>
			</table>
		</div>
</div>
<div dojoType="dijit.Dialog" id="elementDialog" title="Element Information:" minWidth="50px">
		<div dojoType="dijit.form.Form" id="elementForm" onSubmit="return false'">
			<input dojoType="dijit.form.TextBox" type="hidden" name="requestType" id="elementAction"/>
			<table width="400px" margin="0 auto">

<?php 
printElementTable($elementManifest);
?>
			<tr>
				<td colspan=2 align=center>
					<button dojoType="dijit.form.Button" id="elementButtonOK" label="Add" onClick="processElement" iconClass="mailIconOk"></button>
					<button dojoType="dijit.form.Button" id="elementButtonCancel" label="Cancel" onClick="dijit.byId('elementDialog').hide(); dijit.byId('elementDialog').reset();" iconClass="mailIconCancel"></button>
				</td>
			</tr>
			</table>
		</div>
</div>
</body>
</html>

<?php
}
?>
