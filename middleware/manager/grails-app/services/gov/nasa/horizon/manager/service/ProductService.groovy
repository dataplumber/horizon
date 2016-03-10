/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.ingest.api.Lock
import gov.nasa.horizon.manager.domain.IngProduct
import gov.nasa.horizon.sigevent.api.EventType

class ProductService {
   def grailsApplication
   def sigEventService

   private static final long MAXIMUM_PENDING_DURATION = 1000 * 60 * 60 * 24
   private static final String MAXIMUM_PENDING_DURATION_TEXT = "24 hours"
   private static final int PRODUCTS_PER_PAGING = 10
   private static final String SIG_EVENT_CATEGORY = "HORIZON"
   boolean transactional = false

   public void checkProductPending() {
      Date triggeringDate = new Date(new Date().getTime() - MAXIMUM_PENDING_DURATION)
      int productsIndex = 0
      boolean moreProducts = true

      while (moreProducts) {
         log.debug("Checking granules stuck")

         def criteria = IngProduct.createCriteria()
         def products = criteria.list(
               max: PRODUCTS_PER_PAGING,
               offset: productsIndex,
               order: "desc",
               sort: "updated"
         ) {
            and {
               le("updated", triggeringDate.getTime())
               ne("currentLock", Lock.TRASH.toString())
               productType {
                  federation {
                     eq("name", grailsApplication.config.manager_federation)
                  }
               }
            }
         }

         log.debug("\nproducts stack processing: ${products.size()}")

         products.each { product ->
            sigEventService.send(
                  SIG_EVENT_CATEGORY,
                  EventType.Warn,
                  "Product with id=${product.id} has been stuck for ${MAXIMUM_PENDING_DURATION_TEXT}",
                  "Product with id=${product.id} has been stuck for ${MAXIMUM_PENDING_DURATION_TEXT}"
            )
         }

         productsIndex += PRODUCTS_PER_PAGING
         moreProducts = (products.size() > 0)

         log.debug("Done checking granules stuck")
      }
   }
}
