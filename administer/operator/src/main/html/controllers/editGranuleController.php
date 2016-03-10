<?php
session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST FUNCTION
$manager_id = isset($_REQUEST['manager_id']) ? $_REQUEST['manager_id'] : 0;
$OPERATOR_EDIT_GRANULE_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_EDIT_GRANULE";
$OPERATOR_DELETE_GRANULE_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_DELETE_GRANULE";

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
			$param_array['current_state'] = $_REQUEST['current_state'];
			$param_array['current_lock'] = $_REQUEST['current_lock'];
			$param_array['current_retries'] = $_REQUEST['current_retries'];
			$param_array['id'] = $_REQUEST['id'];
			
			$url = $OPERATOR_EDIT_GRANULE_URL;
			$params = http_build_query($param_array);
		}
		elseif($_REQUEST["oper"] == "del") {
			$param_array['id'] = $_REQUEST["id"];
	
			$url = $OPERATOR_DELETE_GRANULE_URL;
			$params = http_build_query($param_array);
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
