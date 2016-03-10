<?

///////////////////////////////////
//CHANGE CUSTOMIZATION STUFF HERE//
///////////////////////////////////

$input = "/www/podaac/doc/internal/admin/mailinglist.txt";
$list = "GENERAL";

///////////////////////////////////

$user = "christian";
$pw = "chris_tian";
$db = "//seadb:1526/daacdev";
$conn = oci_connect($user,$pw,$db);


echo "OPENING $input<br/>";
$file = @fopen("$input", "r");
if ($file) {
    while (!feof($file)) {
        $buffer = fgets($file);
	if ($buffer != "") {
	  $buffer = rtrim($buffer);
	  createEntry($conn, $list, $buffer);
	}
    }
    fclose($file);
}
else {
  echo "COULDNT OPEN $input<br/>";
}

oci_commit($conn);
oci_close($conn);

function createEntry ($conn, $list, $email) {
  $emails = retrieveEmails($conn,$list);
  if (array_search($email, $emails) === FALSE) {
    if (checkEmail($email) ) {
      $query = "insert into $list (id, email) values (email_id.NEXTVAL, '$email')";
      $result = oci_parse($conn, $query);
      oci_execute($result, OCI_DEFAULT);
      echo "ADDED: $email<br/>";
    }
    else {
      echo "INVALID EMAIL FORMAT ENTERED: $email<br/>";
    }
  }
  else {
    echo "DUPLICATE EMAIL ENTERED: $email<br/>";
  }
}

function retrieveEmails ($conn, $list) {
  $emails = array();
  $query = "select email from $list";
  $result = oci_parse($conn, $query);
  oci_execute($result, OCI_DEFAULT);
  while ($list = oci_fetch_array($result, OCI_RETURN_NULLS)) {
    array_push($emails, $list[0]);
  }
  return $emails;
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


?>
