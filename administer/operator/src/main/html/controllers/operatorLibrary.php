<?php

include(dirname(__FILE__)."/../config/operatorConfig.php");
parse_domain();

function do_post_request_DEPRECATED($url, $data, $optional_headers = null) {
	$params = array('http' => array(
		'method' => 'POST',
		'content' => $data));
	if ($optional_headers!== null) {
		$params['http']['header'] = $optional_headers;
	}
	$ctx = stream_context_create($params);
	
	
	//$timeout = 5;
	//$old = ini_set('default_socket_timeout', $timeout);
	
	$fp = @fopen($url, 'rb', false, $ctx);
	if (!$fp) {
		throw new Exception("Problem with $url, $php_errormsg");
	}
	
	//ini_set('default_socket_timeout', $old);
	//stream_set_timeout($fp, $timeout);
	//stream_set_blocking($fp, 0);
	
	$response = @stream_get_contents($fp);
	if ($response === false) {
		throw new Exception("Problem reading data from $url, $php_errormsg");
	}
	return $response;
}


/* fully functioning curl request API. Returns response object with content and status */
function do_curl_request($url, $data, $request_type = null, $put_data=null) {
	$final_url = $url;
	
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url); 
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	if($request_type == "PUT") {
		curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "PUT");
		curl_setopt($ch, CURLOPT_POSTFIELDS,$put_data);
		curl_setopt($ch, CURLOPT_HTTPHEADER, array("Content-Type:application/octet-stream", "Content-length: ".strlen($put_data)));
	}
	else if($request_type == "DELETE") {
		curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "DELETE");	
	}
	else if($request_type == "GET") {
		curl_setopt($ch, CURLOPT_URL, $url."?".http_build_query($data));
	}
	else if($data != null && $data != ""){
		curl_setopt($ch, CURLOPT_POST, true);
		curl_setopt($ch, CURLOPT_POSTFIELDS,http_build_query($data));
	}
	$options = array(
    CURLOPT_FOLLOWLOCATION => true,     // follow redirects
    CURLOPT_AUTOREFERER    => true,     // set referer on redirect
    CURLOPT_CONNECTTIMEOUT => 120,      // timeout on connect
    CURLOPT_TIMEOUT        => 120,      //; timeout on response
    CURLOPT_MAXREDIRS      => 10,       // stop after 10 redirects
	);
	curl_setopt_array($ch, $options);

	$response = array();
	$response['content'] = curl_exec($ch);
	$response['status'] = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	curl_close($ch);
	
	return $response;
}

/* Function returns POST request content (no object) */

function do_post_request($url, $data) {
	$final_url = $url;
	
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url); 
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_POST, true);
	if($data != null && $data != ""){
		curl_setopt($ch, CURLOPT_POSTFIELDS,$data);
	}
	$options = array(
    CURLOPT_FOLLOWLOCATION => true,     // follow redirects
    CURLOPT_AUTOREFERER    => true,     // set referer on redirect
    CURLOPT_CONNECTTIMEOUT => 120,      // timeout on connect
    CURLOPT_TIMEOUT        => 120,      //; timeout on response
    CURLOPT_MAXREDIRS      => 10,       // stop after 10 redirects
	);
	curl_setopt_array($ch, $options);

	$response = curl_exec($ch);
	$status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	

	curl_close($ch);
	if($status != 200) {
		throw new Exception("Problem reading data from $url");
	}
	return $response;
}

/* same as above but uses GET */
function do_get_request($url, $data, $optional_headers = null) {
	$final_url = $url;
	
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url); 
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	if($data != null && $data != ""){
		curl_setopt($ch, CURLOPT_URL, $url."?".$data); 
	}
	$options = array(
    CURLOPT_FOLLOWLOCATION => true,     // follow redirects
    CURLOPT_AUTOREFERER    => true,     // set referer on redirect
    CURLOPT_CONNECTTIMEOUT => 120,      // timeout on connect
    CURLOPT_TIMEOUT        => 120,      //; timeout on response
    CURLOPT_MAXREDIRS      => 10,       // stop after 10 redirects
	);
	curl_setopt_array($ch, $options);

	$response = curl_exec($ch);
	$status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	

	curl_close($ch);
	if($status != 200) {
		throw new Exception("Problem reading data from $url");
	}
	return $response;
} 

function parse_domain() {
    global $OPERATOR_MANAGERS, $OPERATOR_ZOOKEEPER_URL, $OPERATOR_SOLR_URL, $OPERATOR_INVENTORY_URL, $OPERATOR_SIGEVENT_URL, $OPERATOR_AUTH_URL;
    $domain_file = dirname(__FILE__)."/../config/horizondomain.xml";
	if(file_exists($domain_file)) {
	    $domain_xml = simplexml_load_file($domain_file);
	    
	    $OPERATOR_ZOOKEEPER_URL = (!preg_match("/http/", $domain_xml->jobkeeper->webservice)) ? "http://".$domain_xml->jobkeeper->webservice : (string) $domain_xml->jobkeeper->webservice;
	    $OPERATOR_SOLR_URL = (!preg_match("/http/", $domain_xml->discovery)) ? "http://".$domain_xml->discovery : (string) $domain_xml->discovery;
			$OPERATOR_SIGEVENT_URL = (!preg_match("/http/", $domain_xml->sigevent)) ? "http://".$domain_xml->sigevent : (string) $domain_xml->sigevent;
			$OPERATOR_INVENTORY_URL = (!preg_match("/http/", $domain_xml->inventory)) ? "http://".$domain_xml->inventory : (string) $domain_xml->inventory;
			$OPERATOR_AUTH_URL = (!preg_match("/http/", $domain_xml->inventory)) ? "http://".$domain_xml->security : (string) $domain_xml->security;
	    $default = $domain_xml->default;
        foreach($domain_xml->federation as $federation) {
            $fed_array = array();
            $fed_array['name'] = (string) $federation->name;
            $fed_array['url'] = str_replace("/ingest", "", $federation->url);
            if(!preg_match("/localhost/", $fed_array['url']) ) {
                if($default == $fed_array['name'])
                    array_unshift($OPERATOR_MANAGERS, $fed_array);
                else    
                    array_push($OPERATOR_MANAGERS, $fed_array);
            }
        }
        //print_r($OPERATOR_MANAGERS);
	}
	else {
	    throw new Exception("Domain file does not exist!");
	}
}

function check_domain() {
	return file_exists(dirname(__FILE__)."/../config/horizondomain.xml");
}
?>
