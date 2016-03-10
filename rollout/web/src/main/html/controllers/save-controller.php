<?php
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
date_default_timezone_set('GMT');
session_start();
header("Content-Type: text/json");

include_once("../config.php");
include_once("datasetToolLibrary.php");

function returnAttribute($xml, $name, $attribute) {
	foreach($xml->field as $field) {
		if(preg_match("/$name"."[0-9#]*/", $field['name'])) {
			return $field[$attribute];
		}
	}
}


$template = getTemplate("dataset");
$xml = simplexml_load_string("<dataset type=\"".$_POST['requestType']."\" />");


foreach($_POST as $key => $value)
{
	if(preg_match("/.+_.+/", $key)) {
		if(!preg_match("/(.+)TIME$/", $key, $matches) && !preg_match("/(.+)#$/", $key, $matches) ) {
			if(preg_match("/(.+)DATE$/", $key, $matches)) {
				$dateValue = $_POST[$matches[1]."DATE"];
				$timeValue = $_POST[$matches[1]."TIME"];
				
				if($dateValue != "") { //&& $timeValue != "") {
					$value = strtotime($dateValue." ".$timeValue) * 1000;
				}
				else $value = "";
				$key = $matches[1];
			}
			$value = htmlentities($value);
			//Replace unify all new-lines into unix LF:
			$value = str_replace("\r","\n", $value);
			$value = str_replace("\n\n","\n", $value);

			//Replace all new lines with the unicode:
			$value = str_replace("\n","&#10;", $value);
			$value = trim($value);

			$newField = $xml->addChild("field",$value);
			$newField->addAttribute("name", $key);
			$newField->addAttribute("type", returnAttribute($template, $key, "type"));
			$newField->addAttribute("required", returnAttribute($template, $key, "required"));
			//print(returnRequired($template, $key)." $key\n");
		}
	}
}
//print $xml->asXML();
$newShort = $_POST['dataset_shortName'];

if($_POST['requestType'] == "update")
	$requestType = "editDataset";
elseif($_POST['requestType'] == "create")
	$requestType = "newDataset";
else $requestType = "unknown";
$outputFile = "$OUTPUT_DIR/$requestType"."_$newShort.xml";
$fh = fopen($outputFile, 'w') or die("Can't open file");

$dom = dom_import_simplexml($xml)->ownerDocument;
$dom->formatOutput = true;
//echo $dom->saveXML();

fwrite($fh, $dom->saveXML());

print "{response:\"OK\"}";

?>


