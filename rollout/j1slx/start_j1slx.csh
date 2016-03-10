#!/usr/bin/csh
grails -Dgrails.env=podaacdev -Dserver.port=9193 -Dserver.port.https=9194 -Dserver.host=localhost runApp -https
