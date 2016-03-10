<?php
/*
creation-controller.php
*/
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
session_start();
header('Content-type: application/json');
include_once("../config.php");
include_once("datasetToolLibrary.php");

if( $_REQUEST["type"] == "check") {
    $manager_lookup = "$MANAGER_URL/$MANAGER_LOOKUP";
    $sigevent_lookup = "$SIGEVENT_URL/$SIGEVENT_LOOKUP";
    
    $sigevent_params = array(
        "category" => $_REQUEST['dataset'],
        "name" => $_REQUEST['dataset']
        );
    $manager_params = array(
        "userName" => $_SESSION['dmt-username'],
        "password" => $_SESSION['dmt-password'],
        "category" => $_REQUEST['dataset'],
        "name" => $_REQUEST['dataset']
        );
    $sigevent_response = do_post_request($sigevent_lookup, http_build_query($sigevent_params));
    $sigevent_xml = simplexml_load_string($sigevent_response);
    
    $manager_response = do_post_request($manager_lookup, http_build_query($manager_params));
    //$manager_xml = simplexml_load_string($manager_response);    
    
    $sigevent_flag = ((string)$sigevent_xml->Type == "OK");
    if(isset(json_decode($manager_response)->response) )
    	$manager_flag = false;
    else $manager_flag = true;
    
	$response = array("manager_status" => $manager_flag, "sigevent_status" => $sigevent_flag);
	print json_encode($response);
}
elseif($_REQUEST["type"] == "create") {
    if($_REQUEST["source"] == "manager") {
        $baseURL = "$MANAGER_URL/$MANAGER_CREATE";
        $params = array(
	        "userName" => $_SESSION['dmt-username'],
    	    "password" => $_SESSION['dmt-password'],
        	"name" => $_REQUEST['dataset']
        );
    	$manager_response = do_post_request($baseURL, http_build_query($params));
    	if(isset(json_decode($manager_response)->response)) {
    		print json_encode(array("response"=>"ERROR"));
    	} 
    	else print json_encode(array("response"=>"OK"));
    
    }
    elseif($_REQUEST["source"] == "sigevent") {
        $baseURL = "$SIGEVENT_URL/$SIGEVENT_CREATE";
    	$params = array(
        	"format" => "json",
        	"category" => $_REQUEST['dataset']	
        );
        
        //Info
        $params['purgeRate'] = 2;
        $params['type'] = "INFO";
        
		$info_response = do_post_request($baseURL, http_build_query($params));
		$info_flag = json_decode($info_response)->Response->Type;
		
		//Warn
        $params['purgeRate'] = 2;
		$params['type'] = "WARN";
		
		$warn_response = do_post_request($baseURL, http_build_query($params));
		$warn_flag = json_decode($warn_response)->Response->Type;
		
		//Error
        $params['purgeRate'] = 30;
		$params['type'] = "ERROR";
		
		$error_response = do_post_request($baseURL, http_build_query($params));
		$error_flag = json_decode($error_response)->Response->Type;		
		
		//print "INFO = $info_flag\n";
		//print "WARN = $warn_flag\n";
		//print "ERROR = $error_flag\n";
		
		if($info_flag == "OK" && $warn_flag == "OK" && $error_flag == "OK")
			print json_encode(array("response"=>"OK"));
		else print json_encode(array("response"=>"ERROR"));
	}
    
}

?>

