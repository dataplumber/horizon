#!/bin/sh
#
# Copyright (c) 2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
#
# $Id: sqlplus.sh 2404 2008-12-17 01:31:18Z shardman $

#
# This script executes the specified SQL script using sqlplus.
#

if [ $# = "0" ]; then
  echo "Usage: sqlplus.sh <username> <password> <sid> <sql-script> [<sql-arg> [<sql-arg [<sql-arg>]]]"
  exit 1
fi

sqlplus -s $1/$2@$3 \@$4 $5 $6 $7

