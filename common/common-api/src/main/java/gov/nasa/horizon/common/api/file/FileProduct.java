/*****************************************************************************
 * Copyright (c) 2008 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.file;

import gov.nasa.horizon.common.api.util.ChecksumUtility;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to the actual file product that is ready for download.
 * This interface is designed to wrap the internal representation of
 * a file. A file could be local or remote and have different ways
 * to access it.
 *
 * @author T. Huang {Thomas.Huang@jpl.nasa.gov}
 * @version $Id: $
 */
public interface FileProduct {

   enum ChecksumAlgorithm {
      MD2, MD5, SHA1, SHA256, SHA384, SHA512
   };


   /**
    * Method to close the file object and release any allocated resources.
    *
    * @throws java.io.IOException when unable to close a file object
    */
   void close() throws IOException;

   /**
    * Method to return the fully-qualified URI to the file.
    *
    * @return the file name URI string.
    */
   String getFriendlyURI();

   /**
    * Method to return the input stream to the file for reading.
    *
    * @return the input stream to the actual file
    * @throws java.io.IOException when unable to obtain reference to the internal InputStream
    */
   InputStream getInputStream() throws IOException;

   /**
    * Method to return the last modified time on the file in long integer to simplify locale conversion.
    *
    * @return the last modified time
    */
   long getLastModifiedTime();

   /**
    * Method to return the checksum algorithm used for computing the checksum of this product.
    *
    * @return the checksum algorithm or null if no checksum provided
    */
   ChecksumUtility.DigestAlgorithm getDigestAlgorithm();

   /**
    * Method to set the digest algorithm
    *
    * @param alg the digest algorithm
    */
   void setDigestAlgorithm (ChecksumUtility.DigestAlgorithm alg);

   /**
    * Method to return the checksum value, if available.
    *
    * @return the checksum value or null if no checksum provided.
    */
   String getDigestValue();

   /**
    * Method to set the digest value
    *
    * @param digest the digest value
    */
   void setDigestValue (String digest);

   /**
    * Method to return the base name of the file.
    *
    * @return the base name of the file
    */
   String getName();

   /**
    * Method to return the size of the file.
    *
    * @return the file size.
    */
   long getSize();

   /**
    * Method to return true if the file is readable.
    *
    * @return true if the file is readable
    */
   boolean isReadable();

   /**
    * Method to return true if the file is writeable.
    *
    * @return true if the file is writeable
    */
   boolean isWriteable();

   /**
    * Method to return true if the file exists.
    * 
    * @return true if the file exists.
    */
   boolean exists();
   
   /**
    * Method to delete the file.
    * 
    * @return true if the file is deleted.
    */
   boolean delete();
}
