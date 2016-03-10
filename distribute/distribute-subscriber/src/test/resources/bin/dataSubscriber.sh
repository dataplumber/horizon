#!/bin/sh

# Make the classpath:
CLASSPATH=`echo ${distribute.home}/lib/*.jar | tr ' ' ':'`
export CLASSPATH

java -Xmx512m subscriber.hostmap=${distribute.home}/config/hostmap.cfg -Ddistribute.config.file=${distribute.home}/config/distribute.config \
        gov.nasa.podaac.distribute.subscriber.Subscriber $*
