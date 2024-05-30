package bots;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.QueueItem;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.*;
import queueutils.QueueItemUtils;
import utils.*;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.BaseFunctions.*;
import static utils.BaseFunctions.getText;

public class CaptureToken_Performer {
    public static WebDriver driver;
    public static WebDriverWait waitTime;
    public static String strExceptionmessage;
    public static String name;
    static JSONObject failuereJson = new JSONObject();
    public static String strOtpToken;
    public static String detail;
    public static String reason;
    public static int id;
    public static String value,column;
    public static Constants constant=new Constants();
    static ResultSet resultSet;
    static JSONObject outputJson;
    static String mail;
    static String upn;
    static String strWorkItemId;
    static ArrayList<String> noEmailFound=new ArrayList<>();
    static String status="New";
    @FindBy(xpath = "//*[@id=\"kt_aside_menu\"]/ul/li[10]/a/span[2]")
    static WebElement userDirectoryLink;
    @FindBy(xpath = "//*[@id=\"tblIDS\"]/table/tbody/tr/td[4]/span/div/button/i")
    static WebElement threeDotNavigatorbtn;
    @FindBy(xpath = "//*[@id=\"test\"]/a[1]")
    static WebElement userView;
    @FindBy(id="kt_datatable_search_query")
    static WebElement searchBar;
    @FindBy(xpath = "//*[@id=\"kt_content\"]/div/div/div[1]/div[2]/div[1]/div/div/div/div[3]/button[1]")
    static WebElement searchSubmitBtn;
    @FindBy(xpath = "//*[@id=\"tblADLV\"]/table/tbody/tr/td[4]")
    static WebElement otpToken;
    @FindBy(xpath = "//*[@id=\"tblADLV\"]/table/tbody/tr/td[1]")
    static WebElement nameOfPerson;

    @FindBy(xpath = "//*[@id=\"tblADLV\"]/table/tbody/span")
    static WebElement noRecordFound;

    @FindBy(xpath = "//*[@id=\"tblADLV\"]/table/tbody/tr/td[4]/span/div/div")
    static List<WebElement> tokenList;
    static int retryCount;
    public static String file="C:\\Deepnet_Termination\\filesToBeUploaded\\dummy.csv";
    static List<String> columns;
    static List<Object> values;
    static ArrayList<String> tokens=new ArrayList<>();
    static boolean canGo=true,businessflag=false,systemflag=false;
    public static void run() throws Exception {
        constant.processName = "SCS_IAM_DeepNet_CaptureToken_Performer";

        //Get data from work item table with status as 'New' and get the detail
        //QueueUtils queueUtils=new QueueUtils();
        QueueItemUtils queueUtils = new QueueItemUtils();
        while (canGo) {
            tokens.clear();
            QueueItem queueItem;
            queueItem = queueUtils.getQueueItem(constant.tableNameWorkItem, constant.qnamePerformer);

//            resultSet1 = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
//                    constant.SQL_PASS_WORD, constant.SQL_QUERY_TO_SEARCH);
            try {
                upn = queueItem.getDetail();
                if(upn!=null) {
                    Log.info("Work item table data with queueName DeepNet_Performer and status as new are retrived successfully.");
                    System.out.println(upn);
                    JsonObject jsonObject = JsonParser.parseString(upn).getAsJsonObject();
                    id = queueItem.getId();
                    mail = jsonObject.get("upn").getAsString();
                    retryCount = queueItem.getRetry();
                    strWorkItemId = queueItem.getWorkItemId();
                    columns = Arrays.asList(constant.columnStatus);
                    values = Arrays.asList(constant.statusInprogress);
                    queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                    Log.info("Transaction with Id " + id + "was updated as Inprogress");
                    //queueUtils.updateQueueItem(constant.tableNameWorkItem,"status","InProgress",id);
//                    UtilClass.updateDatabase(column, "InProgress", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
//                            constant.SQL_PASS_WORD);

                    // after setting transaction to Inprogress navigate to user directory
                    try {
                        click(userDirectoryLink, waitTime);
                        Log.info("Navigated to the user directory button");
                        click(threeDotNavigatorbtn, waitTime);
                        Log.info("Navigation to Three dot button");
                        click(userView, waitTime);
                        Log.info("Clicked on userview button");
                        click(searchBar, waitTime);
                        Log.info("clicked on search bar");
                    } catch (Exception e) {
                        throw new Exception("Navigation to search tab failed due to:" + e.getMessage());
                    }
                    //Search for each email and collect the token accordingly.

                    System.out.println("mail:" + mail);
                    String substring = mail.length() > 8 ? mail.substring(mail.length() - 8) : mail;
                    Log.info("Mail validation done successfully");
                    if (substring.equals("@hbc.com")) {
                        strOtpToken = "";
                        try {
                            click(searchBar, waitTime);
                            clearTextField(searchBar, waitTime);
                            sendKeys(searchBar, waitTime, mail.trim());
                            click(searchSubmitBtn, waitTime);
                            Thread.sleep(8000);
                            int sizeOfTokenList = tokenList.size();
                            System.out.println("Size of list:" + sizeOfTokenList);
                            if (sizeOfTokenList == 0) {
                                if (getText(noRecordFound, waitTime).equals("No records found")) {
                                    strExceptionmessage = "No record found for this email " + mail;
                                    failuereJson.put("ExceptionType", "Business");
                                    failuereJson.put("FailureReason", strExceptionmessage);
                                    columns = Arrays.asList(constant.columnStatus,constant.columnReason);
                                    values = Arrays.asList(constant.statusFailed,failuereJson.toString());
                                    queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                                    detail = failuereJson.toString();
                                    noEmailFound.add(mail);
                                    throw new BusinessException(strExceptionmessage);
                                }
                            }
                            for (int i = 1; i <= sizeOfTokenList; i++) {
                                System.out.println("         Came:" + i);
                                WebElement item = driver.findElement(By.xpath("//*[@id=\"tblADLV\"]/table/tbody/tr/td[4]/span/div/div[" + i + "]"));
                                strOtpToken = getText(item, waitTime).substring(0, 8);
                                tokens.add(strOtpToken);
                            }
                        }catch (BusinessException e){
                            throw e;
                        }catch (Exception e) {
                            throw new Exception("Failed while capturing token due to:" + e.getMessage());
                        }


                        //  Add the token to json and update in output

                        if (strOtpToken != "") {
                            Log.info("Token captured successfully for mail " + mail);
                            System.out.println(constant.tableNameWorkItem + "\n" + constant.columnStatus + "\n" + constant.statusSuccess + "\n" + id);
                            // queueUtils.updateQueueItem(constant.tableNameWorkItem, constant.columnStatus,constant.statusSuccess,id);
                          /* UtilClass.updateDatabase(column, "Success", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                    constant.SQL_PASS_WORD);*/
                            outputJson = new JSONObject(upn);
                            outputJson.put("token", strOtpToken);
                            String updatedJsonString = outputJson.toString();
                            columns = Arrays.asList(constant.columnOutput);
                            values = Arrays.asList(updatedJsonString);
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                            Log.info("Token updated in output column of id " + id);
                            // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnOutput,updatedJsonString,id);
                           /* UtilClass.updateDatabase(column, updatedJsonString, id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                    constant.SQL_PASS_WORD);*/
                            //Create transactions for File uploader in work item table when the email token is captured successfully
                            // UtilClass.insertDataIntoDb(constant.SQL_INSERT_QUERY_FOR_FILE_UPLOAD, strWorkItemId, constant.qnameCSVWriter, constant.stateNameCSVWriter, status, outputJson, retryCount, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                            System.out.println("array list size:"+tokens.size());
                            for(int i=0;i<tokens.size();i++) {
                                outputJson = new JSONObject(upn);
                                outputJson.put("token", tokens.get(i));
                                updatedJsonString = outputJson.toString();
                                List<Object> columnValues = Arrays.asList(strWorkItemId, constant.qnameCSVWriter, constant.stateNameCSVWriter, constant.statusNew, updatedJsonString,0);
                                queueUtils.addQueueItem(constant.tableNameWorkItem, constant.columnNames, columnValues);
                                Log.info("New transaction with queue name CSV_Writer was created");
                            }
                            columns = Arrays.asList(constant.columnStatus);
                            values = Arrays.asList(constant.statusSuccess);
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                            Log.info("Transaction with id " + id + " status was updated as Success");

                        } else {
                            //In case of any failure, add failure reason with Exception type and exception reason
                            strExceptionmessage = "Token not found for Email:" + mail;
                            failuereJson.put("ExceptionType", "Business");
                            failuereJson.put("FailureReason", strExceptionmessage);
                            columns = Arrays.asList(constant.columnStatus);
                            values = Arrays.asList(constant.statusFailed);
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                            // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnStatus,constant.statusFailed,id);
                            /* UtilClass.updateDatabase(column, "Failed", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                            detail = failuereJson.toString();
                            columns = Arrays.asList(constant.columnReason);
                            values = Arrays.asList(failuereJson.toString());
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                            // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnReason,failuereJson.toString(),id);
                             /* UtilClass.updateDatabase("reason", detail, id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                            throw new BusinessException(strExceptionmessage);
                        }
                        Log.info("Token capturing was successfull");
                        //Exception handling
                    }else{
                        strExceptionmessage = "Invalid email:" + mail;
                        failuereJson.put("ExceptionType", "Business");
                        failuereJson.put("FailureReason", strExceptionmessage);
                        columns = Arrays.asList(constant.columnStatus,constant.columnReason);
                        values = Arrays.asList(constant.statusFailed,failuereJson.toString());
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                        // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnStatus,constant.statusFailed,id);
                            /* UtilClass.updateDatabase(column, "Failed", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnReason,failuereJson.toString(),id);
                             /* UtilClass.updateDatabase("reason", detail, id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        throw new BusinessException(strExceptionmessage);
                    }
                }else{
                    canGo=false;
                }
            } catch (RuntimeException e){
                throw e;
            }
            catch (BusinessException e) {
                businessflag=true;
                strExceptionmessage=e.getMessage();

            } catch (Exception e) {
                systemflag=true;
                strExceptionmessage=e.getMessage();
            }finally {
                if(businessflag){
                    UtilClass.sendEmail(constant.BUSINESS_EXCEPTION, strExceptionmessage, constant.processName,id,constant.tableNameWorkItem,retryCount);
                    businessflag=false;
                   // throw new BusinessException(strExceptionmessage);
                }else if(systemflag){
                    if (retryCount < constant.MAX_RETRY) {
                        retryCount++;
                        // update status as retried
                        columns = Arrays.asList(constant.columnStatus);
                        values = Arrays.asList(constant.statusRetried);
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                        // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnStatus,constant.statusRetried,id);
                       /* UtilClass.updateDatabase("status", "Retried", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        // update retry reason
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("ExceptionType", "System");
                        jsonObject.addProperty("FailureReason", strExceptionmessage);
                        // Assuming value is properly defined
                        System.out.println("--------------------------------");
                        //Before passing handled whether json contanins single quotes.
                        columns = Arrays.asList(constant.columnReason);
                        values = Arrays.asList(jsonObject.toString().replace("'", "\""));
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                        Log.error("Transaction with id "+id+" was retried due to"+strExceptionmessage);
                        // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnReason,jsonObject.toString().replace("'","\""),id);
                       /* UtilClass.updateDatabase("reason",jsonObject.toString().replace("'","\""), id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        //Insert a new row with updated retry count
                        List<Object> columnValues = Arrays.asList(strWorkItemId, constant.qnamePerformer, constant.stateNamePerformer, constant.statusNew, upn, retryCount);
                        queueUtils.addQueueItem(constant.tableNameWorkItem, constant.columnNames, columnValues);
                        //UtilClass.insertDataIntoDb(constant.SQL_WORKITEM, strWorkItemId, constant.qnameCSVWriter, constant.stateNameCSVWriter, status, upn, retryCount, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                        UtilClass.sendEmail(constant.SYSTEM_EXCEPTION, strExceptionmessage, constant.processName,id,constant.tableNameWorkItem,retryCount);
                        Log.error("Transaction with id "+id+" retried, retry mail was sent");
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        // Add data to the JSON object
                        jsonObject.put("ExceptionType", "System");
                        jsonObject.put("FailureReason", strExceptionmessage);
                        columns = Arrays.asList(constant.columnStatus);
                        values = Arrays.asList(constant.statusFailed);
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                        // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnStatus,constant.statusFailed,id);
                        columns = Arrays.asList(constant.columnReason);
                        values = Arrays.asList(jsonObject.toString().replace("'", "\""));
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                        Log.error("Transaction with Id "+id+" was failed due to:"+strExceptionmessage);
                        // queueUtils.updateQueueItem(constant.tableNameWorkItem,constant.columnReason,jsonObject.toString().replace("'","\""),id);
                       /* UtilClass.updateDatabase("status", "Failed", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);
                        UtilClass.updateDatabase("reason",jsonObject.toString().replace("'","\""), id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        UtilClass.sendEmail(constant.SYSTEM_EXCEPTION, strExceptionmessage, constant.processName,id,constant.tableNameWorkItem,retryCount);
                        Log.error("Transaction with id "+id+" failed, fault mail was sent");
                    }
                    systemflag=false;
                }
            }

        }
      }


    public static void main (String[]args) throws Exception {
        // initialization for log4j
        UtilClass util = new UtilClass();
        util.initialiseLog4J();
        //initialize the driver
        driver = util.getDriver();
        //Initialize page factory
        PageFactory.initElements(driver, CaptureToken_Performer.class);
        waitTime = new WebDriverWait(driver, Duration.ofSeconds(constant.INT_TIME_TO_WAIT));
        //kill process + login to deepnet application
        LoginToDeepnetPage login = new LoginToDeepnetPage(driver);
        login.loginPage();
        run();

        // check G sheet works--
//        SheetsQuickstart sheet=new SheetsQuickstart();
//        sheet.mainStarts(constants.strDummyTokenList);
//        EmailGoogleSheet email=new EmailGoogleSheet();
//        email.mainStarts(constants.dummyToken,"17mZa1feO1cDmh5-InFa32FAQUMasyvXP73--pUnxZrk");

    }

}
