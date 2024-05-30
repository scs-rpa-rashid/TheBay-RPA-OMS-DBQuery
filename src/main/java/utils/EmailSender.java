package utils;

import utils.Constant;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class EmailSender {

    public static void main(String[] args) {
        String to = Constant.EMAIL_TO_LIST;
        String cc = Constant.EMAIL_CC_LIST;
        //String subject = Constant.EMAIL_SUBJECT_WARNING;
       // String plainTextContent = Constant.EMAIL_PLAIN_TEXT_WARNING;
       // String htmlContent = Constant.EMAIL_HTML_TEXT;

       // sendEmail(to, cc, subject, plainTextContent, htmlContent,
               // Constant.EMAIL_FROM
               // ,/*"edwn ocal zysh paaq"*/Constant.EMAIL_PASSWORD,
                //Constant.EMAIL_HOST, Constant.EMAIL_PORT);
    }
    public static void sendEmail(String to, String cc, String subject,
                                 String plainTextContent, String htmlContent,
                                 String from, String password, String smtpHost, String smtpPort) {
        // Set up mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Get the Session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a default MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the sender and recipient addresses
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Add CC recipients if provided
            if (cc != null && !cc.isEmpty()) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
            }

            // Set the subject
            message.setSubject(subject);

            // Create Multipart to combine text and HTML content
            Multipart multipart = new MimeMultipart();

            // Plain text part
            BodyPart textPart = new MimeBodyPart();
            textPart.setText(plainTextContent);
            multipart.addBodyPart(textPart);

            // HTML part
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html");
            multipart.addBodyPart(htmlPart);

            // Set the content of the message
            message.setContent(multipart);

            // Send the message
            Transport.send(message);

            System.out.println("Email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
