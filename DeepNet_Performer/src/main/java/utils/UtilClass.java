package utils;

import exceptionutil.ApplicationException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import queueutils.QueueItemUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class UtilClass {
    //chrome drivers instantiation, Kill process
    static ChromeOptions option;
    static WebDriver driver;

    public static WebDriver getDriver() {
        createDriver();
        return driver;
    }
    static ResultSet resultSet;
    static Constants constant=new Constants();
    static String strColumnName,status;
    static int serialNumber,modelCode;
    static String  productId,manufacturCode;
    public static ArrayList<String> mailListToReturn=new ArrayList<>();
    public static BufferedReader reader=null;
    public static String file="\\\\10.124.234.5\\FileServer\\Deepnet_Termination\\filesToBeUploaded\\Deepnet_Bulk.csv";
    public static String line="",upn;
    public static List<String> mailLis;
    static JSONObject jsonObject = new JSONObject();
    public static ArrayList<String> getMailFromCSV() throws IOException, SystemException {
        mailListToReturn.clear();
        try{
            reader=new BufferedReader(new FileReader(file));
            while((line= reader.readLine())!=null){
                String[] oneLine=line.split(",");
                mailListToReturn.add(oneLine[1]);
                System.out.println(oneLine[1]);
            }
        }catch(Exception e){
            Log.error("System Exception:"+e.getMessage());
            throw new SystemException("System Exception:"+e.getMessage());
        }finally {
            reader.close();
        }
        return mailListToReturn;
    }
    public static String work_item_id="Deepnet_Bulk.csv_2024-04-10 17:02:04";
    static QueueItemUtils queueUtils;
    public static String queue_name="DeepNet_Performer";
    public static String state="Performer";
    public static String status_while_uploading="New",detail,reason="";
    public static int retry=0;
    static List<Object> columnValues;

    public static void insertEmailEntryToDB() throws IOException, SystemException, ApplicationException {
        int i=0;
    //    try{
            queueUtils = new QueueItemUtils();
            reader=new BufferedReader(new FileReader(file));

            while((line= reader.readLine())!=null){
                if(i!=0) {
                    queue_name="SCS_IAM_DeepNet_Performer";
                    state="Performer";
                    String[] oneLine = line.split(",");
                    serialNumber = (oneLine[0] != null && !oneLine[0].isEmpty()) ? Integer.parseInt(oneLine[0]) : 0;
                    productId = (oneLine[3] != null && !oneLine[3].isEmpty()) ? oneLine[3] : "EMPTY";
                    manufacturCode = (oneLine[2] != null && !oneLine[2].isEmpty()) ? oneLine[2] : "EMPTY";
                    upn = oneLine[1];
                    modelCode = (oneLine[4] != null && !oneLine[4].isEmpty()) ? Integer.parseInt(oneLine[4]) : 0;

                    jsonObject.put("serial Number", serialNumber);
                    jsonObject.put("upn", upn);
                    jsonObject.put("manufacture Code", manufacturCode);
                    jsonObject.put("product Id", productId);
                    jsonObject.put("model code", modelCode);
                    jsonObject.put("token","");
                    // (work_item_id, queue_name, state, status, detail, retry,reason)
                    detail = jsonObject.toString();
                    System.out.println(detail);
//                    insertDataIntoDb(constant.SQL_WORKITEM, work_item_id, queue_name, state,status_while_uploading, detail, retry, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
//                            constant.SQL_PASS_WORD);
                    columnValues = Arrays.asList(work_item_id, constant.qnamePerformer, constant.stateNamePerformer, constant.statusNew, detail,0);
                    queueUtils.addQueueItem(constant.tableNameWorkItem, constant.columnNames, columnValues);
                    queue_name="SCS_IAM_Deepnet_CSV_Writer";
//                    state="CSV_Writer";
//                    status="New";
//                    jsonObject.put("token","87659087");
//                    detail = jsonObject.toString();
//                    UtilClass.insertDataIntoDb(constant.SQL_INSERT_QUERY_FOR_FILE_UPLOAD,work_item_id,queue_name,state,status,detail,0,constant.SQL_JDBC_URL,constant.SQL_USER_NAME,constant.SQL_PASS_WORD);
//                    System.out.println(oneLine[1]);
                }
                i++;
            }
//        }catch(Exception e){
//            Log.error("System Exception:"+e.getMessage());
//            throw new SystemException("System Exception:"+e.getMessage());
//        }finally {
//            reader.close();
//        }
    }

    public static void initInprogress(int id) throws SQLException {
        strColumnName="status";
        status="InProgress";
            UtilClass.updateDatabase(strColumnName, status,id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD);
            System.out.println(id+ " updated as InProgress");
    }
    public static void updateDatabase(String column, String value, int id, String jdbcUrl, String userName, String password) {
        String updateQuery = "UPDATE "+constant.TABLE_NAME_DEEPNET_WORKITEM +" Set "+column+" = '"+value+"' WHERE id='" + id + "'";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, userName, password);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            // Execute the insert statement
            int rowsUpdated  = statement.executeUpdate();
            System.out.println(statement.toString());
            if (rowsUpdated  > 0) {
                System.out.println("Data updated successfully");
            } else {
                System.out.println("Failed to update the data data.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    public static String getDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }
    public static String getMachineName(){
        String machineName = null;
        try {
            // Get the local host
            InetAddress localhost = InetAddress.getLocalHost();
            machineName = localhost.getHostName();
        } catch (UnknownHostException e) {
            // Handle UnknownHostException
            e.printStackTrace();
        }
        return machineName;
    }
    public static String getUserName(){
        String osName = System.getProperty("os.name").toLowerCase();
        String userName;

        if (osName.contains("windows")) {
            // For Windows
            userName = System.getenv("USERNAME");
        } else {
            // For Unix-like systems
            userName = System.getProperty("user.name");
        }
        return userName;
    }
    public static void sendEmail(String type,String failureMsg,String processName,int id,String tableName,int retryCount) throws Exception {
        String machineName=getMachineName();
        String userName=getUserName();
        String failureMessage =failureMsg;
        String dateTime =getDate();
        String body="";
        SendEmail email=new SendEmail();
        // Subject parameters
        String subject =String.format("%s || Failed due to %s || Machine - %s || Robot - %s || %s",processName,type,machineName,userName,dateTime);
        // Body parameters
        if(type.equals("System Exception")){
            email.setTo(constant.SystemExceptionMailList);
            body = String.format("Hi,<br/><br/>Transaction Id:%s || Table Name:%s || Retry count:%s<br/><br/>%s occured:%s<br/><br/>Regards,<br/><br/>RPA BOT",id,tableName,retryCount, type, failureMessage);
        }else {
            body = String.format("Hi,<br/><br/>Transaction Id:%s || Table Name:%s<br/><br/>%s occured:%s<br/><br/>Regards,<br/><br/>RPA BOT",id,tableName, type, failureMessage);
            email.setTo(constant.BusinessExceptionMailList);
        }
        email.setfrom(constant.MAIL_FROM);
        email.setSubject(subject);
        email.setBody(body);
        email.sendEmail();

    }
    public static void sendEmail(String failureMsg,String[] path,String dateTime) throws Exception {
        String failureMessage =failureMsg;
        String body="";
        // Subject parameters
        String subject =String.format("SCS_IAM_Deepnet: Bulk Upload Process - Report - %s",dateTime);
        body = String.format("Hi,<br/><br/>%s<br/><br/>Regards,<br/><br/>RPA BOT",failureMessage);
        SendEmail email=new SendEmail();
        email.setfrom(constant.MAIL_FROM);
        email.setTo(constant.ReportingMailList);
        email.setSubject(subject);
        email.setBody(body);
        email.setAttachments(path);
        email.sendEmail();

    }
    public static void updateDatabase(String column,String value,String mail,String jdbcUrl, String userName, String password) {
        String updateQuery = String.format("UPDATE RPADev.SCS_IAM_Deepnet.workitem SET %s='%s' WHERE state='File Uploader' AND detail LIKE '%%%s%%'",column, value, mail);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, userName, password);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            // Execute the insert statement
            int rowsUpdated  = statement.executeUpdate();
            System.out.println(statement.toString());
            if (rowsUpdated  > 0) {
                System.out.println("Data updated successfully");
            } else {
                System.out.println("Failed to update the data data.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
    public static ResultSet getQueueItemFromDB(String jdbcUrl, String userName, String password, String sql) {
        try {
            Connection connection =DriverManager.getConnection(jdbcUrl, userName, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return null;
        }
    }
    public static void insertDataIntoDb(String sql, String workItemId, String queueName, String state, String status, Object detail, int retry,String jdbcUrl, String userName, String password) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, userName, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set values for the prepared statement
            statement.setString(1, workItemId);
            statement.setString(2, queueName);
            statement.setString(3, state);
            statement.setString(4, status);
            statement.setString(5, String.valueOf(detail));
            statement.setInt(6, retry);

            // Execute the insert statement
            int rowsInserted = statement.executeUpdate();
            System.out.println(statement.toString());
            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully.");
            } else {
                System.out.println("Failed to insert data.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
    public static void insertDataIntoDb(String sql, String workItemId, String queueName, String state, String status, Object detail, int retry,String jdbcUrl, String userName, String password,String reason) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, userName, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set values for the prepared statement
            statement.setString(1, workItemId);
            statement.setString(2, queueName);
            statement.setString(3, state);
            statement.setString(4, status);
            statement.setString(5, String.valueOf(detail));
            statement.setInt(6, retry);
            statement.setString(7,reason);

            // Execute the insert statement
            int rowsInserted = statement.executeUpdate();
            System.out.println(statement.toString());
            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully.");
            } else {
                System.out.println("Failed to insert data.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
    public static void setDriver(WebDriver driver) {
        UtilClass.driver = driver;
    }

    public static void createDriver(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--ignore-ssl-errors=yes");
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--allow-insecure-localhost");
        chromeOptions.addArguments("--allow-running-insecure-content");
        chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--ignore-certificate-errors,--no-sandbox,--headless,disable-gpu");
        chromeOptions.setAcceptInsecureCerts(true);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().deleteAllCookies();
        setDriver(driver);

    }

    public void initialiseLog4J(){
        //put the below in Constants
        DOMConfigurator.configure("log4j.xml");
    }

}
