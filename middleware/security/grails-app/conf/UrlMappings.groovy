class UrlMappings {

	static mappings = {
		
		"/auth/$realm/$action/"(controller:"auth")
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/"(controller:"session")
		"/grails"(view:"/grails")
		"500"(view:'/error')
	}
}
