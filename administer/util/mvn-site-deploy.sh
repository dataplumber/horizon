#!/bin/sh
# Copyright (c) 2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-site-deploy.sh 8284 2011-08-22 22:19:42Z thuang $

# This script traverses the module directories to build and deploy
# the associated sites to the repository.

cd ..
mvn clean
mvn site-deploy --non-recursive
cd admin-monitor
mvn site-deploy
cd ../operator
mvn site-deploy
