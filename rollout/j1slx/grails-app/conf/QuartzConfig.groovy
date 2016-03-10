
quartz {
    autoStartup = true
    jdbcStore = false
    waitForJobsToCompleteOnShutdown = true
}

environments {
	development {
		quartz {
			autoStartup = true
		}
	}
	test {
        quartz {
            autoStartup = false
        }
    }
}
