/**
 * Response
 */
abstract class Response {
   public static final String ParameterType = "Type"
   public static final String ParameterContent = "Content"
   public static final String ParameterAvailablePages = "AvailablePages"
   public static final String ParameterCurrentPage = "CurrentPage"
   protected Map<String, ResponseParameter> parameters
   
   public Response() {
      parameters = new TreeMap<String, ResponseParameter>()
      
      parameters.put(Response.ParameterType, new ResponseParameter(Response.ParameterType, ""))
      parameters.put(Response.ParameterContent, new ResponseParameter(Response.ParameterContent, "", false))
   }
   
   public ResponseParameter getType() {
      return parameters.get(Response.ParameterType)
   }
   
   public void setType(String type) {
      parameters.put(Response.ParameterType, new ResponseParameter(Response.ParameterType, type))
   }
   
   public String getContent() {
      return parameters.get(Response.ParameterContent).value
   }
   
   public void setContent(String content) {
      parameters.put(Response.ParameterContent, new ResponseParameter(Response.ParameterContent, content, false))
   }
   
   public void set(String type, String content) {
      setType(type)
      setContent(content)
   }
   
   public void set(String type, boolean doEscapeType, String content, boolean doEscapeContent) {
      def typeParameter = parameters.get(Response.ParameterType)
      typeParameter.value = type
      typeParameter.doEscape = doEscapeType
      
      def contentParameter = parameters.get(Response.ParameterContent)
      contentParameter.value = content
      contentParameter.doEscape = doEscapeContent
   }
   
   public ResponseParameter getParameter(String name) {
      return parameters.get(name)
   }
   
   public void addParameter(ResponseParameter responseParameter) {
      parameters.put(responseParameter.name, responseParameter)
   }
   
   public void removeParameter(ResponseParameter responseParameter) {
      parameters.remove(responseParameter.name)
   }
   
   public abstract String getInternetMediaType()
   
   public abstract String toString()
}
