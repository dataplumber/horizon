#!/bin/sh

# Make the classpath:
CLASSPATH=`echo ${archive.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH

java -Darchive.config.file=${archive.home}/config/archive.config \
	gov.nasa.horizon.archive.tool.ArchiveLocation $1
