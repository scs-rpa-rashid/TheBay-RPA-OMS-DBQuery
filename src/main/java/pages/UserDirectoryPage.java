package pages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.*;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static utils.BaseFunctions.*;

public class UserDirectoryPage {

    public static WebDriver driver;
    public static WebDriverWait waitTime;
    public String strExceptionmessage;
    public String name,strOtpToken;
    public String detail,reason;
    public static int id;
    public static String value,column;
    public static Constants constant=new Constants();
    ResultSet resultSet1;
    static int retryCount;
    JSONObject outputJson;
    String mail,upn,strWorkItemId,status="New";
    @FindBy(xpath = "//*[@id=\"kt_aside_menu\"]/ul/li[10]/a/span[2]")
    WebElement userDirectoryLink;
    @FindBy(xpath = "//*[@id=\"tblIDS\"]/table/tbody/tr/td[4]/span/div/button/i")
    WebElement threeDotNavigatorbtn;
    @FindBy(xpath = "//*[@id=\"test\"]/a[1]")
    WebElement userView;
    @FindBy(id="kt_datatable_search_query")
    WebElement searchBar;
    @FindBy(xpath = "//*[@id=\"kt_content\"]/div/div/div[1]/div[2]/div[1]/div/div/div/div[3]/button[1]")
    WebElement searchSubmitBtn;
    @FindBy(xpath = "//*[@id=\"tblADLV\"]/table/tbody/tr/td[4]")
    WebElement otpToken;
    @FindBy(xpath = "//*[@id=\"tblADLV\"]/table/tbody/tr/td[1]")
    WebElement nameOfPerson;
    static JSONObject failuereJson = new JSONObject();
    public static String file="C:\\Deepnet_Termination\\filesToBeUploaded\\Deepnet_Bulk.csv";

    public UserDirectoryPage(WebDriver driver) {
        PageFactory.initElements(driver,this);
        this.driver = driver;
        waitTime = new WebDriverWait(driver, Duration.ofSeconds(constant.INT_TIME_TO_WAIT));
    }
    public static void main(String[] args) throws Exception {
        String processName = "DeepNet_CaptureToken";
        UtilClass.sendEmail(constant.SYSTEM_EXCEPTION,"No element found to click",processName,id,constant.tableNameWorkItem,retryCount);
    }
    public void searchTokenAndUpdate() throws Exception {
        try {
            constant.processName = "DeepNet_CaptureToken";
//        resultSet1 =UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL,constant.SQL_USER_NAME,
//                constant.SQL_PASS_WORD,constant.SQL_QUERY_TO_SEARCH);
        while(resultSet1.next()){
            upn= resultSet1.getString("detail");
            JsonObject jsonObject = JsonParser.parseString(upn).getAsJsonObject();
            id= resultSet1.getInt("id");
            mail= jsonObject.get("upn").getAsString();
            retryCount=resultSet1.getInt("retry");
            strWorkItemId=resultSet1.getString("work_item_id");
            column="status";
            UtilClass.updateDatabase(column,"InProgress",id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD);
            System.out.println("mail:" + mail);
            String substring = mail.length() > 8 ? mail.substring(mail.length() - 8) : mail;
            Log.info("Mail validation done successfully");
            if (substring.equals("@hbc.com")) {
                try {
                    strOtpToken="";
                    sendKeys(searchBar, waitTime, mail.trim());
                    click(searchSubmitBtn, waitTime);
                    name = getText(nameOfPerson, waitTime);
                    Thread.sleep(6000);
                    click(searchBar, waitTime);
                    clearTextField(searchBar, waitTime);
                    strOtpToken = getText(otpToken, waitTime).substring(0, 8);
                }catch (Exception e){
                    throw new SystemException("Failed to capture token due to:"+e.getMessage());
                }
                if(strOtpToken!=""){
                    column="status";
                    UtilClass.updateDatabase(column,"Success",id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                            constant.SQL_PASS_WORD);
                    column="output";
                    outputJson = new JSONObject(upn);
                    outputJson.put("token", strOtpToken);
                    String updatedJsonString = outputJson.toString();
                    UtilClass.updateDatabase(column,updatedJsonString,id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                            constant.SQL_PASS_WORD);
                    //UtilClass.insertDataIntoDb(constant.SQL_INSERT_QUERY_FOR_FILE_UPLOAD,strWorkItemId,constant.qnameCSVWriter,constant.stateNameCSVWriter,status, outputJson,retryCount,constant.SQL_JDBC_URL,constant.SQL_USER_NAME,constant.SQL_PASS_WORD);
                }

            } else {
                column="status";
                failuereJson.put("ExceptionType:","Business");
                UtilClass.updateDatabase(column,"Failed",id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                        constant.SQL_PASS_WORD);
                strExceptionmessage="Token not found for Email:"+mail;
                failuereJson.put("FailureReason",strExceptionmessage);
                detail = jsonObject.toString();
                UtilClass.updateDatabase("reason",detail,id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                        constant.SQL_PASS_WORD);
                throw new BusinessException(strExceptionmessage);
            }

        }
        Log.info("Token capturing was successful");
    }catch (BusinessException e){
            UtilClass.sendEmail("Business Exception",e.getMessage(), constant.processName,id,constant.tableNameWorkItem,retryCount);
            throw e;
    } catch(SystemException e){
        if(retryCount<constant.MAX_RETRY) {
            retryCount++;
            constant.qnameCSVWriter="DeepNet_Performer";
            constant.stateNameCSVWriter="Performer";
            status="New";
            // update status as retried
            UtilClass.updateDatabase("status","Retried",id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD);
            // update retry reason
            value=e.getMessage();
            UtilClass.updateDatabase("reason",value,id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD);
            //Insert a new row with updated retry count
          //  UtilClass.insertDataIntoDb(constant.SQL_WORKITEM, strWorkItemId,constant.qnameCSVWriter,constant.stateNameCSVWriter, status,upn, retryCount, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD);
            searchTokenAndUpdate();
        }else{
            constant.qnameCSVWriter="DeepNet_Performer";
            constant.stateNameCSVWriter="Performer";
            status="Failed";
            reason=e.getMessage();
            column="status";
            failuereJson.put("ExceptionType:","System");
            failuereJson.put("FailureReason",reason);
            UtilClass.updateDatabase("reason",detail,id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD);
           // UtilClass.insertDataIntoDb(constant.SQL_WORKITEM_WITH_REASON, strWorkItemId,constant.qnameCSVWriter,constant.stateNameCSVWriter, status,upn, retryCount, constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD,failuereJson.toString());
            UtilClass.sendEmail(constant.SYSTEM_EXCEPTION,e.getMessage(), constant.processName,id,constant.tableNameWorkItem,retryCount);
            throw new SystemException(e.getMessage());
        }
    } catch(Exception e){
        strExceptionmessage = "Failed to navigate to the user directory page due to:" + e;
        Log.error(strExceptionmessage);
        throw new SystemException(strExceptionmessage);
    }
    }
    public void userDirectoryOperation() throws Exception {
            try {
                click(userDirectoryLink, waitTime);
                Log.info("Navigated to the user director button");
                click(threeDotNavigatorbtn, waitTime);
                Log.info("Navigation to Three dot button");
                click(userView, waitTime);
                Log.info("Clicked on userview button");
                click(searchBar, waitTime);
                Log.info("clicked on search bar");
                searchTokenAndUpdate();
            }catch (Exception e){
                throw new SystemException(e.getMessage());
            }



    }
}
