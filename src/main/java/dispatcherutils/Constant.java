package utils;

public class Constant {

    public static final String LOG4J_XML = "log4j.xml";
    public static final String ASSIGNED_FILE_NAME = "assigned.xlsx" ;
    public static final String UNASSIGNED_FILE_NAME = "unassigned.xlsx" ;
    public static final String[] EXPECTED_HEADERS =  {"serial number", "upn", "manufacturer code", "product code", "model code"};
    public static final String INPROGRESS_FOLDER = "\\Deepnet_Termination" +
            "\\inProgress";
    public static final String ERROR_FOLDER = "\\Deepnet_Termination" +
            "\\error";
    public static final String SUCCESSFUL_FOLDER = "\\Deepnet_Termination" +
            "\\successful";
    public static final String REPORT_FOLDER = "\\Deepnet_Termination" +
            "\\report";
    public static final String INSERT_INTODB_QUERY = "INSERT into RPADev." +
            "SCS_IAM_Deepnet.interim" +
            "(work_item_id, queue_name, state,status, detail, retry)"
            +
            "Values (?,?,?,?,?,?)";
    public static final String QUEUE_NAME = "SCS_IAM_DEEPNET_Dispatcher";
    public static final String Completed =
            "\\Deepnet_Termination" +
            "\\completed";
    public static final String QUEUE_STATE = "Dispatcher";
    public static final String QUEUE_STATUS = "New";
    public static final String SQL_JDBC_URL = "jdbc:sqlserver://thebay-rds-uipath-dev.cyeuvydpkw6m.us-east-1.rds.amazonaws.com:1433;databaseName=TheBayUipathOrchestratorDev;encrypt=true;trustServerCertificate=true";
    public static final String SQL_USER_NAME = "bayrpasqladmin";
    public static final String DB_KEY = "chlp7#r!b=sWa9&7";
    public static final String FETCH_QUEUE_ITEM_QUERY = "SELECT * FROM RPADev.SCS_IAM_Deepnet.interim WHERE status = 'New'";
    public static final int MAX_RETRY = 2;
    public static final String SQL_WORKITEM = "INSERT INTO RPADev.SCS_IAM_Deepnet.workitem (work_item_id, " +
            "queue_name," +
            " state, " +
            "status, detail, retry) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String TABLE_NAME_DEEPNET_WORKITEM = "RPADev.SCS_IAM_Deepnet.workitem";
    public static final String MAIL_FROM = "neha.sharma@hbc.com";
    public static final String TO = "neha.sharma@hbc.com";;
    public static final String COMMON_PROPERTYFILE = "propertyfiles" +
            "/common.properties";
    public static final String WORKITEM_QUEUE = "SCS_IAM_DeepNet_Performer";
    public static String KILL_PROCESS = "taskkill /F /IM excel.exe /T";
    //public static String FILESERVER_PATH =  "\\\\10.237.253.72\\File
    // Server\\";

    public static String FILESERVER_PATH =  "\\\\10.124.234.5\\FileServer";
    public static String INPUT_FOLDER= "\\Deepnet_Termination" +
            "\\input";

    public static String ASSIGNED_FOLDER= "\\Deepnet_Termination" +
            "\\input\\assignment";

    public static String UNASSIGNED_FOLDER= "\\Deepnet_Termination" +
            "\\input\\unassignment";
    public static String FILENAME= "Deepnet_bulk";
    public static String UNASSIGNED_SHEET_NAME= "Deepnet_bulk";
    public static String ASSIGNED_SHEET_NAME= "Deepnet_bulk";
    public static String ASSIGNED_FOLDER_NAME = "AssignedFolder";

    public static String DispatcherprocessName = "SCS_IAM_Deepnet_Dispatcher";
    public static final String MAIL_PROTOCOL="imaps";
    public static final String IMAP_SERVER = "imap.gmail.com";
    public static final int IMAP_PORT = 993;
    public static final String EMAIL_TO_LIST = "karthik.mahalingappa@sakscloudservices.com";
    public static final String EMAIL_FROM = "neha.sharma@hbc.com";
    public static final String EMAIL_PASSWORD = "qmln ohht xrfs hgwu";
    public static final String EMAIL_HOST = "mail.hbc.com";
    public static final String EMAIL_PORT = "25";
    public static final String EMAIL_CC_LIST = "rpa@hbc.com";
}
