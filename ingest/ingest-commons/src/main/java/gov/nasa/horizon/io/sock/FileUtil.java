package gov.nasa.horizon.io.sock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Generic utility class for File manipulation
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: FileUtil.java 1846 2008-09-16 18:34:41Z thuang $
 */
public class FileUtil {
   private static final int DEFINBUFSIZE = 512;

   /**
    * Calculate a message digest (checksum) on a local file. No transfer of the
    * file is made.
    * 
    * @param fileName the full file path with name.
    * @return checksum as byte array
    * @throws java.io.IOException when file I/O fail
    */
   public static byte[] getChecksumInByte(String fileName) throws IOException {
      return FileUtil.getChecksum(fileName).digest();
   }

   /**
    * Utility method to return the MessageDigest object reference for the input
    * file.
    * 
    * @param fileName the input file name
    * @return the MessageDigest object reference
    * @throws IOException when file IO failure
    */
   public static MessageDigest getChecksum(String fileName) throws IOException {

      MessageDigest digest;

      try {
         digest = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException nsa) {
         // SHA not implemented. Any checksum access attempt will fail.
         // This is not likely to happen anywhere.
         throw new IOException("SHA checksum algorithm not implemented: "
               + nsa.getMessage());
      }

      File file = new File(fileName);

      FileUtil.updateChecksum(fileName, digest, 0, file.length());
      return digest;
   }

   /**
    * Utility method to update the input message digest with the input data
    * file.
    * 
    * @param fileName the input file name.
    * @param digest the message digest object reference to be updated
    * @param offset the offset
    * @param length the length in bytes
    * @throws IOException when file IO fialure
    */
   public static void updateChecksum(String fileName, MessageDigest digest,
         long offset, long length) throws IOException {

      long bytesToRead = length;

      byte[] data = new byte[FileUtil.DEFINBUFSIZE];

      FileInputStream fin = new FileInputStream(fileName);
      fin.skip(offset);

      while (bytesToRead > 0) {
         int bytesRead;
         int buffersize = FileUtil.DEFINBUFSIZE;
         if (FileUtil.DEFINBUFSIZE >= bytesToRead)
            buffersize = (int) bytesToRead;

         bytesRead = fin.read(data, 0, buffersize);
         if (bytesRead < 1)
            throw new IOException("File reading error.");

         digest.update(data, 0, bytesRead);
         bytesToRead -= bytesRead;
      }
      fin.close();
   }

   /**
    * Calculate a message digest (checksum) on a local file. No transfer of the
    * file is made.
    * 
    * @param fileName the full file path with name.
    * @return checksum in string representation
    * @throws java.io.IOException when file I/O fail
    */
   public static String getStringChecksum(String fileName) throws IOException {
      return FileUtil.checksumToString(FileUtil.getChecksumInByte(fileName));
   }

   /**
    * Convert a checksum to string.
    * 
    * @param checksum byte array representation of checksum
    * @return string representation of checksum
    */
   public static String checksumToString(byte[] checksum) {
      StringBuffer sb = new StringBuffer("");
      String hex = "0123456789abcdef";
      byte value;
      int checksumLength = checksum.length;

      for (int i = 0; i < checksumLength; i++) {
         value = Array.getByte(checksum, i);
         sb.append(hex.charAt((value & 0xf0) >>> 4));
         sb.append(hex.charAt(value & 0x0f));
      }
      return sb.toString();
   }

   /**
    * Convert HEX ascii string to byte array.
    * 
    * @param checksumStr string representation of checksum
    * @return checksum byte array
    */
   public static byte[] stringToChecksum(String checksumStr) {
      int byteValue;
      int strLen = checksumStr.length();
      byte[] checksum = new byte[strLen / 2];

      // Convert string to byte array.
      for (int i = 0; i < strLen; i += 2) {
         byteValue = Integer.parseInt(checksumStr.substring(i, i + 2), 16);
         checksum[i / 2] = (byte) (byteValue & 0x000000ff);
      }
      return checksum;
   }

   /**
    * Utility method to perform an one-way encryption of the input message.
    * 
    * @param msg The input byte sequence.
    * @return The encrypted message.
    */
   public static String encryptMessage(byte[] msg) {
      MessageDigest digest;
      try {
         digest = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException nsa) {
         /*
          * * unable to perform one-way hash, so just return * the message
          * string as it is.
          */
         return new String(msg);
      }
      return FileUtil.checksumToString(digest.digest(msg));
   }
}
