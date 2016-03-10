#!/bin/sh
# Make the classpath:
CLASSPATH=.:`echo ${archive.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH

# Start AIP Sender Unit Test 
java -Darchive.config.file=${archive.home}/config/archive.config \
        junit.textui.TestRunner gov.nasa.horizon.archive.AIPSenderTest
