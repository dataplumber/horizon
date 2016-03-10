package gov.nasa.horizon.security.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Client {

	private static Log log = LogFactory.getLog(Client.class)
	
	def api
	def needHost = false;
	def needPort = false;
	def host
	def port
	def SPLITTER = ':='
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*
		 * This class /Main method is a simple command line tool to input
		 * a username, password, and realm and genereate a token for that realm.
		 * It will then genereate the token and create the .maple file for you with the new token and 
		 * any other tokens you already had in there.
		 */
	
		Client c = new Client();
		c.run();
	
	}
	
	public void run(){
		init();

		def realm, user, pass
		
		def console = System.console()
		try{
			if (console) {
				if(needHost)
					host = console.readLine('> Please enter the SecurtyService Host (i.e. https://lanina): ')
				if(needPort)	
					port = console.readLine('> Please enter the SecurtyService Port: ')
				try{
					port = Integer.valueOf(port);
				}catch(Exception){}
					
				realm = console.readLine('> Please enter the realm you want to create a token for: ')
				user = console.readLine('> Please enter your username: ')
				pass = console.readPassword('> Please enter your password: ')
				pass = new String(pass);
				
			} else {
				log.error "Cannot get console."
			}
		}catch(java.io.IOError e){
			log.debug("User canceled input.");
			println "User canceled input..."
			System.exit(0);
		}
		
		execute(host, port, realm,user, pass)
		System.exit(0);
				
	}
	def execute = {host, port, realm,user, pass ->
		log.info("Using configuration: $host | $realm | $user");
		api =  SecurityAPIFactory.getInstance(host,port);
		def listing = readMaple();
		def result = api.tokenize(user,pass,realm)
		if(result == null || result?.size() == 0){
			//error
			println "Error genereating token. User/Pass incorrect."
			return;
		}
		result.each{ 
			def resArray = result.split(':');
			listing.put(resArray[0], resArray[1]);
		}
		writeMaple(listing)
	}
	
	public void init(){
		//read the property file, else ask for data.
		api =  SecurityAPIFactory.getInstance();
		host = api.fetchConfig("host")
		port = api.fetchConfig("port")
		if(host == null)
			needHost = true;
		if(port == null)	
			needPort = true;
	}
	
	//read the hidden token file
	public Map<String,String> readMaple(){
		def maple = System.getProperty("user.home") + "/.maple"
		log.info("Fetching $maple")
		File inFile = new File(maple)
		def mp = [:]
		if(!inFile.exists()){
			return mp;
		}
		inFile.eachLine { line ->
			//split into at msot 2 items.
			def ary = line.split(SPLITTER, 2)
			mp.put(ary[0], ary[1])
		}
		return mp;
	}
	
	//write the hidden token file
	public writeMaple(Map<String,String> mapping){
		def ls = System.getProperty("line.separator");
		def maple = System.getProperty("user.home") + "/.maple"
		
		File file = new File("$maple")
		
		def w = file.newWriter()
		mapping.each{ k, v->
			w << "$k${SPLITTER}$v$ls"
		}
		w.close();
	} 
	
}
