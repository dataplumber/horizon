<?php
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
date_default_timezone_set('GMT');
session_start();
header("Content-Type: text/json");

include_once("../config.php");
include_once("datasetToolLibrary.php");

function returnAttribute($xml, $name, $attribute) {
	$name = preg_replace("/\d/", "", $name);
	foreach($xml->field as $field) {
		if(preg_match("/$name.*/", $field['name'])) {
			return $field[$attribute];
		}
	}
}

$template = getTemplate("dataset");
$xml = simplexml_load_string("<dataset type=\"".$_POST['requestType']."\" />");

$newField = $xml->addChild("field",$_SESSION['dmt-username']);
$newField->addAttribute("name", "user");

foreach($_POST as $key => $value)
{
	//$value = stripslashes($value);
	if(preg_match("/.+_.+/", $key)) {
		if(!preg_match("/(.+)TIME$/", $key, $matches) && !preg_match("/(.+)#$/", $key, $matches) ) {
			if(preg_match("/(.+)DATE$/", $key, $matches)) {
				$dateValue = $_POST[$matches[1]."DATE"];
				$timeValue = $_POST[$matches[1]."TIME"];
				if($dateValue != "") { 
					if ($timeValue != "")
						$value = strtotime($dateValue." ".$timeValue) * 1000;
					else $value = strtotime($dateValue." 	T00:00:00") * 1000;
				}
				else $value = "";
				$key = $matches[1];
			}

			
			$value = trim($value);
			$newField = $xml->addChild("field",$value);
			$newField->addAttribute("name", $key);
			$newField->addAttribute("type", returnAttribute($template, $key, "type"));
			$newField->addAttribute("required", returnAttribute($template, $key, "required"));
		}
	}
}


$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=".urlencode($xml->asXML());
$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
//print "$params\n";
//print "$INVENTORY_URL/$INVENTORY_PROCESS?$params";
$responseContent = simplexml_load_string($response)->Status;
$responseDescription = str_replace("\n", "\\n", htmlentities(simplexml_load_string($response)->Description));
print "{\"response\":\"$responseContent\", \"description\":\"$responseDescription\"}";

// Move saved file to processed dir if submit succeeds
if($responseContent == "OK" || $responseContent == "201") {
	$newShort = $_POST['dataset_shortName'];
	if($_POST['requestType'] == "update")
		$requestType = "editDataset";
	elseif($_POST['requestType'] == "create")
		$requestType = "newDataset";
	else $requestType = "unknown";
	$sourceFile = "$OUTPUT_DIR/$requestType"."_$newShort.xml";
	$movedFile = "$PROCESSED_DIR/$requestType"."_$newShort.xml.".date("Ymd_His");
	rename($sourceFile, $movedFile);
}

//print($response);
//print($xml->asXML());
//print "{response:\"OK\"}";

?>


