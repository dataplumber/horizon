package gov.nasa.horizon.inventory.model

class Product {

   //Properties
   Long id
   Long ptId
   String name
   Date startTime
   Date stopTime
   Date createTime
   Date archiveTime
   Integer versionNum
   String status
   String rootPath
   String relPath
   HashSet<ProductArchive> productFiles= null;

   //Methods
   public void addFile(ProductArchive pa)
   {
      productFiles.add(pa)
   }

   public boolean removeFiles(ProductArchive pa)
   {
      if(!productFiles.isEmpty())
         return productFiles.remove(pa)
      else
         return false;
   }

   public boolean equals(Product p)
   {
      if(this.id != p.getId())
         return false;
      if(!this.name.equals(p.getName()))
         return false;
      return true;
   }

   //Constructor
   public Product() {
      productFiles = new HashSet<ProductArchive>();
   }
}
