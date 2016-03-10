package gov.nasa.jpl.horizon.sigevent.api;

import java.util.*;
import gov.nasa.jpl.horizon.sigevent.api.*;

public class SigEventTest {
   private static final String SIGEVENT_URL = "http://localhost:9090/sigevent";
   
   public SigEventTest() {
      SigEvent sigEvent = new SigEvent(SIGEVENT_URL);
      /*
      Response response = sigEvent.create(
         EventType.Info,
         "NERV",
         "source",
         "atsuya",
         "computer",
         "description"
      );
      */
      Response response = sigEvent.delete(111111111);
      if(response.hasError()) {
         System.out.println("Error: "+response.getError());
      } else {
         System.out.println("Ok: ");
         
         List<Map> parameters = response.getResult();
         for(Map parameter : parameters) {
            for(Object object : parameter.entrySet()) {
               Map.Entry entry = (Map.Entry)object;
               System.out.println("key: "+entry.getKey()+", value: "+entry.getValue());
            }
         }
      }
   }
   
   public static void main(String[] args) {
      new SigEventTest();
   }
}
