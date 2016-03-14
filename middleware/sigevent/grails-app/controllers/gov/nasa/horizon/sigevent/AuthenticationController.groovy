package gov.nasa.horizon.sigevent

class AuthenticationController {
   def authenticationService
   
   def authenticate = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         if((!params.username) || (!params.password)) {
            response.set("ERROR", true, "Username and/or password missing.", true)
         } else {
            def result = authenticationService.authenticate(params.username, params.password)
            if (result) {
               def content = "{"
               content += '"message": "Login success!",'
               content += '"username": "'+result.username+'",'
               content += '"role": "'+result.role+'",'
               content += '"admin": "'+result.admin+'"'
               content += "}"
               response.set("OK", true, content, false) 
            } else {
               response.set("ERROR", true, "Username and password does not match.", true)
            }
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
}