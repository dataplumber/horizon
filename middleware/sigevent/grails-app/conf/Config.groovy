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

grails.gorm.failOnError = true

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
//grails.json.legacy.builder = false
grails.json.legacy.builder = true

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

gov.nasa.horizon.log.root = ""

environments {
    development {
        grails.logging.jul.usebridge = true
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
    }
    production {
        grails.logging.jul.usebridge = false
        gov.nasa.horizon.log.root = System.getProperty('user.dir')
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
    smap_cal_val {
        grails.logging.jul.usebridge = true
        gov.nasa.horizon.log.root = "/data/tie/logs/sigevent/"
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
      console name:'stdoutLogger', 
         layout: pattern(
            conversionPattern: '%d{ABSOLUTE} %-5p [%c{1}:%L] {%t} %m%n')

      appender new org.apache.log4j.DailyRollingFileAppender(
         name:'fileLogger',
         fileName: "${Holders.config.gov.nasa.horizon.log.root}/sigevent.log",
         layout: pattern(
            conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
         datePattern: "'.'yyyy-MM-dd"
      )
      
      appender new org.apache.log4j.DailyRollingFileAppender(
         name:'stackTraceLogger',
         fileName: "${Holders.config.gov.nasa.horizon.log.root}/sigevent.stacktrace",
         layout: pattern(
            conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
         datePattern: "'.'yyyy-MM-dd"
      )
   }
     
   root {
      debug 'stdoutLogger', 'fileLogger'
      additivity = true
   }
     
   error stackTraceLogger: "StackTrace"
     
   info 'grails.app',
         'org.quartz'

   error 'org.codehaus.groovy.grails.web.servlet',        // controllers
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

environments {
   local {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_email_from = 'sigevent-noreply'
      horizon_provider_url = "jnp://localhost:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://localhost:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
   }

   thuang {
      horizon_email_host = 'localhost'
      horizon_email_from = 'noreply@localhost'
      horizon_provider_url = "jnp://localhost:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://localhost:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
   }

   development {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_email_from = 'noreply@localhost'
      horizon_provider_url = "jnp://localhost:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://localhost:8100/sigevent/events/data"
      sigevent_twitter_username = "sigeventdev"
      sigevent_twitter_password = 'SigEvent@07'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
   }

   test {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_email_from = 'noreply@localhost'
      horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://lanina.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
   }
   
   testing {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_email_from = 'noreply@localhost'
      horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://lanina.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
   }

   production {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_email_from = 'noreply@localhost'
      horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://prawn.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
   }
   
   operation {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_email_from = 'noreply@localhost'
      horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://prawn.jpl.nasa.gov:8100/sigevent/events/data"
      sigevent_twitter_username = "username"
      sigevent_twitter_password = 'password'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
   }

   smap_cal_val {
      horizon_email_host = 'smtp.jpl.nasa.gov'
      horizon_email_from = 'noreply@localhost'
      horizon_provider_url = "jnp://localhost:1099"
      horizon_jms_username = "thuang"
      horizon_jms_password = "txh388"
      sigevent_api_data_uri = "http://tie-2-aws.jpl.nasa.gov:3230/sigevent/events/data"
      sigevent_twitter_username = "sigeventdev"
      sigevent_twitter_password = 'SigEvent@07'
      sigevent_twitter_characters_limit = 140
      //SecurityServiceInfo
      gov.nasa.horizon.security.service.enable = true
      gov.nasa.horizon.security.host = "https://tie-2-aws.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 3040
      gov.nasa.horizon.security.realm = "HORIZON-SIGEVENT"
      gov.nasa.horizon.security.role="ADMIN"
  }
}

