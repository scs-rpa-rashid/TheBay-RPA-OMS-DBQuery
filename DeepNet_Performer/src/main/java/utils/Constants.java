package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
    public String STR_USER_EMAIL_ADDRESS="deepnet.rpabot@hbc.com";
    public String STR_URL_TO_LOGIN_PAGE ="https://admin.safeid.io/Account/IdentityProviderSelection";
    public String STR_PASSWORD ="A-g[fhxG.b)G<87G";

    public String badRequesterror="Error: Failed to remove token(s): BadRequest";
    public  int INT_TIME_TO_WAIT =120;

    public String getQueueOfFileUploader(int time,String status,String state) {
        String SQL = "SELECT  *\n" +
                "FROM   RPADev.SCS_IAM_Deepnet.workitem\n" +
                "WHERE   state='"+state+"' and create_timestamp  >= DATEADD(MINUTE, -"+time+", GETDATE()) and status='"+status+"'";
        return SQL;
    }
    public String TABLE_NAME_DEEPNET_WORKITEM ="RPADev.SCS_IAM_Deepnet.workitem";
    public String SQL_JDBC_URL ="jdbc:sqlserver://thebay-rds-uipath-dev.cyeuvydpkw6m.us-east-1.rds.amazonaws.com:1433;databaseName=TheBayUipathOrchestratorDev;encrypt=true;trustServerCertificate=true";
    public String SQL_USER_NAME = "bayrpasqladmin";
    public String qnameCSVWriter="SCS_IAM_Deepnet_CSV_Writer";// use scs
    public String qnameFileUploader="SCS_IAM_Deepnet_FileUploader";
    public String stateNameFileUploader="File_Uploader";
    public String qnamePerformer="SCS_IAM_DeepNet_Performer";
    public String stateNamePerformer="Performer";
    public String statusInprogress="InProgress";
    public int intHoursBack=5;
    public String reportPath="\\\\10.124.234.5\\FileServer\\Deepnet_Termination\\report\\";
    public String statusNew="New";
    public List<String> columnNames= Arrays.asList("work_item_id","queue_name","state","status","detail","retry");
    public String statusSuccess="Successful";
    public String tableNameWorkItem="RPADev.SCS_IAM_Deepnet.workitem";
    public String statusFailed="Failed";
    public String statusRetried="Retried";
    public String columnStatus="status";

    public String columnReason="reason";
    public String columnOutput="output";

    public String stateNameCSVWriter="CSV_Writer";
    public String SQL_PASS_WORD = "chlp7#r!b=sWa9&7";
    public Map<String, String> ERROR_LIST = new HashMap<>();
    public String SYSTEM_EXCEPTION="System Exception";
    public String BUSINESS_EXCEPTION="Business Exception";
    public int MAX_RETRY=2;

    public String MAIL_FROM="rpa@hbc.com";
    public String  SystemExceptionMailList="karthik.mahalingappa@sakscloudservices.com,haroonrashid.tatagar@sakscloudservices.com,varunkumar.venkatesh@hbc.com";
    public String  BusinessExceptionMailList="karthik.mahalingappa@sakscloudservices.com,haroonrashid.tatagar@sakscloudservices.com,varunkumar.venkatesh@hbc.com";
    public String ReportingMailList="karthik.mahalingappa@sakscloudservices.com,haroonrashid.tatagar@sakscloudservices.com,varunkumar.venkatesh@hbc.com,itsecurityaccess@hbc.com";
    //public String TO="karthik.mahalingappa@sakscloudservices.com,amruta.jayanthbangera@sakscloudservices.com,haroonrashid.tatagar@sakscloudservices.com,jahnavi.madhuranthakam@sakscloudservices.com,priya.rani-m@sakscloudservices.com,varunkumar.venkatesh@hbc.com,yashaswini.mkotegar@sakscloudservices.com,lishma.chengappa@sakscloudservices.com";
    public String BUSINESS_MAILTO="karthik.mahalingappa@sakscloudservices.com";

    public String processName;
}

