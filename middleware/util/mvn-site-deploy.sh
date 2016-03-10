#!/bin/sh
# Copyright (c) 2007-2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-site-deploy.sh 2244 2008-11-04 05:21:06Z shardman $

# This script traverses the module directories to build and deploy
# the associated sites to the repository.

cd ..
mvn clean
mvn site-deploy --non-recursive
cd manager
mvn site-deploy
cd ../sigevent
mvn site-deploy -Dmaven.test.skip=true
cd ../inventory
mvn site-deploy -Dmaven.test.skip=true
cd ../security
mvn site-deploy -Dmaven.test.skip=true
