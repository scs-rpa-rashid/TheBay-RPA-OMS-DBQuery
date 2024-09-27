package bots;

import exceptionutil.ApplicationException;
import exceptionutil.BusinessException;
import logutil.Log;
import model.QueueItem;
import org.json.JSONObject;
import queueutils.QueueItemUtils;
import utility.*;

import java.util.List;

public class Template {

    int id;
    String exceptionType;
    String exceptionMessage;
    JSONObject failurereason;
    QueueItemUtils queueUtils;

    SendExceptionMail exceptionMail;
    String reason;

    public static void main(String[] args) throws Exception {

    }

    public void run() throws Exception {
        try {


        } catch (Exception e) {
            Log.error(e.getMessage());
            exceptionType = "SystemException";
            exceptionMessage = e.getMessage();
            exceptionMail.sendExceptionMail(ReadPropertyFile.getProperty("ProcessName"), exceptionType, exceptionMessage);
            throw new ApplicationException(e.getMessage());
        }


    }

    public void initialisation() throws Exception {
        try {
            //Instantiate Util class
            UtilClass.initialiseLog4j();

            //Read dispatcher property file
            ReadPropertyFile.loadProperties(Constants.DBQUERY_PROPERTYFILE);



        } catch (Exception e) {
            Log.error("Initialisation failure: " + e.getMessage());
            throw new ApplicationException("Initialisation failure: " + e.getMessage());
        }
    }

    public QueueItem getTransactions() throws Exception {
        // Implement logic to fetch transactions from database.
        // Use QueueItemUtils class to fetch queue item transaction
        queueUtils = new QueueItemUtils();
        return queueUtils.getQueueItem("RPADev.TheBay_OMS_DBQuery.interim",
                "TheBay_OMS_DBQuery");
    }

    public void processTransaction() throws Exception {
        try {
            throw new BusinessException("");

        } catch (BusinessException e) {
            exceptionMessage = e.getMessage();
            exceptionType = "BusinessException";
        } catch (Exception e) {
            exceptionMessage = e.getMessage();
            exceptionType = "SystemException";

        } finally {
            Log.info("Updating Transaction status");
            setTransactionStatus();

        }
    }



    public void setTransactionStatus() throws Exception {
        if (exceptionType != null) {

        }
        failurereason = new JSONObject();
        failurereason.put("ExceptionType",
                exceptionType);
        failurereason.put("FailureReason",
                exceptionMessage);
        reason = failurereason.toString();
        queueUtils.updateQueueItem(ReadPropertyFile.getProperty("interimTableName"), List.of("status", "reason"), List.of("Failed", reason), id);
        exceptionMail =
                new SendExceptionMail();

        exceptionMail.sendExceptionMail(ReadPropertyFile.getProperty("dispatcherProcessName"), exceptionType, exceptionMessage);
    }


    public void endProcess() {



    }
}


