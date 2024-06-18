package bots;

import exceptionutil.ApplicationException;
import exceptionutil.BusinessException;
import model.QueueItem;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import pages.*;
import queueutils.QueueItemUtils;
import utility.*;
import logutil.Log;
import sysutils.*;
import utility.ReadPropertyFile;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TheBay_OMS_DBQuery_Performer_Framework {

    String exceptionType;
    String exceptionMessage;
    JSONObject failurereason;
    QueueItemUtils queueUtils;
    SendExceptionMail exceptionMail;
    String reason;
    WebDriver driver;
    String strPartialotp;
    String strOtp;
    static String[][] strarrayinputdata;
    static String[][] strarrayoutputdata;
    LocalDateTime localDateTime;
    String workitemId;
    List<String> columnNames;
    List<Object> columnValues;
    LocalDate localDate;
    String detail;
    int id;
    KillProcess kill;

    public static void main(String[] args) throws Exception {

        TheBay_OMS_DBQuery_Performer_Framework bot=
                new TheBay_OMS_DBQuery_Performer_Framework();
        bot.run();
    }

    public void run() throws Exception {
        try {
            initialisation();
            getTransactions();
            processTransaction();
            endProcess();
        } catch (Exception e) {
            Log.error("Exception source: "+e.getCause());
            Log.error("Exception message: "+e.getMessage());
            exceptionType = "SystemException";
            exceptionMessage = e.getMessage();
            try {
                exceptionMail.sendExceptionMail(ReadPropertyFile.getProperty("ProcessName"),
                        exceptionType,
                        exceptionMessage);
            }
            catch (Exception e1){
                Log.error("Failure in sending email due to: "+e1.getMessage());
            }
            throw new ApplicationException(e.getMessage());
        }
    }

    public void initialisation() throws Exception {
        try {
            //Initialise Log
            Log.initLog4j("log4j.xml");
            //Read property file
            ReadPropertyFile.loadProperties(Constants.DBQUERY_PROPERTYFILE);

            //Kill Excel Process
          kill=new KillProcess();
           kill.killExcel();
           kill.killChrome();
           exceptionMail=new SendExceptionMail();
            UtilClass util = new UtilClass();
            //Get Driver
            driver = util.getDriver();
            LoginPage loginPage = new LoginPage(driver);
            loginPage.login(driver);
            IBMidPage ibMidPage= new IBMidPage(driver);
            ibMidPage.sendOTP(driver);
            OTPVerificationPage otppage = new OTPVerificationPage(driver);
            //Capture partial OTP
            strPartialotp = otppage.capturePartialOTP();
            //Get OTP from email service account
            Thread.sleep(Constants.EMAIL_DELAY);

            GetEmailFromServiceAccount emailFromServiceAccount = new GetEmailFromServiceAccount();
            strOtp = emailFromServiceAccount.GetMailOTP(strPartialotp);

            //Load Page factory for Enter OTP page
            EnterOTPPage otpPage = new EnterOTPPage(driver);
            //Enter OTP
            otpPage.enterOtp(strOtp);
            localDateTime = LocalDateTime.now();
            workitemId = localDateTime.toString();
            localDate = LocalDate.now();
            detail = localDate.toString();
            columnNames= new ArrayList<>();
            columnValues=new ArrayList<>();
            columnNames= Arrays.asList("work_item_id","queue_name","state",
                    "status","detail","reason","comment","output","retry");
            columnValues=Arrays.asList(workitemId,"TheBay_OMS_DBQuery",
                    "Performer","New",detail,"","","",0);
            queueUtils = new QueueItemUtils();
            queueUtils.addQueueItem("RPADev.TheBay_OMS_DBQuery.interim",
                    columnNames,columnValues);
        } catch (Exception e) {
            Log.error("Initialisation failure: " + e.getMessage());
            throw new ApplicationException("Initialisation failure: " + e.getMessage());
        }
    }

    public QueueItem getTransactions() throws Exception {
        // Implement logic to fetch transactions from database.
        // Use QueueItemUtils class to fetch queue item transaction

        return queueUtils.getQueueItem("RPADev.TheBay_OMS_DBQuery.interim",
                "TheBay_OMS_DBQuery");
    }

    public void processTransaction() throws Exception {
        try {
            QueueItem queueItem=getTransactions();
            id=queueItem.getId();
            queueUtils.updateQueueItem("RPADev.TheBay_OMS_DBQuery.interim",
                    List.of("status"),List.of("InProgress"),id);
            ReadExcelInput excelInput = new ReadExcelInput();
            strarrayinputdata = excelInput.getDataFromSheet();
            //Load page factory for Execute Db query
            ExecuteDbQueryPage executeDbQuery = new ExecuteDbQueryPage(driver);
            //Get the output data after executing Db query

            strarrayoutputdata = executeDbQuery.entersqlquery(strarrayinputdata);

            BuildCriticalAndWarningData getoutput = new BuildCriticalAndWarningData();

            getoutput.buildCriticalAndWarningData(strarrayoutputdata);

            queueUtils.updateQueueItem("RPADev.TheBay_OMS_DBQuery.interim",
                    List.of("status"),List.of("Successful"),id);

        } catch (BusinessException e) {
            exceptionMessage = e.getMessage();
            exceptionType = "BusinessException";
        } catch (ApplicationException e) {
            exceptionMessage = e.getMessage();
            exceptionType = "SystemException";

        } finally {
            Log.info("Updating Transaction status");
           setTransactionStatus();

        }
    }
    public void setTransactionStatus() throws Exception {
        if (exceptionType != null) {
            failurereason = new JSONObject();
            failurereason.put("ExceptionType",
                    exceptionType);
            failurereason.put("FailureReason",
                    exceptionMessage);

            reason = failurereason.toString();
            Log.error("Failure reason: "+reason);
            queueUtils.updateQueueItem("RPADev.TheBay_OMS_DBQuery.interim",
                    List.of("status", "reason"), List.of("Failed",
                            reason.replace("'", "\\'")),id);
            try {
                exceptionMail = new SendExceptionMail();
                exceptionMail.sendExceptionMail(ReadPropertyFile.getProperty("ProcessName"), exceptionType, exceptionMessage);
            }
            catch (Exception e){
                Log.error("Failure in sending email due to: "+e.getMessage());
            }
        }
    }


    public void endProcess() throws Exception {
        try {
            driver.quit();
        }
        catch (Exception e)
        {
            Log.warn("Failed to close the browser");
        }
        finally {
            System.exit(0);
        }
    }
}


