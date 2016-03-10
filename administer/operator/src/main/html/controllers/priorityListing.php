<?php

session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST/CURL FUNCTION
$manager_id = isset($_REQUEST['manager_id']) ? $_REQUEST['manager_id'] : 0;
$OPERATOR_COUNT_PRIORITY_GRANULE_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_COUNT_PRIORITY_GRANULE";
$OPERATOR_COUNT_PRIORITY_STORAGE_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_COUNT_PRIORITY_STORAGE";
$OPERATOR_LIST_GRANULE_URL = $OPERATOR_MANAGERS[$manager_id]['url']."/$OPERATOR_LIST_GRANULE";


function granuleCount() {
	global $OPERATOR_COUNT_PRIORITY_GRANULE_URL;
	if(!isset($_REQUEST['startTime'])) {
		$newDate = getDate();
		$currentMonth = $newDate['mon'];
		$currentYear = $newDate['year'];
		$startTimeObject = new DateTime();
		$startTimeObject->setDate($currentYear, $currentMonth, 1);
		$startTime = $startTimeObject->getTimestamp() * 1000;
	}
	else {
		$startTime = $_REQUEST['startTime'];
	}
	
	$params = array();
	$params['userName'] = $_SESSION['operator-username'];
	$params['password'] = $_SESSION['operator-password'];
	$params['archivedTime'] = $startTime;
	
	$response = do_curl_request($OPERATOR_COUNT_PRIORITY_GRANULE_URL, $params);
	return(json_decode($response['content']));
	
}

function jobCount() {
	global $OPERATOR_COUNT_PRIORITY_STORAGE_URL;
	
	$params = array();
	$params['userName'] = $_SESSION['operator-username'];
	$params['password'] = $_SESSION['operator-password'];
	
	$response = do_curl_request($OPERATOR_COUNT_PRIORITY_STORAGE_URL, $params);
	return(json_decode($response['content']));
}

function averageCount(){
	global $OPERATOR_LIST_GRANULE_URL;
	
	$param_array = array();
	$param_array['userName'] = $_SESSION['operator-username'];
	$param_array['password'] = $_SESSION['operator-password'];	
	$param_array['sidx'] = "priority";
	$param_array['sord'] = "asc";
	$param_array['page'] = 1;
	$param_array['rows'] = 99999;
	$param_array['priority'] = "HIGH";
	
	$priority_values = array("HIGH", "NORMAL", "LOW");
	$result_array = array();
	foreach($priority_values as $priority) {
		//$priority = "HIGH";
		$param_array['priority'] = $priority;
		$average_array = array();
		$response = do_curl_request($OPERATOR_LIST_GRANULE_URL, $param_array);
		$response_json = json_decode($response['content']);
		foreach($response_json->rows as $row) {
			if(isset($row->cell[7]) && $row->cell[7] != NULL && $row->cell[7] > 0) {
				$archivedAt = round($row->cell[7]/1000);
				$createdAt = round($row->cell[5]/1000);
				$average_array[] = $archivedAt - $createdAt;
			}
		}
		$count = count($average_array);
		if($count > 0) {
			$result_array[$priority] = round(array_sum($average_array)/count($average_array));
		}
	}
	if(sizeof($result_array) > 0)
		return($result_array);
	else return "null";
}


if(!isset($_SESSION['operator-username'])) {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	if(isset($_REQUEST['oper'])) {
			switch ($_REQUEST['oper']) {
			case 'jobsByStorage':
				print json_encode(jobCount());
				break;
			case 'granulesByPriority':
				print json_encode(granuleCount());
				break;
			case 'averageArchiveTime':
				print json_encode(averageCount());
				break;
			default:
				$response = array("granuleCount" => granuleCount(), "jobCount" => jobCount(), "averageTime" => averageCount());
				print json_encode($response);
		}
	}
	else {
		//$dummy = '{"page":1,"total":1,"records":21,"rows":[{"id":1,"name":"horizonIngestDev_1","counts":[{"priority":"HIGH","count":15}]},{"id":4,"name":"horizonArchiveDev_1","counts":[{"priority":"HIGH","count":3},{"priority":"NORMAL","count":3}]}]}';
		//$dummy = json_decode($dummy);
		$response = array("granuleCount" => granuleCount(), "jobCount" => jobCount(), "averageTime" => averageCount());
		print json_encode($response);
	}
}

?>
