package utility;

import emailutil.SendEmail;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SendExceptionMail{

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

    public void sendExceptionMail(String processname,String exceptiontype,
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



        if(exceptiontype.equals("BusinessException")){
            System.out.println(ReadPropertyFile.getProperty(
                    "businessexceptionMailBody"));
            System.out.println(exceptionMessage);
            exceptionMailBody=String.format(ReadPropertyFile.getProperty(
                    "businessexceptionMailBody"),exceptionMessage);
            exceptionMailSubject=String.format(ReadPropertyFile.getProperty(
                            "businessexceptionMailSubject"),getProcessName(),
                    getMachineName(),getUserName(),formattedDateTime);

        }
        if(exceptiontype.equals("SystemException")){
            System.out.println(ReadPropertyFile.getProperty(
                    "systemexceptionMailBody"));
            System.out.println(exceptionMessage);
            exceptionMailBody=String.format(ReadPropertyFile.getProperty(
                    "systemexceptionMailBody"),exceptionMessage);
            exceptionMailSubject=String.format(ReadPropertyFile.getProperty(
                            "systemexceptionMailSubject"),getProcessName(),
                    getMachineName(),getUserName(),formattedDateTime);
        }
        SendEmail email=new SendEmail();
        email.setEmailSubject(exceptionMailSubject);
        email.setEmailBody(exceptionMailBody);
        email.setFromMailId(Constants.SENDMAIL_FROM);
        email.setToMailId(Constants.EXCEPTION_MAILTO);
        email.sendEmail();
    }

}
