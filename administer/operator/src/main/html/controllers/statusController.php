<?php

session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST/CURL FUNCTION

if(!isset($_SESSION['operator-username'])) {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	switch ($_REQUEST['oper']) {
		case 'summary':
			//Managers, inventory, zookeeper, sigevent,solr
			//Managers
			$return_object = array();
			foreach($OPERATOR_MANAGERS as $manager) {
				$manager_object = array();
				$response = null;
				$status = "offline";
				try {
					$response = do_get_request($manager['url']."/".$OPERATOR_MANAGER_HEARTBEAT, "");
				}
				catch (exception $e) {
					
				}
				if($response != null){
					$status = "online";
				}
				
				$manager_object['type'] = "manager";
				$manager_object['name'] = $manager['name'];
				$manager_object['url'] = $manager['url'];
				$manager_object['status'] = $status;
				array_push($return_object, $manager_object);
			}
			//Inventory
			$inventory_object = array();
			$response = null;
			$status = "offline";
			try {
			  $response = do_curl_request($OPERATOR_INVENTORY_URL."/inventory/".$OPERATOR_INVENTORY_HEARTBEAT, array());
			}
			catch (exception $e) {
				
			}
			if($response != null){
				$status = "online";
			}
			$inventory_object['type'] = "inventory";
			$inventory_object['url'] = $OPERATOR_INVENTORY_URL;
			$inventory_object['status'] = $status;
			array_push($return_object, $inventory_object);			
			
			//Zookeeper
			$zookeeper_object = array();
			$response = null;
			$status = "offline";
			try {
				$response = do_curl_request($OPERATOR_ZOOKEEPER_URL."/".$OPERATOR_ZOOKEEPER_SUFFIX."/", array("view" => "children"));
			}
			catch (exception $e) {
				//$status = "offline";
			}
			if($response != null){
				$status = "online";
			}
			$zookeeper_object['type'] = "zookeeper";
			$zookeeper_object['url'] = $OPERATOR_ZOOKEEPER_URL."/".$OPERATOR_ZOOKEEPER_SUFFIX."/";
			$zookeeper_object['status'] = $status;
			array_push($return_object, $zookeeper_object);		
			
			//Solr
			$solr_object = array();
			$response = null;
			$status = "offline";
			try {
				$response = do_curl_request($OPERATOR_SOLR_URL, array());
			}
			catch (exception $e) {
				
			}
			if($response['status'] == 200 || $response['status'] == 302){
				$status = "online";
			}
			$solr_object['type'] = "solr";
			$solr_object['url'] = $OPERATOR_SOLR_URL;
			$solr_object['status'] = $status;
			array_push($return_object, $solr_object);		
			
			print json_encode($return_object);
			break;
	}
	
}

?>
