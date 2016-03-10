<?php

session_start();

try {
	include("operatorLibrary.php"); //SETS GLOBAL VARS + POST/CURL FUNCTION
}
catch (Exception $e){
	
}

//$OPERATOR_EDIT_DATASET_URL = $OPERATOR_MANAGERS[$manager_id]."/$OPERATOR_EDIT_DATASET";

if(!isset($_SESSION['operator-username']) && $_REQUEST['oper'] != "update") {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	switch ($_REQUEST['oper']) {
	    case "update":
	        $param_array = array();
            $param_array['userName'] = $_REQUEST['username'];
            $param_array['password'] = $_REQUEST['password'];
		    $url_prefix = preg_match("/http/", $_REQUEST['url']) ? $_REQUEST['url'] : "http://".$_REQUEST['url'];
		    $url = $url_prefix."/manager/$OPERATOR_DOMAIN_UPDATE";
		    $response = do_post_request($url, http_build_query($param_array));
		    if($response != "" && preg_match("/xml/", $response)) {
			    $handler = fopen("../config/horizondomain.xml", "w");
    			fwrite($handler, $response);
				
				// Everything is fine so far so process the login too!
				/*$OPERATOR_AUTH_URL = $url_prefix."/manager/$OPERATOR_AUTH";
				$json = do_post_request($OPERATOR_AUTH_URL, http_build_query($param_array));
				$login_response = json_decode($json);
				if($login_response->Status == "OK") {
					$_SESSION['operator-username'] = $param_array['userName'];
					$_SESSION['operator-password'] = $param_array['password'];
					$_SESSION['operator-role'] = $login_response->Role;
					$_SESSION['operator-admin'] = $login_response->Admin;
					//$_SESSION['operator-fullname'] = $login_response->fullName;
				}*/
				// End login process

	    		print "{\"status\":\"OK\",\"response\":\"Domain file successfully generated.\"}";
	    	}
	    	else {
	    		print $response;
	    	}
	      break;
	        
	    case "refresh":
			if(!isset($_SESSION['operator-username']) || !$_SESSION['operator-admin']) {
				header("HTTP/1.1 530 User Not Logged In");
			}
			else {
		        $param_array = array();
	            $param_array['userName'] = $_SESSION['operator-username'];
	            $param_array['password'] = $_SESSION['operator-password'];
			    $url = $OPERATOR_MANAGERS[0]['url']."/$OPERATOR_DOMAIN_UPDATE";
			    $response = do_post_request($url, http_build_query($param_array));
			    if($response != "" && preg_match("/xml/", $response)) {
				    $handler = fopen("../config/horizondomain.xml", "w");
	    			fwrite($handler, $response);
		    		print "{\"status\":\"OK\",\"response\":\"Domain file successfully generated.\"}";
					
					//Log sigevent
					date_default_timezone_set('America/Los_Angeles');
					$sigevent_url = $OPERATOR_SIGEVENT_URL."/events/create";
					$user = $_SESSION['operator-username'];
					$sigevent_data = array("format"=>"json", 
									"type"=>"WARN", 
									"category"=>"DMAS", 
									"source"=>"Operator Tool",
									"provider"=>"Operator Tool",
									"computer"=>gethostname(),
									"description"=>"The configuration file was updated on the Operator Interface by ".$user." on ".date("r"),
									"pid"=>getmypid(),
									"data"=>"The configuration file was updated on the Operator Interface by ".$user." on ".date("r") );
					try {
						$sigevent_response = do_post_request($sigevent_url, http_build_query($sigevent_data));
					}
					catch (Exception $e){
					}
		    	}
		    	else {
		    		print $response;
		    	}
	    	}
	        break;
		case "test":
				print $sigevent_url."?".http_build_query($data);
        default:
            break;
	}
}

?>
