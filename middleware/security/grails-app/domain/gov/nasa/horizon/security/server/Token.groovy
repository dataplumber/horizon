package gov.nasa.horizon.security.server

class Token {
	
	static belongsTo = [ realm:Realm]
	String user
	String token
	Long createDate = new Date().getTime();	
	
	static mapping = {
		user column: 'USER_NAME'
      id generator:'sequence', params:[sequence:'token_id_seq']
	}
}
