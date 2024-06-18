package utility;

import emailutil.SendEmail;
import logutil.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class BuildCriticalAndWarningData extends GenerateHTMLBody {
    Map<String, String> criticaldata = new HashMap<>();
    Map<String, String> warningdata = new HashMap<>();

    String htmlText;
    String warningThreshold;
    String criticalThreshold;
    String htmlbody;

    public void buildCriticalAndWarningData(String[][] strarrayoutputdata) throws Exception {
        htmlText = Constants.HTML_TABLEHEADER;
        for (int i = 1; i < Arrays.stream(strarrayoutputdata).count(); i++) {

            warningThreshold = strarrayoutputdata[i][5];
            criticalThreshold = strarrayoutputdata[i][6];
            Log.info("Queue name: "+strarrayoutputdata[i][0]);
            Log.info("Warning threshold: " + strarrayoutputdata[i][5]);
            Log.info("Critical threshold: " + strarrayoutputdata[i][6]);
            Log.info("Result count: " + strarrayoutputdata[i][11]);

            if (strarrayoutputdata[i][11].isBlank() || strarrayoutputdata[i][11].isEmpty()) {
                Log.info("Result value is blank for Alert: " + strarrayoutputdata[i][0]);
                // System.out.println("Result value is blank for Alert: " + strarrayoutputdata[i][1]);
            } else {
                try{
                    if ((Integer.parseInt(strarrayoutputdata[i][11]) <= Integer.parseInt(strarrayoutputdata[i][5])) && (Integer.parseInt(strarrayoutputdata[i][11]) > Integer.parseInt(strarrayoutputdata[i][6]))) {
                        //Add Alert name to Warning table
                        Log.info("Adding item to warning hashmap: " + strarrayoutputdata[i][0]);
                        warningdata.put(strarrayoutputdata[i][0],
                                strarrayoutputdata[i][11]);
                    } else if ((Integer.parseInt(strarrayoutputdata[i][11]) <= Integer.parseInt(strarrayoutputdata[i][6]))) {
                        //Add Alert name to Warning table
                        Log.info("Adding item to critical hashmap: " + strarrayoutputdata[i][0]);
                        criticaldata.put(strarrayoutputdata[i][0],
                                strarrayoutputdata[i][11]);
                    } else {
                        Log.info("No warning or critical values");
                    }
                }
                catch (NumberFormatException e){
                    warningdata.put(strarrayoutputdata[i][0],
                            strarrayoutputdata[i][11]);
                }

            }
        }

        SendEmail email=new SendEmail();
        email.setFromMailId(Constants.SENDMAIL_FROM);
        email.setToMailId(Constants.MAIL_TO);
        if (!criticaldata.isEmpty()) {
            //System.out.println("html body: "+generateHtmlBody(criticaldata));
           htmlbody= String.format(Constants.CRITICAL_MAILBODY,generateHtmlBody(criticaldata));
            Log.info("Sending Critical email alert");
            email.setEmailSubject(Constants.CRITICAL_MAILSUBJECT);
            email.setEmailBody(htmlbody);
            email.sendEmail();
        }
        else{
            Log.info("No critical alerts found to send email notification");
        }
        if (!warningdata.isEmpty()) {
            htmlbody= String.format(Constants.WARNING_MAILBODY,generateHtmlBody(warningdata));
            Log.info("Sending warning email alert");
            email.setEmailSubject(Constants.WARNING_MAILSUBJECT);
            email.setEmailBody(htmlbody);
            email.sendEmail();
        }
        else{
            Log.info("No warning alerts found to send email notification");
        }

    }

}
