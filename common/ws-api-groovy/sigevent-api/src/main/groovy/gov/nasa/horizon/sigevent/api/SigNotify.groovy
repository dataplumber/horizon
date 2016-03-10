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
public class SigNotify extends SigEventBase {
   private static Log logger = LogFactory.getLog(SigNotify.class)
   
   private static final Map Uris = [
      "create": "notifies/create",
      "list": "notifies/list",
      "update": "notifies/update",
      "delete": "notifies/delete",
      "show": "notifies/show"
   ]
   
   public SigNotify(String baseUrl) {
      super(baseUrl)
   }
   
   public SigNotify(String baseUrl, int socketTimeOut) {
      super(baseUrl, socketTimeOut)
   }
   
   public Response create(EventType eventType,
      String category,
      NotifyMethod notifyMethod,
      String contact,
      long rate,
      MessageFormat messageFormat,
      NotifyContent notifyContent,
      String note = null) {
      def parameters = [
         "type": eventType.value,
         "category": category,
         "method": notifyMethod.value,
         "contact": contact,
         "rate": rate,
         "messageFormat": messageFormat.value,
         "content": notifyContent.value,
         "note": note
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
      logger.debug "url: ${url}"

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
