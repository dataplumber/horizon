import grails.util.Holders

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password','pass']

//sigeven info
gov.nasa.horizon.security.sig.category="TOOL_SECURITY"
gov.nasa.horizon.security.sig.url="http://horizon:8100/sigevent/"

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

//The ports below must be defined in both this config file and in "BuildConfig.groovy" in this same directory.
//You'll see an corresponding http/https port description in BuildConfig.groovy
//This lets the scurity plugin know SSL must be used, and which ports to map https -> https
grails.plugins.springsecurity.portMapper.httpPort=9196
grails.plugins.springsecurity.portMapper.httpsPort=9197
grails.plugins.springsecurity.secureChannel.definition = [
	'/**':         'REQUIRES_SECURE_CHANNEL'
	//<port-mapping http="9080" https="9443" />
  ]


security{
	token{
		/*
		 * A job exists to expire tokens after a certain amount of time, which is set in the "Realm".
		 */
		jobStartDelay =5 //seconds before the jobs kickoff
		jobInterval = 900 // second between job. 900 = 15 minutes.
	}
	caching{
		/*
		 * a job exists to purge the cache every so often. This keeps the in memory cache from growing too large.
		 * 
		 */
		jobStartDelay =10 //seconds before the jobs kickoff
		jobInterval = 60 //seconds between the job
		
		/*
		 * Cache specific configuration
		 * The cache is used so we don't keep hammering LDAP/Oracle for consecutive requests for authentication.
		 * It's also used to track requests from ipaddress/realm to lock accounts/prevent denial of service requests.
		 * The parameters are as follows. 
		 */
		cacheTimeLimit = 120 // Time, in minutes, that a cached entry is usable for. 
		misses = 10 // Number of consecutive misses before a user is locked from authenticating
		lockTimeLimit = 15 // Amount of time, in minutes, that a entry is locked from authentication once consecutive misses are hit(Can't authenticate via the service)
	}
	plugins {
		LDAP{
			host = 'ldap://ldap.jpl.nasa.gov:636'
			searchDn = 'ou=personnel,dc=dir,dc=jpl,dc=nasa,dc=gov'
		}
		DATABASE{
			//no database specific config
		}
//		OTHER{
//		//future plugins/A&A mechanisms (user reg?))	
//		}
	}
}

gov.nasa.horizon.log.root = ""

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
        grails.logging.jul.usebridge = false
        //grails.serverURL = "http://www.changeme.com"
    }
    development {
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
        grails.logging.jul.usebridge = true
		//logFile="sapi.log"
        //grails.serverURL = "http://localhost:8080/${appName}"
    }
	local {
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
        grails.logging.jul.usebridge = true
        //logFile="sapi.log"
		//grails.serverURL = "http://localhost:8080/${appName}"
	}
	podaacdev{
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
        grails.logging.jul.usebridge = true
        //logFile="sapi.log"
		//grails.serverURL = "http://localhost:8080/${appName}"
	}
   thuang {
       gov.nasa.horizon.log.root = System.getProperty('user.dir')
       grails.logging.jul.usebridge = true
       //logFile="sapi.log"
      //grails.serverURL = "http://localhost:8080/${appName}"
   }
   test {
       gov.nasa.horizon.log.root = System.getProperty('user.dir')
       grails.logging.jul.usebridge = true
       //logFile="sapi.log"
        //grails.serverURL = "http://localhost:8080/${appName}"
    }
	local {
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
        grails.logging.jul.usebridge = true
        //logFile="sapi.log"
		//grails.serverURL = "http://localhost:8080/${appName}"
	}
	mac{
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
        grails.logging.jul.usebridge = true
        //logFile="sapi.log"
		//grails.serverURL = "http://localhost:8080/${appName}"
	}
    smap_cal_val {
        grails.logging.jul.usebridge = true
        gov.nasa.horizon.log.root = "/data/tie/logs/security/"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
   appenders {
      console name: 'stdoutLogger',
            layout: pattern(
                  conversionPattern: '%d{ABSOLUTE} %-5p [%c{1}:%L] {%t} %m%n')

      appender new org.apache.log4j.DailyRollingFileAppender(
            name: 'fileLogger',
            fileName: "${Holders.config.gov.nasa.horizon.log.root}/security.log",
            layout: pattern(
                  conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
            datePattern: "'.'yyyy-MM-dd"
      )

      appender new org.apache.log4j.DailyRollingFileAppender(
            name: 'stackTraceLogger',
            fileName: "${Holders.config.gov.nasa.horizon.log.root}/security.stacktrace",
            layout: pattern(
                  conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
            datePattern: "'.'yyyy-MM-dd"
      )
   }

   root {
      error 'stdoutLogger', 'fileLogger'
      warn 'stdoutLogger', 'fileLogger'
      info 'stdoutLogger', 'fileLogger'
      debug 'stdoutLogger', 'fileLogger'
      
      additivity = true
   }

   error stackTraceLogger: "StackTrace"

   debug 'grails.app',
         'org.quartz',
         'gov.nasa.horizon.inventory.api.InventoryApi',
         'gov.nasa.horizon.sigevent.api'
         'gov.nasa.horizon.security.client'
   
    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate',
           'org.apache.tomcat.util.digester',
           'org.apache.tomcat.util',
           'org.apache.catalina.util',
           'org.apache',
           'org.grails.plugin.resource',
           'org.codehaus.groovy.grails.context.support',
           'com.mchange.v2'
}
