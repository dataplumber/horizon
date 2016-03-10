#!/bin/sh
#
# Copyright (c) 2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
#
# $Id: distribute_schema.sh 1503 2008-07-24 00:18:15Z shardman $

#
# This script allows the user to create or drop the Distribute
# database schema by executing the ant script, distribute_schema.xml.
#

if [ $# = "0" ]; then
  ant -f distribute_schema.xml
  exit 1
fi

ant -f distribute_schema.xml $*
