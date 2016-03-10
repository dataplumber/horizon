<?php
session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST FUNCTION
$manager_id = isset($_REQUEST['manager_id']) ? $_REQUEST['manager_id'] : 0;
$OPERATOR_EDIT_DATASET_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_EDIT_DATASET";

if(!isset($_SESSION['operator-username'])) {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	if(!$_SESSION['operator-admin']) {
		print "{\"response\":\"ERROR\", \"content\":\"You do not have permission for that operation\"}";
	}
	else {
		$param_array = array();	
		$param_array['userName'] = $_SESSION['operator-username'];
		$param_array['password'] = $_SESSION['operator-password'];
		if($_REQUEST["oper"] == "edit") {
			$param_array['deliveryRate'] = $_REQUEST['delivery_rate'];
			$param_array['priority'] = $_REQUEST['priority'];
			$param_array['id'] = $_REQUEST['id'];
			
			$url = $OPERATOR_EDIT_DATASET_URL;
			$params = http_build_query($param_array);
		}
		elseif($_REQUEST["oper"] == "del") {
		//Delete code comes later
		}
		try{	
			$response = do_post_request($url, $params);
		} catch(Exception $e) { 
			 header("HTTP/1.1 500 Server Error");
		}
		print $response;
	}
}


?>
