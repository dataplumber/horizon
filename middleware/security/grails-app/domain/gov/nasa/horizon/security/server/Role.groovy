package gov.nasa.horizon.security.server

class Role {

	static belongsTo = [realm:Realm]
	
	String roleName
	String roleGroup
	
	public String toString(){
		return "${realm.getVerifier().toString()}($roleName - > $roleGroup)"
	}
   static mapping = {
      id generator:'sequence', params:[sequence:'role_id_seq']
   }
}
