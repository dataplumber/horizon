<?php
/*
listing-controller.php
input: $_GET params
	type = "dataset" - returns listing of datasets
	type = "source" - returns listing of sources
	type = "sensor" - returns listing of sensors
	project
	provider
	collection
	contact

output: json file with array of items as "field_id":"field_shortName"


*/
session_start();
header('Content-type: application/json');
include_once("../config.php");
include_once("datasetToolLibrary.php");

function inResults($results, $fieldLabel, $id) {
	foreach($results as $index=>$result) {
		if($results[$index][$fieldLabel] == $id)
			return true;
	}
	return false;
}

function printList($listXML, $valueLabel, $fieldLabel, $idLabel) {
	$results = array();

	foreach($listXML->field as $field) {
		$value = "$field";
		$fieldName = $field['name'];
		$required = $field['required'];
		if(preg_match("/$valueLabel([0-9]+)/", $fieldName, $matches)) {
			$id = getFieldValue($listXML, $idLabel.$matches[1]);	
			if(!inResults($results, $fieldLabel, $id)) $results[] = array($fieldLabel =>(int) $id, $valueLabel => $value);
		}
	}
	print "{ \"identifier\":\"$fieldLabel\",
		\"label\": \"$valueLabel\",
		\"items\": \n";
	print json_encode($results);
	print "}";
}



function printSingle($listXML, $valueLabel, $fieldLabel) {
	$results = array();
	$sorted = array();
	foreach($listXML->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");

	foreach($sorted as $field) {
		$value = "$field";
		$fieldName = $field['name'];
		$required = $field['required'];

		if($fieldName != "contac_notifyType" && !preg_match("/^collectionDataset.*/", $fieldName)) //&& (preg_match("/datasetCharacter\_.*$/",$fieldName) || preg_match("/datasetElement\_.*$/",$fieldName) || preg_match("/datasetReal\_.*$/",$fieldName) )) 
			$results[] = array("field" => "$fieldName", "value" => "$value");
	}
	print "{ \"items\": \n";
	print json_encode($results);
	print "}";
}

function printAjaxList($listXML, $field) {
	$results = array();
	foreach($listXML->dataset as $item) {
	    if((string) $item->$field != "")
            $results[] = (string) $item->$field;
	}
	print "{ \"identifier\":\"$field\",
		\"label\": \"$field\",
		\"items\": \n";
	print json_encode($results);
	print "}";
}

function printDatasetList($listXML, $valueLabel, $fieldLabel, $idLabel) {
	$results = array();

	foreach($listXML->field as $field) {
		$value = "$field";
		$fieldName = $field['name'];
		$required = $field['required'];
		if(preg_match("/$valueLabel([0-9]+)/", $fieldName, $matches)) {
			$id = getFieldValue($listXML, $idLabel.$matches[1]);
			$accessType = getFieldValue($listXML, "datasetPolicy_accessType".$matches[1]);
			if(!inResults($results, $fieldLabel, $id)) $results[] = array($fieldLabel =>(int) $id, $valueLabel => $value, "datasetPolicy_accessType" => $accessType);
		}
	}
	print "{ \"identifier\":\"$fieldLabel\",
		\"label\": \"$valueLabel\",
		\"items\": \n";
	print json_encode($results);
	print "}";
}

$type = $_GET["type"];
if($type == "dataset") {
	if(isset($_GET['dataset_datasetId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<dataset type=\"list\"><field name=\"object_id\">".$_GET['dataset_datasetId']."</field></dataset>";
		$fieldLabel = "dataset";	
		$valueLabel = "dataset_shortName";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest="."<dataset type=\"list\"></dataset>";
		//$paramsArray = Array("userName" => $_SESSION['dmt-username'], "password" => $_SESSION['dmt-password'], "manifest" => "<dataset type=\"list\"></dataset>");
		//$params =  http_build_query($paramsArray);
		//print "$params\n";
		$fieldLabel = "dataset_id";	
		$valueLabel = "dataset_shortName";
		
		//print "$INVENTORY_URL/$INVENTORY_PROCESS"."?".$params."\n";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		//print $response."\n";
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_string($response);
		printDatasetList($listXML, $valueLabel, $fieldLabel, "dataset_datasetId");
	}	

}

if($type == "source") {
	if(isset($_GET['source_sourceId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<source type=\"list\"><field name=\"object_id\">".$_GET['source_sourceId']."</field></source>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);	
		//$listXML = simplexml_load_file("../xml/sourceListing.xml");
		$fieldLabel = "source_id";	
		$valueLabel = "source_shortName";
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<source type=\"list\"></source>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);	
		//$listXML = simplexml_load_file("../xml/sourceListing.xml");
		$fieldLabel = "source_id";	
		$valueLabel = "source_shortName";
		printList($listXML, $valueLabel, $fieldLabel, "source_sourceId");
	}
}

if($type == "sensor") {
	if(isset($_GET['sensor_sensorId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<sensor type=\"list\"><field name=\"object_id\">".$_GET['sensor_sensorId']."</field></sensor>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/sensorListing.xml");
		$fieldLabel = "sensor_id";	
		$valueLabel = "sensor_shortName";
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<sensor type=\"list\"></sensor>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/sensorListing.xml");
		$fieldLabel = "sensor_id";	
		$valueLabel = "sensor_shortName";
		printList($listXML, $valueLabel, $fieldLabel, "sensor_sensorId");
	}
}

if($type == "project") {
	if(isset($_GET['project_projectId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<project type=\"list\"><field name=\"object_id\">".$_GET['project_projectId']."</field></project>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "project_id";	
		$valueLabel = "project_shortName";
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<project type=\"list\"></project>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "project_id";	
		$valueLabel = "project_shortName";
		printList($listXML, $valueLabel, $fieldLabel, 'project_projectId');

	}
}

if($type == "provider") {
	if(isset($_GET['provider_providerId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<provider type=\"list\"><field name=\"object_id\">".$_GET['provider_providerId']."</field></provider>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "provider_id";	
		$valueLabel = "provider_shortName";
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<provider type=\"list\"></provider>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "provider_providerId";	
		$valueLabel = "provider_shortName";
		printList($listXML, $valueLabel, $fieldLabel, 'provider_providerId');

	}
}

if($type == "contact") {
	if(isset($_GET['contact_contactId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<contact type=\"list\"><field name=\"object_id\">".$_GET['contact_contactId']."</field></contact>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "contact_id";	
		$valueLabel = "contact_name";
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<contact type=\"list\"></contact>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "contact_contactId";	
		$valueLabel = "contact_name";
		printList($listXML, $valueLabel, $fieldLabel, 'contact_contactId');

	}
}

if($type == "collection") {
	if(isset($_GET['collection_collectionId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<collection type=\"list\"><field name=\"object_id\">".$_GET['collection_collectionId']."</field></collection>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		//print "$INVENTORY_URL/$INVENTORY_PROCESS?$params";
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//print $params."\n";
		//print $response;
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "collection_id";	
		$valueLabel = "collection_name";
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<collection type=\"list\"></collection>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "collection_CollectionId";	
		$valueLabel = "collection_shortName";
		printList($listXML, $valueLabel, $fieldLabel, 'collection_collectionId');

	}
}

if($type == "element") {
	if(isset($_GET['element_elementId']) ) {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<element type=\"list\"><field name=\"object_id\">".$_GET['element_elementId']."</field></element>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "element_elementId";	
		$valueLabel = "element_shortName";
		printSingle($listXML, $valueLabel, $fieldLabel);
	}
	else {
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<element type=\"list\"></element>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);

		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//$listXML = simplexml_load_file("../xml/projectListing.xml");
		$fieldLabel = "elementDD_elementId";	
		$valueLabel = "elementDD_shortName";
		printList($listXML, $valueLabel, $fieldLabel, 'elementDD_elementId');

	}
}

if($type == "ajax_persistent") {
	//$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<element type=\"list\"></element>";
	$response = do_get_request("$INVENTORY_URL/$INVENTORY_LIST", "");
	$listXML = simplexml_load_string($response);
	
	printAjaxList($listXML, "persistent-id");
}
if($type == "ajax_shortName") {
	//$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<element type=\"list\"></element>";
	$response = do_get_request("$INVENTORY_URL/$INVENTORY_LIST", "");
	$listXML = simplexml_load_string($response);
	
	printAjaxList($listXML, "short-name");
}

?>
		
