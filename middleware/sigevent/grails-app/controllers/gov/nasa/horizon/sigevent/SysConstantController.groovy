package gov.nasa.horizon.sigevent

import gov.nasa.horizon.sigevent.SysEventGroup

class SysConstantController {
   private static final List EventTypes = [
      "INFO",
      "WARN",
      "ERROR"
   ]
   private static final List MessageFormats = [
      "TEXT",
      "XML",
      "JSON",
      "TWEET"
   ]
   private static final List NotifyContents = [
      "COMPLETE",
      "DESCRIPTION"
   ]
   private static final List NotifyMethods = [
      "EMAIL",
      "JMS",
      "MULTICAST",
      "TWITTER"
   ]
   private static final List ResponseFormats = [
     "XML",
     "JSON",
     "DOJO_JSON",
     "TEXT",
     "RAW"
   ]
   
   def listEventTypes = {
      list(SysConstantController.EventTypes)
   }
   
   def listMessageFormats = {
      list(SysConstantController.MessageFormats)
   }
   
   def listNotifyContents = {
      list(SysConstantController.NotifyContents)
   }
   
   def listNotifyMethods = {
      list(SysConstantController.NotifyMethods)
   }
   
   def listResponseFormats = {
      list(SysConstantController.ResponseFormats)
   }
   
   def listCategories = {
      def categories = []
      
      def groups = SysEventGroup.list("sort": "category", "order": "asc")
      groups.each {group ->
         if(!categories.contains(group.category)) {
            categories.add(group.category)
         }
      }
      list(categories)
   }
   
   private void list(List list) {
      ResponseFormat responseFormat = ResponseFormatUtility.get(params.format)
      Response response = ResponseFactory.create(responseFormat)
      
      try {
         def formatter = new FormatterSysConstant()
         try {
            String content = formatter.format(responseFormat, list)
            
            response.set("OK", content)
            if(ResponseFormat.DojoJson.getName() == responseFormat.getName()) {
               response.addParameter(new ResponseParameter(ResponseDojoJson.ParameterLabel, "Value"))
            }
         } catch(Exception exception) {
            response.set("ERROR", true, "Failed to generate response: "+exception.toString()+": "+exception.getMessage(), true)
         }
      } catch(RuntimeException exception) {
         response.set("ERROR", true, exception.getMessage(), true)
      }
      
      render(contentType: response.getInternetMediaType(), text: response.toString())
   }
}
