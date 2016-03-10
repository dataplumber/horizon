package gov.nasa.horizon.common.api.ssl;

import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.jsslutils.extra.apachehttpclient.SslContextedSecureProtocolSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import java.security.KeyStore;
import org.apache.http.conn.scheme.Scheme;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLFactory {

	public static SSLContext getCustomSSLFactory() {
		SSLContext sc = null;
	   // Create a trust manager that does not validate certificate chains
		
		TrustManager tm = new X509TrustManager(){
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
			}
			public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
			}
		};
		
	   TrustManager[] trustAllCerts =  new TrustManager[] {tm};
	   // Install the all-trusting trust manager
	   try {
		   sc = SSLContext.getInstance("SSL");
		   sc.init(null, trustAllCerts, new java.security.SecureRandom());
	   } catch (Exception e) {	}
	   return  sc;
   }
	
	public static Scheme getNaiveScheme(int port, SSLContext sc){
		return getNaiveScheme("https", port, sc);
	}
	
	public static Scheme getNaiveScheme(String scheme, int port, SSLContext sc){
		SSLSocketFactory sf = new SSLSocketFactory(sc);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return new Scheme(scheme, sf, port);
	}
	
	
	
}
