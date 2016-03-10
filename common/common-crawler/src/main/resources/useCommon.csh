#!/bin/csh -f
#
### ==================================================================== ###
#                                                                          #
#  The HORIZON Ingest Client Setup Script                                  #
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

setenv COMMONC ${cwd}

if(! $?CLASSPATH) then
        setenv CLASSPATH `echo ${COMMONC}/lib/*.jar | tr ' ' ':'`
else
        setenv CLASSPATH `echo ${COMMONC}/lib/*.jar | tr ' ' ':'`:${CLASSPATH}
endif

setenv PATH ${COMMONC}/bin:${PATH}
