<?php

session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST/CURL FUNCTION

//$OPERATOR_EDIT_DATASET_URL = $OPERATOR_MANAGERS[$manager_id]."/$OPERATOR_EDIT_DATASET";
$OPERATOR_ZOOKEEPER_SERVICE = $OPERATOR_ZOOKEEPER_URL."/".$OPERATOR_ZOOKEEPER_SUFFIX;


if(!isset($_SESSION['operator-username'])) {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	switch ($_REQUEST['oper']) {
		case 'proxy':
			$path = $_REQUEST['path'];
			$data = array();
			foreach($_REQUEST as $key=>$value) {
				if($key != "oper" || $key != "path")
					$data[$key] = $value;
			}
			$url = $OPERATOR_ZOOKEEPER_SERVICE."/".$path;
			$result = do_curl_request($url, $data, "GET");
			print($result['content']);
			break;			
		
		case 'list_storages':
			$url = $OPERATOR_ZOOKEEPER_SERVICE."/engines";
			$data = array("view" => "children");
			$result = do_curl_request($url, $data, "GET");
			print($result['content']);
			break;
		
		case 'list_engines':
			$storage_name = $_REQUEST['storage'];
			$url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage_name;
			$data = array("view" => "children");
			$result = do_curl_request($url, $data, "GET");
			print($result['content']);
			break;
		
		case 'list_data':
			$storage_name = $_REQUEST['storage'];
			$engine_name = $_REQUEST['engine'];
			$url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage_name."/".$engine_name;
			$data = array("dataformat" => "utf8");
			$result = do_curl_request($url, $data, "GET");
			print($result['content']);
			break;
			
		case 'shutdown':
			if($_SESSION['operator-admin']){
				$storage_name = $_REQUEST['storage'];
				$engine_name = $_REQUEST['engine'];
				$url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage_name."/".$engine_name;
				$result = do_curl_request($url, "", "DELETE");
				if (isset($result) && $result['status'] == 204)
					print json_encode(array("status"=>"OK", "content"=>"Process successful"));
				else 
					print json_encode(array("status"=>"ERROR", "content"=>"Process did not complete successfully"));
			}
			else print json_encode(array("status"=>"ERROR", "content"=>"User does not have the correct admin rights."));
			break;
		
		case 'pause':
			if($_SESSION['operator-admin']){
				$storage_name = $_REQUEST['storage'];
				$engine_name = $_REQUEST['engine'];
				$url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage_name."/".$engine_name;
				$result = do_curl_request($url, "", "PUT", "PAUSE");
				if (isset($result) && $result['status'] == 200)
					print json_encode(array("status"=>"OK", "content"=>"Process successful"));
				else 
					print json_encode(array("status"=>"ERROR", "content"=>"Process did not complete successfully"));
			}
			else print json_encode(array("status"=>"ERROR", "content"=>"User does not have the correct admin rights."));
			break;
		
		case 'resume':
			if($_SESSION['operator-admin']){
				$storage_name = $_REQUEST['storage'];
				$engine_name = $_REQUEST['engine'];
				$url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage_name."/".$engine_name;
				$time = time()*1000;
				$result = do_curl_request($url, "", "PUT", "Processing resumed at ".$time);
				$data = array("dataformat" => "utf8");
				$status_result = do_curl_request($url, $data, "GET");
				if (isset($status_result) && $status_result['status'] == 200)
					print json_encode(array("status"=>"OK", "content"=>"Process successful"));
				else 
					print json_encode(array("status"=>"ERROR", "content"=>"Process did not complete successfully"));
			}
			else print json_encode(array("status"=>"ERROR", "content"=>"User does not have the correct admin rights."));
			break;
			
		case 'summary':
			$return_object = array();
			$return_object['storages'] = array();
		
			$url = $OPERATOR_ZOOKEEPER_SERVICE."/engines";
			$data = array("view" => "children");
			$response = do_curl_request($url, $data, "GET");
			if($response['content'] != ""){
				$storage_response_object = json_decode($response['content']);
				foreach($storage_response_object->children as $storage) {
					$new_storage_object = array();
					$new_storage_object['name'] = $storage;
					$new_storage_object['engines'] = array();
					
					$engine_url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage;
					$data = array("view" => "children");
					$engine_response = do_curl_request($engine_url, $data, "GET");
					if($engine_response['content'] !="") {
						$engine_response_object = json_decode($engine_response['content']);
						foreach($engine_response_object->children as $engine) {
							$engine_object = array();
							$engine_object['name'] = $engine;
							$engine_url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage."/".$engine;
							$data = array("dataformat" => "utf8");
							$engine_response = do_curl_request($engine_url, $data, "GET");
							if($engine_response['content'] != "") {
								$engine_response_object = json_decode($engine_response['content']);
								$engine_object['status'] = $engine_response_object->dataUtf8;
							}
							else header("HTTP/1.1 500 Server Error");
							array_push($new_storage_object['engines'], $engine_object);
						}
					}
					else {
						header("HTTP/1.1 500 Server Error");
					}
					array_push($return_object['storages'], $new_storage_object);
				}
			}
			else {
				header("HTTP/1.1 500 Server Error");
			}
			print json_encode($return_object);
			break;
			
		case "storage_summary":
		  $storage = $_REQUEST['storage'];
		  $new_storage_object = array();
			$new_storage_object['name'] = $storage;
			$new_storage_object['engines'] = array();
					
			$engine_url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage;
			$data = array("view" => "children");
			
			$engine_response = do_curl_request($engine_url, $data, "GET");
			if($engine_response['content'] !="") {
				$engine_response_object = json_decode($engine_response['content']);
				if(isset($engine_response_object->children)) {
					foreach($engine_response_object->children as $engine) {
						$engine_object = array();
						$engine_object['name'] = $engine;
						$engine_url = $OPERATOR_ZOOKEEPER_SERVICE."/engines/".$storage."/".$engine;
						$data = array("dataformat" => "utf8");
						$engine_response = do_curl_request($engine_url, $data, "GET");
						if($engine_response['content'] != "") {
							$engine_response_object = json_decode($engine_response['content']);
							foreach($engine_response_object as $key=>$val) {
								//$engine_object[$key] = $val;
							}
							if(preg_match("/Registered for processing at (\d+)/", $engine_response_object->dataUtf8, $matches)) {
								$engine_object['status'] = "online";
								$engine_object['timeStarted'] = $matches[sizeof($matches)-1];
							}
							else if(preg_match("/Processing resumed at (\d+)/", $engine_response_object->dataUtf8, $matches)) {
								$engine_object['status'] = "online";
								$engine_object['timeStarted'] = $matches[sizeof($matches)-1];
							}
							else if($engine_response_object->dataUtf8 == "PAUSE" || $engine_response_object->dataUtf8 == "PAUSED"){
								$engine_object['status'] = "paused";
							}
							$engine_object['data'] = $engine_response_object->dataUtf8;
						}
						else header("HTTP/1.1 500 Server Error");
						array_push($new_storage_object['engines'], $engine_object);
					}
				}
				print json_encode($new_storage_object);
			}
			else {
				header("HTTP/1.1 500 Server Error");
			}
			
			break;
			
		default:
			break;
	}
}

?>
