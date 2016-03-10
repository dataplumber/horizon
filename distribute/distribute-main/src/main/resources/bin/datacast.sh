#!/bin/sh

# Make the classpath:
CLASSPATH=`echo ${distribute.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH

java -Xmx512m -Ddistribute.config.file=${distribute.home}/config/distribute.config \
        gov.nasa.podaac.distribute.datacasting.Datacast $*
