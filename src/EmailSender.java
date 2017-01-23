import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class EmailSender {

    final String senderEmail = "testemailforproject55@gmail.com";
    final String senderPassword = "testpassword1*";
    final String emailSMTPserver = "smtp.gmail.com";
    final String emailServerPort = "587";
    String receiverEmail = null;
    String emailSubject = null;
    String emailBody = null;

    public EmailSender(String receiverEmail, String Subject, String message) {
        this.receiverEmail = receiverEmail;
        this.emailSubject = Subject;
        this.emailBody = message;

        Properties props = new Properties();
        props.put("mail.smtp.user", senderEmail);
        props.put("mail.smtp.host", emailSMTPserver);
        props.put("mail.smtp.port", emailServerPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", emailServerPort);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        SecurityManager security = System.getSecurityManager();

        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);

            Message msg = new MimeMessage(session);
            msg.setText(emailBody);
            msg.setSubject(emailSubject);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(receiverEmail));
            Transport.send(msg);
            System.out.println("send successfully");
        } catch (Exception ex) {
            System.err.println("Error occurred while sending.!");
        }

    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmail, senderPassword);
        }
    }

//    public static void main(String[] args) {
//        EmailSender send = new EmailSender("ashi5393@gmail.com", "Test Email", "Hello Ash!");
//    }

}