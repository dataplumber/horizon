<?

$input = "/usr/www/conf/.mailpw";
$file = @fopen("$input", "r");
if ($file) { 
    $buffer = fgets($file);
    $buffer = rtrim($buffer);
    $db = $buffer;
    $buffer = fgets($file);
    $buffer = rtrim($buffer);
    $user = $buffer;
    $buffer = fgets($file);
    $buffer = rtrim($buffer);
    $pw = $buffer;
  fclose($file);
}

$conn = oci_connect($user,$pw,$db);



set_time_limit(0);

$lists = array();
$r = array();

foreach(array_keys($_POST) as $key){
  if ($key != 'subject' && $key != 'body' && $key != 'send') {
    array_push($lists, $key);
  }
}


foreach($lists as $list) {
	$query = "select email, id from $list";
	$result = oci_parse($conn, $query);
	oci_execute($result, OCI_DEFAULT);
	while ($r = oci_fetch_array($result, OCI_RETURN_NULLS)) {
	  $emails[$r[0]][$list] = $r[1];
	}
}	


?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Email Sender</title>
</head>

<body>

<?
$keys = array_keys($emails);


foreach ($keys as $email) {
	//RETRIEVE FORM OBJECTS
	$subject = $_POST[subject];
	$message = $_POST[body];
	
	$whichlists = $emails[$email];
	$lists = array_keys($whichlists);
	
	
	$subject = stripslashes($subject);
	$message = stripslashes($message);
	
	// ADD FOOTER TO MESSAGES	
	$reg_message = $message."\n\n--------------------------------------------------------------------\n".
	  "User Services Office              \n".
	  "Physical Oceanography DAAC     Email:   podaac@podaac.jpl.nasa.gov\n".
	  "Jet Propulsion Laboratory      WWW URL: http://podaac.jpl.nasa.gov\n\n".
	  "--------------------------------------------------------------------\n\n";
	foreach($lists as $list) {
	  $id = $whichlists[$list];
	  $link = "http://podaac.jpl.nasa.gov/cgi-bin/emailremoval.pl?id=id".$id."&list=".$list;
	  $reg_message = $reg_message."To be removed from the $list list, please send an email to podaac@podaac.jpl.nasa.gov with your request.";
	}
	  
	
	$HTMLMessage = htmlentities($message);
	$HTMLMessage = ereg_replace("[[:alpha:]]+://[^<>[:space:]]+[[:alnum:]/]","<a href=\"\\0\">\\0</a>", $HTMLMessage); 
	$HTMLMessage = $HTMLMessage."\n\n--------------------------------------------------------------------\n".
	  "User Services Office\n".
	  "Physical Oceanography DAAC &nbsp &nbsp &nbsp Email: &nbsp <a href=\"mailto:podaac@podaac.jpl.nasa.gov\">podaac@podaac.jpl.nasa.gov</a>\n".
	  "Jet Propulsion Laboratory &nbsp &nbsp &nbsp WWW URL: <a href=\"http://podaac.jpl.nasa.gov\">http://podaac.jpl.nasa.gov</a>\n\n".
	  "--------------------------------------------------------------------\n\n";
	
	//Generate unsubscribe links
	foreach($lists as $list) {
	  $id = $whichlists[$list];
	  $link = "http://podaac.jpl.nasa.gov/cgi-bin/emailremoval.pl?id=id$id&list=$list";
	  $HTMLMessage = $HTMLMessage."To be removed from the $list list,";
	}
	
	
	  
	$HTMLMessage = $HTMLMessage." send an email to <a href=\"mailto:podaac@podaac.jpl.nasa.gov\">podaac@podaac.jpl.nasa.gov</a> with your request.";
	$HTMLMessage = nl2br($HTMLMessage);
	$HTMLMessage = "<html><body><font size=2 face=\"Courier New\">".$HTMLMessage."</font></body></html>";
	
	$body = "--==Multipart_Boundary_xc75j85x\n".
	  "Content-Type: text/plain; ".
	  "charset=\"us-ascii\"; ".
	  "format=flowed\n".
	  $reg_message."\n".
	  "--==Multipart_Boundary_xc75j85x\n".
	  "Content-Type: text/html; ".
	  "charset=\"us-ascii\" \n\n".
	  $HTMLMessage."\n";
	
	$headers = "From: podaac@podaac.jpl.nasa.gov"."\r\n".
	  "Reply-To: www@podaac.jpl.nasa.gov"."\r\n".
	  "X-Mailer: PHP/".phpversion()."\r\n".
	  "Mime-Version: 1.0\n".
	  "Content-Type: multipart/alternative;\n".
	  "          boundary=\"==Multipart_Boundary_xc75j85x\"";
	
	
	$param = "-r www@podaac.jpl.nasa.gov";
	$result = mail($email, $subject, $body, $headers, $param); 
	if ($result) 
	  echo "Mail Sent to $email <br/>";
	else 		
	  echo "FAILED: $email <br/>";
}


oci_close($conn);

?>
<br/><a href="mailinglist.php">Back</a> to Mailing List page

</body>
</html>
 
