package utils;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class UtilClass {
    //chrome drivers instantiation, Kill process
    static ChromeOptions option;
    static WebDriver driver;

    public static WebDriver getDriver() {
        createDriver();
        return driver;
    }
    static ResultSet resultSet;
    static Constant constant=new Constant();
    static String strColumnName,status;
    static int productId,token,manufacturCode,serialNumber,modelCode;
    public static ArrayList<String> mailListToReturn=new ArrayList<>();
    public static BufferedReader reader=null;
    public static String file="C:\\Deepnet_Termination\\filesToBeUploaded\\Deepnet_Bulk.csv";
    public static String line="",upn;
    public static List<String> mailLis;
    static JSONObject jsonObject = new JSONObject();
    public static ArrayList<String> getMailFromCSV() throws IOException,
            ApplicationException {
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
            throw new ApplicationException("System Exception:"+e.getMessage());
        }finally {
            reader.close();
        }
        return mailListToReturn;
    }
    public static String work_item_id="Deepnet_Bulk.csv_2024-04-10 17:02:04";
    public static String queue_name="DeepNet_Performer";
    public static String state="Performer";
    public static String status_while_uploading="Success",detail,reason="";
    public static int retry=0;



    public static void initInprogress(int id) throws SQLException {
        strColumnName="status";
        status="InProgress";
        UtilClass.updateDatabase(strColumnName, status,id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                Constant.DB_KEY);
        System.out.println(id+ " updated as InProgress");
    }
    public static void updateDatabase(String column, String value, int id, String jdbcUrl, String userName, String password) {
        String updateQuery =
                "UPDATE "+ Constant.TABLE_NAME_DEEPNET_WORKITEM +" Set "+column+" = '"+value+"' WHERE id='" + id + "'";

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
    public static void sendEmail(String type,String failureMsg,String processName) throws Exception {
        String machineName=getMachineName();
        String userName=getUserName();
        String failureMessage =failureMsg;
        String dateTime =getDate();
        // Subject parameters
        String subject =String.format("%s || Failed due to %s || Machine - %s || Robot - %s - %s",processName,type,machineName,userName,dateTime);
        // Body parameters
        String body = String.format("Hi<br/><br/>%s occured:%s<br/><br/>Regards,<br/><br/>RPA BOT",type,failureMessage);
        SendEmail email=new SendEmail();
        email.setfrom(Constant.MAIL_FROM);
        email.setTo(Constant.TO);
        email.setSubject(subject);
        email.setBody(body);
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
        //WebDriverManager.chromedriver().setup();
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
