package gov.nasa.horizon.security.server

class Verifier {

	String name //LDAP //maps to a class in src/groovy that implements the logic 
	
	public String toString(){
		return name
	}
   static mapping = {
      id generator:'sequence', params:[sequence:'verifier_id_seq']
   }
}
