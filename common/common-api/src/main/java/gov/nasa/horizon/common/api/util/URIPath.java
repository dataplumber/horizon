/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A utility class to simplify operations related to URI string. This class is an immutable class.
 * The standard URI has the following format (without the query entry) [scheme:][//authority][path]
 * Where the Authority entry can have the following format [user-info@]host[:port]
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id:$
 */
public class URIPath {

   // internal enum for internal lookup table
   private enum FIELD {
      SCHEME, HOST, PORT, PATH, USER, PASSWORD;
   }

   private static Log _logger = LogFactory.getLog(URIPath.class);

   /**
    * Utility method to parse the input URI string to extract data fields
    *
    * @param uri input URI string
    * @return a lookup table used by the caller.
    * @throws URISyntaxException when unable to parse the input URI.
    */
   protected synchronized static Hashtable<FIELD, Object> _parseURI(String uri)
	         throws URISyntaxException {
	      Hashtable<FIELD, Object> result = new Hashtable<FIELD, Object>();

	      // Manually encode the user info component of the URI to catch illegal characters in the username/pass.
	      // (GIBS-852)
	      String rawUserInfo = "", safeUri = "";
    	  Pattern p = Pattern.compile("//(.+:.+)@");
    	  Matcher m = p.matcher(uri);
    	  if(m.find()) {
    		  rawUserInfo = m.group(1);
    		  String[] userInfo = rawUserInfo.split(":");
    		  String safeUserInfo = encodeURIcomponent(userInfo[0]).concat(":").concat(encodeURIcomponent(userInfo[1]));
    		  safeUri = uri.replace(rawUserInfo, safeUserInfo);
    	  }
    	  
    	  if(safeUri != null && !safeUri.equals("")) {
    	      uri = safeUri; //Use the safe URI which has illegal chars encoded in its user info component.
    	  }
    	  
	      URI path = new URI(uri).normalize();
	      if (!path.isAbsolute() || path.getScheme() == null) {
	         try {
	            path = new File(uri).toURL().toURI();
	         } catch (MalformedURLException e) {
	            if (URIPath._logger.isDebugEnabled()) {
	               e.printStackTrace();
	            }
	            throw new URISyntaxException("Unable to parse URI: " + uri + ".", e
	                  .getMessage());
	         }
	      }

	      result.put(FIELD.SCHEME, path.getScheme());
	      result.put(FIELD.PATH, path.getPath());

	      if (path.getHost() != null) {
	         result.put(FIELD.HOST, path.getHost());
	         if (path.getPort() > -1) {
	            result.put(FIELD.PORT, new Integer(path.getPort()));
	         }
	      }

	      String info = path.getUserInfo();
	      if (info != null) {
	         String tokens[] = info.split(":");
	         if (tokens != null && tokens.length == 2) {
	            result.put(FIELD.USER, tokens[0]);
	            result.put(FIELD.PASSWORD, tokens[1]);
	         }
	      }
	      return result;
   }

   /**
    * Method to take the input URI to create a URI Path object by normalizing the URI, extracting user info from the
    * input URI and create a host URI without the file path for authentication table. For URI with file:// scheme, the
    * host URI and user/password will be null.
    *
    * @param uri input URI string
    * @return the URIPath object
    * @throws URISyntaxException when unable to parse the input URI
    */
   public static URIPath createURIPath(String uri) throws URISyntaxException {
      Hashtable<FIELD, Object> tokens = URIPath._parseURI(uri);

      if (tokens.size() == 0)
         throw new URISyntaxException("Unable to process URI.", uri);

      String scheme = tokens.get(FIELD.SCHEME).toString();
      String host = null;
      if (tokens.get(FIELD.HOST) != null)
         host = tokens.get(FIELD.HOST).toString();

      int port = -1;
      if (tokens.get(FIELD.PORT) != null
            && tokens.get(FIELD.PORT) instanceof Integer) {
         port = (Integer) tokens.get(FIELD.PORT);
      }

      String path = tokens.get(FIELD.PATH).toString();

      String user = null;
      String password = null;

      if (tokens.get(FIELD.USER) != null && tokens.get(FIELD.PASSWORD) != null) {
         user = tokens.get(FIELD.USER).toString();
         password = tokens.get(FIELD.PASSWORD).toString();
      }

      return new URIPath(scheme, host, port, path, user, password);
   }

   /**
    * Method to create a URIPath object by normalizing the input URI, removing the user info (if any) from the input URI
    * and create a host URI without the file path for authentication. For URI with file:// scheme, the host URI and
    * user/password will be null.
    *
    * @param uri the input URI string
    * @param user the user name use for connection
    * @param pass the password use for connection
    * @return a URIPath object
    * @throws URISyntaxException when unable to parse the input URI
    */
   public static URIPath createURIPath(String uri, String user, String pass)
         throws URISyntaxException {
      URIPath result = URIPath.createURIPath(uri);
      result._user = user;
      result._password = pass;
      return result;
   }

   private String _scheme = null;
   private String _host = null;
   private int _port = -1;
   private String _path = null;
   private String _user = null;
   private String _password = null;

   /**
    * Internal constructor for set each fields of the URI path.
    *
    * @param scheme the URI scheme
    * @param host the host name (if any)
    * @param port the port (if any)
    * @param path the path to the file
    * @param user the user name
    * @param password the password
    */
   protected URIPath(String scheme, String host, int port, String path,
         String user, String password) {
      this._scheme = scheme;
      this._host = host;
      this._port = port;
      this._path = path;
      this._user = user;
      this._password = password;
   }

   /**
    * Method to return the name of the file referenced by the URI path. If the path contains no file name, then this
    * method returns a null.
    *
    * @return the name of the file
    */
   public synchronized String getFilename() {
      String result = null;
      if (!this._path.endsWith("/")) {
         result =
               this._path.substring(this._path.lastIndexOf("/") + 1, this._path
                     .length());
      }
      return result;
   }

   /**
    * Method to return the host name
    *
    * @return the host name
    */
   public String getHost() {
      return this._host;
   }

   /**
    * Method to return the host URI without the path and user info
    *
    * @return the host URI string
    */
   public synchronized String getHostURI() {
      String result = null;
      if (this._host != null) {
         result = this._scheme + "://" + this._host;
         if (this._port > -1) {
            result += ":" + this._port;
         }
      }
      return result;
   }

   /**
    * Method to return the URI path without the file name
    *
    * @return the URI path
    */
   public synchronized String getHostPath() {
      String result = this.getHostURI();
      result += "/" + this._path.substring(0, this._path.lastIndexOf("/"));
      return result;
   }

   /**
    * Method to return the password
    *
    * @return the user password
    */
   public String getPassword() {
      return this._password;
   }

   /**
    * Method to return the path to the file
    *
    * @return the path to the file
    */
   public String getPath() {
      return this._path;
   }


   /**
    * Method to return only the directory path portion of the URI
    *
    * @return the directory path
    */
   public String getPathOnly() {
      return this._path.substring(0, this._path.lastIndexOf("/"));
   }

   /**
    * Method to return the host port. -1 if a port is not set (i.e. to use the default port).
    *
    * @return the port number
    */
   public int getPort() {
      return this._port;
   }

   /**
    * Method to return the scheme
    *
    * @return the URI scheme
    */
   public String getScheme() {
      return this._scheme;
   }

   /**
    * Method to return the normalized URI without the user info
    *
    * @return the normalized URI.
    */
   public synchronized String getURI() {
      String result = this.getHostURI();
      if (result == null) {
         result = this._scheme + "://";
      }
      result += this._path;

      return result;
   }

   /**
    * Method to return the user name
    *
    * @return the user name
    */
   public String getUser() {
      return this._user;
   }

   /**
    * Method to return true if the URI is local
    *
    * @return true if the URI is local
    */
   public boolean isLocal() {
      return (this._host == null);
   }

   /**
    * Method to set the user password
    *
    * @param password the user password
    */
   public void setPassword(String password) {
      this._password = password;
   }

   /**
    * Method to set the user name
    *
    * @param user the user name.
    */
   public void setUser(String user) {
      this._user = user;
   }
   
   /** 
    * Sanitizes components of a URI by encoding illegal characters. 
    * For best results, do not call this function on an entire URI string,
    * as this will have undesired effects (such as encoding "http://").
    * Best used on specific sub-components of a URI.
    **/
   private static String encodeURIcomponent(String s)
   {
       StringBuilder o = new StringBuilder();
       for (char ch : s.toCharArray()) {
           if (isUnsafe(ch)) {
               o.append('%');
               o.append(toHex(ch / 16));
               o.append(toHex(ch % 16));
           }
           else o.append(ch);
       }
       return o.toString();
   }

   private static char toHex(int ch)
   {
       return (char)(ch < 10 ? '0' + ch : 'A' + ch - 10);
   }

   private static boolean isUnsafe(char ch)
   {
       if (ch > 128 || ch < 0)
           return true;
       return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
   }
}
