import groovy.xml.MarkupBuilder
//import grails.util.JSonBuilder

/**
 * Formatter
 */
abstract class Formatter {
   private static final Map Methods = [
      (ResponseFormat.Text): ["getBuilderSupportForText", "formatInText"],
      (ResponseFormat.Json): ["getBuilderSupportForJson", "formatInJson"],
      (ResponseFormat.Xml): ["getBuilderSupportForXml", "formatInXml"],
      (ResponseFormat.DojoJson): ["getBuilderSupportForDojoJson", "formatInDojoJson"]
   ]
   boolean complete
   
   public Formatter(boolean complete = false) {
      this.complete = complete
   }
   
   public String format(ResponseFormat responseFormat, List<Object> objects) throws Exception {
      def entry = Methods.find{it.getKey().getName() == responseFormat.getName()}
      if(!entry) {
         throw new Exception("ResponseFormat not supported: "+responseFormat.getName())
      }
      
      def builderMethod = entry.getValue()[0]
      def formatMethod = entry.getValue()[1]
      
      def result = ""
      StringWriter stringWriter = new StringWriter()
      def builderSupport = this."$builderMethod"(stringWriter)
      if(builderSupport) {
         formatWithBuilderSupport(builderSupport, objects)
         result = stringWriter.toString()
      } else {
         result = this."$formatMethod"(objects)
      }
      
      return result
   }
   
   protected String formatInXml(List<Object> objects) throws Exception {
      return null
   }
   
   protected String formatInJson(List<Object> objects) throws Exception {
      return null
   }
   
   protected String formatInText(List<Object> objects) throws Exception {
      return null
   }
   
   protected String formatInDojoJson(List<Object> objects) throws Exception {
      return null
   }
   
   protected abstract void formatWithBuilderSupport(BuilderSupport builderSupport, List<Object> objects) throws Exception
   
   protected BuilderSupport getBuilderSupportForXml(Writer writer) {
      return new MarkupBuilder(writer)
   }
   
   protected BuilderSupport getBuilderSupportForJson(Writer writer) {
      return null
   }
   
   protected BuilderSupport getBuilderSupportForText(Writer writer) {
      return null
   }
   
   protected BuilderSupport getBuilderSupportForDojoJson(Writer writer) {
      return null
   }
}
