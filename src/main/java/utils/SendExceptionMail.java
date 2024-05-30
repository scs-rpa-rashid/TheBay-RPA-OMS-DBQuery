package utils;
import javax.mail.MessagingException;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class SendExceptionMail extends SendEmail {
    String exceptionMailBody;
    String exceptionMailSubject;
    String ProcessName;
    String userName;
    String machineName;
    LocalDateTime now;
    DateTimeFormatter formatter;
    public String getProcessName() {
        return ProcessName;
    }
    public void setProcessName(String processName) {
        ProcessName = processName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getMachineName() {
        return machineName;
    }
    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
    public void sendExceptionMail(ReadPropertyFile propertyFile,
                                  String processname,
                                  String exceptiontype,
                                  String exceptionMessage) throws Exception{
        // Get the current date and time
        now = LocalDateTime.now();
        // Define the desired date and time format
        formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        // Format the current date and time using the defined format
        String formattedDateTime = now.format(formatter);
        //Set username
        setUserName(System.getProperty("user.name"));
        //Set process name
        setProcessName(processname);
        // Get the local host
        InetAddress localhost = InetAddress.getLocalHost();
        // Get the machine name
        machineName = localhost.getHostName();
        setMachineName(machineName);

        SendEmail email=new SendEmail();
        if(exceptiontype.equals("BusinessException")){
            exceptionMailBody=String.format(propertyFile.getProperty(
                    "businessexceptionMailBody"),exceptionMessage);
            exceptionMailSubject=String.format(propertyFile.getProperty(
                            "businessexceptionMailSubject"),getProcessName(),
                    getMachineName(),getUserName(),formattedDateTime);

        }
        if(exceptiontype.equals("SystemException")){
            exceptionMailBody=String.format(propertyFile.getProperty(
                    "systemexceptionMailBody"),exceptionMessage);
            exceptionMailSubject=String.format(propertyFile.getProperty(
                            "systemexceptionMailSubject"),getProcessName(),
                    getMachineName(),getUserName(),formattedDateTime);
        }

        email.setSubject(exceptionMailSubject);
        email.setBody(exceptionMailBody);
        email.setfrom("rpa@hbc.com");
        email.setTo("neha.sharma@sakscloudservices.com");
        email.sendEmail();
    }
    public static void main(String[] args) throws MessagingException, IOException {
    }
}
