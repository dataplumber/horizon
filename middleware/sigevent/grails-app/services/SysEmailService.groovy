import org.springframework.mail.MailException
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage

/**
 * Simple service for sending emails.
 *
 * Work is planned in the Grails roadmap to implement first-class email
 * support, so there's no point in making this code any more sophisticated
 */
class SysEmailService {
   boolean transactional = false
   MailSender mailSender
   SimpleMailMessage mailMessage // a "prototype" email instance

   /**
    * Send a list of emails
    *
    * @param mails a list of maps
    */
   def notify(mails) throws MailException {
      // Build the mail messages
      def messages = []
      for (mail in mails) {
         // Create a thread safe "sandbox" of the message
         SimpleMailMessage message = new SimpleMailMessage(mailMessage)
         message.from = mail.from
         message.to = mail.to
         message.text = mail.text
         message.subject = mail.subject
         messages << message
      }
      // Send them all together
      //try {
         log.debug("about to send ${messages.size()} messages to:n${messages.to.join('n')}")
         mailSender.send(messages as SimpleMailMessage[])
      //} catch (MailException ex) {
         //println "Failed to send emails"
         //ex.printStackTrace()
      //}
   }
}
