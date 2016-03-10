package gov.nasa.horizon.sigevent

class SysNotifyController {
   def sysNotifyService
   
   def create = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         SysNotify notify = sysNotifyService.create(params)
         
         def formatter = new FormatterSysNotify()
         try {
            String content = formatter.format(responseFormat, [notify])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response: "+exception.toString()+": "+exception.getMessage(), true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      log.info(response.toString())
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def list = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def result = sysNotifyService.list(params)
         
         int pages = sysNotifyService.getPages(result[SysNotifyService.CountName])
         String page = (params.page) ? params.page : "0"
         
         def formatter = new FormatterSysNotify()
         try {
            String content = formatter.format(responseFormat, result[SysNotifyService.ItemsName])
            
            response.set("OK", content)
            response.addParameter(
               new ResponseParameter(Response.ParameterAvailablePages, pages.toString())
            )
            response.addParameter(
               new ResponseParameter(Response.ParameterCurrentPage, page.toString())
            )
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def show = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def notify = sysNotifyService.show(params)
         
         def formatter = new FormatterSysNotify(true)
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
         def notify = sysNotifyService.update(params)
         
         def formatter = new FormatterSysNotify()
         try {
            String content = formatter.format(responseFormat, [notify])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      log.info(response.toString())
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def delete = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def notify = sysNotifyService.delete(params)
         
         def formatter = new FormatterSysNotify()
         try {
            String content = formatter.format(responseFormat, [notify])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      log.info(response.toString())
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
}
