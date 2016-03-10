#!/bin/sh
# Copyright (c) 2008-2009, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-site-deploy.sh 3733 2009-09-01 21:24:15Z thuang $

# This script traverses the module directories to build and deploy
# the associated sites to the repository.

cd ..
mvn clean
mvn site-deploy --non-recursive
cd distribute-gcmd
mvn site-deploy
cd ../distribute-gen
mvn site-deploy --non-recursive
cd distribute-echo-collection
mvn site-deploy
cd ../distribute-echo-granule
mvn site-deploy
cd ../distribute-gcmd-dif
mvn site-deploy
cd ../../distribute-main
mvn site-deploy
cd ../distribute-subscriber
mvn site-deploy
