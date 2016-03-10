<?php
session_start();
error_reporting(E_ALL ^ (E_NOTICE | E_WARNING));

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST FUNCTION
$manager_id = isset($_REQUEST['manager_id']) ? $_REQUEST['manager_id'] : 0;
$OPERATOR_LIST_GRANULE_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_LIST_DATASET";

if(!isset($_SESSION['operator-username'])) {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	$param_array = array();
	$param_array['userName'] = $_SESSION['operator-username'];
	$param_array['password'] = $_SESSION['operator-password'];
	$param_array['sidx'] = $_REQUEST['sidx'];
	$param_array['sord'] = $_REQUEST['sord'];
	$param_array['page'] = $_REQUEST['page'];
	$param_array['rows'] = $_REQUEST['rows'];
	$param_array['id'] = $_REQUEST['id'];
	$param_array['name'] = $_REQUEST['name'];
	$param_array['purge_rate'] = $_REQUEST['purgeRate'];
	$param_array['event_category'] = $_REQUEST['eventCategory'];
	$param_array['note'] = $_REQUEST['note'];
	$param_array['delivery_rate'] = $_REQUEST['deliveryRate'];
	$param_array['priority'] = $_REQUEST['priority'];
	
	foreach($param_array as $key => $value) {
		if($value == NULL) unset($param_array[$key]);
	}
	$params = http_build_query($param_array);
	//print_r($param_array);
	try{	
		$response = do_post_request($OPERATOR_LIST_GRANULE_URL, $params);
	} catch(Exception $e) { 
		 header("HTTP/1.1 500 Server Error");
	}
	//print $response;
	$response_object = json_decode($response);
	/*foreach($response_object->rows as $index => $row) {
		foreach ($response_object->rows[$index]->cell as $x=>$cell) {
			$response_object->rows[$index]->cell[$x] = (string)$response_object->rows[$index]->cell[$x];
		}
		$response_object->rows[$index]->cell[9] = htmlentities(str_replace("{", "", $response_object->rows[$index]->cell[9]), ENT_QUOTES);
		$response_object->rows[$index]->cell[9] = htmlentities(str_replace("}", "", $response_object->rows[$index]->cell[9]), ENT_QUOTES);
	}*/
	print json_encode($response_object);
	
}

?>
