/**
 * ResponseRaw
 */
class ResponseRaw extends Response {
   public String getInternetMediaType() {
      return "text/plain"
   }
   
   public String toString() {
      return getContent()
   }
}
