<?php

// To be filled in by manifest parser (DO NOT MODIFY... unless manual configuration is necessary)
$OPERATOR_MANAGERS = array();												 
$OPERATOR_ZOOKEEPER_URL = "";
$OPERATOR_SOLR_URL = "";
$OPERATOR_INVENTORY_URL = "";
$OPERATOR_SIGEVENT_URL = "";
$OPERATOR_AUTH_URL = "";

//Constants for separate functions
$OPERATOR_AUTH_PATH = "security/auth/PODAAC-MANAGER/authenticate";
$OPERATOR_ADMIN_CHECK_PATH = "security/auth/PODAAC-MANAGER/authorizedRoles";

$OPERATOR_LIST_GRANULE = "product/search";
$OPERATOR_EDIT_GRANULE = "product/update";
$OPERATOR_DELETE_GRANULE = "product/delete";
$OPERATOR_COUNT_PRIORITY_GRANULE = "product/countByPriority";

$OPERATOR_LIST_STORAGE = "storage/list";
$OPERATOR_EDIT_STORAGE = "storage/update";
$OPERATOR_EDIT_LOCATION = "location/update";
$OPERATOR_COUNT_PRIORITY_STORAGE = "storage/countJobs";
$OPERATOR_LIST_SUMMARY = "product/count";
$OPERATOR_LIST_ENGINE = "operator/listEngines";
$OPERATOR_LIST_DATASET = "productType/search";
$OPERATOR_EDIT_DATASET = "productType/update";
$OPERATOR_PAUSE_MANAGER = "job/pause";
$OPERATOR_RESUME_MANAGER = "job/resume";
$OPERATOR_STATE_MANAGER = "job/state";

$OPERATOR_DOMAIN_UPDATE = "domain/export";

$OPERATOR_ZOOKEEPER_SUFFIX = "znodes/v1";

$OPERATOR_INVENTORY_HEARTBEAT = "heartbeat";
$OPERATOR_MANAGER_HEARTBEAT = "heartbeat";
?>
