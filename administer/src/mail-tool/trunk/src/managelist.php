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
		
	  /////////////////////////
	  // Delete Later
	  /////////////////////////
	  if ($list[0] != 'EMAILADDRESS')
	    array_push($lists, $list[0]);
	}
	return $lists;
}

function deleteList ($conn) {
  $lists = array();
  foreach(array_keys($_POST) as $key){
    if ($_POST[$key] == 'check') {
      array_push($lists, $key);
    }
  }
  
  foreach($lists as $list) { 
  $query = "drop table $list";
  $result = oci_parse($conn, $query);
  oci_execute($result, OCI_DEFAULT);
  }
}

function createList ($conn) {
  global $error;
  $list = $_POST["new"];
  $list = strtoupper($list);
  $lists = retrievelists($conn);
  if (array_search($list, $lists) === FALSE) {
    if(ereg("^[A-Z][A-Z0-9]*", $list)) {
      $query = "create table $list (id number primary key, email varchar2(64))";
      $result = oci_parse($conn, $query);
      oci_execute($result, OCI_DEFAULT);
    }
    else {
      $error = "INVALID LIST NAME (please use alphanumeric characters only): $list";
    }
  }
  else {
    $error = "LIST ALREADY EXISTS: $list";
  }
}

///////
// Figure out what action to take
///////

$error = "";

if (array_key_exists("delete", $_POST)) {
  deleteList($conn);
}
else if (array_key_exists("create", $_POST)) {
  createlist($conn);
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
			<li><a class="active">Manage Lists</a></li>
	  </ul>
	</div>
	<div id="menubottom"></div>

	
	<div id="content">

		<div id="primarycontainer">
			<strong>Manage Mailing Lists</strong><br />

       <?
          if($error != "") {
	    echo "<br/><error><strong>$error</strong></error>";
	  }
       ?>

<table width="700" border="0">

    <tr>
      <td class="style4">
        <form id="form1" name="form1" method="post" action="managelist.php">
          <br />Create New List : <br />
          <input name="new" type="text" size="50" />
          <input name="create" type="submit" id="create" value="Create" />
                </form>      </td>
    </tr>
    <tr>
      <td height="80"><form id="form2" name="form2" method="post" action="managelist.php">
        <p class="style4"><br />Modify/Delete Lists : <br />
          <span class="style7">(Click on list to modify, check box to delete) </span><br/>
        <span class="style5">
          <?

	  	foreach ($lists as $list) {
			echo "<input name=\"$list\" type=\"checkbox\" value=\"check\" /><a href=\"modifylist.php?list=$list\"> $list</a><br />";
		}

	  ?>
          <input type="submit" name="delete" value="Delete Checked Lists" />
        </span></p>
	  </form>
      </td>
    </tr>
  </table>



</div>
	 <div class="divider1"></div>
		
   
     </div>
</div>

</body>
</html>
