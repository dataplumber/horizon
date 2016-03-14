package gov.nasa.horizon.manager.job

/**
 * ProductJob
 */
class ProductJob {
   static triggers = {
      simple name: "ProductJob", startDelay: 60000, repeatCount: 0
   }
   def concurrent = false
   def sessionRequired = true
   def productService
   
   def execute() {
      try {
         def methodTime = null
         
         methodTime = new Date()
         productService.checkProductPending()
         log.debug("[LOOKATTHIS]job-product: checkGranulesPending: "+(new Date().getTime() - methodTime.getTime()))
      } catch(Exception exception) {
         log.debug("Exception in ProductJob.", exception)
      } finally {
         try {
            Thread.sleep(1000)
            ProductJob.schedule(1000, 0, [ProductJob:"rescheduled Job"])
         } catch (Exception exception) {
            log.debug("Exception caught in rescheduling ProductJob", exception)
         }
      }
   }
}