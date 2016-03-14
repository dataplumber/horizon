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
      all: '*/*',
      atom: 'application/atom+xml',
      css: 'text/css',
      csv: 'text/csv',
      form: 'application/x-www-form-urlencoded',
      html: ['text/html', 'application/xhtml+xml'],
      js: 'text/javascript',
      json: ['application/json', 'text/json'],
      multipartForm: 'multipart/form-data',
      rss: 'application/rss+xml',
      text: 'text/plain',
      xml: ['text/xml', 'application/xml']
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
grails.web.disable.multipart = false

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
      gov.nasa.horizon.log.root = ""
       // TODO: grails.serverURL = "http://www.changeme.com"
   }
   smap_cal_val {
      grails.logging.jul.usebridge = true
      gov.nasa.horizon.log.root = "/data/tie/logs/manager"
    }
}

// log4j configuration
log4j = {
   appenders {
      console name: 'stdoutLogger',
            layout: pattern(
                  conversionPattern: '%d{ABSOLUTE} %-5p [%c{1}:%L] {%t} %m%n')

      appender new org.apache.log4j.DailyRollingFileAppender(
            name: 'fileLogger',
            fileName: "${Holders.config.gov.nasa.horizon.log.root}/manager.log",
            layout: pattern(
                  conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
            datePattern: "'.'yyyy-MM-dd"
      )

      appender new org.apache.log4j.DailyRollingFileAppender(
            name: 'stackTraceLogger',
            fileName: "${Holders.config.gov.nasa.horizon.log.root}/manager.stacktrace",
            layout: pattern(
                  conversionPattern: '%d %-5p [%c{1}:%L] {%t} %m%n'),
            datePattern: "'.'yyyy-MM-dd"
      )
   }

   root {
      trace 'stdoutLogger', 'fileLogger'
      additivity = true
   }

   error stackTraceLogger: "StackTrace"

   debug 'grails.app',
         'org.quartz',
         'gov.nasa.horizon.inventory.api.InventoryApi',
         'gov.nasa.horizon.common.api.zookeeper',
         'gov.nasa.horizon.security.client'


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

// configuration for Grails Spring Security plugin
grails.plugins.springsecurity.portMapper.httpPort = (System.getProperty('server.port') ?: 8080).toInteger()
grails.plugins.springsecurity.portMapper.httpsPort = (System.getProperty('server.port.https') ?: 8443).toInteger()
grails.plugins.springsecurity.secureChannel.definition = [
      '/**': 'REQUIRES_SECURE_CHANNEL'
]

environments {
   local {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "gibsDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.inventory.port = 9192
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }

   oracle {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "gibsDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.inventory.port = 9192
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }

   development {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://localhost:8100/sigevent"
      horizon_zookeeper_url = "localhost:2181"
      horizon_zookeeper_ws_url = "localhost:9998"
      horizon_discovery_url = "http://localhost:8983/solr.war"
      horizon_dataset_update_federation = "localDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://localhost"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.url = "http://localhost:9192"
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }
   
   sit {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://localhost:8100/sigevent"
      horizon_zookeeper_url = "localhost:2181"
      horizon_zookeeper_ws_url = "localhost:9998"
      horizon_discovery_url = "http://localhost:8983/solr.war"
      horizon_dataset_update_federation = "localDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://localhost"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.url = "http://localhost:9192"
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }

   test {
      //horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_inventory_url = "http://lanina.jpl.nasa.gov:9191"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "gibsTest"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.inventory.port = 9192
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }

   testing {
      //horizon_provider_url = "jnp://lanina.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "gibsTest"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.inventory.port = 9192
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }

   production {
      //horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacOps"
      horizon_dataset_update_purge_rate = 4320
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 2], [type: "WARN", purgeRate: 2], [type: "ERROR", purgeRate: 30]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.inventory.port = 9192
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }

   operation {
      //horizon_provider_url = "jnp://prawn.jpl.nasa.gov:1099"
      horizon_sigevent_url = "http://lanina.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "lanina.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "lanina.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://lanina.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "podaacOps"
      horizon_dataset_update_purge_rate = 4320
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 2], [type: "WARN", purgeRate: 2], [type: "ERROR", purgeRate: 30]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 9197
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.host = "https://lanina.jpl.nasa.gov"
      gov.nasa.horizon.inventory.port = 9192
      gov.nasa.horizon.inventory.user = "thuang"
      gov.nasa.horizon.inventory.pass = "txh388"
   }

   smap_cal_val {
      //horizon_provider_url = "jnp://localhost:1099"
      horizon_sigevent_url = "http://tie-2-aws.jpl.nasa.gov:8100/sigevent"
      horizon_zookeeper_url = "tie-2-aws.jpl.nasa.gov:2181"
      horizon_zookeeper_ws_url = "tie-2-aws.jpl.nasa.gov:9998"
      horizon_discovery_url = "http://tie-1-aws.jpl.nasa.gov:8983/solr.war"
      horizon_dataset_update_federation = "localDev"
      horizon_dataset_update_purge_rate = 1
      horizon_dataset_update_sigevent = [[type: "INFO", purgeRate: 1], [type: "WARN", purgeRate: 1], [type: "ERROR", purgeRate: 1]]
      //SecurityServiceInfo
      gov.nasa.horizon.security.host = "https://tie-2-aws.jpl.nasa.gov"
      gov.nasa.horizon.security.port = 3040
      gov.nasa.horizon.security.realm = "HORIZON-MANAGER"

      //Host and port of inventory service
      gov.nasa.horizon.inventory.url = "http://tie-2-aws.jpl.nasa.gov:3130"
      gov.nasa.horizon.inventory.user = "gibs"
      gov.nasa.horizon.inventory.pass = "gibs"
  }
}
