package utils;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SendEmail {
    private String from;
    private String to;
    private String subject;
    private String body;
    private String cc; //optional
    private String bcc; //optional
    private String[] attachments; //optional
    public SendEmail(){
    }
    public SendEmail(String from, String to, String subject, String body, String cc, String bcc, String [] attachments){
        this.from=from;
        this.cc=cc;
        this.to=to;
        this.bcc=bcc;
        this.body=body;
        this.subject=subject;
        this.attachments=attachments;
    }
    // Getters and setters for email attributes
    public String getFrom() {
        return from;
    }
    public void setfrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getCc() {
        return cc;
    }
    public void setCc(String cc) {
        this.cc = cc;
    }
    public String getBcc() {
        return bcc;
    }
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }
    public String[] getAttachments() {
        return attachments;
    }
    public void setAttachments(String[] attachments) {
        this.attachments = attachments;
    }

    public static void main(String args[]) throws MessagingException, IOException {
        SendEmail email=new SendEmail();
        email.setfrom("rpa@hbc.com");
        email.setTo("neha.sharma@sakscloudservices.com");
        email.setSubject("Testing");
        email.setBody("Test");
        email.sendEmail();
    }
    // Method to send the email
    public void sendEmail() throws MessagingException, IOException {
        Properties props = new Properties();
        //set property file path
        InputStream inputStream=new FileInputStream(".\\propertyfiles\\common" +
                ".properties");
        props.load(inputStream);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", props.getProperty("mailServer")); // Set your SMTP host here
        props.put("mail.smtp.port", props.getProperty("mailPort")); // Set your SMTP port here
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(props.getProperty("mailUsername"), props.getProperty("mailPassword"));
            }
        });
        //SendEmail email = new SendEmail();
        //email.setBody("Test");
        // email.setSubject("test");
        // Create a multipart message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(getFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getTo()));
        if (this.cc != null && !this.cc.isEmpty()) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(getCc()));
        }
        if (this.bcc != null && !this.bcc.isEmpty()) {
            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(getBcc()));
        }
        message.setSubject(getSubject());
        // Create the message body
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(getBody(), "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        // Add attachments if any
        if (getAttachments() != null) {
            for (String attachment : getAttachments()) {
                MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(source.getName());
                multipart.addBodyPart(attachmentBodyPart);
            }
        }
        else {
            System.out.println("Skipping null attachment.");
        }
        message.setContent(multipart);
        // Send the email
        Transport.send(message);
        System.out.println("Email sent successfully.");
    }
}