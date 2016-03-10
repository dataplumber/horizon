<?php
/*
file-controller.php
input: none

output: json file with array of items with attributes of files in the xml directory

*/
//header('Content-type: application/json');
date_default_timezone_set('GMT');
include_once("../config.php");

header('Content-type: application/json');
$results = array();

if($dir = opendir("$OUTPUT_DIR")) {
	while($file = readdir($dir)) {
		if(preg_match("/^([A-Za-z]*)_(.+)\.xml$/", $file, $matches)) {
			$fileType = $matches[1];
			$datasetName = $matches[2];
			$fileArray = array("file_name" => $file, 
				"file_dateModified" => date("U", filemtime("$OUTPUT_DIR/$file")),//date("F d Y H:i:s.", filemtime("$OUTPUT_DIR/$file")), 
				"file_type" => $fileType, 
				"file_dataset" => $datasetName);
			$results[] = $fileArray;
		}
	}
}
closedir($dir);

print "{ \"items\": \n";
print json_encode($results);
print "}";

?>
		
