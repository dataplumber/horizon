<?php
session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST FUNCTION
$manager_id = isset($_REQUEST['manager_id']) ? $_REQUEST['manager_id'] : 0;
$OPERATOR_LIST_SUMMARY_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_LIST_SUMMARY";

if(!isset($_SESSION['operator-username'])) {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	$param_array = array();
	$param_array['userName'] = $_SESSION['operator-username'];
	$param_array['password'] = $_SESSION['operator-password'];	
	foreach($param_array as $key => $value) {
		if($value == NULL) unset($param_array[$key]);
	}
	$params = http_build_query($param_array);
	try{	
		$response = do_post_request($OPERATOR_LIST_SUMMARY_URL, $params);
	} catch(Exception $e) { 
		 header("HTTP/1.1 500 Server Error");
	}
	
	print $response;
	//print '{"page":1,"total":1,"records":14,"rows":[{"currentState":"PENDING","currentLock":"REPLACE","count":1},{"currentState":"ARCHIVED","currentLock":"TRASH","count":6},{"currentState":"ARCHIVED","currentLock":"DELETE","count":6},{"currentState":"PENDING_ARCHIVE","currentLock":"ARCHIVE","count":1}]}';
	
}

?>
