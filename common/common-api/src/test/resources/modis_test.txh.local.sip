<?xml version="1.0" encoding="UTF-8"?>

<message xmlns="http://podaac.jpl.nasa.gov/webservice/schema"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" type="PRODUCT"
   xsi:schemaLocation="http://podaac.jpl.nasa.gov/webservice/schema file:///Users/thuang/Development/work/ingest/src/ingest-api/src/test/resources/podaac_ingest.xsd">
   <productMsg>
      <ingestMsg metadataExpanded="true">
         <project>JPL-L2P-MODIS_A</project>
         <batch>1192145906-4</batch>
         <contributor>Thomas.Huang@jpl.nasa.gov</contributor>
         <ingestiblePackages>
            <ingestiblePackage category="GHRSST INVENTORY">
               <version>0</version>
               <files count="1">
                  <fileIngestible isMetadata="false" name="zzz" type="NETCDF">
                     <source>
                        <link>ftp://localhost/Users/ingest/Development/private/active_archive/nobackup/mmr_drop/MODIS_A/2007/213/20070801-MODIS_A-JPL-L2P-A2007213074500.L2_LAC_GHRSST-v01.nc.bz2</link>
                     </source>
                     <ingestMode>BINARY</ingestMode>
                     <metadata>
                        <collectionId>0</collectionId>
                        <granuleId>0</granuleId>
                        <datasetName>JPL-L2P-MODIS_A</datasetName>
                        <granuleName>JPL-L2P-MODIS_A</granuleName>
                        <version>1</version>
                        <temporalCoverage>
                           <startTime>1185954308000</startTime>
                           <stopTime>1185954606000</stopTime>
                        </temporalCoverage>
                        <createTime>1185970528000</createTime>
                        <spatialCoverage>
                           <boundingRectangle>
                              <westLongitude>76.309</westLongitude>
                              <northLatitude>-1.600</northLatitude>
                              <eastLongitude>101.738</eastLongitude>
                              <southLatitude>-22.541</southLatitude>
                           </boundingRectangle>
                        </spatialCoverage>
                     </metadata>
                  </fileIngestible>
               </files>
               <status>READY</status>
            </ingestiblePackage>
         </ingestiblePackages>
         <internalId>2007_10_11_0003</internalId>
         <comment>Quicklook MODIS L2P created at the GHRSST Global Data Assembly Center (GDAC), Wed
            Aug 1 12:15:39 2007</comment>
      </ingestMsg>
      <notify>
         <lastName>Huang</lastName>
         <firstName>Thomas</firstName>
         <email>Thomas.Huang@jpl.nasa.gov</email>
      </notify>
   </productMsg>
   <origin agent="qchau@sipsubmit" ip="127.0.0.1" time="1192147092433"/>
   <target agent="QSCAT:QSCAT_2" ip="127.0.0.1" time="1192147092455"/>
</message>
