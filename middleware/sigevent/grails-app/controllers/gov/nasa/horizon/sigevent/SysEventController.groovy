package gov.nasa.horizon.sigevent

import grails.converters.*

class SysEventController {
   def sysEventService
   
   def create = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         SysEvent event = sysEventService.create(params)
         
         def formatter = new FormatterSysEvent()
         try {
            String content = formatter.format(responseFormat, [event])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
	  log.debug(response.toString())
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def list = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         log.info("before list")
         def result = sysEventService.list(params)
         log.info("after list")
         int pages = sysEventService.getPages(result[SysEventService.CountName])
         String page = (params.page) ? params.page : "0"
         
         def formatter = new FormatterSysEvent()
         try {
            String content = formatter.format(responseFormat, result[SysEventService.ItemsName])
            
            response.set("OK", content)
            response.addParameter(
               new ResponseParameter(Response.ParameterAvailablePages, pages.toString())
            )
            response.addParameter(
               new ResponseParameter(Response.ParameterCurrentPage, page.toString())
            )
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
            log.debug("Ouch", exception)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
         log.debug("Ouchaaa", exception)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def show = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def event = sysEventService.show(params)
         
         def formatter = new FormatterSysEvent(true)
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
   
   def update = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def result = sysEventService.update(params)
         
         def formatter = new FormatterSysEvent()
         try {
            String content = formatter.format(responseFormat, result[SysEventService.ItemsName])
            
            response.set("OK", content)
            response.addParameter(
               new ResponseParameter(Response.ParameterAvailablePages, "1")
            )
            response.addParameter(
               new ResponseParameter(Response.ParameterCurrentPage, "0")
            )
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def delete = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def event = sysEventService.delete(params)
         
         def formatter = new FormatterSysEvent()
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
   
   def data = {
      ResponseFormat responseFormat = ResponseFormat.Raw
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def event = sysEventService.show(params)
         if(!event.data) {
            throw new RuntimeException("Data is not available for SysEvent with ID: "+event.id)
         }
         
         response.set("OK", event.data)
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
}
