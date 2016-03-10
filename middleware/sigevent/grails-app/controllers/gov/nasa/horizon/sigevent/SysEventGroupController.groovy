package gov.nasa.horizon.sigevent

import grails.converters.*


class SysEventGroupController {
   //def sessionFactory
   def sysEventGroupService
   
   def create = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         SysEventGroup group = sysEventGroupService.create(params)
         
         def formatter = new FormatterSysEventGroup()
         try {
            String content = formatter.format(responseFormat, [group])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def list = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def result = sysEventGroupService.list(params)
         
         int pages = sysEventGroupService.getPages(result[SysEventGroupService.CountName])
         String page = (params.page) ? params.page : "0"
         
         def formatter = new FormatterSysEventGroup()
         try {
            String content = formatter.format(responseFormat, result[SysEventGroupService.ItemsName])
            
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
         def group = sysEventGroupService.show(params)
         
         def formatter = new FormatterSysEventGroup(true)
         try {
            String content = formatter.format(responseFormat, [group])
            
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
         def group = sysEventGroupService.update(params)
         
         def formatter = new FormatterSysEventGroup()
         try {
            String content = formatter.format(responseFormat, [group])
            
            response.set("OK", content)
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
         def group = sysEventGroupService.delete(params)
         
         def formatter = new FormatterSysEventGroup()
         try {
            String content = formatter.format(responseFormat, [group])
            
            response.set("OK", content)
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response.", true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
   
   def showByCategory = {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def group = sysEventGroupService.showByCategory(params)
         
         def formatter = new FormatterSysEventGroup(true)
         try {
            String content = formatter.format(responseFormat, group)
            
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
