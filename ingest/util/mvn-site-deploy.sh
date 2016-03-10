#!/bin/sh
# Copyright (c) 2007-2008, by the California Institute of Technology.
# ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
#
# $Id: mvn-site-deploy.sh 4530 2010-01-28 23:18:03Z thuang $

# This script traverses the module directories to build and deploy
# the associated sites to the repository.

cd ..
mvn clean
mvn site-deploy --non-recursive
cd ingest-api
mvn compile
mvn javadoc:javadoc
mvn site-deploy
cd ../ingest-client
mvn site-deploy
cd ../ingest-commons
mvn site-deploy
cd ../ingest-gen
mvn site-deploy --non-recursive
cd ingest-config
mvn site-deploy
cd ../ingest-domain
mvn site-deploy
cd ../../ingest-server
mvn site-deploy

