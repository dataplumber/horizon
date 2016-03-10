package gov.nasa.horizon.sigevent.api

/**
 *
 */
public class SigEventTest {
   public void test() {
      def response
      
      def sigEvent = new SigEvent("http://localhost:9090/sigevent")
      /*
      response = sigEvent.create(EventType.Info, "podaac_test", "source", "atsuya", "computer", "description", 10, "data data")
      println "create: "+(response.hasError() ? "FAILED: "+response.content : "OK")
      */
      response = sigEvent.delete(161)
      println "create: "+(response.hasError() ? "FAILED: "+response.error : "OK")
      response.getResult().each {
         println "entry:"
         it.each {key, value ->
            println "\t"+key+": "+value
         }
      }
   }
   
   public static void main(String[] args) {
      new SigEventTest().test()
   }
}
