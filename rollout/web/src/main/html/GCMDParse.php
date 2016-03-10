<?php
ini_set('display_errors', '0');     # don't show any errors...
error_reporting(E_ALL | E_STRICT);  # ...but do log them
header('Content-type: application/json');



$type = $_GET["type"];



if($type=="category") {
$index = 0;
}
elseif($type=="topic"){
$index = 0;
}
elseif($type=="term"){
$index = 1;
}
elseif($type=="variable"){
$index = 2;
}
elseif($type=="variable_detail"){
$index = 3;
}

$file = "DMAS/GCMDTopic";

$handler = fopen($file, "r");
$firstLine = fgets($handler);
$headers = explode(" > ", $firstLine);

$items = array();
$values = array();
$row = array();

while(($line = fgets($handler)) !== FALSE) {
	$data = explode(" > ", $line);
	if( !(in_array(rtrim($data[$index]), $values)) ) {
		$values[] = rtrim($data[$index]);
		$row["value"] = rtrim($data[$index]);
		$items[] = $row;
	}
}

sort($items);

echo "{ 
	identifier: \"value\",
	label: \"value\",
	items: ";

echo json_encode($items)."\n";
echo "}";

fclose($handler);

?>
