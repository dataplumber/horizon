#!/bin/sh
# Make the classpath:
CLASSPATH=.:`echo ${archive.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH

# Start Archive Status Unit Test
java -Darchive.config.file=${archive.home}/config/archive.config \
	junit.textui.TestRunner gov.nasa.horizon.archive.FileUtilTest
