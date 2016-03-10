/*
* Copyright (c) 2013 Jet Propulsion Laboratory,
* California Institute of Technology.  All rights reserved
*/
package gov.nasa.horizon.manager.service

import gov.nasa.horizon.ingest.api.Lock
import gov.nasa.horizon.ingest.api.State
import gov.nasa.horizon.manager.domain.IngProduct
import org.springframework.dao.OptimisticLockingFailureException

class ProductStatusService {
   private static final long PENDING_LIMIT_IN_MILLISECOND = 24 * 60 * 60 * 1000
   boolean transactional = true

   def getProducts(int page, int productsPerPage) {
      def productsCriteria = IngProduct.createCriteria()
      def products = productsCriteria.list {
         or {
            'in'("currentState", [State.ABORTED.toString(), State.PENDING_STORAGE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
            and {
               'in'("currentState", [State.PENDING.toString(), State.PENDING_ARCHIVE.toString()])
               lt('updated', (new Date().getTime() - PENDING_LIMIT_IN_MILLISECOND))
            }
         }
         firstResult((page * productsPerPage))
         maxResults(productsPerPage)
         order("updated", 'desc')
      }

      return products
   }

   int getProductsCount() {
      def productsCriteria = IngProduct.createCriteria()
      int count = productsCriteria.count {
         or {
            'in'("currentState", [State.ABORTED.toString(), State.PENDING_STORAGE.toString(), State.PENDING_ARCHIVE_STORAGE.toString()])
            and {
               'in'("currentState", [State.PENDING.toString(), State.PENDING_ARCHIVE.toString()])
               lt('updated', (new Date().getTime() - PENDING_LIMIT_IN_MILLISECOND))
            }
         }
      }

      return count
   }

   IngProduct getProduct(int id) {
      IngProduct product = IngProduct.findById(id);
      return product
   }

   def updateProduct(int id, State state, Lock lock) throws Exception {
      def product = IngProduct.get(id)
      if (!product) {
         throw new Exception("Failed to lock")
      }

      def loop = true
      while (loop) {
         loop = false
         try {
            product.refresh()
            product.currentState = state.toString()
            product.currentLock = lock.toString()
            product.save(flush: true)
         } catch (OptimisticLockingFailureException e) {
            log.trace (e.message, e)
            product.discard()
            loop = true
         }
      }
   }
}
