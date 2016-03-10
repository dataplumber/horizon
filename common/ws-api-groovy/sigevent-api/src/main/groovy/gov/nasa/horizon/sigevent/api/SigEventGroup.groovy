/**
 * 
 */
package gov.nasa.horizon.sigevent.api

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author axt
 *
 */
public class SigEventGroup extends SigEventBase {
   private static Log logger = LogFactory.getLog(SigEventGroup.class)

   private static final Map Uris = [
      "create": "groups/create",
      "list": "groups/list",
      "update": "groups/update",
      "delete": "groups/delete",
      "show": "groups/show"
   ]
   
   public SigEventGroup(String baseUrl) {
      super(baseUrl)
   }
   
   public SigEventGroup(String baseUrl, int socketTimeOut) {
      super(baseUrl, socketTimeOut)
   }
   
   public Response create(EventType eventType,
                          String category,
                          long purgeRate) {
      def parameters = [
         "type": eventType.value,
         "category": category,
         "purgeRate": purgeRate
      ]
      def url = createApiUrl(Uris.create)
      logger.debug "url: ${url}"
      
      return this.getResponse(url, parameters)
   }
   
   public Response list(Integer page = null,
                        EventType eventType = null,
                        String category = null,
                        String sort = null,
                        String order = null) {
      def parameters = [
         "page": page,
         "type": (eventType) ? eventType.value : null,
         "category": category,
         "sort": sort,
         "order": order
      ]
      def url = createApiUrl(Uris.list)
      logger.debugger "url: ${url}"

      return this.getResponse(url, parameters)
   }
   
   public Response update(int id,
                          Map map) {
      def parameters = [
         "id": id
      ]
      parameters.putAll(map)
      
      def url = createApiUrl(Uris.update)
      logger.debug "url: ${url}"
      
      return this.getResponse(url, parameters)
   }
   
   public Response delete(int id) {
      def parameters = [
         "id": id
      ]
      def url = createApiUrl(Uris.delete)
      logger.debug "url: ${url}"

      return this.getResponse(url, parameters)
   }
   
   public Response show(int id) {
      def parameters = [
         "id": id
      ]
      def url = createApiUrl(Uris.show)
      logger.debug "url: ${url}"
      
      return this.getResponse(url, parameters)
   }
}
