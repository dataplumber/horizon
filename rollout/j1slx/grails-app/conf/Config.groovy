// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

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
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']


//SecurityServiceInfo
gov.nasa.podaac.security.host="https://localhost"
gov.nasa.podaac.security.port=9197
gov.nasa.podaac.security.realm="PODAAC-J1SLX"
gov.nasa.podaac.security.role="USER"

//sigeven info
gov.nasa.podaac.j1slx.sig.category="TOOL_J1SLX"
gov.nasa.podaac.j1slx.sig.url="http://lanina:8100/sigevent/"

//ldap info
gov.nasa.podaac.j1slx.ldap.host = 'ldap://ldap.jpl.nasa.gov:636'
gov.nasa.podaac.j1slx.ldap.group = 'podaac.j1slx.dev'
gov.nasa.podaac.j1slx.ldap.searchDn = 'ou=personnel,dc=dir,dc=jpl,dc=nasa,dc=gov'


// The host for the inventory client to talk to.
gov.nasa.podaac.j1slx.inventory.host = 'https://lanina'
gov.nasa.podaac.j1slx.inventory.port = 9192
gov.nasa.podaac.j1slx.inventory.user = 'thuang'
gov.nasa.podaac.j1slx.inventory.pass = 'password'


gov.nasa.podaac.j1slx.testReassociate = false 

//because we can add/remove datasets we'll need to track when each was run individually, not with a single "meta" time.
//These are hashes of dataset,version,and what open type they are mapped to.
gov.nasa.podaac.j1slx.datasets = [
	[dataset:'JASON-1_GDR_NASA',version:'C',mappedTo:'gdr'],
	[dataset:'JASON-1_GDR_CNES',version:'C',mappedTo:'gdr'],
	[dataset:'JASON-1_L2_OST_SGDR_VER-C_BINARY_NASA',version:'C',mappedTo:'sgdr'],
	[dataset:'JASON-1_L2_OST_SGDR_VER-C_BINARY_CNES',version:'C',mappedTo:'sgdr'],
	[dataset:'JASON-1_SGDR_C_NETCDF_NASA',version:'C',mappedTo:'sgdr_netcdf'],
	[dataset:'JASON-1_SGDR_C_NETCDF_CNES',version:'C',mappedTo:'sgdr_netcdf'],
	[dataset:'JASON-1_GDR_NETCDF_C_NASA',version:'C',mappedTo:'gdr_netcdf'],
	[dataset:'JASON-1_GDR_NETCDF_C_CNES',version:'C',mappedTo:'gdr_netcdf'],
	[dataset:'JASON-1_GDR_SSHA_NETCDF_NASA',version:'C',mappedTo:'ssha_netcdf'],
	[dataset:'JASON-1_GDR_SSHA_NETCDF_CNES',version:'C',mappedTo:'ssha_netcdf']
]

//these are the open, versionless datasets into which data gets moved .
gov.nasa.podaac.j1slx.dataset.gdr = 'JASON-1_L2_OST_GDR_Ver-C_Binary'
gov.nasa.podaac.j1slx.dataset.sgdr = 'JASON-1_L2_OST_SGDR_VER-C_BINARY'
gov.nasa.podaac.j1slx.dataset.gdr_netcdf = 'JASON-1_GDR_NETCDF'
gov.nasa.podaac.j1slx.dataset.sgdr_netcdf = 'JASON-1_SGDR_NETCDF'
gov.nasa.podaac.j1slx.dataset.ssha_netcdf = 'JASON-1_GDR_SSHA_NETCDF'


//for the first run of the system, set the time (in milliseconds/epoch time) to set as the 'start time' to begin fetching data from. 
gov.nasa.podaac.j1slx.initialStartTime = 100000L 

//default version if no version info is available. Don't think we need this.
gov.nasa.podaac.j1slx.productVersion = 'C'
gov.nasa.podaac.j1slx.jobStartDelay = 10 //seconds before starting job that checks for granules.
gov.nasa.podaac.j1slx.jobInterval = 30 //seconds between each run of checking for new granules.

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://www.changeme.com"
		grails.plugins.springsecurity.secureChannel.definition = [
			'/**': 'REQUIRES_SECURE_CHANNEL'
		]
    }
    development {
        grails.serverURL = "https://localhost:8080/${appName}"
		grails.plugins.springsecurity.secureChannel.definition = [
			'/**': 'REQUIRES_SECURE_CHANNEL'
		]
    }
    podaacdev {
        grails.serverURL = "https://localhost:9192/${appName}"
		grails.plugins.springsecurity.secureChannel.definition = [
			'/**': 'REQUIRES_SECURE_CHANNEL'
		]
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
		grails.plugins.springsecurity.secureChannel.definition = [
			'/**': 'REQUIRES_SECURE_CHANNEL'
		]
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
   appenders {
      rollingFile name:'catalinaOut', maxFileSize:102400, fileName:"j1slx.out", file:"j1slx.out"	
      file name: 'stacktrace', file: "j1slx.out.st", layout: pattern(conversionPattern: '%c{2} %m%n')
      console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n') 
   }
	info   'gov.nasa.podaac'
	debug  'grails.app.controller.gov.nasa.podaac.j1slx',
		   'grails.app.service',
		   'grails.app.conf',
		   'grails.app.utils',
		   'grails.app.jobs',
		   'grails.app.task',
		   'grails.app.bootstrap',
		   'gov.nasa.podaac.archive'
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}
