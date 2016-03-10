package gov.nasa.horizon.inventory.model

public class ProductType {

   //Properties
   Long id
   String identifier
   String title
   String description;
   HashSet<Product> products= new HashSet<Product>();

   //Constructor
   public ProductType(String name) {
      this.identifier=name;
   }
}
