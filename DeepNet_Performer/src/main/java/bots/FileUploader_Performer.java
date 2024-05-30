package bots;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.QueueItem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BulkUpload;
import pages.LoginToDeepnetPage;
import queueutils.QueueItemUtils;
import utils.*;

import java.time.Duration;
import java.util.*;

public class FileUploader_Performer {
    static WebDriver driver;
    public static String strExceptionmessage;
    static String column;
    static Constants constant = new Constants();
    static String detail;
    static String token;
    static int id, id_of_file_Uploader;
    static String upn;
    static String productid;
    static String manufacturecode;
    static String serialNumber;
    static String modelcode;
    static String work_item_id = null;
    static JsonObject json;
    static JSONObject failuereJson = new JSONObject();
    static JSONArray jsonArray = new JSONArray();
    public static WebDriverWait waitTime;
    static int retryCount = 0;
    static ArrayList<Integer> idAddedToCSV=new ArrayList<>();
    static JSONObject failureReason = new JSONObject();
    static boolean canGo=true;
    static JSONObject updatedDetail;
    static List<String> columns;
    static List<Object> values;
    static List<Object> columnValues;
    static QueueItemUtils queueUtils;
    static QueueItem queueItem;
    static Map<String, String> checkListForEmptyFields = new HashMap<>();
    static ArrayList<String> emptyFields=new ArrayList<>();
    static boolean isFirstTransaction=true;

    // static CheckQueue queue = new CheckQueue();

    public static void run() throws Exception {
            queueUtils = new QueueItemUtils();
          Log.info("Getting queue items with state CSV_writer");
            while (canGo) {
                constant.processName = "SCS_IAM_DeepNet_File_Uploader";
                // insert File_uploader transaction with merged json.
                queueItem = queueUtils.getQueueItem(constant.tableNameWorkItem, constant.qnameCSVWriter);
                /*resultSet = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                        constant.SQL_PASS_WORD, constant.SQL_QUERY_TO_SEARCH_FILE_UPLOADER2);*/

                if (queueItem.getWorkItemId()!=null) {
                    try {
                        isFirstTransaction=false;
                        id =queueItem.getId();
                        work_item_id =queueItem.getWorkItemId();
                        retryCount =queueItem.getRetry();
                        //update status as InProgress
                        columns = Arrays.asList(constant.columnStatus);
                        values = Arrays.asList(constant.statusInprogress);
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                        /*UtilClass.updateDatabase(column, "InProgress", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        updatedDetail = new JSONObject();
                        detail = queueItem.getDetail();
                        json = JsonParser.parseString(detail).getAsJsonObject();
                        upn = json.get("upn").getAsString();
                        checkListForEmptyFields.put("upn",upn);
                        serialNumber = json.get("serial Number").getAsString();
                        checkListForEmptyFields.put("serial Number",serialNumber);
                        token = json.get("token").getAsString();
                        checkListForEmptyFields.put("token",token);
                        productid = json.get("product Id").getAsString();
                        checkListForEmptyFields.put("product Id",productid);
                        manufacturecode = json.get("manufacture Code").getAsString();
                        checkListForEmptyFields.put("manufacture code",manufacturecode);
                        modelcode = json.get("model code").getAsString();
                        checkListForEmptyFields.put("model code",modelcode);
                        // which field is empty?
                        if (!upn.equals("") && !token.equals("") && !productid.equals("") && !manufacturecode.equals("") && !modelcode.equals("")) {
                            updatedDetail.put("upn", upn);
                            updatedDetail.put("product Id", productid);
                            updatedDetail.put("model code", modelcode);
                            updatedDetail.put("serial Number", serialNumber);
                            updatedDetail.put("manufacture Code", manufacturecode);
                            updatedDetail.put("token", token);
                            idAddedToCSV.add(id);
                        } else {
                            columns = Arrays.asList(constant.columnStatus);
                            values = Arrays.asList(constant.statusFailed);
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                           /* UtilClass.updateDatabase(column, "Failed", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                    constant.SQL_PASS_WORD);*/
                            for (Map.Entry<String,String> entry : checkListForEmptyFields.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if(value.equals("")){
                                    emptyFields.add(key);
                                }
                            }
                            strExceptionmessage = "This "+emptyFields+" field in detail column json fields are empty at id:" + id;
                            Log.info("This "+emptyFields+" field in detail column json fields are empty at id:" + id);
                            failureReason.put("ExceptionType", "Business");
                            failureReason.put("FailureReason", strExceptionmessage);
                            columns = Arrays.asList(constant.columnReason);
                            values = Arrays.asList(failureReason.toString());
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                           /* UtilClass.updateDatabase("reason", detail, id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                    constant.SQL_PASS_WORD);*/
                            throw new BusinessException(strExceptionmessage);
                        }
                        jsonArray.put(updatedDetail);
                    } catch (BusinessException e) {
                        failuereJson.put("ExceptionType", "Business");
                        failuereJson.put("FailureReason", e.getMessage().replace("'", "\""));
                        columns = Arrays.asList(constant.columnStatus);
                        values = Arrays.asList(constant.statusFailed);
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                       /* UtilClass.updateDatabase(column, "Failed", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        detail = failuereJson.toString();
                        columns = Arrays.asList(constant.columnReason);
                        values = Arrays.asList(detail);
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id);
                        /*UtilClass.updateDatabase("reason", detail, id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                        UtilClass.sendEmail(constant.BUSINESS_EXCEPTION, e.getMessage(), constant.processName,id,constant.tableNameWorkItem,retryCount);
                    } catch (Exception e) {
                        //In case of any failure, add failure reason with Exception type and exception reason
                        if (retryCount < constant.MAX_RETRY) {
                            retryCount++;
                            // set status as Retried
                            failuereJson.put("ExceptionType", "System");
                            failuereJson.put("FailureReason", e.getMessage().replace("'","\""));
                            columns = Arrays.asList(constant.columnStatus,constant.columnReason);
                            values = Arrays.asList(constant.statusRetried,failuereJson.toString());
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
                            //UtilClass.updateDatabase("status", "Retried", id_of_file_Uploader, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                            //insert new row
                            columnValues = Arrays.asList(work_item_id, constant.qnameCSVWriter, constant.stateNameCSVWriter, constant.statusNew, detail, retryCount);
                            queueUtils.addQueueItem(constant.tableNameWorkItem, constant.columnNames, columnValues);
                            UtilClass.sendEmail(constant.SYSTEM_EXCEPTION, strExceptionmessage, constant.processName,id,constant.tableNameWorkItem,retryCount);
                            //  UtilClass.insertDataIntoDb(constant.SQL_INSERT_QUERY_FOR_FILE_UPLOAD, work_item_id, queue_name, state, "New", detail, retryCount, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                        } else {
                            failuereJson.put("ExceptionType", "System");
                            failuereJson.put("FailureReason", e.getMessage().replace("'","\""));
                            columns = Arrays.asList(constant.columnStatus,constant.columnReason);
                            values = Arrays.asList(constant.statusFailed,failuereJson.toString());
                            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
                            //UtilClass.updateDatabase("status", "Failed", id_of_file_Uploader, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                            // UtilClass.updateDatabase("reason", failuereJson.toString(), id_of_file_Uploader, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                        }
                        UtilClass.sendEmail(constant.SYSTEM_EXCEPTION, e.getMessage(), constant.processName,id,constant.tableNameWorkItem,retryCount);
                    }
                } else {
                    canGo = false;
                }

            }
                System.out.println(jsonArray);
                Log.info("Combining of json data was completed successfully.");
                columnValues=Arrays.asList(work_item_id, constant.qnameFileUploader, constant.stateNameFileUploader, constant.statusNew, jsonArray.toString(),0);
                queueUtils.addQueueItem(constant.tableNameWorkItem,constant.columnNames,columnValues);
                Log.info("Transaction with state File_Uploader was inserted successfully.");
               // UtilClass.insertDataIntoDb(constant.SQL_INSERT_QUERY_FOR_FILE_UPLOAD, work_item_id, queue_name, state, "New", jsonArray, 0, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                // To check any new row inserted or not.If inserted then only updated status to success of new entries.
                queueItem=queueUtils.getQueueItem(constant.tableNameWorkItem,constant.qnameFileUploader);
               /* resultSet = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                        constant.SQL_PASS_WORD, constant.SQL_QUERY_TO_SEARCH_FILE_UPLOADER);*/
                if (queueItem.getDetail()!=null) {
                    for (int id = 0; id < idAddedToCSV.size(); id++) {
                        columns = Arrays.asList(constant.columnStatus);
                        values = Arrays.asList(constant.statusSuccess);
                        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, idAddedToCSV.get(id));
                       /* UtilClass.updateDatabase(column, "Success", idAddedToCSV.get(id), constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                    }
                }

    }
        public static void main (String[]args) throws Exception {
            UtilClass util = new UtilClass();
            util.initialiseLog4J();
            // process q item and enter new entry as File Uploader.
            run();
            driver = UtilClass.getDriver();
            PageFactory.initElements(driver, FileUploader_Performer.class);
            waitTime = new WebDriverWait(driver, Duration.ofSeconds(constant.INT_TIME_TO_WAIT));
            // Kill process + login to deepNet
            LoginToDeepnetPage login = new LoginToDeepnetPage(driver);
            login.loginPage();
            // update to csv file
            BulkUpload bulk = new BulkUpload(driver);
            bulk.fileUpload();

        }
    }

