package utility;


import java.time.Duration;
public class Constants {
   public static String LOG4J_XML ="log4j.xml";
   public static String KILL_PROCESS = "taskkill /F /IM chrome.exe /T";
   public static String CHROMEDRIVER_PATH = "C:\\Chrome\\chromedriver-win64" +
           "\\chromedriver.exe";
   public static String OMS_URL = "https://hudsn-prod-1.oms.supply-chain.ibm.com/smcfs/yfshttpdbi/sterlingdbqueryclient.jsp";
   public static  String USER_NAME = "srvc_oms_automation@hbc.com";
   public static String PASSWORD = "HBCINDIAPassw0rd9";
   public static Duration WAIT_TIME = Duration.ofSeconds(60);
   public static String MAIL_PROTOCOL = "imaps";
   public static String IMAP_SERVER = "imap.gmail.com";
   public static int IMAP_PORT = 993;
   public static String MAILBOX_FOLDER = "Inbox";
   public static String SENDER_MAILADDRESS = "ibmacct@iam.ibm.com";
   public static String OTP_MAIL_SUBJECT =
           "[EXTERNAL] Verify your identity"; //space before and after variable
   public static String IMAP_USERNAME = "srvc_oms_automation@hbc.com";
   public static String IMAP_PASSWORD = "wpdcyaevmtbvgbgi";
   public static String INPUT_SHEET = "ProposedTemplate_30Minutes";
   public static String TIME_ZONE="America/New_York";
   public static String TIME_FORMAT="H:mm";
}
