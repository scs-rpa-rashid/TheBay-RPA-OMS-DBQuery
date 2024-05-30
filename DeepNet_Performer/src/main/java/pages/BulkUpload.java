package pages;

import com.google.gson.JsonObject;
import com.opencsv.CSVWriter;
import model.QueueItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import queueutils.QueueItemUtils;
import utils.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static pages.SheetsQuickstart.removeEmailFromGoogleSheet;
import static utils.BaseFunctions.*;
import static utils.BaseFunctions.getText;
import static utils.UtilClass.getDate;

public class BulkUpload {
    static File uploadFile;
    static WebDriver driver;
    public static String strExceptionmessage;
    public static String strTotalProccesed;
    public static String strTotalSuccesfullyProcessed;
    public static String strTotalFailed;
    public static String strTotalWarning;
    public static String child;
    public static String parent;
    public static int numberOfFailedMail;
    static Constants constant = new Constants();
    static List<List<String>> updatedLines = new ArrayList<>();
    static String detail;
    static String token;
    static int id, id_of_file_Uploader;
    static QueueItemUtils queueUtils = new QueueItemUtils();
    static String csvFilePath = "\\\\10.124.234.5\\FileServer\\Deepnet_Termination\\filesToBeUploaded\\"; // Specify the path where you want to save the CSV file
    public static String headers = "Serial Number,upn,manufacturer code,product code,model code";
    public static ArrayList<String> mailList=new ArrayList<>();
    public Map<String, String> mailAndToken = new HashMap<>();
    static String upn;
    static String productid;
    static String manufacturecode;
    static String serialNumber;
    static String modelcode;
    static String work_item_id = null;
    static JSONObject failuereJson = new JSONObject();
    static int retryCount=0;

    @FindBy(xpath = "//button[contains(@data-toggle,'dropdown')]")
    static WebElement bulkOpButton;

    @FindBy(id="bulkStatusText")
    static WebElement errorMsgWhileFileUploading;

    @FindBy(xpath = "//span[contains(@class,'navi-text')][1]")
    static WebElement assignmentButton;


    @FindBy(xpath = "//span[text()='Unassignment']")
    WebElement unassigmentbtn;

    @FindBy(xpath = "//*[@id=\"failedRequestTable\"]/table/tbody/tr")
    static List<WebElement> listOfIdentifiers;

    @FindBy(id = "bulkFileInput")
    static WebElement chooseFilebtn;

    @FindBy(id = "cbActivateTokens")
    static WebElement activateTokenCheckBox;

    @FindBy(id = "cbBulkSetDefaultSignInToCode")
    static WebElement sendOtpCheckBox;

    @FindBy(xpath = "//*[@id=\"bulkStatusText\"]")
    static WebElement badRequestError;
    @FindBy(id = "btnStart")
    static WebElement startButton;

    @FindBy(xpath = "//*[@id=\"bulkStats\"]/div/button")
    static WebElement openReportButton;

    @FindBy(id = "totalProcessed")
    static WebElement totalProcessed;

    @FindBy(id = "totalSuccessfullyProcessed")
    static WebElement totalSuccessfullyProcessed;

    @FindBy(id = "totalFailed")
    static WebElement totalFailed;

    @FindBy(id = "totalWarnings")
    static WebElement totalWarnings;

    public static WebDriverWait waitTime;

    @FindBy(id="bulkModalClose2")
    static WebElement closebtn;

    @FindBy(xpath = "//*[@id=\"kt_aside_menu\"]/ul/li[10]/a/span[2]")
    static WebElement userDirectoryLink;

    @FindBy(xpath = "//*[@id=\"tblIDS\"]/table/tbody/tr/td[4]/span/div/button/i")
    static WebElement threeDotNavigatorbtn;

    @FindBy(xpath = "//*[@id=\"test\"]/a[1]")
    static WebElement userView;
    @FindBy(xpath = "//*[@id=\"bulkFileInput\"]")
    static WebElement file_name;

    static JSONObject outputForFileUploader=new JSONObject();
    static JSONObject summaryJsonData=new JSONObject();
    static ArrayList<String> failedEmailList=new ArrayList<>();
    static ArrayList<Object> finalJsonArrayForOutput=new ArrayList<>();
    @FindBy(xpath = "//*[@id=\"warningsTable\"]/table/tbody/tr[2]/td")
    static List<WebElement> warningColumns;

    @FindBy(xpath = "//*[@id=\"warningsTable\"]/table/tbody/tr")
    static List<WebElement> warningRows;
    static ArrayList<Object> warnings=new ArrayList<>();
    static ArrayList<JSONObject> unavailableDataGoogleSheetUpdation;
    static String[] warningTableHeader={"Identifier","Serial Number","Manufacturer Code",
            "Product Code",
            "Model Code",
            "Warning"};

    static List<JSONObject> listOfFailedEmails=new ArrayList<>();
    static List<JSONObject> listOfSuccessEmails=new ArrayList<>();
    static Map<String, String> duplicatemailAndToken;
    static String checkBadrequestExist="";

    public static void captureProcessData(ArrayList<String> mails,Map<String, String> mailAndToken,int id_of_file_Uploader) throws Exception {
      try {
           duplicatemailAndToken=mailAndToken;
            strTotalProccesed = getText(totalProcessed, waitTime);
            strTotalWarning = getText(totalWarnings, waitTime);
            strTotalSuccesfullyProcessed = getText(totalSuccessfullyProcessed, waitTime);
            strTotalFailed = getText(totalFailed, waitTime);
            summaryJsonData.put("Total Processed",strTotalProccesed);
            summaryJsonData.put("Total Succesfully Processed",strTotalSuccesfullyProcessed);
            summaryJsonData.put("Total Failed",strTotalFailed);
            summaryJsonData.put("Total Warnings",strTotalWarning);
            if(strTotalSuccesfullyProcessed.equals("")){
                throw new SystemException("Empty field found");
            }
            if(Integer.parseInt(strTotalSuccesfullyProcessed)>0){
                Log.info("Successfully unassigned");
                columns = Arrays.asList(constant.columnStatus);
                values = Arrays.asList(constant.statusSuccess);
                queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values,id_of_file_Uploader);
                Log.info("Trasaction with id "+id_of_file_Uploader+" status was updated to success.");
            }else{
                Log.info("Failed to unassign");
            }
            if(strTotalFailed.equals("0")){
                System.out.println(mailAndToken);
                outputForFileUploader.put("Success Email",duplicatemailAndToken);
               unavailableDataGoogleSheetUpdation=removeEmailFromGoogleSheet(duplicatemailAndToken);
                Log.info("Email was removed successfully from Google sheet");
            }else if(Integer.parseInt(strTotalFailed)>0){
                // if some email id fails
                click(openReportButton, waitTime);
        driver.getWindowHandles();
        Set<String> st1= driver.getWindowHandles();
        Iterator<String> it2=st1.iterator();
        parent= it2.next();
        child=it2.next();
        driver.switchTo().window(child);
        numberOfFailedMail=listOfIdentifiers.size();
        for(int i=1;i<=numberOfFailedMail;i++){
            WebElement email=driver.findElement(By.xpath("//*[@id=\"failedRequestTable\"]/table/tbody/tr["+i+"]/td[1]/span"));
            WebElement errorMessege=driver.findElement(By.xpath("//*[@id=\"failedRequestTable\"]/table/tbody/tr["+i+"]/td[6]/span"));
            WebElement tokenElement=driver.findElement(By.xpath("//*[@id=\"failedRequestTable\"]/table/tbody/tr["+i+"]/td[2]"));
            String tempEmail=getText(email,waitTime);
            String tempError=getText(errorMessege,waitTime);
            String tempToken=getText(tokenElement,waitTime);
            constant.ERROR_LIST.put(tempEmail,tempError);
            JSONObject failureMailAndToken=new JSONObject();
            failureMailAndToken.put("Email",tempEmail);
            failureMailAndToken.put("Token",tempToken);
            failureMailAndToken.put("FailureReason",tempError);
            mails.remove(tempEmail);
            mailAndToken.remove(tempToken);
            duplicatemailAndToken.remove(tempToken);
            listOfFailedEmails.add(failureMailAndToken);
        }
        System.out.println("mail and corresponding error:\n"+constant.ERROR_LIST);
        if(Integer.parseInt(strTotalWarning)>0){
            for(int i=1;i<=warningRows.size();i++)
            {
                JSONObject warning=new JSONObject();
                for(int j=1;j<=warningColumns.size();j++){
                    WebElement warningElement=driver.findElement(By.xpath("//*[@id=\"warningsTable\"]/table/tbody/tr["+i+"]/td["+j+"]"));
                    warning.put(warningTableHeader[j-1],getText(warningElement,waitTime));
                }
                warnings.add(warning);
            }
        }
        System.out.println("Warnings list:"+warnings);
        // get all success status with state='Deepnet_CSV_Writter'
        driver.close();
        driver.switchTo().window(parent);
        click(closebtn,waitTime);
                System.out.println(mailAndToken);
                outputForFileUploader.put("Success Email",duplicatemailAndToken);
        unavailableDataGoogleSheetUpdation=removeEmailFromGoogleSheet(duplicatemailAndToken);
        Log.info("Some of the failed emails "+failedEmailList+"were not removed from Google sheet,Successfully processed emails removed.");
            }
        } catch(Exception e) {
            strExceptionmessage="System Excepotion"+e.getMessage();
            Log.error(strExceptionmessage);
            throw new SystemException(strExceptionmessage);
        }
        // storing failed emails
        for (Map.Entry<String, String> entry : constant.ERROR_LIST.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            JSONObject inedxOfArray=new JSONObject();
            inedxOfArray.put(key,value);
            finalJsonArrayForOutput.add(inedxOfArray);
        }

        outputForFileUploader.put("Summary",summaryJsonData);
        outputForFileUploader.put("Failed Email",listOfFailedEmails);
        outputForFileUploader.put("Google Sheet Error",unavailableDataGoogleSheetUpdation);
        System.out.println("processed:" + strTotalProccesed);
        System.out.println("successfully processed:" + strTotalSuccesfullyProcessed);
        System.out.println("Failed:" + strTotalFailed);
        System.out.println("warnings:" + strTotalWarning);
        columns = Arrays.asList(constant.columnOutput);
        values = Arrays.asList(outputForFileUploader.toString());
        queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
        Log.info("File Upload process completed successfully");
    }

    public BulkUpload(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        waitTime = new WebDriverWait(driver, Duration.ofSeconds(constant.INT_TIME_TO_WAIT));
    }
    static List<String> columns;
    static List<Object> values;
    static List<Object> columnValues;

    public static void writeCSV(String headers, List<List<String>> data, String csvFilePath) throws Exception {
        final Path path = Paths.get(csvFilePath);

        try (
                final BufferedWriter writer = Files.newBufferedWriter(path,
                StandardCharsets.UTF_8, StandardOpenOption.CREATE)){
            // Writing headers
            writer.write(headers);
            Log.info("Header setting to csv file was done successfully.");
            writer.newLine();

            // Writing data
            for (List<String> row : data) {
                System.out.println("array:"+row);
                for (int i = 0; i < row.size(); i++) {
                    if (i > 0) {
                        writer.write(",");
                    }
                    writer.write(row.get(i));
                }
                writer.newLine();
            }

        }
        catch (Exception e){
            Log.error("csv writting was failed due to :"+e.getMessage());
            UtilClass.sendEmail(constant.SYSTEM_EXCEPTION,e.getMessage(),constant.processName,id_of_file_Uploader,constant.tableNameWorkItem,retryCount);
            throw e;
        }
    }
    public static QueueItem queueItem;
    public static String tempWorkItemId="";
    public void fileUpload() throws Exception {
        constant.processName = "SCS_IAM_DeepNet_File_Uploader";
        // all token and email details add it to new csv file
        queueItem = queueUtils.getQueueItem(constant.tableNameWorkItem, constant.qnameFileUploader);
        /*resultSet =UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL,constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD,constant.SQL_QUERY_TO_SEARCH_FILE_UPLOADER);*/
        if (queueItem.getDetail() != null) {
             try{
            id_of_file_Uploader = queueItem.getId();
            Log.info("Transaction with id " + id_of_file_Uploader + " with state File_Uploader was retrieved successfully.");
            columns = Arrays.asList(constant.columnStatus);
            values = Arrays.asList(constant.statusInprogress);
            queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
            Log.info("Transaction with id " + id_of_file_Uploader + " with status File_uploader status updated to InProgress.");
            // navigate to upload page
            click(userDirectoryLink, waitTime);
            Log.info("Successfully navigated to user directory");
            // retry count updated here
            retryCount = queueItem.getRetry();
            work_item_id = queueItem.getWorkItemId();
           /* UtilClass.updateDatabase(column,"InProgress", id_of_file_Uploader,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD);*/
            detail = queueItem.getDetail();
            JSONArray jsonArray = new JSONArray(detail);
            tempWorkItemId=work_item_id;
            tempWorkItemId=tempWorkItemId.replace(".csv_","_");
            tempWorkItemId=tempWorkItemId.replace(":","");
                 tempWorkItemId=tempWorkItemId.replace(" ","_");
                 tempWorkItemId=tempWorkItemId.replace("-","_");
            csvFilePath=csvFilePath+tempWorkItemId+".csv";
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.length() > 0) {
                        List<String> temp = new ArrayList<String>();
                        upn = jsonObject.optString("upn", "");
                        mailList.add(upn);
                        productid = jsonObject.optString("product Id", "");
                        modelcode = jsonObject.optString("model code", "");
                        serialNumber = jsonObject.optString("serial Number", "");
                        manufacturecode = jsonObject.optString("manufacture Code", "");
                        token = jsonObject.optString("token", "");
                        mailAndToken.put(token,upn);
                        temp.add(token);
                        temp.add(upn);
                        temp.add(manufacturecode);
                        temp.add(productid);
                        temp.add(modelcode);
                        updatedLines.add(temp);
                    } else {
                        throw new BusinessException("Empty json data found during CSV Writing process at index" + i);// give meaning full sentence
                    }
                } catch (JSONException e) {
                    throw new SystemException(e.getMessage());
                }
            }
            System.out.println("Total:" + updatedLines);
            System.out.println("mails:" + mailList);
            Log.info("Deserialization of json data for csv writing done successfully");
            writeCSV(headers, updatedLines, csvFilePath);
            Log.info("CSV writing was completed to " + csvFilePath + " successfully");
            click(threeDotNavigatorbtn, waitTime);
            click(userView, waitTime);
            click(bulkOpButton, waitTime);
            click(unassigmentbtn, waitTime);
            Log.info("successfully navigated to upload button");
            uploadFile = new File(csvFilePath);
            sendKeys(chooseFilebtn, waitTime, uploadFile.getAbsolutePath());
            Log.info("File was attached and uploaded successfully");
            click(startButton, waitTime);
            captureProcessData(mailList,mailAndToken,id_of_file_Uploader);

        /*UtilClass.updateDatabase(column,"Success", id_of_file_Uploader,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD);*/

                   }
             catch (BusinessException e){
                 failuereJson.put("ExceptionType", "Business");
                 failuereJson.put("FailureReason", e.getMessage().replace("'", "\""));
                 columns = Arrays.asList(constant.columnStatus);
                 values = Arrays.asList(constant.statusFailed);
                 queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
                       /* UtilClass.updateDatabase(column, "Failed", id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                 detail = failuereJson.toString();
                 columns = Arrays.asList(constant.columnReason);
                 values = Arrays.asList(detail);
                 queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
                        /*UtilClass.updateDatabase("reason", detail, id, constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                                constant.SQL_PASS_WORD);*/
                 UtilClass.sendEmail(constant.BUSINESS_EXCEPTION, e.getMessage(), constant.processName,id_of_file_Uploader,constant.tableNameWorkItem,retryCount);
             }
        catch(Exception e){

                 Log.info("Navigation to user directory was failed.");
                 checkBadrequestExist=getText(badRequestError,waitTime);
                 if(!checkBadrequestExist.equals("")){
                     failuereJson.put("ExceptionType", "System");
                     failuereJson.put("FailureReason", constant.badRequesterror.replace("'","\""));
                     columns = Arrays.asList(constant.columnStatus,constant.columnReason);
                     values = Arrays.asList(constant.statusFailed,failuereJson.toString());
                     queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
                     UtilClass.sendEmail( constant.SYSTEM_EXCEPTION,constant.badRequesterror,constant.processName,id_of_file_Uploader,constant.tableNameWorkItem,retryCount);
                     throw e;
                 }
            work_item_id=queueItem.getWorkItemId();
            retryCount=queueItem.getRetry();
            detail=queueItem.getDetail();
            if(retryCount<constant.MAX_RETRY){
                retryCount++;
                // set status as Retried
                failuereJson.put("ExceptionType", "System");
                failuereJson.put("FailureReason", e.getMessage().replace("'","\""));
                columns = Arrays.asList(constant.columnStatus,constant.columnReason);
                values = Arrays.asList(constant.statusRetried,failuereJson.toString());
                queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
                //UtilClass.updateDatabase("status","Retried",id_of_file_Uploader,constant.SQL_JDBC_URL,constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                //insert new row
                columnValues = Arrays.asList(work_item_id, constant.qnameFileUploader, constant.stateNameFileUploader, constant.statusNew, detail, retryCount);
                queueUtils.addQueueItem(constant.tableNameWorkItem, constant.columnNames, columnValues);
                //UtilClass.insertDataIntoDb(constant.SQL_INSERT_QUERY_FOR_FILE_UPLOAD,work_item_id,queue_name,state,"New",jsonArray,retryCount,constant.SQL_JDBC_URL,constant.SQL_USER_NAME,constant.SQL_PASS_WORD);
            }else{
                failuereJson.put("ExceptionType", "System");
                failuereJson.put("FailureReason", e.getMessage().replace("'","\""));
                columns = Arrays.asList(constant.columnStatus,constant.columnReason);
                values = Arrays.asList(constant.statusFailed,failuereJson.toString());
                queueUtils.updateQueueItem(constant.tableNameWorkItem, columns, values, id_of_file_Uploader);
               /* UtilClass.updateDatabase("status","Failed",id_of_file_Uploader,constant.SQL_JDBC_URL,constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
                reason=e.getMessage();
                failuereJson.put("ExceptionType","System");
                failuereJson.put("FailureReason",reason);
                UtilClass.updateDatabase("reason",failuereJson.toString(),id_of_file_Uploader,constant.SQL_JDBC_URL,constant.SQL_USER_NAME, constant.SQL_PASS_WORD);*/
            }
            fileUpload();
            UtilClass.sendEmail( constant.SYSTEM_EXCEPTION,e.getMessage(),constant.processName,id_of_file_Uploader,constant.tableNameWorkItem,retryCount);
            throw e;
          }
        }
        else{
            Log.error("No new transaction found with state File_Uploader.");

       }
        }

}
