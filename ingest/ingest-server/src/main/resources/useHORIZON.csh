#!/bin/csh -f
#
### ==================================================================== ###
#                                                                          #
#  The HORIZON Ingest Server Setup Script                                     #
#                                                                          #
#  Function:                                                               #
#  Simple c-shell script to add Ingest launchers to client's path.         #
#                                                                          #
#  Created:                                                                #
#  August 29, 2007 - T. Huang {Thomas.Huang@jpl.nasa.gov}                  #
#                                                                          #
#  Modifications:                                                          #
### ==================================================================== ###
#
# $Id: $
#

if (! $?JAVA_HOME) then
   setenv JAVA_HOME /usr/local/java
   setenv PATH ${JAVA_HOME}/bin:${PATH}
endif

setenv HORIZON ${cwd}/config

setenv PATH ${HORIZON}/../sbin:${PATH}
