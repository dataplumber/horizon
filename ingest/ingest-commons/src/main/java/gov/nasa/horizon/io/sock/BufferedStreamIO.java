/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.io.sock;

import gov.nasa.horizon.common.api.util.ChecksumUtility.DigestAlgorithm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Buffered stream I/O class to encapsulate all buffer I/O operation. This class
 * is being used by the socket-based client/server ingest services
 * 
 * @author T. Huang <mailto:thomas.huang@jpl.nasa.gov>Thomas.Huang@jpl.nasa.gov</mailto>
 * @version $Id: BufferedStreamIO.java 1846 2008-09-16 18:34:41Z thuang $
 */
public class BufferedStreamIO {

   private static final Log _logger = LogFactory.getLog(BufferedStreamIO.class);
   
   public static final String SEPERATORSTR = ":";
   public static final char SEPERATORCHAR = ':';

   // Server reply should have 4 elements. It has the following format
   // <errno>:<UUID>:<transactionId>:<msg>
   public static final int REPLYLENGHT = 4;

   public static final int DEFINBUFSIZE = 512;
   public static final int DEFOUTBUFSIZE = 512;
   private static final String RAF_WRITE_MODE = "rw"; 

   private BufferedInputStream _bis;
   private BufferedOutputStream _bos;
   private int _inBufSize = DEFINBUFSIZE;
   private int _outBufSize = DEFOUTBUFSIZE;

   public BufferedStreamIO(BufferedInputStream bis, BufferedOutputStream bos) {
      this._bis = bis;
      this._bos = bos;
   }

   public byte[] genChecksumFromBuffer(byte[] buffer, DigestAlgorithm alg) {
      MessageDigest digest = alg.createDigest();
      if (digest == null)
         return null;
      return digest.digest(buffer);
   }

   public void readBufferFromStream(byte[] buffer, int bufferSize)
            throws IOException {
      BufferedStreamIO._logger.debug("Reading buffer, size " + bufferSize);

      this._readBuffer(this._bis, buffer, bufferSize);
   }

   public byte[] readAndVerifyBufferFromStream(byte[] buffer, int bufferSize,
            DigestAlgorithm alg) throws IOException, VerifyException {
      BufferedStreamIO._logger.debug("Reading buffer, size " + bufferSize
               + "  verify using " + alg.toString());

      if (buffer.length < bufferSize)
         throw new IOException(
                  "Insufficient buffer size for the incoming data.");

      MessageDigest calculatedDigest = alg.createDigest();
      if (calculatedDigest == null) {
         // this should cause the client to stop sending the buffer.
         throw new IOException("Unsupported Message Digest Algorithm: \""
                  + alg.toString());
      }

      DigestInputStream digestis =
               new DigestInputStream(this._bis, calculatedDigest);

      this._readBuffer(digestis, buffer, bufferSize);

      BufferedStreamIO._logger.debug("Now receiving checksum");
      byte[] receivedChecksum = new byte[calculatedDigest.getDigestLength()];
      this._bis.read(receivedChecksum, 0, calculatedDigest.getDigestLength());
      if (MessageDigest.isEqual(receivedChecksum, calculatedDigest.digest()) == false)
         throw new VerifyException("Message digest comparison failed.");

      return calculatedDigest.digest();
   }

   
   /**
    * Reads a block of binary data from stream and writes to output stream.
    * 
    * @param out the output file stream
    * @param offset the offset byte
    * @param length the lenght of byte stream
    * @throws IOException when network/file IO failure
    */

   public void readFileFromStream(OutputStream out, long offset, long length)
         throws IOException {

      byte[] data = new byte[this._inBufSize];
      int buffersize;
      long bytesToRead = length;

      try {

         this._logger.debug("StreamIO - Reading file, size "
               + (bytesToRead + offset));
         this._logger.debug("StreamIO - Input buffer " + this._inBufSize);

         while (bytesToRead > 0) {
            /**
             * Casting of long to int here is OK. The if statement guarantees
             * that by the time we fall into the case where we're doing the
             * casting bytesToRead will be "castable"
             */
            buffersize = this._inBufSize;
            if (this._inBufSize >= bytesToRead)
               buffersize = (int) bytesToRead;

            int retVal;

            if ((retVal = this._bis.read(data, 0, buffersize)) < 1)
               throw new IOException("Unexpected EOF from network peer.");

            // trying to detect buffer write failure such as insufficant
            // memory,
            // etc.
            try {
               out.write(data, 0, retVal);
            } catch (IOException e) {
               this._logger.debug("Buffer write failed.");
               bytesToRead = 0;
               throw e;
            } finally {
               out.flush();
            }
            bytesToRead -= retVal;
         }
      } catch (IOException e) {
         this._logger.debug("Flush remaining data from peer");
         int retVal;
         while (bytesToRead > 0) {
            if (this._inBufSize < bytesToRead)
               retVal = this._bis.read(data, 0, this._inBufSize);
            else
               retVal = this._bis.read(data, 0, (int) bytesToRead);

            // need to add this check here. If the IOException was thrown
            // due to lost of connection, then we need to stop pulling.
            if (retVal < 1)
               break;
            bytesToRead -= retVal;
         }
         throw e; // rethrow the exception.
      } finally {
         if (out != null) {
            // Now sync the file. This call guarantees that dirty buffers
            // associated with the file are written to the physical medium.
            // Similar to the c function fsync.
            // outFile.getFD().sync();
            out.flush();
            out.close();
         }
      }
   }
   
   /**
    * Reads from the (buffered) input stream and writes it to file with the
    * specified offset. A message digest will be cloned and the reference
    * <code>calculatedChecksum</code> updated. If this is a partical file
    * transfer, then the message digest will be first calculated from byte 0 to
    * offset before resume transfer-time calculation of message digest. See
    * <code>MessageDigest</code> for methods to retrieve "checksum":
    * <code>toString()</code> and <code>digest()</code>.
    * 
    * @param fileName the name of the file
    * @param offset the offset value to begin the transfer
    * @param length the number of bytes to be transfered
    * @return the checksum byte array
    * @throws IOException when network/file IO failure
    * @throws VerifyException when checksum failed to verify.
    */

   public byte[] readAndVerifyFileFromStream(DigestAlgorithm alg) throws IOException, VerifyException {
      byte[] data = new byte[this._inBufSize];
      int retVal;
      byte[] calculatedChecksum = null;

      try {
      	BufferedStreamIO._logger.debug("StreamIO - Input buffer " + this._inBufSize);

         MessageDigest inputDigest = alg.createDigest();
         if (inputDigest == null) {
            // this should cause the client to stop sending the buffer.
            throw new IOException("Unsupported Message Digest Algorithm: \""
                     + alg.toString());
         }

         DigestInputStream digestis =
                  new DigestInputStream(this._bis, inputDigest);

         MessageDigest outputDigest = alg.createDigest();
         if (outputDigest == null) {
            // this should cause the client to stop sending the buffer.
            throw new IOException("Unsupported Message Digest Algorithm: \""
                     + alg.toString());
         }
         
         DigestOutputStream digestos =
         	new DigestOutputStream(this._bos, outputDigest);
         
         while ((retVal = digestis.read(data, 0, this._inBufSize)) > 0) {
            try {
               digestos.write(data, 0, retVal);
            } catch (IOException e) {
               BufferedStreamIO._logger.debug("File write failed.");
               throw e;
            }
         }
         
         if (!MessageDigest.isEqual(inputDigest.digest(), outputDigest.digest()))
         	throw new VerifyException("Message digest comparison failed.");

         calculatedChecksum = outputDigest.digest();
      } finally {
      	this._bis.close();
      	this._bos.close();
      }
      return calculatedChecksum;
   }

   protected void _readBuffer(InputStream is, byte[] buffer, int bufferSize)
            throws IOException {
      if (buffer.length < bufferSize)
         throw new IOException(
                  "Insufficient buffer size for the incoming data.");

      int retVal;
      int bytesRead = 0;
      while (bytesRead < bufferSize) {
         retVal = is.read(buffer, bytesRead, bufferSize - bytesRead);
         if (retVal < 1)
            throw new IOException("Unexpected end-of-file from network peer.");
         bytesRead += retVal;
      }
   }

   public MessagePkg readMessage() throws IOException {

      String line = this.readLine();
      BufferedStreamIO._logger.debug(line);
      StringTokenizer tokens =
               new StringTokenizer(line, BufferedStreamIO.SEPERATORSTR);

      if (tokens.countTokens() != BufferedStreamIO.REPLYLENGHT) {
         throw new IOException("Unexpected network response");
      }

      int errno = Integer.parseInt(tokens.nextToken());
      UUID id = UUID.fromString(tokens.nextToken());
      int transactionId = Integer.parseInt(tokens.nextToken());
      String msg = tokens.nextToken();
      return new MessagePkg(errno, id, transactionId, msg);
   }

   public String readLine() throws IOException {
      StringBuffer buf = new StringBuffer();
      int c = 0;
      while ((c = this._bis.read()) != -1) {
         char ch = (char) c;
         if (ch != '\r' && ch != '\n')
            buf.append(ch);

         if (ch == '\n')
            break;
      }
      if (c == -1)
         throw new IOException("Unexpected EOF from network peer.");
      return buf.toString();
   }

   public void writeMessage(MessagePkg msgPkg) throws IOException {
      String msg =
               msgPkg.getErrno() + BufferedStreamIO.SEPERATORSTR
                        + msgPkg.getId() + BufferedStreamIO.SEPERATORSTR
                        + msgPkg.getTransactionId()
                        + BufferedStreamIO.SEPERATORSTR + msgPkg.getMessage();
      this.writeLine(msg);
   }

   public void writeBufferToStream(byte[] buffer, int bufferSize)
            throws IOException {
      BufferedStreamIO._logger.debug("Writing buffer from message to stream");
      this._bos.write(buffer, 0, bufferSize);
      this._bos.flush();
   }

   public byte[] writeAndVerifyBufferToStream(byte[] buffer, DigestAlgorithm alg)
            throws IOException {
      BufferedStreamIO._logger.debug("Writing buffer, size " + buffer.length
               + "  verify using " + alg.toString());

      MessageDigest calculatedDigest = alg.createDigest();
      if (calculatedDigest == null) {
         // this should cause the client to stop sending the buffer.
         throw new IOException("Unsupported Message Digest Algorithm: \""
                  + alg.toString());
      }

      DigestOutputStream digestos =
               new DigestOutputStream(this._bos, calculatedDigest);

      digestos.write(buffer, 0, buffer.length);
      digestos.flush();

      this._bos.write(calculatedDigest.digest(), 0, calculatedDigest
               .getDigestLength());
      this._bos.flush();
      return calculatedDigest.digest();
   }

   public void writeLine(String s) throws IOException {
      final byte[] b = s.getBytes();
      this._bos.write(b, 0, b.length);
      this._bos.write('\n');
      this._bos.flush();
   }

   public void write(String s) throws IOException {
      byte[] b = s.getBytes();
      this._bos.write(b, 0, b.length);
      this._bos.flush();
   }

   public void closeIO() throws IOException {
      this._bos.close();
      this._bis.close();
   }

}
