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
if (!$conn) {
  $e = oci_error();
  print $e['message'];
  exit;
}


function retrievelists ($conn) {
	$lists = array();
	$query = "select table_name from tabs";
	$result = oci_parse($conn, $query);
	oci_execute($result, OCI_DEFAULT);
	while ($list = oci_fetch_array($result, OCI_RETURN_NULLS)) {
	  array_push($lists, $list[0]);
	}
	return $lists;
}

$lists = retrievelists($conn);
	
oci_close($conn);
	
?>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>PODAAC Mailing List Tool</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link rel="stylesheet" type="text/css" href="default.css" />
<style type="text/css">
<!--
body {
	background-color: #000000;
}
-->
</style></head>
<body>


<div id="outer">


	<div id="header">
		<div id="headercontent">
			<h1>PO.DAAC Mailing List Tool<sup>1.0</sup></h1>
			<h2> </h2>
	  </div>
	</div>

	<div id="headerpic"></div>

	
	<div id="menu">
		<!-- HINT: Set the class of any menu link below to "active" to make it appear active -->
		<ul>
			<li><a class="active">Send Mail </a></li>
			<li><a href="managelist.php">Manage Lists</a></li>
	  </ul>
	</div>
	<div id="menubottom"></div>

	
	<div id="content">

<form id="email" name="email" method="post" action="mailout.php">
		<div id="primarycontainer">
			<strong>Send E-Mail Announcment</strong><br />


<table width="700" height="446" border="0">

  <tr>
    <td height="21" colspan="2" align="left" valign="top"><br />
      Announcement Subject </td>
  </tr>
  <tr>
    <td height="21" colspan="2" align="left" valign="top"><input name="subject" type="text" value="PO.DAAC Announcement: &lt;replace with product&gt;" size="80" /></td>
  </tr>
  <tr>
    <td height="21" align="left" valign="top" class="style2"><br />
      Announcement Body </td>
    <td height="21" align="left" valign="top" class="style2"><br />
      &nbsp&nbsp&nbsp Target Mailing Lists </td>
  </tr>
  <tr>
    <td width="497" height="325" align="left" valign="top"><textarea name="body" cols="60" rows="20" id="body"></textarea></td>
    <td width="254" align="left" valign="top" class="style2"><p>
	  
	  <?
	  
	  foreach($lists as $list) {
	    echo "&nbsp&nbsp&nbsp&nbsp<input type=\"checkbox\" name=\"$list\" value=\"checkbox\" /> $list<br />";
	  }
	  
	  ?>

	  </p>
     </td>
  </tr>
  
  <tr>
    <td height="36" colspan="2"><input name="send" type="submit" id="send" value="Send Mail" /></td>
  </tr>
</table>


</div>
	 <div class="divider1"></div>
		
     </form>
     </div>
</div>

</body>
</html>
