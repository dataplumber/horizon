/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: EmailUtility.java 244 2007-10-02 20:12:47Z axt $
 */
public class EmailUtility {
   protected EmailUtility() {
   }

   public static void send(String smtpHost, int smtpPort, EmailAddress sender,
         Set<EmailAddress> recipients, String title, String message)
         throws IOException {
      SMTPClient smtpClient = null;

      try {
         smtpClient = new SMTPClient();
         smtpClient.connect(smtpHost, smtpPort);
         if (!SMTPReply.isPositiveCompletion(smtpClient.getReplyCode())) {
            throw new IOException("Failed to connect.");
         }
         smtpClient.login();

         smtpClient.setSender(sender.toString());
         StringBuilder recipientList = new StringBuilder();
         for (EmailAddress recipient : recipients) {
            smtpClient.addRecipient(recipient.toString());

            if (!"".equals(recipientList.toString())) {
               recipientList.append(",");
            }
            recipientList.append(recipient);
         }

         SimpleSMTPHeader header =
               new SimpleSMTPHeader(sender.toString(),
                     recipientList.toString(), title);
         header.addHeaderField("MIME-Version", "1.0");
         header.addHeaderField("Content-Type",
               "text/plain; charset=\"iso-8859-1\"");
         header.addHeaderField("Content-Transfer-Encoding", "8bit");
         Writer writer = smtpClient.sendMessageData();
         writer.write(header.toString());
         writer.write(message);
         writer.close();

         if (!smtpClient.completePendingCommand()) {
            throw new IOException("Failed to complete command.");
         }
      } catch (IOException exception) {
         throw exception;
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (smtpClient.isConnected()) {
            smtpClient.logout();
            smtpClient.disconnect();
         }
      }
   }
}
