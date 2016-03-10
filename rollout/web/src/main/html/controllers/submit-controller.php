<?php
/*
submit-controller.php
input: $_GET params
	type = "dataset" - submit a dataset form
	type = "source" - 
	type = "sensor" -
	project
	provider
	collection
	contact

output: json with response, description from server


*/
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
session_start();
header('Content-type: application/json');
include_once("../config.php");
include_once("datasetToolLibrary.php");

function returnAttribute($xml, $name, $attribute) {
	foreach($xml->field as $field) {
		if(preg_match("/$name"."[0-9#]*/", $field['name'])) {
			return $field[$attribute];
		}
	}
}

$type = $_GET['type'];
$requestType = $_POST['requestType'];
//$requestType = "bogus";
//CODE TO ADD ID TO POST ARRAY
if($requestType != "create") {
	foreach($_GET as $key=>$field) {
		if($key != "type") {
			$_POST[$key] = $field;		
		}
	}
}

$template = getTemplate($type);
$xml = simplexml_load_string("<$type type=\"$requestType\" />");

$newField = $xml->addChild("field",$_SESSION['dmt-username']);
$newField->addAttribute("name", "user");

foreach($_POST as $key => $value)
{
	if(preg_match("/.+_.+/", $key) && $key != "requestType") {
		if(($key == "collectionLegacyProduct_legacyProductId" && $value != "") || $key != "collectionLegacyProduct_legacyProductId") {
			$value = htmlentities(trim($value));
			$newField = $xml->addChild("field",$value);
			$newField->addAttribute("name", $key);
			$newField->addAttribute("type", returnAttribute($template, $key, "type"));
			$newField->addAttribute("required", returnAttribute($template, $key, "required"));
		}
	}
}

$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=".urlencode($xml->asXML());
$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);

$responseStatus = simplexml_load_string($response)->Status;
$responseDescription = str_replace("\n", "\\n", htmlentities(simplexml_load_string($response)->Description));
print "{\"response\":\"$responseStatus\", \"description\":\"$responseDescription\"}";
//print "{\"response\": \"".simplexml_load_string($response)->Status."\"}";



?>

