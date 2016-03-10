package gov.nasa.horizon.ingest.api

class QueryString {
   Map params = [:]

   QueryString(Map params) {
      if (params) {
         this.params.putAll(params)
      }
   }

   void add(String name, Object value) {
      params.put(name, value)
   }

   String toString() {
      def list=[]
      params.each{name, value->
         list << "$name=" + URLEncoder.encode(value.toString())
      }
      return list.join("&")
   }
}
