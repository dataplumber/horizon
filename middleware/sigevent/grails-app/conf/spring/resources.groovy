/*
* Copyright (c) 2015 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/

import com.mchange.v2.c3p0.ComboPooledDataSource

beans = {
   def config = application.config
   
      mailSender(org.springframework.mail.javamail.JavaMailSenderImpl) {
         host = config.horizon_email_host
      }
   
      mailMessage(org.springframework.mail.SimpleMailMessage) {
         from = 'do_not_reply@horizon.jpl.nasa.gov'
      }
      /*
      globalConnectionFactory(JndiObjectFactoryBean) {
         jndiName = "/ConnectionFactory"
         jndiEnvironment = [
               "java.naming.provider.url": config.horizon_provider_url,
               "java.naming.factory.initial": config.horizon_naming_context,
               "java.naming.factory.url.pkgs": config.horizon_naming_factory
         ]
      }
   
      jmsConnectionFactory(UserCredentialsConnectionFactoryAdapter) {
         targetConnectionFactory = ref(globalConnectionFactory)
         username = config.horizon_jms_username
         password = config.horizon_jms_password
      }
      */

      if(application.config.dataSource.url =~ 'postgresql') {
         System.setProperty(
            "com.mchange.v2.c3p0.management.ManagementCoordinator",
            "com.mchange.v2.c3p0.management.NullManagementCoordinator")
         dataSource(ComboPooledDataSource) {
            driverClass = 'org.postgresql.Driver'
            user = application.config.dataSource.username
            password = application.config.dataSource.password
            jdbcUrl = application.config.dataSource.url
            initialPoolSize = 5
            minPoolSize = 3
            maxPoolSize = 25
            acquireIncrement = 1
            maxIdleTime = 600
            propertyCycle = 60
         }
      }

      if(config.dataSource.url =~ 'oracle') {
         System.setProperty(
            "com.mchange.v2.c3p0.management.ManagementCoordinator",
            "com.mchange.v2.c3p0.management.NullManagementCoordinator")
         dataSource(ComboPooledDataSource) {
            driverClass = 'oracle.jdbc.driver.OracleDriver'
            user = config.dataSource.username
            password = config.dataSource.password
            jdbcUrl = config.dataSource.url
            initialPoolSize = 5
            minPoolSize = 3
            maxPoolSize = 20
            acquireIncrement = 1
            maxIdleTime = 600
            propertyCycle = 60
         }
      }
}
