/**
 * 
 */
package gov.nasa.horizon.sigevent.api

/**
 * @author axt
 *
 */
public class SigConstant extends SigEventBase {
   private static final Map Uris = [
      "eventTypes": "constants/eventTypes/list",
      "messageFormats": "constants/messageFormats/list",
      "notifyContents": "constants/notifyContents/list",
      "notifyMethods": "constants/notifyMethods/list",
      "responseFormats": "constants/responseFormats/list"
   ]
   
   public SigConstant(String baseUrl) {
      super(baseUrl)
   }
   
   public SigConstant(String baseUrl, int socketTimeOut) {
      super(baseUrl, socketTimeOut)
   }
   
   public Response listEventTypes() {
      def url = createApiUrl(Uris.eventTypes)
      return this.getResponse(url, [:])
   }
   
   public Response listMessageFormats() {
      def url = createApiUrl(Uris.messageFormats)
      return this.getResponse(url, [:])
   }
   
   public Response listNotifyContents() {
      def url = createApiUrl(Uris.notifyContents)
      return this.getResponse(url, [:])
   }
   
   public Response listNotifyMethods() {
      def url = createApiUrl(Uris.notifyMethods)
      return this.getResponse(url, [:])
   }
   
   public Response listResponseFormats() {
      def url = createApiUrl(Uris.responseFormats)
      return this.getResponse(url, [:])
   }
}
