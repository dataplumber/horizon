/*****************************************************************************
 * Copyright (c) 2009 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/
 
package gov.nasa.horizon.ingest.api

import java.security.MessageDigest

/**
 * Utility class to take the input message (e.g. password) and generate
 * a hashed string as a form of encryption.  Of course this form of
 * encryption is a one-way encryption.
 *
 * @author T. Huang
 * @version $Id: $
 */
class Encrypt {
   static String encrypt(byte[] data, String alg="SHA-1") {
      MessageDigest digest = MessageDigest.getInstance(alg)
      byte[] buf = digest.digest(data)
      StringBuffer sb = new StringBuffer("")
      String hex = "0123456789abcdef"
      byte value
      for (i in buf) {
         sb.append(hex.charAt((i & 0xf0)>>4))
         sb.append(hex.charAt(i & 0x0f))
      }
      return sb.toString()
   }
   
   /**
    * Class method to take the input message and encrypt it with the specified
    * algorithm.
    *
    * @param message the message string to be encrypted
    * @param alg the algorthm to be used for the encryption
    * @return the encrypted message
    */
   static String encrypt(String message, String alg="SHA-1") {
      return encrypt(message.bytes, alg)
   }
   
   private Encrypt(){}
}
