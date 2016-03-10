#!/bin/sh
# Copyright (c) 2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-site-deploy.sh 10557 2012-08-31 18:48:18Z thuang $

# This script traverses the module directories to build and deploy
# the associated sites to the repository.

cd ..
mvn clean
mvn site-deploy --non-recursive
cd j1-streamline
mvn site-deploy -Dmaven.test.skip=true
cd ../web
mvn site-deploy -Dmaven.test.skip=true
