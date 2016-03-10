package gov.nasa.horizon.sigevent.api

/**
 *
 */
public class SigConstantTest {
   public void test() {
      def response
      
      def sigConstant = new SigConstant("http://localhost:9090/sigevent")
      /*
      response = sigEvent.create(EventType.Info, "podaac_test", "source", "atsuya", "computer", "description", 10, "data data")
      println "create: "+(response.hasError() ? "FAILED: "+response.content : "OK")
      */
      /*
      response = sigNotify.create(
         EventType.Error,
         "test",
         NotifyMethod.Email,
         "atsuya.takagi@gmail.com",
         20,
         NotifyFormat.Json,
         NotifyContent.Complete,
         "this is note"
      )
      */
      response = sigConstant.listResponseFormats()
      println "create: "+(response.hasError() ? "FAILED: "+response.error : "OK")
      response.getResult().each {
         println "entry:"
         it.each {key, value ->
            println "\t"+key+": "+value
         }
      }
   }
   
   public static void main(String[] args) {
      new SigConstantTest().test()
   }
}
