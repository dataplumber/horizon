class AuthenticationFilters {
   def filters = {
       loginCheck(controller:'*', action:'*') {
           before = {
				if(!session.loggedIn && (!actionName.equals('login') && !actionName.equals('doLogin'))) {
                  redirect(controller:'webtool',action:'login')
                  return false
               }
           }
			   	
           }
   }
}
