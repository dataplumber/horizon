#!/bin/sh
# Copyright (c) 2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-site-deploy.sh 5254 2010-07-12 19:37:15Z thuang $

# This script traverses the module directories to build and deploy
# the associated sites to the repository.

cd ..
mvn clean
mvn site-deploy --non-recursive
cd common-gen
mvn site-deploy --non-recursive
cd common-serviceprofile
mvn site-deploy
cd ../common-mmr
mvn site-deploy
cd ../common-hostmap
mvn site-deploy
cd ../common-crawlercache
mvn site-deploy
cd ../../common-api
mvn site-deploy
cd ../common-crawler
mvn site-deploy
cd ../common-groovy
mvn site-deploy -Dmaven.test.skip=true
cd ../common-httpfetch
mvn site-deploy -Dmaven.test.skip=true
