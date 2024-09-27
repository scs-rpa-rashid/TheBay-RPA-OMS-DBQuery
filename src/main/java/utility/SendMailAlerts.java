package utility;

import emailutil.SendEmail;
import utility.*;

import java.util.Map;

public class SendMailAlerts extends SendEmail {

    String htmlText;
    public void sendMailAlert(Map<String,String> mapData) throws Exception
    {
        htmlText=Constants.HTML_TABLEHEADER;
        for (Map.Entry<String,String> map : mapData.entrySet())
        {

            htmlText = htmlText + "<tr align='center' border ='2'>"+"<td>" + map.getKey() + "</td>"
                    + "<td>" + map.getValue() + "</td>"+"</tr>";
        }

        if (!mapData.isEmpty())
        {
            sendEmail();
           //sendMail(Constants.CRITICAL_MAILSUBJECT, Constants.CRITICAL_MAILBODY.replace("{0}",htmlText));
        }
    }
}
