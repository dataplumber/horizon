class UrlMappings {

	static mappings = {
		//"/webtool/approve"(controller:"webtool"){action=[POST:"approve"]}
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
