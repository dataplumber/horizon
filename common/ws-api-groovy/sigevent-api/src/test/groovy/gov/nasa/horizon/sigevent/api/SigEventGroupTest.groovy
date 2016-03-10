package gov.nasa.horizon.sigevent.api

/**
 *
 */
public class SigEventGroupTest {
   public void test() {
      def response
      
      def sigGroup = new SigEventGroup("http://localhost:9090/sigevent")
      /*
      response = sigEvent.create(EventType.Info, "podaac_test", "source", "atsuya", "computer", "description", 10, "data data")
      println "create: "+(response.hasError() ? "FAILED: "+response.content : "OK")
      */
      response = sigGroup.create(EventType.Error, "test", 30)
      println "create: "+(response.hasError() ? "FAILED: "+response.error : "OK")
      response.getResult().each {
         println "entry:"
         it.each {key, value ->
            println "\t"+key+": "+value
         }
      }
   }
   
   public static void main(String[] args) {
      new SigEventGroupTest().test()
   }
}
