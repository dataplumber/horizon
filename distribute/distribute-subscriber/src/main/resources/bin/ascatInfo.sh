#!/bin/sh

# Make the classpath:
CLASSPATH=`echo ${distribute.home}/lib/* | tr ' ' ':'`
export CLASSPATH
printenv CLASSPATH

#cmd line params are:
#1 granule name
#2 dataset name
#3 location to write out to
#4 subscriber write location

# exec cmd in distribute.config should look like:
#sh ${distribute.home}/bin/ascatInfo.sh $gName $dsName OUTPUT_DIR $gDir


#get Extent info
# you will need the 'HORIZON' variable set up and be able to run Quis Ascat code (startup_polygon)
#get Extent info
/PATH/TO/DATA-HANDLERS/ascat-3.0.0/bin/startup_polytransform -f $4/$1.gz
EXTENTS=`grep "SDO_ORDINATE_ARRAY" SQL_script_to_be_ran_after.sql | sed 's/SDO_ORDINATE_ARRAY//' | sed 's/(//' | sed 's/)//'`
EXTENTS=`echo "$EXTENTS"`
#This will use hibernate to execute the inventory functions of the ascat script
#java -Dgov.nasa.podaac.inventory.factory=gov.nasa.podaac.distribute.common.direct.Query -Dinventory.hibernate.config.file=${distribute.home}/config/hibernate.cfg.xml gov.nasa.podaac.distribute.subscriber.plugins.ascatRunscript $1 $2 $3 "$EXTENTS"
#this will use the IWS to do the functionality
java -Dlog4j.configuration=file:///${distribute.home}/config/distribute.log.properties -Dinventory.ws.url=https://localhost -Dinventory.ws.port=9192  gov.nasa.podaac.distribute.subscriber.plugins.ascatRunscript $1 $2 $3 "$EXTENTS"
rm $4/*
rm -r $4 
