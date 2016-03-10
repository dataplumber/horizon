package gov.nasa.horizon.inventory.api

public class InventoryException extends Exception {
   private static final long serialVersionUID = 1L;

   public InventoryException() {
      super()
   }

   public InventoryException(String message) {
      super(message);
   }

   public InventoryException(String message, Throwable cause) {
      super(message, cause);
   }

   public InventoryException(Throwable cause) {
      super(cause);
   }
}
