package gov.nasa.horizon.security.server

class SessionController {

   def verifierService
   
    def index = { 
         //get tokens
         def tokens = Token.findAllByUser(session.user)
         if(!session.user) {
            redirect(action:"login")
         }
         else 
            [tokens:tokens]
      }
   
   def login = {
   
    }


   def doLogin = {
      
      def ip = request.getRemoteAddr()
      if(ip == null || ip == '')
         ip = request.getHeader("X-Forwarded-For")
      if(ip == null || ip == '')
         ip = request.getHeader("Client-IP")
      log.debug("IP Address: $ip")
      
      def realm = 'HORIZON-SECURITY'
      def user = params.user
      def pass = params.pass
      
   
      log.debug("logging in as $user");
      
      if(verifierService.authenticate(ip,realm,user,pass)){
         session.user = user;
         session.loggedIn = true
         if(verifierService.authorize(realm, user, 'ADMIN'))
         {
            session.admin = true;
         }
         response.status = 200;
         redirect(uri: '/')
      }
      else{
         flash.error = "Unable to authenticate! Check your username and password and try again."
         response.status = 404;
         redirect(uri: '/session/login')
      }
      
   }
   
   def logout = {
      session.user = null;
      session.loggedIn = false
      log.debug("Successfull logged out.");
      flash.message = "Successfully logged out."
      response.status = 200;
      redirect(uri: '/session/login')
   }
   
}
