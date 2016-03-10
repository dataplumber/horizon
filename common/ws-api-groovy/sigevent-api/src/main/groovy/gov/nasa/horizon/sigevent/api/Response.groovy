/**
 * 
 */
package gov.nasa.horizon.sigevent.api

/**
 * @author axt
 *
 */
public class Response {
   String error
   String content
   
   public Response() {
   }
   
   public boolean hasError() {
      return (error)
   }
   
   public List<Map> getResult() {
      def list = []
      
      if(!hasError()) {
         def xml = new XmlParser().parseText(content)
         def items = xml.Content[0].children()[0]
         items.children().each {item ->
            //println "item: "+item.name()
            
            def map = [:]
            item.children().each {element ->
               //println "\t"+element.name()+": "+element.text()
               map.put(element.name(), element.text())
            }
            list.add(map)
         }
      }
      
      return list
   }
}
