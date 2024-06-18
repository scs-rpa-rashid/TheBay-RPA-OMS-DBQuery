package utility;


import java.time.Duration;
public class Constants {

   public static String DBQUERY_PROPERTYFILE="propertyfiles/dbquery" +
           ".properties";
   public static String LOG4J_XML ="log4j.xml";
   public static String KILL_PROCESS = "taskkill /F /IM chrome.exe /T";

   public static String CHROMEDRIVER_PATH =".\\chromedriver\\chromedriver-win64\\chromedriver-win64\\chromedriver (2).exe";

   public  static String INPUT_FILE="\\\\10.124.234.5\\FileServer\\OMSQueueMonitoring\\ThresholdLimitFile\\BAY OMoC Monitoring via RPA.xlsx";
   public static String OMS_URL = "https://hudsn-prod-1.oms.supply-chain.ibm.com/smcfs/yfshttpdbi/sterlingdbqueryclient.jsp";
   public static  String USER_NAME = "srvc_oms_automation@hbc.com";
   public static String PASSWORD = "HBCINDIAPassw0rd2024";
   public static Duration WAIT_TIME = Duration.ofSeconds(60);
   public static Duration LOADER_WAIT_TIME = Duration.ofSeconds(180);
    public static Duration LOGIN_ERROR_WAIT_TIME=Duration.ofSeconds(10);
   public static String MAIL_PROTOCOL = "imaps";
   public static String IMAP_SERVER = "imap.gmail.com";
   public static int IMAP_PORT = 993;

   public static int EMAIL_DELAY=120000;
   public static String MAILBOX_FOLDER = "Inbox";
   public static String SENDER_MAILADDRESS = "ibmacct@iam.ibm.com";
   public static String OTP_MAIL_SUBJECT =
           "[EXTERNAL] Verify your identity"; //space before and after variable
   public static String IMAP_USERNAME = "rpa@hbc.com";
   public static String IMAP_PASSWORD = "iigpohqqpeghxsmx";
   public static String INPUT_SHEET = "ProposedTemplate_30Minutes";
   public static String TIME_ZONE="America/New_York";
   public static String TIME_FORMAT="H:mm";

   public static String CRITICAL_MAILSUBJECT="CRITICAL - OMS Database Query Monitoring- Alert";
   public static String WARNING_MAILSUBJECT="WARNING/ERROR - OMS Database Query Monitoring- Alert";

   public static String CRITICAL_MAILBODY="Hi Team<br/><br/>The BOT has successfully completed it's run. The queries " +
           "listed below have passed the Critical threshold.<br/><br/>Please see below:<br/><br/>%s<br/><br/>Regards,<br/><br/>OMS BOT";

   public static String WARNING_MAILBODY="Hi Team<br/><br/>The BOT has successfully completed it's run. The queries listed below have either passed the warning threshold or triggered an error in IBM.<br/><br/>Please see below.<br/><br/%s<br/><br/>Regards,<br/><br/>OMS BOT";

public static String MAIL_CC="varunkumar.venkatesh@thebay.com,vishnu.mb@thebay.com,neha.sharma@sakscloudservices.com,haroonrashid.tatagar@sakscloudservices.com";

public static String MAIL_BCC="";

   public  static String SENDMAIL_FROM="rpa@hbc.com";

   public static String SENDMAIL_PASSWORD="xnzicmnejdvfxnzx";

   public static String EXCEPTION_MAILTO="varunkumar.venkatesh@thebay.com,vishnu.mb@thebay.com,neha.sharma@sakscloudservices.com,haroonrashid.tatagar@sakscloudservices.com";

   public static String MAIL_TO="rpa-support@hbc.com,bayomssupport@hbc.com,R02GYUBNHQ85IJYYA45UGZ2B51IU3KTL@hbcdigital.pagerduty.com,5d9529a4-5bc3-4a14-bfb7-76c9d43a1ae5@saks.opsgenie.net,varunkumar.venkatesh@thebay.com,vishnu.mb@thebay.com,neha.sharma@sakscloudservices.com,haroonrashid.tatagar@sakscloudservices.com";

   public static String SMTP_HOST="mail.hbc.com";

   public static int SMTP_PORT=25;

   public static String HTML_TABLEHEADER =
           "<table width='100%' border='2' align='center' style='border-collapse: collapse; border: 2px solid black;'>"
                   + "<tr align='center'>"
                   + "<td style='border: 2px solid black; width: auto;'><b>Alert Name</b></td>"
                   + "<td style='border: 2px solid black; width: auto;'><b>Query Result Count</b></td>"
                   + "</tr>";

   public static String SQLDB_CONNECTION_STRING="jdbc:sqlserver://thebay-rds-uipath-dev.cyeuvydpkw6m.us-east-1.rds.amazonaws.com;" +
           "databaseName=TheBayUipathOrchestratorDev;encrypt=true;trustServerCertificate=true";

   public static String SQLDB_USERNAME="bayrpasqladmin";

   public static String SQLDB_PASSWORD="chlp7#r!b=sWa9&7";

   public static  String QUEUE_NAME="TheBay_OMS_DbQuery";

   public static String INSERT_INTODB_QUERY="INSERT into RPADev.TheBay_OMS_DBQuery" +
           ".interim" +
           "(work_item_id,queue_name,state,status,detail)" +
           " " +
           "Values (?,?,?,?,?)";

   public static String SELECTQUERY_GETQUEUEITEM ="Select * from RPADev.TheBay_OMS_DBQuery.interim where status = ? and work_item_id = ?";

   public static String UPDATEQUERY_GETQUEUEITEM="UPDATE RPADev.TheBay_OMS_DBQuery.interim SET status = ? WHERE status = ? and work_item_id= ?";

}
