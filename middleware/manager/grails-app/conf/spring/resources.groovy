/*
* Copyright (c) 2015 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/

import com.mchange.v2.c3p0.ComboPooledDataSource

beans = {

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
}
