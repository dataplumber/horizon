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
public class SigEvent extends SigEventBase {

   private static Log logger = LogFactory.getLog(SigEvent.class)

   private static final Map Uris = [
         "create": "events/create",
         "list": "events/list",
         "update": "events/update",
         "delete": "events/delete",
         "show": "events/show"
   ]

   public SigEvent(String baseUrl) {
      super(baseUrl)
   }
   
   public SigEvent(String baseUrl, int socketTimeOut) {
      super(baseUrl, socketTimeOut)
   }

   public Response create(EventType eventType,
                          String category,
                          String source,
                          String provider,
                          String computer,
                          String description,
                          Integer pid = null,
                          String data = null) {
      def parameters = [
            "type": eventType.value,
            "category": category,
            "source": source,
            "provider": provider,
            "computer": computer,
            "description": description,
            "pid": pid,
            "data": data
      ]
      def url = createApiUrl(Uris.create)
      SigEvent.logger.debug "url: ${url}"

      return this.getResponse(url, parameters)
   }

   public Response list(Long page = null,
                        String sort = null,
                        String order = null) {
      def parameters = [
            "page": page,
            "sort": sort,
            "order": order
      ]
      def url = createApiUrl(Uris.list)
      SigEvent.logger.debug "url: ${url}"

      return this.getResponse(url, parameters)
   }

   public Response update(int id,
                          Map map) {
      def parameters = [
            "id": id
      ]
      parameters.putAll(map)

      def url = createApiUrl(Uris.update)
      SigEvent.logger.debug "url: ${url}"

      return this.getResponse(url, parameters)
   }

   public Response delete(int id) {
      def parameters = [
            "id": id
      ]
      def url = createApiUrl(Uris.delete)
      SigEvent.logger.debug "url: ${url}"

      return this.getResponse(url, parameters)
   }

   public Response show(int id) {
      def parameters = [
            "id": id
      ]
      def url = createApiUrl(Uris.show)
      SigEvent.logger.debug "url: ${url}"

      return this.getResponse(url, parameters)
   }
}
