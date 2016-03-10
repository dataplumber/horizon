<?php
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
session_start();

if(!isset($_SESSION['dmt-username'])) {
	header('Location: login.php' ) ;
}
else {

include_once("controllers/datasetToolLibrary.php");
function printFormContents() {
	print "<table border=\"0px\">";
	foreach($_POST as $key=>$value) {
		$value=stripslashes($value);
		$label = getLabel($key);
		if ($label!=""){ //&& !preg_match("/^datasetElement.*/", $key)) {
			print "<tr><td width=\"25%\" style=\"font-weight:bold;\">";
			print $key;
			print "</td><td>$value</td></tr>";
		}
	}

	print "</table>";
}


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
dojo.require("dijit.Dialog");

var managerFlag = false;
var sigeventFlag = false;
var managerStatus, sigeventStatus;
var requestType = "<?php print $_POST['requestType']; ?>";
var shortName = "<?php print $_POST['dataset_shortName']; ?>";

function showGenericMessage(text){
	dojo.byId("gmessage").innerHTML=text;
	dijit.byId("gmessageDialog").show();
}

function submitDataset() {
	var formData = "<?php foreach($_POST as $key => $value) {
		//$value = addslashes($value);
		//$value = (string)str_replace(array("\r", "\r\n", "\n"), '', $value);
		//$value = (string)str_replace(array("%"), ' percent', $value);
		print "$key=".urlencode($value)."&";		
		//print "\"$key\": \"$value\",\n";	
	}
?>";		
	//console.log(formData);
	var button = this;
	button.attr("label", "Loading...");
	button.attr("disabled", true);
	dojo.xhrPost( {
       		postData:formData,
		url:"controllers/submitDataset-controller.php",
        	handleAs: "json",
        	load: function(responseObject, ioArgs) {
			button.attr("label", "Submit Dataset");
			button.attr("disabled", false);
			if(responseObject.response == "OK" || responseObject.response == "201") {
				if(requestType == "create"){
					managerCreate();
					sigeventCreate();
				}
				else {
					showGenericMessage("Submitted!");
					setTimeout("window.location.replace('index.php'); ", 1000);			
				}
			}
			else showGenericMessage("Error encountered: "+responseObject.description);
			return responseObject;
        	},
		error: function(responseObject, ioArgs) {
			//console.log(ioArgs);
			//showGenericMessage("Error encountered while submitting!");
			showGenericMessage("Error encountered: "+responseObject.description);
			return responseObject;
        	}
			
	});
}

function combinedCallback(){
	if(managerFlag && sigeventFlag){
		var managerText = (managerStatus) ? "Manager creation <span style='color:green'>successful</span>" : "Manager entries <span style='color:red'>not created</span>"
		var sigeventText = (sigeventStatus) ? "Sigevent creation <span style='color:green'>successful</span>" : "Sigevent entries <span style='color:red'>not created</span>"
		showGenericMessage("Submitted!<br><br>"+managerText+"<br>"+sigeventText);
		setTimeout("window.location.replace('index.php'); ", 5000);			
	}

}

function managerCreate(){
	dojo.xhrGet({
		url:"controllers/creation-controller.php?type=create&source=manager&dataset="+shortName,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			managerFlag = true;
			if(responseObject.response == "OK") {
				managerStatus = true;
			}
			else {
				managerStatus = false;			
			}		
			combinedCallback();
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});	
}

function sigeventCreate(){
	dojo.xhrGet({
		url:"controllers/creation-controller.php?type=create&source=sigevent&dataset="+shortName,
		handleAs: "json",
		load: function(responseObject, ioArgs) {
			sigeventFlag = true;
			if(responseObject.response == "OK") {
				sigeventStatus = true;
			}
			else {
				sigeventStatus = false;			
			}
			combinedCallback();
		},
		error: function(responseObject, ioArgs) {
			showGenericMessage("A server error has occured");
			return responseObject;
		}
	});	
}



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
	<div dojoType="dijit.layout.ContentPane" style="width: 98%; height: 75%; background:#fff;" region="center">
		<span style="font-weight:bold; font-size:1.5em;">Summary of Form Entries (Request Type: <?php print $_POST['requestType'];?>)</span><br/><br/>
		<?php printFormContents(); ?>
		<br/><br/><div style="font-size:2em; font-weight:bold;"> Are you certain you would like to commit these changes?</div>
		<button dojoType="dijit.form.Button" onClick="submitDataset">Submit Dataset</button>
		<button dojoType="dijit.form.Button" onClick="window.location.replace('<?php
$url = "";
if($_POST['requestType'] == "update")
	$url = "main.php?requestType=continue&file=editDataset_".$_POST['dataset_shortName'].".xml";
else if($_POST['requestType'] == "create")
	$url = "main.php?requestType=continue&file=newDataset_".$_POST['dataset_shortName'].".xml";
print $url;
?>
');">Go Back</button>
	</div>
n>
</div>
<!-- Generic Message Popup -->	
<div dojoType="dijit.Dialog" id="gmessageDialog" title="Attention:" minWidth="50px">
		<div id="gmessage">
		</div>
</div>
<body>
</html>

<?php
}
?>
