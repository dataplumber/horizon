#!/bin/sh

# Make the classpath:
CLASSPATH=`echo ${distribute.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH

java -Ddistribute.config.file=${distribute.home}/config/distribute.config \
        gov.nasa.podaac.distribute.echo.CreateCollection $*
