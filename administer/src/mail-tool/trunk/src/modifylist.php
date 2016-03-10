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
$list = $_GET["list"];

function retrieveEmails ($conn, $list) {
  $emails = array();
  $query = "select email from $list";
  $result = oci_parse($conn, $query);
  oci_execute($result, OCI_DEFAULT);
  while ($list = oci_fetch_array($result, OCI_RETURN_NULLS)) {
    array_push($emails, $list[0]);
  }
  sort($emails);
  return $emails;
}

function deleteEntries ($conn, $list) {
  $emails = array();
  foreach(array_keys($_POST) as $key){
    if ($_POST[$key] != 'delete') {
      array_push($emails, $_POST[$key]);
    }
  }
  
  //Run Delete sql commands
  foreach($emails as $email) {
    $query = "delete from $list where email = '$email'";
    $result = oci_parse($conn, $query);
    oci_execute($result, OCI_DEFAULT);
  }
    
}

function createEntry ($conn, $list) {
  global $error;
  $email = $_POST["new"];
  $emails = retrieveEmails($conn,$list);
  //print_r($emails);
  if (array_search($email, $emails) === FALSE) {
    if (checkEmail($email) ) {
      $query = "insert into $list (id, email) values (email_id.NEXTVAL, '$email')";
      $result = oci_parse($conn, $query);
      oci_execute($result, OCI_DEFAULT);
    }
    else {
      $error = "INVALID EMAIL FORMAT ENTERED: $email";
    }
  }
  else {
    $error = "DUPLICATE EMAIL ENTERED: $email";
  }
}

function checkEmail($email) {
  // First, we check that there's one @ symbol, and that the lengths are right
  if (!ereg("^[^@]{1,64}@[^@]{1,255}$", $email)) {
    // Email invalid because wrong number of characters in one section, or wrong number of @ symbols.
    return false;
  }
  // Split it into sections to make life easier
  $email_array = explode("@", $email);
  $local_array = explode(".", $email_array[0]);
  for ($i = 0; $i < sizeof($local_array); $i++) {
    if (!ereg("^(([A-Za-z0-9!#$%&'*+/=?^_`{|}~-][A-Za-z0-9!#$%&'*+/=?^_`{|}~\.-]{0,63})|(\"[^(\\|\")]{0,62}\"))$", $local_array[$i])) {
      return false;
    }
  }
  if (!ereg("^\[?[0-9\.]+\]?$", $email_array[1])) { // Check if domain is IP. If not, it should be valid domain name
    $domain_array = explode(".", $email_array[1]);
    if (sizeof($domain_array) < 2) {
      return false; 
    }
    for ($i = 0; $i < sizeof($domain_array); $i++) {
      if (!ereg("^(([A-Za-z0-9][A-Za-z0-9-]{0,61}[A-Za-z0-9])|([A-Za-z0-9]+))$", $domain_array[$i])) {
	return false;
      }
    }
  }
  return true;
}


///////
// Figure out what action to take
///////

$error = "";

if (array_key_exists("delete", $_POST)) {
  deleteEntries($conn, $list);
}
else if (array_key_exists("create", $_POST)) {
  createEntry($conn, $list);
}

$emails = retrieveEmails($conn,$list);

oci_commit($conn);
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

error {
 color: #CC3333;
 font-size: 15px;
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
		<ul>
			<li><a href="mailinglist.php">Send Mail </a></li>
			<li><a href="managelist.php" class="active">Manage Lists</a></li>
	  </ul>
	</div>
	<div id="menubottom"></div>

	
	<div id="content">

		<div id="primarycontainer">
			<strong>Manage E-mails in <? echo $list ?></strong><br />
      
       <?
          if($error != "") {
	    echo "<br/><error><strong>$error</strong></error>";
	  }
       ?>


<table width="800" border="0">
  <tr>
    <td class="style4"><form id="form1" name="form1" method="post" action="modifylist.php?list=<?echo $list ?>">
      <br />Create New Entry : <br />
      <input name="new" type="text" size="50" />
      <input name="create" type="submit" id="create" value="Create" />
	    
     </form></td>
  </tr>
  <tr>
    <td height="80"><form id="form2" name="form2" method="post" action="modifylist.php?list=<?echo $list ?>">
      <p class="style4"><br/>Delete Entries : <br/>
      <span class="style5">

        <?
	  	foreach ($emails as $email) {
			echo "<input name=\"$email\" type=\"checkbox\" value=\"$email\" /> $email<br />";
		}
	  ?>

        <input type="submit" name="delete" value="Delete Checked Emails" />
      </span>
      </p>
	<a href="managelist.php">Back</a> to Managing Lists
	</form></td>
  </tr>
</table>



</div>

	 <div class="divider1"></div>
		
  
     </div>
</div>

</body>
</html>
