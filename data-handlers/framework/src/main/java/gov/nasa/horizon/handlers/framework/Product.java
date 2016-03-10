/*****************************************************************************
 * Copyright (c) 2014 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
package gov.nasa.horizon.handlers.framework;

import gov.nasa.horizon.common.api.file.FileProduct;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * A Product belongs to a Product Type and it contains one or more files.
 *
 * @author T. Huang
 * @version $Id: $
 */
public class Product {

   private ProductType productType;
   private String name;
   Date created;
   Date ingestStart;
   Date ingestStop;
   String shadowLocation;
   String stageLocation;

   List<FileProduct> fileProducts = new Vector<>();

   public Product(ProductType productType, String name) {
      this.productType = productType;
      this.name = name;
   }

   public ProductType getProductType() {
      return this.productType;
   }

   public String getName() {
      return this.name;
   }

   public void setCreated (Date created) {
      this.created = created;
   }

   public Date getCreated() {
      return this.created;
   }

   public void setIngestStart(Date ingestStart) {
      this.ingestStart = ingestStart;
   }

   public Date getIngestStart() {
      return this.ingestStart;
   }

   public void setIngestStop(Date ingestStop) {
      this.ingestStop = ingestStop;
   }

   public Date getIngestStop() {
      return this.ingestStop;
   }

   public void setShadowLocation(String shadowLocation) {
      this.shadowLocation = shadowLocation;
   }

   public String getShadowLocation() {
      return this.shadowLocation;
   }

   public void setStageLocation(String stageLocation) {
      this.stageLocation = stageLocation;
   }

   public String getStageLocation() {
      return this.stageLocation;
   }

   public void addFileProduct(FileProduct fileProduct) {
      this.fileProducts.add(fileProduct);
   }

   public void clearFileProducts() {
      this.fileProducts.clear();
   }

   public Iterator<FileProduct> getFiles() {
      return this.fileProducts.iterator();
   }
}
