package gov.nasa.horizon.security.client.core

import groovyx.net.http.HTTPBuilder;
import gov.nasa.horizon.common.api.ssl.*;
import org.apache.commons.httpclient.*
import groovyx.net.http.HttpResponseException
import org.apache.commons.httpclient.methods.*
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.HttpResponseException
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.jsslutils.extra.apachehttpclient.SslContextedSecureProtocolSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import java.security.KeyStore
import org.apache.http.conn.scheme.Scheme
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.Properties;
import java.io.*;
import gov.nasa.horizon.security.client.*

import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.util.List

import gov.nasa.horizon.security.client.api.Command
import gov.nasa.horizon.security.client.api.SecurityAPI
import gov.nasa.horizon.security.client.commands.*

class SecurityImpl implements SecurityAPI {

	private static Log log = LogFactory.getLog(SecurityImpl.class)
	private Integer PORT = 8443 //default
	private String host
	def configObject
	
	public SecurityImpl(){
		log.info("Initializing securityImplementation")
		host = fetchConfig('host')
		try{
			PORT = Integer.valueOf(fetchConfig('port'));
		}catch(Exception e){
			log.debug("Could not parse property 'port'. Using default: 8443.")
		}
		log.info("host set to $host, port: $PORT")
	}
	
	public SecurityImpl(String url, int port){
		log.info("Initializing securityImplementation")
		host = url;
		PORT = port;
	}
	
	@Override
	public boolean authenticate(String user, String pass) {
		def realm  = fetchConfig('realm')
		if(realm == null){
			log.error("No realm specified or listed in configuration file.")
			return false;
		}
		if(pass == null){
			pass = fetchToken(realm)
		}
		
		
		CommandAuthenticate ac = new CommandAuthenticate(realm,user,pass);
		return execute(ac).getBooleanResponse();
		
	}

	@Override
	public boolean authenticate(String user, String pass, String realm) {
		
		if(pass == null){
			pass = fetchToken(realm)
		}
		CommandAuthenticate ac = new CommandAuthenticate(realm,user,pass);
		return execute(ac).getBooleanResponse();
	}

	@Override
	public String tokenize(String user, String pass) {
		def realm  = fetchConfig('realm')
		if(realm == null){
			log.error("No realm specified or listed in configuration file.")
			return null;
		}
		if(pass == null){
			log.error("Password msut be specified to create tokens. Cannot use an existing token to create a new token.")
			return null;
		}
		CommandTokenize ct = new CommandTokenize(realm, user, pass)
		
		return execute(ct).getStringResponse();
	}

	@Override
	public String tokenize(String user, String pass, String realm) {
		if(pass == null){
			log.error("Password msut be specified to create tokens. Cannot use an existing token to create a new token.")
			return null;
		}
		CommandTokenize ct = new CommandTokenize(realm, user, pass)
		return execute(ct).getStringResponse();
	}

	@Override
	public boolean authorize(String user, String group) {
		def realm  = fetchConfig('realm')
		if(realm == null){
			log.error("No realm specified or listed in configuration file.")
			return false;
		}
		CommandAuthorize ca = new CommandAuthorize(realm, user, group)
		return execute(ca).getBooleanResponse();
	}

	@Override
	public boolean authorize(String user, String group, String realm) {
		CommandAuthorize ca = new CommandAuthorize(realm, user, group)
		
		return execute(ca).getBooleanResponse();
	}

	@Override
	public List<String> getRoles(String user) {
		def realm  = fetchConfig('realm')
		if(realm == null){
			log.error("No realm specified or listed in configuration file.")
			return null;
		}
		CommandListRoles clr = new CommandListRoles(realm, user)
		return execute(clr).getListResponse()
		
	}

	@Override
	public List<String> getRoles(String user, String realm) {
		CommandListRoles clr = new CommandListRoles(realm, user)
		return execute(clr).getListResponse()
	}

	private CommandResponse execute(Command c){
		log.debug("Runningc command ${c.getCommandName()}")
		CommandResponse cr = new CommandResponse(false);
		log.info("URL: " + host  + c.getUrl())
		
		def http = new HTTPBuilder(host);
		
		SSLContext sc = SSLFactory.getCustomSSLFactory();
		SslContextedSecureProtocolSocketFactory secureProtocolSocketFactory = 
			new SslContextedSecureProtocolSocketFactory(sc, false);
		secureProtocolSocketFactory.setHostnameVerification(false);
		http.client.connectionManager.schemeRegistry.register( SSLFactory.getNaiveScheme(PORT, sc))
			try{
				http.request( POST, TEXT ) {
					uri.path = c.getUrl()
					uri.query = c.getParams()
	
					response.success = { resp, xml ->
						
						log.debug("Response status: ${resp.statusLine.statusCode}")
						
						return c.processResponse(resp,xml)
						
					}
					// handler for any failure status code:
					response.failure = { resp ->
						log.debug("Unauthorized");
						log.debug("Response status: ${resp.statusLine.statusCode}")
						return c.processResponse(resp,null)
					}
				}
			}catch(Exception e){
				log.debug("Caught exception...", e);
				log.error("Error executing request. Check security service server configuration and status.")
				println "Error executing request. Check security service server configuration and status."
				return c.processResponse(null,null)
			}
	}
	
	public String fetchConfig(String key){
		if(configObject == null)
			loadConfig();
		return configObject?.getProperty("security.service."+key)
	}
	
	private String fetchToken(String realm){
		
		def maple = System.getProperty("user.home") + "/.maple"
		log.debug("Fetching $maple")
		def token = new ConfigSlurper().parse(new File(maple).toURL()).toProperties().getProperty(realm)
		log.debug("Returning $token")
		return token;
	}
	
	public void loadConfig(){
		def propFile = System.getProperty("sapi.properties")
		log.debug("Fetching $propFile")
		if(propFile == null){
			return
		}
		configObject = new ConfigSlurper().parse(new File(propFile).toURL()).toProperties();
	}	
	
   
   public static void main(String[] args) {
      println "weee"
      def sapi = SecurityAPIFactory.getInstance("localhost", 9196)
      sapi.authenticate("calarcon", "calarcon", "HORIZON-MANAGER")
   }
}
