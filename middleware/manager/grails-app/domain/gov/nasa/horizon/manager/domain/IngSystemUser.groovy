/*
 * Copyright (c) 2008-2013 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 */
package gov.nasa.horizon.manager.domain

/**
 * Definition of System User class
 *
 * @author T. Huang
 * @version $Id: $
 */
class IngSystemUser {

   /**
    * The user name
    */
   String name

   // one way encrypted password
   String password

   /**
    * Full name of the user
    */
   String fullname

   /**
    * The user email address
    */
   String email

   /**
    * Flag for admin privilege
    */
   Boolean admin

   /**
    * Flag for read privilege
    */
   Boolean readAll

   /**
    * Flag for write privilege
    */
   Boolean writeAll

   /**
    * Text description
    */
   String note

   static constraints = {
      name(unique: true, size: 1..180)
      password(nullable: false, size: 1..40)
      fullname(nullable: false, size: 1..40)
      email(nullable: false, email: true)
      admin(nullable: false)
      readAll(nullable: false)
      writeAll(nullable: false)
      note(nullable: true, size: 1..255)
   }

   static mapping = {
      id generator: 'sequence', params: [sequence: 'ing_systemuser_id_seq']
   }
}
