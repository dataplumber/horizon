<?php
session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST FUNCTION
$manager_id = isset($_REQUEST['manager_id']) ? $_REQUEST['manager_id'] : 0;
$OPERATOR_EDIT_STORAGE_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_EDIT_STORAGE";
$OPERATOR_EDIT_LOCATION_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_EDIT_LOCATION";

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
			$param_array['id'] = $_REQUEST['storageId'];
			$param_array['priority'] = $_REQUEST['priority'];
			$url = $OPERATOR_EDIT_STORAGE_URL;
			$params = http_build_query($param_array);
			try{	
				$response = do_post_request($url, $params);
			} catch(Exception $e) { 
				 header("HTTP/1.1 500 Server Error");
			}			
			
			
			unset($param_array['priority']);
			$param_array['id'] = $_REQUEST['id'];
			$param_array['space_used'] = $_REQUEST['used'];
			$param_array['space_reserved'] = $_REQUEST['reserved'];
			$param_array['space_threshold'] = $_REQUEST['threshold'];
			$param_array['active'] = $_REQUEST['active'];
	
			$url = $OPERATOR_EDIT_LOCATION_URL;
			$params = http_build_query($param_array);
			
			try{	
				$response = do_post_request($url, $params);
			} catch(Exception $e) { 
				 header("HTTP/1.1 500 Server Error");
			}
			
			print $response;
		}
	}
}


?>
