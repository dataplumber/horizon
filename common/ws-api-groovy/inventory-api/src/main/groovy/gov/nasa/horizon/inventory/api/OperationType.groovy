package gov.nasa.horizon.inventory.api

public enum OperationType  {
   ACQUIRED("ACQUIRED"),
   INGEST("INGEST"),
   INVENTORY("INVENTORY"),
   ARCHIVE("ARCHIVE");

   final String value
   
   public OperationType(value) {
      this.value = value
   }
   
   public String value() {
      return this.value
   }
}
