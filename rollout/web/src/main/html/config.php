<?php
// Config file for Dataset Manager Tool

//DEV
$INVENTORY_URL = "https://lanina.jpl.nasa.gov:9192/inventory";  
$MANAGER_URL = "http://lanina.jpl.nasa.gov:8090/manager";
$SIGEVENT_URL = "http://lanina.jpl.nasa.gov:8100/sigevent";
$SECURITY_URL = "https://lanina.jpl.nasa.gov:9197/security";

//TEST
//$INVENTORY_URL = "https://test-ingest.jpl.nasa.gov:9192/inventory";  
//$MANAGER_URL = "http://test-ingest.jpl.nasa.gov:8090/manager";  
//$SIGEVENT_URL = "http://test-ingest.jpl.nasa.gov:8100/sigevent";
//$SECURITY_URL = "https://test-ingest.jpl.nasa.gov:9197";

//OPS
//$INVENTORY_URL = "https://podaac-ingest.jpl.nasa.gov:9192/inventory";  
//$MANAGER_URL = "http://podaac-ingest.jpl.nasa.gov:8090/manager";  
//$SIGEVENT_URL = "http://podaac-ingest.jpl.nasa.gov:8100/sigevent";
//$SECURITY_URL = "https://podaac-ingest.jpl.nasa.gov:9197";




$SECURITY_LOGIN = "auth/PODAAC-MANAGER/authenticate";
$SECURITY_AUTHORIZE = "auth/PODAAC-MANAGER/authorizedRoles";
$INVENTORY_PROCESS = "DMTmanifest";
$INVENTORY_LIST = "datasets";
$OUTPUT_DIR = "../xml"; # MUST PROVIDE ABSLUTE PATH (or relative path from controller directory)
$PROCESSED_DIR = "../processed"; # MUST PROVIDE ABSLUTE PATH (or relative path from controller directory)

//Paths for creation tab
$MANAGER_LOOKUP = "productType/showByName";
$MANAGER_CREATE = "productType/create";
$SIGEVENT_LOOKUP = "groups/showByCategory";
$SIGEVENT_CREATE = "groups/create";

$unit_lookup = array(
	"dataset_acrossTrackResolution"  => "Meters",
	"dataset_alongTrackResolution"  => "Meters",
	"dataset_longitudeResolution"  => "Degrees",
	"dataset_latitudeResolution"  => "Degrees",
	"datasetPolicy_dataDuration"  => "Days",
	"datasetPolicy_dataFrequency"  => "Hours",
	"datasetPolicy_dataLatency"  => "Hours",
	"datasetPolicy_dataVolume"  => "Files",
	"source_orbitPeriod" => "Minutes",
	"source_inclinationAngle" => "Degrees",
	"sensor_swathWidth" => "Km"
);

$length_lookup = array(
	"dataset_shortName"=> 160,
	"dataset_longName"=> 255,
	"dataset_persistentId"=> 24,
	"datasetCitation_citationDetail"=> 255,
	"datasetCitation_creator"=> 255,
	"datasetCitation_onlineResource"=> 255,
	"datasetCitation_publisher"=> 160,
	"datasetCitation_releaseDate"=> 38,
	"datasetCitation_releasePlace"=> 160,
	"datasetCitation_seriesName"=> 160,
	"datasetCitation_title"=> 255,
	"datasetCitation_version"=> 80,
	"datasetCollection_CollectionId"=> 38,
	"datasetCollection_granuleFlag"=> 1,
	"datasetCollection_granuleRange360"=> 1,
	"datasetCollection_startGranuleId"=> 38,
	"datasetCollection_stopGranuleId"=> 38,
	"datasetContact_contactId"=> 38,
	"datasetCoverage_easternmostLongitude"=> 38,
	"datasetCoverage_maxAltitude"=> 38,
	"datasetCoverage_maxDepth"=> 38,
	"datasetCoverage_minAltitude"=> 38,
	"datasetCoverage_minDepth"=> 38,
	"datasetCoverage_northernmostLatitude"=> 38,
	"datasetCoverage_southernmostLatitude"=> 38,
	"datasetCoverage_startTime"=> 38,
	"datasetCoverage_stopTime"=> 38,
	"datasetCoverage_westernmostLongitude"=> 38,
	"datasetElement_deId"=> 38,
	"datasetElement_elementId"=> 38,
	"datasetElement_obligationFlag"=> 1,
	"datasetElement_scope"=> 20,
	"datasetLocationPolicy_basePath"=> 255,
	"datasetLocationPolicy_type"=> 20,
	"datasetMetaHistory_creationDateLong"=> 38,
	"datasetMetaHistory_lastRevisionDate"=> 38,
	"datasetMetaHistory_revisionHistory"=> 1024,
	"datasetMetaHistory_versionId"=> 38,
	"datasetParameter_category"=> 80,
	"datasetParameter_term"=> 80,
	"datasetParameter_topic"=> 80,
	"datasetParameter_variable"=> 80,
	"datasetParameter_variableDetail"=> 160,
	"datasetPolicy_accessConstraint"=> 1024,
	"datasetPolicy_accessType"=> 20,
	"datasetPolicy_basePathAppendType"=> 20,
	"datasetPolicy_checksumType"=> 20,
	"datasetPolicy_compressType"=> 20,
	"datasetPolicy_dataClass"=> 20,
	"datasetPolicy_dataDuration"=> 38,
	"datasetPolicy_dataFormat"=> 20,
	"datasetPolicy_dataFrequency"=> 38,
	"datasetPolicy_dataLatency"=> 38,
	"datasetPolicy_dataVolume"=> 38,
	"datasetPolicy_spatialType"=> 20,
	"datasetPolicy_useConstraint"=> 1024,
	"datasetPolicy_viewOnline"=> 1,
	"datasetProject_projectId"=> 38,
	"datasetRegion_region"=> 128,
	"datasetRegion_regionDetail"=> 1024,
	"datasetResource_description"=> 1024,
	"datasetResource_name"=> 80,
	"datasetResource_path"=> 255,
	"datasetResource_type"=> 31,
	"datasetSource_sensor"=> 38,
	"datasetSource_source"=> 38,
	"datasetVersion_descritpion"=> 1024,
	"datasetVersion_version"=> 80,
	"datasetVersion_versionDate"=> 38,
	"datasetVersion_versionId"=> 38,
	"dataset_acrossTrackResolution"=> 38,
	"dataset_alongTrackResolution"=> 38,
	"dataset_altitudeResolution"=> 80,
	"dataset_ascendingNodeTime"=> 80,
	"dataset_datasetId"=> 38,
	"dataset_depthResolution"=> 80,
	"dataset_description"=> 900000,
	"dataset_ellipsoidType"=> 160,
	"dataset_horizontalResolutionRange"=> 80,
	"dataset_latitudeResolution"=> 38,
	"dataset_longitudeResolution"=> 38,
	"dataset_metadata"=> 1024,
	"dataset_originalProvider"=> 160,
	"dataset_processingLevel"=> 10,
	"dataset_projectionDetail"=> 1024,
	"dataset_projectionType"=> 160,
	"dataset_providerDatasetName"=> 160,
	"dataset_providerId"=> 38,
	"dataset_reference"=> 1024,
	"dataset_remoteDataset"=> 1,
	"dataset_sampleFrequency"=> 38,
	"dataset_swathWidth"=> 38,
	"dataset_temporalRepeat"=> 1024,
	"dataset_temporalRepeatMax"=> 1024,
	"dataset_temporalRepeatMin"=> 1024,
	"dataset_temporalResolution"=> 1024,
	"dataset_temporalResolutionRange"=> 80,
	
);

?>
