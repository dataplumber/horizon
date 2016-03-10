<?php

session_start();

include("operatorLibrary.php"); //SETS GLOBAL VARS + POST/CURL FUNCTION

//$OPERATOR_EDIT_DATASET_URL = $OPERATOR_MANAGERS[$manager_id]."/$OPERATOR_EDIT_DATASET";

if(!isset($_SESSION['operator-username'])) {
	header("HTTP/1.1 530 User Not Logged In");
}
else {
	header('Content-type: application/json');
	switch ($_REQUEST['oper']) {
		case "count_by_dataset":
			$query = "/granule/select?q=*:*&version=2.2&indent=off&wt=json&facet.field=Dataset-ShortName-Full&facet=true&facet.sort=index&rows=0";
			$url = $OPERATOR_SOLR_URL.$query;
			$response = do_post_request($url, "");
			if ($response) {
				$response_object = json_decode($response);
				
				$return_object = array();
				
				$pairs_array = $response_object->facet_counts->facet_fields->{'Dataset-ShortName-Full'};
				$pairs_length = sizeof($pairs_array);
				for ($x = 0; $x < $pairs_length; $x+=2) {
					$dataset_object = array();
					if($pairs_array[$x+1] > 950) {
						$dataset_object['label'] = $pairs_array[$x];
						$dataset_object['data'] = $pairs_array[$x+1];
						array_push($return_object, $dataset_object); 
					}
				}
				print json_encode($return_object);
			}
			else header("HTTP/1.1 500 Server Error");
			break;
		case "size_by_dataset":
			$query = "/granule/select?q=*:*&version=2.2&indent=off&wt=json&stats=true&stats.field=GranuleArchive-FileSize&rows=0&stats.facet=Dataset-ShortName-Full";
			$url = $OPERATOR_SOLR_URL.$query;
			
			$return_object = array();
			
			$response = do_post_request($url, "");
			if ($response) {
				$response_object = json_decode($response);
				$dataset_list = $response_object->stats->stats_fields->{'GranuleArchive-FileSize'}->facets->{'Dataset-ShortName-Full'};
				foreach($dataset_list as $key=>$val) {
					$dataset_object = array();
					$dataset_object['label'] = $key;
					$dataset_object['data'] = $val->sum;
					array_push($return_object, $dataset_object);
				}
				print json_encode($return_object);
			}
			else header("HTTP/1.1 500 Server Error");
			break;
		case "count_by_month":
			$return_object = array();
			
			$newDate = getDate();
			$currentMonth = $newDate['mon'];
			$currentYear = $newDate['year'];
			
			$firstDay = new DateTime();
			$firstDay->setDate($currentYear, $currentMonth, 1);
			
			$lastDay = clone $firstDay;
			$lastDay->modify("+1 month");
			$lastDay->modify("-1 day");
			
			for($count = 0; $count < 30; $count++){
				$firstDayStamp = $firstDay->getTimestamp() * 1000;
				$lastDayStamp = $lastDay->getTimestamp() * 1000;
				$labelDay = clone $firstDay;
				$labelDay->modify("+1 day");
				$query = "/granule/select?q=*:*&version=2.2&indent=off&wt=json&rows=0&fq=Granule-ArchiveTimeLong:[".$firstDayStamp."+TO+".$lastDayStamp."]";
				$url = $OPERATOR_SOLR_URL.$query;
				$response = do_post_request($url,"");
				if($response){
					$response_object = json_decode($response);
					$sum = $response_object->response->numFound;
					array_push($return_object, array($labelDay->getTimestamp() * 1000, $sum));
				}
				else header("HTTP/1.1 500 Server Error");
				
				$lastDay = clone $firstDay;
				$lastDay->modify("-1 day");
				$firstDay->modify("-1 month");
			}
			print json_encode($return_object);
			break;
		default:
			break;	
	}	
}
?>
