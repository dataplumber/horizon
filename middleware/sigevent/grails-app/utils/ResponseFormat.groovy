/**
 * ResponseFormat
 */
public enum ResponseFormat {
   Xml("XML"),
   Json("JSON"),
   Text("TEXT"),
   DojoJson("DOJO_JSON"),
   Raw("RAW")

   private final String name

   ResponseFormat(String name) {
      this.name = name
   }

   public static ResponseFormat detect(String name) {
      return values().find { it.getName().toUpperCase() == name.toString().toUpperCase() }
   }

   public String getName() {
      return name
   }
}
