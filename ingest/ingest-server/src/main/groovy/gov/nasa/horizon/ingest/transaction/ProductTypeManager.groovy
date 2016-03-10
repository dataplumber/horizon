package gov.nasa.horizon.ingest.transaction

class ProductTypeManager {
   
   private static final INSTANCE = new ProductTypeManager()
   private def registry;
   private String serverName
   
   static getInstance() { return INSTANCE }
   private ProductTypeManager() {}
   
   void initialize (registry, String serverName) throws Exception {
      this.registry = registry
      this.serverName = serverName
   }
}
