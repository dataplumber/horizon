package gov.nasa.horizon.sigevent

import grails.converters.*

class SysUserController {
   def sysUserService
   
   def show = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def notify = sysUserService.show(params)
         
         def formatter = new FormatterSysUserSetting(true)
         try {
            String content = formatter.format(responseFormat, [notify])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def update = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def event = sysUserService.update(params)
         
         def formatter = new FormatterSysUserSetting()
         try {
            String content = formatter.format(responseFormat, [event])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
}
