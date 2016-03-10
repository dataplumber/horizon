/*
* Copyright (c) 2015 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/

quartz {
    autoStartup = true
    jdbcStore = false
    waitForJobsToCompleteOnShutdown = true
}

environments {
    development {
        quartz {
            autoStartup = false
        }
    }
    test {
        quartz {
            autoStartup = false
        }
    }
    production {
        quartz {
            autoStartup = false
        }
    }
    smap_cal_val {
        quartz {
            autoStartup = false
        }
    }
}
