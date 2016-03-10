import org.apache.commons.lang.StringEscapeUtils

/**
 * ResponseDojoJson
 */
class ResponseDojoJson extends Response {
   public static final String ParameterLabel = "DojoJson-Label"

   public String getInternetMediaType() {
      return "application/json"
   }

   public String toString() {
      String response = "{"

      if (parameters[ParameterLabel]) {
         response += '"identifier": '
         response += '"' + StringEscapeUtils.escapeJavaScript(parameters[ParameterLabel].value) + '",'
         response += '"label": '
         response += '"' + StringEscapeUtils.escapeJavaScript(parameters[ParameterLabel].value) + '",'
      }

      response += '"items": ['
      parameters.eachWithIndex { key, parameter, index ->
         if (key != ParameterLabel) {
            String entry
            if (!parameter.doEscape) {
               entry = parameter.value
            } else {
               entry = '"type": "' + key + '",'
               /*
                * @TODO this is for dojo. need to come back to this
                */
               entry += '"Value": "",'
               entry += '"' + key + '": '
               entry += '"' + StringEscapeUtils.escapeJavaScript(parameter.value) + '"'
               entry = '{' + entry + '}'
            }
            response += entry

            if ((index < (parameters.size() - 1)) && (entry)) {
               response += ","
            }
         }
      }

      response += "]}"

      return response
   }
}
