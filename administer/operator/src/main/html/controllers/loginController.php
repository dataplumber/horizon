<?php
session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST FUNCTION
header('Content-type: application/json');
$manager_id = isset($_REQUEST['manager_id']) ? $_REQUEST['manager_id'] : 0;
$OPERATOR_AUTH = $OPERATOR_AUTH_URL."/".$OPERATOR_AUTH_PATH;
$OPERATOR_ADMIN_CHECK = $OPERATOR_AUTH_URL."/".$OPERATOR_ADMIN_CHECK_PATH;

$username = $_REQUEST['username'];
$password = $_REQUEST['password'];

$params = array("user" => $username, "pass" => $password);

$response = do_curl_request($OPERATOR_AUTH, $params);

if($response['status'] == 200) {
	
	$admin_flag = false;
	$role_response = do_curl_request($OPERATOR_ADMIN_CHECK, array("user" => $username));
	if($role_response['status'] == 200) {
		$role_xml = new SimpleXMLElement($role_response['content']);
		foreach($role_xml->accessRole as $role) {
			$admin_flag = ($role == "ADMIN") ? true:  $admin_flag;
		}
	}
	$_SESSION['operator-username'] = $username;
	$_SESSION['operator-password'] = $password;
	$_SESSION['operator-admin'] = $admin_flag;
	print "{\"status\": \"OK\", \"response\": \"$response[content]\"}";
}
else if($response['status'] == 401){
	print "{\"status\": \"ERROR\", \"response\": \"$response[content]\"}";
}
else {
	print "{\"status\": \"ERROR\", \"response\": \"Error communicating with security service.\"}";
}
	
//$response = json_decode($response_json['content']);

/*
$message = $response->Description;
if($response->Status == "OK") {
	$_SESSION['operator-username'] = $username;
	$_SESSION['operator-password'] = $password;
	$_SESSION['operator-role'] = $response->Role;
	$_SESSION['operator-admin'] = $response->Admin;
	//$_SESSION['operator-fullname'] = $response->fullName;
	print "{\"status\": \"OK\", \"response\": \"$message\"}";
}
else print "{\"status\": \"ERROR\", \"response\": \"$message\"}";
*/

?>
