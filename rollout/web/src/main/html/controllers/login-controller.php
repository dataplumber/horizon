<?php
session_start();

include_once("../config.php");
include_once("datasetToolLibrary.php");

header('Content-type: application/json');

$username = $_REQUEST['username'];
$password = $_REQUEST['password'];

$loginURL = "$SECURITY_URL/$SECURITY_LOGIN";
$authorizeURL = "$SECURITY_URL/$SECURITY_AUTHORIZE";

//?userName=$username&password=$password";
$response = do_curl_request($loginURL, array("user"=>$username, "pass"=>$password));

if($response['status'] == 200) {
	
	$admin_flag = false;
	$role_response = do_curl_request($authorizeURL, array("user"=>$username));
	if($role_response['status'] == 200) {
		$role_xml = new SimpleXMLElement($role_response['content']);
		foreach($role_xml->accessRole as $role) {
			$admin_flag = ($role == "ADMIN") ? true:  $admin_flag;
		}
	}
	$_SESSION['dmt-username'] = $username;
	$_SESSION['dmt-password'] = $password;
	$_SESSION['dmt-admin'] = $admin_flag;
	print "{\"status\": \"OK\", \"response\": \"$response[content]\"}";
}
else if($response['status'] == 401){
	print "{\"status\": \"ERROR\", \"response\": \"$response[content]\"}";
}
else {
	print "{\"status\": \"ERROR\", \"response\": \"Error communicating with security service.\"}";
}

/* DEPRECATED
$message = $xml->Description;

if($xml->Status == "OK") {
	$_SESSION['dmt-username'] = $username;
	$_SESSION['dmt-password'] = $_REQUEST['password']; //Not SHA1
	$_SESSION['dmt-role'] = (string)$xml->Role;
	$_SESSION['dmt-admin'] = (string)$xml->Admin;
	$_SESSION['dmt-fullname'] = (string)$xml->fullName;
	print "{\"status\": \"OK\", \"response\": \"$message\"}";
}
else print "{\"status\": \"ERROR\", \"response\": \"$message\"}";
*/
?>
