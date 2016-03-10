<?php
function do_post_request($url, $data, $optional_headers = null) {
	$final_url = $url;
	
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url); 
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	curl_setopt($ch, CURLOPT_POST, true);
	if($data != null && $data != ""){
		curl_setopt($ch, CURLOPT_POSTFIELDS,$data);
	}

	$response = curl_exec($ch);
	$status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	

	curl_close($ch);
	if($status != 200) {
		throw new Exception("Problem reading data from $url");
	}
	return $response;
} 

function do_get_request($url, $data, $optional_headers = null) {
	$final_url = $url;
	
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url); 
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
	if($data != null && $data != ""){
		curl_setopt($ch, CURLOPT_URL, $url."?".$data); 
	}

	$response = curl_exec($ch);
	$status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	

	curl_close($ch);
	if($status != 200) {
		throw new Exception("Problem reading data from $url");
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

	$response = array();
	$response['content'] = curl_exec($ch);
	$response['status'] = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	curl_close($ch);
	
	return $response;
}

function getTemplate($table) {
	global $INVENTORY_URL;
	global $INVENTORY_PROCESS;
	$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<$table type=\"template\"></$table>";
	$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
	$manifest = simplexml_load_string(simplexml_load_string($response)->Content);
	return $manifest;
}

function granuleCheck($dataset) {
    global $INVENTORY_URL;
    $granuleURL = "$INVENTORY_URL/dataset/$dataset/granuleList";
    $responseObject = do_curl_request($granuleURL, array());
    if(preg_match("Dataset\:.*", $responseObject->content)) {
        return true;
    }
    else {
        $xml = simplexml_load_string($responseObject[content]);
        $granules = array();
        foreach($xml->integer as $granule) {
            $granules[] = "$granule";
        }
        if(sizeof($granules) > 0) {
            return true;
        }
        else return false;
    }
}

function getLength($fieldName) {
	global $length_lookup;
	$fields = array_keys($length_lookup);
	$match = "";
	foreach ($fields as $field) {
		preg_match("/$field/", $fieldName, $matches);
		if (isset($matches[0])) $match = $matches[0];
	}
	return isset($length_lookup[$match]) ? $length_lookup[$match] : 99999;
}

function getFieldList($manifest, $fieldType) {
	$fieldList = array();
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		if(preg_match("/($fieldType\_[A-Za-z]+)1$/", $fieldName, $matches) || preg_match("/($fieldType\_granuleRange360)1$/", $fieldName, $matches)) {
			$fieldList[] = $matches[1];
		}
	}
	return $fieldList;
}

function getFieldListSingle($manifest, $fieldType) {
	$fieldList = array();
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		if(preg_match("/($fieldType\_.*)$/", $fieldName, $matches)) {
			$fieldList[] = $matches[1];
		}
	}
	return $fieldList;
}

function getIdList($manifest, $fieldType) {
	$fieldList = array();
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		if(preg_match("/^$fieldType([0-9]+)$/", $fieldName, $matches)) {
			$fieldList[] = $matches[1];
		}
	}
	return $fieldList;

}

function getFieldValue($manifest, $fieldNameTarget) {
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		if(preg_match("/^$fieldNameTarget$/", $fieldName)) {
			return "$field";
		}
	}
}

function getFieldArray($manifest, $fieldNameTarget) {
	$fieldArray = array();
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		$value = "$field";
		$required =  $field['required'];
		$fieldType = $field['type'];
		if(preg_match("/^$fieldNameTarget$/", $fieldName)) {
			$fieldArray = array("name" => (string)$fieldName, "value" => (string)$value, "required" => (string)$required, "type"=>(string)$fieldType);
		}
	}
	return $fieldArray;
}

function getElementId($manifest, $deId) {
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		$value = "$field";
		if(preg_match("/^datasetElement\_deId([0-9]+)$/", $fieldName, $matches)) {
			if($value == $deId) 
				return getFieldValue($manifest, "datasetElement_elementId".$matches[1]);
		}
	}
}

function getDatasetElementValueArray($manifest, $deId) {
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		$value = "$field";
		if(preg_match("/^(dataset(Character|Real|DateTime|Integer|Spatial))\_deId([0-9]+)$/", $fieldName, $matches)) { 
			if($value == $deId) {
				return $matches;
			}
		}
	}
}

function getElementArray($elementId) {
	global $INVENTORY_URL;
	global $INVENTORY_PROCESS;
	$finalArray = array(); 
	$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<element type=\"list\"><field name=\"object_id\">$elementId</field></element>";
	$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
	$manifest = simplexml_load_string(simplexml_load_string($response)->Content);
	foreach($manifest->field as $field) {
		$key = (string)$field['name'];
		$finalArray[$key] = "$field";
	}
	return $finalArray;
}

function createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value, $readOnly = false, $onChangeJS="") {
    $readOnlyText = ($readOnly) ? "			readonly=\"$readOnly\"\n" : "";
	print "<tr>\n";
	print "		<td style=\"min-width:100px\"><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><input dojoType=\"dijit.form.ValidationTextBox\"\n";
	print "			id=\"$fieldName\"\n";
	print "			maxLength=$maxLength\n";
	print "			name=\"$fieldName\"\n";	
	print "			value=\"$value\"\n";	
	print "			required=\"$required\"";
	if($onChangeJS != "")
		print "		onChange=\"$onChangeJS\"\n";
	if ($required == "true")
		print "\n			invalidMessage=\"Required.\"";
    if ($readOnly) {
        print "\n			readonly=\"true\"\n";
    }
	print ">\n";
	if ($tooltip != "") print "		    <span dojoType=\"dijit.Tooltip\" connectId=\"$fieldName\">$tooltip</span>\n";
	print "		</td>\n";
	print "</tr>\n";
}

function createLargeCharField($fieldName, $required, $maxLength, $label, $tooltip, $value) {
	print "<tr>\n";
	print "		<td><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><input dojoType=\"dijit.form.Textarea\"\n";
	print "			id=\"$fieldName\"\n";
	print "			maxLength=$maxLength\n";
	print "			name=\"$fieldName\"\n";	
	print "			value=\"$value\"\n";
	print "			required=\"$required\"";
	if ($required == "true")
		print "\n			invalidMessage=\"Required.\"";
	print ">\n";
	if ($tooltip != "") print "		    <span dojoType=\"dijit.Tooltip\" connectId=\"$fieldName\">$tooltip</span>\n";
	print "		</td>\n";
	print "</tr>\n";
}

function createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value) {
	global $unit_lookup;
	$units = $unit_lookup[(string) $fieldName];
	$label = !isset($units) ? $label : $label." ($units)";
	print "<tr>\n";
	print "		<td><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><input dojoType=\"dijit.form.NumberTextBox\"\n";
	print "			id=\"$fieldName\"\n";
	print "			maxLength=$maxLength\n";
	print "			constraints=\"{type:'decimal', round:'-1'}\"\n";
	//print "			regExp=\"[\\d\]+\.*\"\n";
	print "			name=\"$fieldName\"\n";	
	print "			value=\"$value\"\n";
	print "			required=\"$required\"";
	if ($required == "true")
		print "\n			invalidMessage=\"Required.\"";
	print ">\n";
	if ($tooltip != "") print "		    <span dojoType=\"dijit.Tooltip\" connectId=\"$fieldName\">$tooltip</span>\n";
	print "		</td>\n";
	print "</tr>\n";
}

function createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value) {
	print "<tr>\n";
	print "		<td><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><select dojoType=\"dijit.form.ComboBox\"\n";
	print "			id=\"$fieldName\"\n";
	print "			maxLength=$maxLength\n";
	print "			name=\"$fieldName\"\n";	
	print "			value=\"$value\"\n";
	print "			required=\"$required\"";
	if ($required == "true")
		print "\n			invalidMessage=\"Required.\"";
	print ">\n";
		
	foreach($options as $option) {
		print "			<option>$option</option>\n";
	}	
	print "		   </select>";
	if ($tooltip != "") print "		    <span dojoType=\"dijit.Tooltip\" connectId=\"$fieldName\">$tooltip</span>\n";
	print "		</td>\n";
	print "</tr>\n";
}

function createDateField($fieldName, $required, $maxLength, $label, $tooltip, $value, $onChangeJS="") {
	
	///////////////////////// CHANGE THIS EVENTUALLY!!!!!!!!!!!
	//print "datetime is $value\n";
	if($value == "") {
		$dateValue = "";
		$timeValue = "";
	}
	else {
		
		//print "passed value is $value";	
		$value = (int)$value / 1000;
		//$value = (int)$value;
		$dateValue = date("Y-m-d", $value);
		$timeValue = date("\TH:i:s", $value);
		//$dateValue = $value;
		//$timeValue = $value;
	}
	//print "date is $dateValue and time is $timeValue\n";
	print "<tr>\n";
	print "		<td><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><input dojoType=\"dijit.form.DateTextBox\"\n";
	print "			id=\"$fieldName"."DATE\"\n";
	print "			maxLength=$maxLength\n";
	if($onChangeJS != "")
		print "		onChange=\"$onChangeJS\"\n";
	print "			name=\"$fieldName"."DATE\"\n";	
	print "			value=\"$dateValue\"\n";
	print "			required=\"$required\"";
	if ($required == "true")
		print "\n			invalidMessage=\"Required.\"";
	print ">\n";
	print "		   <input dojoType=\"dijit.form.TimeTextBox\"\n";
	print "			id=\"$fieldName"."TIME\"\n";
	print "			maxLength=$maxLength\n";
	print "			name=\"$fieldName"."TIME\"\n";	
	print "			value=\"$timeValue\"\n";
	print "			required=\"$required\"";
	if ($required == "true")
		print "\n		invalidMessage=\"Required.\"";
	print "				constraints= \"{\n";
        print "      			 	timePattern: 'HH:mm:ss',\n";
        print "       			 	clickableIncrement: 'T00:15:00',\n";
        print "     			  	visibleIncrement: 'T00:15:00',\n";
        print "       				visibleRange: 'T01:00:00'}\"\n";
	print ">\n";
	if ($tooltip != "") print "		    <span dojoType=\"dijit.Tooltip\" connectId=\"$fieldName\">$tooltip</span>\n";
	print "		</td>\n";
	print "</tr>\n";
}

function createDateOnlyField($fieldName, $required, $maxLength, $label, $tooltip, $value) {
	
	///////////////////////// CHANGE THIS EVENTUALLY!!!!!!!!!!!
	//print "datetime is $value\n";
	if($value == "") {
		$dateValue = "";
	}
	else {
		
		//print "passed value is $value";	
		$value = (int)$value  / 1000;
		$dateValue = date("Y-m-d", $value);
	}
	//print "date is $dateValue and time is $timeValue\n";
	print "<tr>\n";
	print "		<td><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><input dojoType=\"dijit.form.DateTextBox\"\n";
	print "			id=\"$fieldName"."DATE\"\n";
	print "			maxLength=$maxLength\n";
	print "			name=\"$fieldName"."DATE\"\n";	
	print "			value=\"$dateValue\"\n";
	print "			required=\"$required\"";
	if ($required == "true")
		print "\n			invalidMessage=\"Required.\"";
	print ">\n";
	if ($tooltip != "") print "		    <span dojoType=\"dijit.Tooltip\" connectId=\"$fieldName\">$tooltip</span>\n";
	print "		</td>\n";
	print "</tr>\n";
}

function createFilteringSelectField($fieldName,$required, $label,$store,$searchAttr, $value) {
	print "<tr>\n";
	print "		<td><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><select dojoType=\"dijit.form.FilteringSelect\" 
						style=\"width: 10em;\"
						id=\"$fieldName\" 
						name=\"$fieldName\" 
						store=\"$store\"
						searchAttr=\"$searchAttr\" 
						labelAttr=\"$searchAttr\"
						required=\"$required\"></select>";
	print "		</td>\n";
	print "</tr>\n";
}

function createHiddenField($fieldName, $maxLength, $value) {
	print "		<input dojoType=\"dijit.form.TextBox\" type=\"hidden\" id=\"$fieldName\" value=\"$value\" name=\"$fieldName\"/>\n";
}

function createUneditableTextField($fieldName, $label, $value) {
	print "<tr>\n";
	print "		<td style=\"min-width:100px\"><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><div id=\"$fieldName\">$value</div>";
	print "		</td>\n";
	print "</tr>\n";
}

function createPersistentIdField($fieldName, $required, $maxLength, $label, $tooltip, $value, $readOnly = false, $onChangeJS="") {
	print "<tr>\n";
	print "		<td style=\"min-width:100px\"><label for=\"$fieldName\">$label:</label></td>\n";
	print "		<td><input dojoType=\"dijit.form.ValidationTextBox\"\n";
	print "			id=\"$fieldName\"\n";
	print "			maxLength=$maxLength\n";
	print "			name=\"$fieldName\"\n";	
	print "			value=\"$value\"\n";	
	print "			required=\"$required\"\n";
	if($onChangeJS != "")
		print "		onChange=\"$onChangeJS\"\n";
	if ($readOnly) {
        print "			readonly=\"true\"\n";
    }
	print "			invalidMessage=\"Must be all uppercase letters<br/> or numbers with the following <br/>format: XXXXXX-XXXXX-XXXXX.\"\n";
	print "			regExp=\"[A-Z]{6}-[A-Z0-9]{5}-[A-Z0-9]{5}\"\n";
	print ">\n";
	if ($tooltip != "") print "		    <span dojoType=\"dijit.Tooltip\" connectId=\"$fieldName\">$tooltip</span>\n";
	print "		</td>\n";
	print "</tr>\n";
}

function getLabel($name) {
	preg_match_all("/^.*_([A-Za-z]*)/", $name, $match);
	$fieldLabel = $match[1][0];
	preg_match_all("/^([a-z]*).*/", $fieldLabel, $first);
	preg_match_all("/([A-Z][a-z]*)/", $fieldLabel, $rest);
	$label = ucfirst($first[1][0]);
	foreach ($rest[1] as $word)
		$label = $label." ".$word;
	return $label;
}

function datasetSort($a, $b) {
	if($a['name'] == "dataset_shortName")
		return -1;
	elseif($b['name'] == "dataset_shortName")
		return 1;
	elseif($a['name'] == "dataset_longName")
		return -1;
	elseif($b['name'] == "dataset_longName")
		return 1;
	elseif($a['name'] == "dataset_persistentId")
		return -1;
	elseif($b['name'] == "dataset_persistentId")
		return 1;		
	else return strcmp($a['name'], $b['name']);
}

function printDatasetTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	//print_r($sorted);
	foreach($sorted as $field) {
		$value = htmlentities("$field", ENT_QUOTES);
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";
	
		if($label == "Metadata") $label = "Keywords (Comma separated tags)";

		if(preg_match("/^dataset\_.*/",$fieldName)) {
			if($fieldName == "dataset_datasetId") {
				if($_GET['requestType'] == "copy") 
					$value = "";
				createHiddenField($fieldName, $maxLength, $value);
			}
			elseif($fieldName == "dataset_longName"  && $_GET['requestType'] == "copy")
			{
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, "");
			}
			elseif($fieldName == "dataset_shortName" && ($_GET['requestType'] == "copy" || $_GET['requestType'] == "new"))
			{
			    createCharField($fieldName, $required, $maxLength, $label, $tooltip, "", false, "validateShortName");
			}
			elseif($fieldName == "dataset_shortName" && !$_SESSION['dmt-admin'] && $_GET['requestType'] != "new" && $_GET['requestType'] != "copy" ) {
			    createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value, true, "validateShortName");
			}
			elseif($fieldName == "dataset_shortName")
			{
			    createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value, false, "validateShortName");
			}
			elseif($fieldName == "dataset_description") {
				createLargeCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			}
			elseif($fieldName == "dataset_remoteDataset") {
				$options = array("L", "R", "C");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);				
			}
			elseif($fieldName == "dataset_persistentId") {
			    $readOnly = (!$_SESSION['dmt-admin'] && $_GET['requestType'] != "new" && $_GET['requestType'] != "copy");
			    $value = ($_GET['requestType'] == "copy") ? "" : $value;
				createPersistentIdField($fieldName, $required, $maxLength, $label, $tooltip, $value, $readOnly, "validatePersistentID");
			}
			elseif($fieldType == "datetime") 
				createDateField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldName != "dataset_providerId") {
				if($fieldType == "char")
					createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
				elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
					createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
				elseif($fieldType == "datetime")
					createDateField($fieldName, $required, $maxLength, $label, $tooltip, $value);
				else
					createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			}
		}
		//elseif(preg_match("/datasetElement\_.*/", $fieldName))
				//createHiddenField($fieldName, $maxLength, $value);
	}
}

function printDatasetPolicyTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field", ENT_QUOTES);
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";
		//print "$fieldName $value\n";
		if(preg_match("/datasetPolicy\_.*/",$fieldName)) {
			if($fieldName == "datasetPolicy_dataClass") {
				$options = array("ARCHIVE-DIST", "ARCHIVE-ONLY", "DIST-ONLY", "ROLLING-STORE", "SANDBOX", "REMOTE-DIST");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_accessType") {
				$options = array("PREVIEW", "OPEN", "RETIRED", "SHARED", "CONTROLLED", "DORMANT", "REMOTE");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_basePathAppendType") {
				$options = array("BATCH", "CYCLE", "NONE", "YEAR", "YEAR-DOY", "YEAR-MONTH", "YEAR-WEEK", "YEAR-MONTH-DAY");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_dataFormat") {
				$options = array("RAW", "HDF", "HDF5", "NETCDF", "GRIB", "ASCII", "GRIB", "JPG", "TIFF", "GeoTIFF", "GIF", "PNG");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_compressType") {
				$options = array("BZIP2", "GZIP", "NONE", "ZIP");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_checksumType") {
				$options = array("MD2", "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_spatialType") {
				$options = array("NONE", "ORACLE", "BACKTRACK");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_viewOnline") {
				$options = array("Y", "N");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldName == "datasetPolicy_dataFrequency" || $fieldName == "datasetPolicy_dataLatency" || $fieldName == "datasetPolicy_dataVolume" || $fieldName == "datasetPolicy_dataDuration") {
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			}
			else {
				if($fieldType == "char")
					createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
				elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
					createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
				elseif($fieldType == "datetime")
					createDateField($fieldName, $required, $maxLength, $label, $tooltip, $value);
				else
					createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			}
		}
	}
}

function printDatasetCoverageTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";

		if(preg_match("/^datasetCoverage\_.*/",$fieldName)) {
			if($fieldType == "char")
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldName == "datasetCoverage_startTime")
				createDateField($fieldName, $required, $maxLength, $label, $tooltip, $value, "dijit.byId('datasetCoverage_stopTimeDATE').constraints.min = arguments[0];");
			elseif($fieldName == "datasetCoverage_stopTime")
				createDateField($fieldName, $required, $maxLength, $label, $tooltip, $value, "dijit.byId('datasetCoverage_startTimeDATE').constraints.max = arguments[0];");
			elseif($fieldType == "datetime")
				createDateField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printDatasetCitationTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";
		if(preg_match("/^datasetCitation\_.*/",$fieldName)) {
			if($fieldType == "datetime")
				createDateOnlyField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "char")
				 createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			else 
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printProjectTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";

		if(preg_match("/project_.*/",$fieldName)) {
			if($fieldName == "project_projectId")
				createUneditableTextField($fieldName, $label, $value);
			elseif($fieldType == "char")
				 createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			else 
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printProviderTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";

		if(preg_match("/^provider\_.*/",$fieldName)) {
			if($fieldName == "provider_providerId")
				createUneditableTextField($fieldName, $label, $value);
			elseif($fieldName == "provider_type") {
				$options = array("DATA-PROVIDER", "DATA-CENTER");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif(!preg_match("/.*path.*/", $fieldName))
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printCollectionTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";

		if(!preg_match("/collectionDataset\_.*/",$fieldName)) {
			if($fieldName == "collection_collectionId")
				createUneditableTextField($fieldName, $label, $value);
			else if($fieldName == "collectionProduct_visibleFlag"){
				$options = array("N", "Y");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			else if($fieldName == "collection_aggregate"){
				$options = array("N", "Y");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);
			}
			else if($fieldName == "collectionContact_contactId#")
				createFilteringSelectField("collectionContact_contactId1",$required, $label, "contactStore","contact_name", $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif(!preg_match("/.*path.*/", $fieldName))
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printElementTable($manifest) {
	foreach($manifest->field as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip = "";

		if(preg_match("/element\_.*/",$fieldName)) {
			if($fieldName == "element_elementId")
				createUneditableTextField($fieldName,$label, $value);
			elseif($fieldName == "element_type") {
				$options = array("character", "integer", "real", "date");				
				createComboField($fieldName, $required, $maxLength, $label, $tooltip, $options, $value);	
			}
			elseif($fieldType == "char")
				 createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			else 
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printSourceTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip ="";

		if(preg_match("/source_.*/",$fieldName)) {
			if($fieldName == "source_sourceId")
				createUneditableTextField($fieldName,$label, $value);
			elseif($fieldType == "char")
				 createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			else 
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printSensorTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip ="";

		if(preg_match("/sensor_.*/",$fieldName)) {
			if($fieldName == "sensor_sensorId")
				createUneditableTextField($fieldName,$label, $value);
			elseif($fieldType == "char")
				 createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			else 
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}

function printContactTable($manifest) {
	foreach($manifest->field as $field) {
		$sorted[] = $field;
	}
	usort($sorted, "datasetSort");
	foreach($sorted as $field) {
		$value = htmlentities("$field");
		if($value == "null") $value = "";
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		$label = getLabel($fieldName);
		$tooltip ="";

		if(preg_match("/contact_.*/",$fieldName)) {
			if($fieldName == "contact_contactId")
				createUneditableTextField($fieldName,$label, $value);
			elseif($fieldName == "contact_providerId")
				createFilteringSelectField($fieldName,$required, $label, "providerStore","provider_shortName", $value);
// DELETE THIS

			elseif($fieldName == "contact_provider")
				createFilteringSelectField("contact_providerId",$required, $label, "providerStore","provider_shortName", $value);
			elseif($fieldType == "char")
				 createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			elseif($fieldType == "int" || $fieldType == "float" || $fieldType == "double")
				createNumberField($fieldName, $required, $maxLength, $label, $tooltip, $value);
			else 
				createCharField($fieldName, $required, $maxLength, $label, $tooltip, $value);
		}
	}
}


///////////////////////////
//JAVASCRIPT GENERATION (for dynamic creation buttons)
///////////////////////////

function printExistingDatasetElementJS($manifest) {
	global $INVENTORY_URL, $INVENTORY_PROCESS;
	$elementProperties = getFieldList($manifest, "datasetElement");
	$ids = getIdList($manifest, $elementProperties[0]);
	sort($ids);
	//print($manifest->asXML());
	
    $globalAdminFlag = ($_SESSION['dmt-admin'] && !granuleCheck($_GET['dataset_datasetId']));
	
	foreach($ids as $id) {
		$obligationFlag = getFieldValue($manifest, "datasetElement_obligationFlag$id");
		$deId = getFieldValue($manifest, "datasetElement_deId$id");
		$scope = getFieldValue($manifest, "datasetElement_scope$id");
		$elementId = getFieldValue($manifest, "datasetElement_elementId$id");
		
		$params = "userName=".$_SESSION['dmt-username']."&password=".$_SESSION['dmt-password']."&manifest=<element type=\"list\"><field name=\"object_id\">$elementId</field></element>";
		$response = do_post_request("$INVENTORY_URL/$INVENTORY_PROCESS", $params);
		$listXML = simplexml_load_string(simplexml_load_string($response)->Content);
		//print $listXML->asXML();
		
		$results = array();
		foreach($listXML->field as $field) {
			$tmpValue = "$field";
			$fieldName = $field['name'];
			$required = $field['required'];
			$results[] = array("field" => "$fieldName", "value" => "$tmpValue");
			if($fieldName == "element_type") {
				$elementType = $tmpValue;
			}
		}
		if($elementType == "character") {
			$elementType = "Character";
		}
		else if($elementType == "integer") {
			$elementType = "Integer";
		}
		else if($elementType == "real") {
			$elementType = "Real";
		}
		else if($elementType == "date" || $elementType == "datetime" || $elementType == "time") {
			$elementType = "DateTime";	
		}		
		else if($elementType == "spatial") {
		    $elementType = "Spatial";
		}

		$data = "{ \"items\": \n";
		$data = $data.json_encode($results);
		$data = $data. "}";

		//////////////////////// CHANGE!!!!! MUST GIVE A PROPER VALUE
		$valueDeId = getDatasetElementValueArray($manifest, $deId);
		if($deId == "") {
			//New element! search by elementId
			foreach($manifest->field as $field) {
				$fieldValue = htmlentities("$field");
				if($fieldValue == "null") $fieldValue = "";
				$fieldName = $field['name'];
				if(preg_match("/dataset".$elementType."_elementId(.*)/", $fieldName, $matches)) {
					if($fieldValue == $elementId) {
						$value = getFieldValue($manifest, "dataset".$elementType."_value".$matches[1]);
						$units = getFieldValue($manifest, "dataset".$elementType."_units".$matches[1]);
					}
				}
			}
		}
		else {
			//Exisiting element, search by deId
			foreach($manifest->field as $field) {
				$fieldValue = htmlentities("$field");
				if($fieldValue == "null") $fieldValue = "";
				$fieldName = $field['name'];
				if(preg_match("/dataset".$elementType."_deId(.*)/", $fieldName, $matches)) {
					if($fieldValue == $deId) {
						$value = getFieldValue($manifest, "dataset".$elementType."_value".$matches[1]);
						$units = getFieldValue($manifest, "dataset".$elementType."_units".$matches[1]);
					}
				}
			}
		}
		$value = str_replace("\n","\\n", $value);
	
		$adminFlag = ($globalAdminFlag || $scope == "DATASET" || $deId == "") ;
		$tempFlag = $scope == "DATASET";
		$adminFlag = ($adminFlag) ? "true": "false";
				
		print "var num = elementNum;\n";
		print "var elementData = $data;\n";
		print "handleNewElement(elementData, null, ".$adminFlag.");\n";
		if($deId != "") {
			print "dijit.byId(\"datasetElement_deId\"+num).attr(\"disabled\", false);\n";
			print "dijit.byId(\"datasetElement_deId\"+num).attr(\"value\", \"$deId\");\n";
		}
		print "dijit.byId(\"datasetElement_scope\"+num).attr(\"value\", \"$scope\");\n";
		print "dijit.byId(\"datasetElement_obligationFlag\"+num).attr(\"value\", \"$obligationFlag\");\n";
		if($scope == "DATASET" || $scope == "BOTH")  {
			if($deId == "") {
				print "dijit.byId(\"dataset".$elementType."_elementId\"+num).attr(\"disabled\", false);\n";
				print "dijit.byId(\"dataset".$elementType."_elementId\"+num).attr(\"value\", \"$elementId\");\n";
			}
			else {
				print "dijit.byId(\"dataset".$elementType."_deId\"+num).attr(\"disabled\", false);\n";
				print "dijit.byId(\"dataset".$elementType."_deId\"+num).attr(\"value\", \"$deId\");\n";
			}
			print "dojo.byId(\"elementValueLabel\"+num).style.display = 'block';\n";
			if($elementType == "DateTime") {
				print "dijit.byId(\"dataset".$elementType."_value\"+num+\"DATE\").domNode.style.display = 'block';\n";
				print "dijit.byId(\"dataset".$elementType."_value\"+num+\"DATE\").attr(\"disabled\", false);\n";
				print "dijit.byId(\"dataset".$elementType."_value\"+num+\"DATE\").attr(\"value\", \"$value\");\n";
				print "dijit.byId(\"dataset".$elementType."_value\"+num+\"TIME\").domNode.style.display = 'block';\n";
				print "dijit.byId(\"dataset".$elementType."_value\"+num+\"TIME\").attr(\"disabled\", false);\n";
				print "dijit.byId(\"dataset".$elementType."_value\"+num+\"TIME\").attr(\"value\", \"$value\");\n";
			}	
			else {
				print "dijit.byId(\"dataset".$elementType."_value\"+num).domNode.style.display = 'block';\n";
				print "dijit.byId(\"dataset".$elementType."_value\"+num).attr(\"disabled\", false);\n";
				print "dijit.byId(\"dataset".$elementType."_value\"+num).attr(\"value\", \"$value\");\n";
			}
			if($elementType == "Integer" || $elementType == "Real") {
				print "dojo.byId(\"elementUnitsLabel\"+num).style.display = 'block';\n";
				print "dijit.byId(\"dataset".$elementType."_units\"+num).domNode.style.display = 'block';\n";
				print "dijit.byId(\"dataset".$elementType."_units\"+num).attr(\"disabled\", false);\n";
				print "dijit.byId(\"dataset".$elementType."_units\"+num).attr(\"value\", \"$units\");\n";
			}
		}	// if($scope == "DATASET" || $scope == "BOTH") 
	} //foreach($ids as $id) 

/*
    [element_type] => character
    [element_maxLength] => 
    [element_shortName] => regionDetail
    [element_description] => More specific names not specified in the GCMD valids list.
    [element_longName] => region detail
    [element_scope] => null
    [element_elementId] => 49
*/


	/*
	$maxLength = 900000;
	$elementTypes = array("datasetCharacter", "datasetReal", "datasetDateTime", "datasetInteger", "datasetSpatial");
	foreach($elementTypes as $elementType) {
		$fieldList = getFieldList($manifest, $elementType);
		$idList = getIdList($manifest, $fieldList[0]);	
		foreach($idList as $id) {
			$deId = getFieldValue($manifest, $elementType."_deId".$id);
			$value = getFieldValue($manifest, $elementType."_value".$id);
			$elementId = getElementId($manifest, $deId);
			$elementArray = getElementArray($elementId);
			if($elementType=="datasetCharacter") {
				createCharField($elementType."_value".$id, "true", $maxLength, $elementArray['element_longName'],$elementArray['element_description'] , $value);
			}
			elseif($elementType=="datasetReal" || $elementType=="datasetInteger") {
				createNumberField($elementType."_value".$id, "true", $maxLength, $elementArray['element_longName'],$elementArray['element_description'] , $value);
			}
			elseif($elementType == "datasetDateTime") {
				createDateField($elementType."_value".$id, "true", $maxLength, $elementArray['element_longName'],$elementArray['element_description'] , $value);
			}
			createHiddenField($elementType."_deId".$id, $maxLength, $deId);
				
		}

	}
	*/
}


function printExistingDatasetProjectJS($manifest) {
	$value = getFieldValue($manifest, "datasetProject_projectId");
	if($value == "null") $value = "";
	$value = str_replace("\n","\\n", $value);
	print "dijit.byId(\"datasetProject_projectId\").attr(\"value\", \"$value\");\n";
}

function printExistingDatasetProviderJS($manifest) {
	$value = getFieldValue($manifest, "dataset_providerId");
	if($value == "null") $value = "";
	$value = str_replace("\n","\\n", $value);
	print "dijit.byId(\"dataset_providerId\").attr(\"value\", \"$value\");\n";
}

function printExistingDatasetSourceSensorJS($manifest) {
	$fieldList = getFieldList($manifest, "datasetSource");
	$idList = getIdList($manifest, $fieldList[0]);
	foreach($idList as $id) {	
		print "var num = sensorNum;\n";
		print "addNewSourceSensorRow();\n";
		foreach($fieldList as $field) {
			$value = getFieldValue($manifest, "$field"."$id");
			$value = str_replace("\n","\\n", $value);
			if($value == "null") $value = "";
			print "dijit.byId(\"$field\"+num).attr(\"value\", \"$value\");\n";

		}
	}
}

function printExistingDatasetContactJS($manifest) {
	$fieldList = getFieldList($manifest, "datasetContact");
	$idList = getIdList($manifest, $fieldList[0]);
	foreach($idList as $id) {	
		print "var num = contactNum;\n";
		print "createNewContact();\n";
		foreach($fieldList as $field) {
			$value = getFieldValue($manifest, "$field"."$id");
			$value = str_replace("\n","\\n", $value);
			if($value == "null") $value = "";
			print "dijit.byId(\"$field\"+num).attr(\"value\", \"$value\");\n";

		}
	}
}

function printExistingDatasetResourceJS($manifest) {
	$fieldList = getFieldList($manifest, "datasetResource");
	if(isset($fieldList[0])) {
		$idList = getIdList($manifest, $fieldList[0]);
		foreach($idList as $id) {	
			print "var num = resourceNum;\n";
			print "createNewResource();\n";
			foreach($fieldList as $field) {
				$value = getFieldValue($manifest, "$field"."$id");
				if($value == "null") $value = "";
				$value = str_replace("\n","\\n", $value);
				print "dijit.byId(\"$field\"+num).attr(\"value\", \"$value\");\n";

			}
		}
	}
}

function printDatasetParameterJS($manifest) {
	$order = array("datasetParameter_category#", "datasetParameter_topic#", "datasetParameter_term#", "datasetParameter_variable#", "datasetParameter_variableDetail#");
	foreach($order as $searchName) {
		foreach($manifest->field as $field) {
			$fieldName = $field['name'];
			$fieldType = $field['type'];
			$required = $field['required'];
			$maxLength = getLength($fieldName);
			if(preg_match("/^(datasetParameter_.*)#$/", $fieldName, $matches) && $fieldName == $searchName) {
				$fieldName = $matches[1];
				if(preg_match("/datasetParameter\_topic[0-9]*/", $fieldName)) {
					print "var topicLabel = new dojo.create(\"div\", {innerHTML:\"Topic Name (from GCMD list):  \", style:\"float:none;\"});\n";
					print "var topic = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", store: GCMDTopicStore, searchAttr:\"value\", labelAttr:\"value\", required:$required, value:\"\", id:\"$fieldName\"+num, name:\"$fieldName\"+num });\n";
					print "labels.push(topicLabel);\n";
					print "inputs.push(topic.domNode);\n";	
				}
				elseif(preg_match("/datasetParameter\_term[0-9]*/", $fieldName)) {
					print "var topicLabel = new dojo.create(\"div\", {innerHTML:\"Term (from GCMD list):  \", style:\"float:none;\"});\n";
					print "var topic = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", store: GCMDTermStore, searchAttr:\"value\", labelAttr:\"value\", required:$required, value:\"\", id:\"$fieldName\"+num, name:\"$fieldName\"+num });\n";
					print "labels.push(topicLabel);\n";
					print "inputs.push(topic.domNode);\n";	
				}
				elseif(preg_match("/datasetParameter\_variableDetail[0-9]*/", $fieldName)) {
					print "var topicLabel = new dojo.create(\"div\", {innerHTML:\"Variable Detail (from GCMD list):  \", style:\"float:none;\"});\n";
					print "var topic = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", store: GCMDVariableDetailStore, searchAttr:\"value\", labelAttr:\"value\", required:$required, value:\"\", id:\"$fieldName\"+num, name:\"$fieldName\"+num });\n";
					print "labels.push(topicLabel);\n";
					print "inputs.push(topic.domNode);\n";	
				}
				elseif(preg_match("/datasetParameter\_variable[0-9]*/", $fieldName)) {
					print "var topicLabel = new dojo.create(\"div\", {innerHTML:\"Variable (from GCMD list):  \", style:\"float:none;\"});\n";
					print "var topic = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", store: GCMDVariableStore, searchAttr:\"value\", labelAttr:\"value\", required:$required, value:\"\", id:\"$fieldName\"+num, name:\"$fieldName\"+num });\n";
					print "labels.push(topicLabel);\n";
					print "inputs.push(topic.domNode);\n";	
				}
				else {
					print "var categoryLabel = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName)." (from GCMD list):  \", style:\"float:none;\"});\n";
					print "var category = new dijit.form.ValidationTextBox({maxLength:\"$maxLength\", value:\"Earth Science\" , style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num,required:true});\n";
					print "labels.push(categoryLabel);\n";
					print "inputs.push(category.domNode);	";
				}
			}
		}
	}
}

function printExistingDatasetParameterJS($manifest) {
	$fieldList = getFieldList($manifest, "datasetParameter");
	$idList = getIdList($manifest, $fieldList[0]);
	foreach($idList as $id) {	
		print "var num = paramNum;\n";
		print "createNewParameter();\n";
		foreach($fieldList as $field) {
			$value = getFieldValue($manifest, "$field"."$id");
			if($value == "null") $value = "";
			$value = str_replace("\n","\\n", $value);
			print "dijit.byId(\"$field\"+num).attr(\"value\", \"$value\");\n";

		}
	}
}

function printDatasetRegionJS($manifest) {
		foreach($manifest->field as $field) {
			$fieldName = $field['name'];
			$fieldType = $field['type'];
			$required = $field['required'];
			$maxLength = getLength($fieldName);
			if(preg_match("/^(datasetRegion\_.*)#$/", $fieldName, $matches)) {
				$fieldName = $matches[1];
				print "	var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var field = new dijit.form.ValidationTextBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
		}
}

function printExistingDatasetRegionJS($manifest) {
	$fieldList = getFieldList($manifest, "datasetRegion");
	$idList = getIdList($manifest, $fieldList[0]);
	foreach($idList as $id) {	
		print "var num = regionNum;\n";
		print "createNewRegion();\n";
		foreach($fieldList as $field) {
			$value = getFieldValue($manifest, "$field"."$id");
			if($value == "null") $value = "";
			$value = str_replace("\n","\\n", $value);
			print "dijit.byId(\"$field\"+num).attr(\"value\", \"$value\");\n";

		}
	}
}

function printDatasetCitationJS($manifest) {
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		if(preg_match("/(datasetCitation\_.*)/", $fieldName, $matches)) {
			$fieldName = $matches[1];
			if(preg_match("/datasetCitation_releaseDate.*/", $fieldName)) {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).": \", style:\"float:none;\"});
					var field = new dijit.form.DateTextBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName"."DATE\"+num, required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
			else {
				print "	var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var field = new dijit.form.ValidationTextBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
		}
	}
}

function printExistingDatasetCitationJS($manifest) {
	$fieldList = getFieldListSingle($manifest, "datasetCitation");
	//$fieldList = getFieldList($manifest, "datasetCollection");
	//print_r($fieldList);
	foreach($fieldList as $field) {
		$value = getFieldValue($manifest, "$field");
		if($value == "null") $value = "";
		$value = str_replace("\n","\\n", $value);
		if($field != "datasetCitation_releaseDate")
			print "dijit.byId(\"$field\").attr(\"value\", \"$value\");\n";
		elseif($field == "datasetCitation_releaseDate") {
			if($value != "") {
				$newValue = (int)$value + 86400000;
				print "startDate = new Date($newValue);\n";
				print "oldDate = $value;";
				print "dijit.byId(\"datasetCitation_releaseDateDATE\").attr(\"value\", startDate);\n";	
			}	
		}
		// ADD CODE FOR ELSE
	}

}

function printToggleCitationJS($manifest) {
	print "var citationField = new Array();\n";
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		if ($fieldName == "datasetCitation_releaseDate")
			print "citationField.push(\"datasetCitation_releaseDateDATE\");\n";
		else if (preg_match("/datasetCitation.*/",$fieldName)) print "citationField.push(\"$fieldName\");\n";	
	}
		
}

function printDatasetLocationPolicyJS($manifest) {
	global $requestType;
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		if(!$_SESSION['dmt-admin'] && $requestType != "create")
			$disableText = "readOnly:true,";
		else 
			$disableText = "";
		
		if(preg_match("/(datasetLocationPolicy_.*)#/", $fieldName, $matches)) {
			$fieldName = $matches[1];
			if(preg_match("/datasetLocationPolicy_type.*/", $fieldName)) {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var typeData = {identifier:\"value\",
							label:\"value\",
							items:[{value:\"ARCHIVE-CONTROLLED\"},
								{value:\"ARCHIVE-PREVIEW\"},
								{value:\"LOCAL-FTP\"},
								{value:\"ARCHIVE-OPEN\"},
								{value:\"ARCHIVE-SHARED\"},
								{value:\"ARCHIVE-RETIRED\"},
								{value:\"ARCHIVE-SIMULATED\"},
								{value:\"LOCAL-OPENDAP\"},
								{value:\"REMOTE-OPENDAP\"},
								{value:\"REMOTE-FTP\"},
								{value:\"REMOTE-HTTP\"},]};
					var locationTypeStore = new dojo.data.ItemFileWriteStore({data:typeData});
					var field = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, $disableText required:$required, store: locationTypeStore, searchAttr:\"value\", labelAttr:\"value\"});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
			else {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var field = new dijit.form.ValidationTextBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, $disableText required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
		}
	}
}

function printExistingDatasetLocationPolicyJS($manifest) {
	$fieldList = getFieldList($manifest, "datasetLocationPolicy");
	$idList = getIdList($manifest, $fieldList[0]);
	foreach($idList as $id) {	
		print "var num = locationPolicyNum;\n";
		print "createNewLocationPolicy();\n";
		foreach($fieldList as $field) {
			$value = getFieldValue($manifest, "$field"."$id");
			if($value == "null") $value = "";
			$value = str_replace("\n","\\n", $value);
			print "dijit.byId(\"$field\"+num).attr(\"value\", \"$value\");\n";

		}
	}
}

function printDatasetResourceJS($manifest) {
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		if(preg_match("/(datasetResource_.*)#/", $fieldName, $matches)) {
			$fieldName = $matches[1];
			if(preg_match("/datasetResource_description.*/", $fieldName)) {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var field = new dijit.form.Textarea({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
			else if(preg_match("/datasetResource_type.*/", $fieldName)) {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var resourceTypeData = {identifier:\"value\",
							label:\"value\",
							items:[{value:\"Thumbnail\"},
								{value:\"EXTRACT_NETCDF3\"},
								{value:\"EXTRACT_NETCDF4\"},
								{value:\"EXTRACT_HDF4\"},
								{value:\"EXTRACT_HDF5\"},
								{value:\"QUICKLOOK\"}]};
					var resourceTypeStore = new dojo.data.ItemFileWriteStore({data:resourceTypeData});
					var field = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, $disableText required:$required, store: resourceTypeStore, searchAttr:\"value\", labelAttr:\"value\"});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
			else {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var field = new dijit.form.ValidationTextBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
		}
	}
}

function printExistingDatasetCollectionJS($manifest) {
	$fieldList = getFieldList($manifest, "datasetCollection");
	$idList = getIdList($manifest, $fieldList[0]);
	foreach($idList as $id) {	
		print "var num = collectionNum;\n";
		print "createNewCollection();\n";
		foreach($fieldList as $field) {
			$value = getFieldValue($manifest, "$field"."$id");
			if($value == "null") $value = "";
			$value = str_replace("\n","\\n", $value);
			print "dijit.byId(\"$field\"+num).attr(\"value\", \"$value\");\n";

		}
	}
}

function printDatasetCollectionJS($manifest) {
	foreach($manifest->field as $field) {
		$fieldName = $field['name'];
		$fieldType = $field['type'];
		$required = $field['required'];
		$maxLength = getLength($fieldName);
		if(preg_match("/(datasetCollection\_.*)/", $fieldName, $matches)) {
			$fieldName = $matches[1];
			if($fieldName == "datasetCollection_CollectionId") {
				print "var label = new dojo.create(\"div\", {innerHTML:\"Collection:  \", style:\"float:none;\"});
					var entryContainer = dojo.create(\"div\");
					var field = new dijit.form.FilteringSelect({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, required:$required, store:collectionStore, searchAttr:\"collection_shortName\", labelAttr:\"collection_shortName\"});
					var collectionEditButton = new dijit.form.Button({
										label:\"View/Edit Details\", 
										collectionForm:\"$fieldName\"+num, 
										onClick:editCollection});
					entryContainer.appendChild(field.domNode);
					entryContainer.appendChild(collectionEditButton.domNode);
					labels.push(label);
					inputs.push(entryContainer);\n";
			}
			else if($fieldName == "datasetCollection_granuleRange360") {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var typeData = {identifier:\"value\",
							label:\"value\",
							items:[{value:\"Y\"},
								{value:\"N\"}]};
					var rangeStore = new dojo.data.ItemFileWriteStore({data:typeData});
					var field = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, $disableText required:$required, store: rangeStore, searchAttr:\"value\", labelAttr:\"value\"});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
			else if($fieldName == "datasetCollection_granuleFlag") {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var typeData = {identifier:\"value\",
							label:\"value\",
							items:[{value:\"A\"},
								{value:\"P\"}]};
					var rangeStore = new dojo.data.ItemFileWriteStore({data:typeData});
					var field = new dijit.form.ComboBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, $disableText required:$required, store: rangeStore, searchAttr:\"value\", labelAttr:\"value\"});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
			else if($fieldType == "char") {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var field = new dijit.form.ValidationTextBox({maxLength:\"$maxLength\", style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
			else if($fieldType == "int" || $fieldType == "float" || $fieldType == "double") {
				print "var label = new dojo.create(\"div\", {innerHTML:\"".getLabel($fieldName).":  \", style:\"float:none;\"});
					var field = new dijit.form.NumberTextBox({maxLength:\"$maxLength\", constraints:{type:'decimal', round:'-1'}, style:\"float:none;\", id:\"$fieldName\"+num, name:\"$fieldName\"+num, required:$required});
					labels.push(label);
					inputs.push(field.domNode);\n";
			}
		} 
	}
}


?>
