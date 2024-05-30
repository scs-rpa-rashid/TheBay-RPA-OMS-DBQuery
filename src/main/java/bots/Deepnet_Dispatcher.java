package bots;

import exceptionutil.ApplicationException;
import org.json.JSONObject;
import utils.*;

import java.io.File;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.CSVUtililty.*;

public class Deepnet_Dispatcher {
    //*************************Declaration*****************************//
    static String inputFolder,
            errorFolder, successfulFolder, reportFolder, unassignedFolder, assignedFolder, assignedFileName, unassignedFileName, CompletedFolder, detail, workitemId, insertTransactionsQuery,
            inProgressFolder, fileNameKeyword, state, status, strSpecificData, strWorkitemId, strQueueName,
            reason, output, exceptionType, exceptionMessage, strWorkitemQueue;
    static ResultSet resultSet;
    static JSONObject failurereason;
    static int intRetry, intId;
    static String[] headers;
    static LocalDateTime localDateTime;

    public static void main(String[] args) throws Exception {
        try {
            //*************************Initialization*************************//
            // Constants
            String userDir = Constant.FILESERVER_PATH;
            inputFolder = userDir + Constant.INPUT_FOLDER;
            errorFolder = userDir + Constant.ERROR_FOLDER;
            successfulFolder = userDir + Constant.SUCCESSFUL_FOLDER;
            reportFolder = userDir + Constant.REPORT_FOLDER;
            CompletedFolder = userDir + Constant.Completed;
            unassignedFolder = userDir + Constant.UNASSIGNED_FOLDER;
            assignedFolder = userDir + Constant.ASSIGNED_FOLDER;
            inProgressFolder = userDir + Constant.INPROGRESS_FOLDER;
            assignedFileName = Constant.ASSIGNED_FILE_NAME;
            unassignedFileName = Constant.UNASSIGNED_FILE_NAME;
            headers = Constant.EXPECTED_HEADERS;
            localDateTime = LocalDateTime.now();
            fileNameKeyword = Constant.FILENAME;
            insertTransactionsQuery = Constant.INSERT_INTODB_QUERY;
            localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_uuuu-MM-dd HH:mm:ss");
            LocalDate localDate = LocalDate.now();
            state = Constant.QUEUE_STATE;
            status = Constant.QUEUE_STATUS;
            strQueueName = Constant.QUEUE_NAME;
            strWorkitemQueue = Constant.WORKITEM_QUEUE;

            // Initialize logging
            DispatcherUtilityClass.initialiseLog4j();
            Log.info("Starting DeepnetTerminationDispatcher...");

            // Kill CSV
            KillAllProcess killprocess = new KillAllProcess();
            killprocess.killCSV();

            // Folder Validation
            FolderValidator.validateFolders(inputFolder, inProgressFolder,
                    errorFolder, reportFolder,
                    unassignedFolder, assignedFolder, CompletedFolder);

            // Get list of  csv files from unassigned folder
            FileValidation fileValidation = new FileValidation();
            List<File> unassignedFiles = fileValidation.getFiles(unassignedFolder, ".csv");

            // Get list of  files from assigned folder
            List<File> assignedFiles = fileValidation.getFiles(assignedFolder, ".csv");

            // Get the lists of assigned and unassigned files
            List<File> Inputfiles = new ArrayList<>();
            Inputfiles.addAll(unassignedFiles);
            Inputfiles.addAll(assignedFiles);

            // If the combined list of files is not empty then only proceed, else no file to process
            if (!Inputfiles.isEmpty()) {
                // Move files from both folders to inProgressFolder
                for (File file : Inputfiles) {
                    fileValidation.moveFile(file.getPath(), inProgressFolder + "\\" + file.getName());
                    detail = "{\"FileToProcess\": \"" + file.getPath() + "\"}";
                    workitemId = file.getName() + localDateTime.format(formatter);
                    intRetry = 0;

                    // Pass the filename to the addQueueItem method
                    DatabaseUtil.insertDataIntoDb(insertTransactionsQuery, workitemId, strQueueName, state, status, detail, intRetry);

                    //Fetch transaction from Queue
                    resultSet = DatabaseUtil.fetchDataFromDb(Constant.SQL_JDBC_URL, Constant.SQL_USER_NAME,
                            Constant.DB_KEY, Constant.FETCH_QUEUE_ITEM_QUERY);

                    //If data is present in queue
                    if (resultSet != null) {
                            //Process the ResultSet here , for each row in
                            // database until all the items are processed
                            while (resultSet.next())
                                try {
                                // Retrieve column values from the Database
                                intId = resultSet.getInt("id");
                                strSpecificData = resultSet.getString("detail");
                                System.out.println(detail);

                                //Set status to inprogress if output value is null
                                DatabaseUtil.updateDatabase("status", "InProgress", intId);

                                // Get the list of files present in the
                                // inProgressFolder and validate
                                File inProgress = new File(inProgressFolder);
                                File[] filesInProgress = inProgress.listFiles();
                                for (File csvFile : filesInProgress) {
                                    if (csvFile.getName().toLowerCase().endsWith(".csv")) {
                                        String[] actualHeaders = validateCSVHeader(csvFile, headers);
                                        if (actualHeaders == null) {
                                            reason = "CSV header does " +
                                                    "not match the expected sequence. Expected " +
                                                    "header : " + Arrays.toString(headers) + csvFile.getName();
                                            throw new BusinessException(reason);
                                        } else if (validateMissingValues(csvFile, 1)) {
                                            reason = "Missing values " +
                                                    "found in the upn " +
                                                    "column of CSV file: " + csvFile.getName();
                                            throw new BusinessException(reason);
                                        } else {
                                            // Read CSV files
                                            convertCSVToJSON(csvFile,
                                                    workitemId,
                                                    strWorkitemQueue);

                                            //Set status to Successfuk if
                                            // output value is null
                                            DatabaseUtil.updateDatabase(
                                                    "status", "Successful",
                                                    intId);

                                            //move file from inProgress
                                            // to Completed folder
                                            fileValidation.moveFile(csvFile.getPath(), CompletedFolder + "\\" + csvFile.getName());

                                        }
                                    }
                                }

                        } catch (BusinessException e) {
                            exceptionMessage = e.getMessage();
                            exceptionType = "BusinessException";
                        } catch (Exception e) {
                            exceptionType = "SystemException";
                            exceptionMessage = e.getMessage();
                        } finally {
                            if (exceptionType != null) {
                                File inProgress = new File(inProgressFolder);
                                File[] filesInProgress = inProgress.listFiles();
                                for (File csvFile : filesInProgress) {
                                    fileValidation.moveFile(csvFile.getPath()
                                            , errorFolder + "\\" + csvFile.getName());
                                }
                                DatabaseUtil.updateDatabase("status", "Failed", intId);
                                failurereason = new JSONObject();
                                failurereason.put("ExceptionType",
                                        exceptionType);
                                failurereason.put("FailureReason",
                                        exceptionMessage);
                                reason = failurereason.toString();
                                DatabaseUtil.updateDatabase("reason",reason,
                                        intId);
                                UtilClassDispatcher.sendEmail(exceptionType,exceptionMessage,
                                        Constant.DispatcherprocessName);
                            }
                            System.out.println("No More transactions in the Queue to Process");
                            Log.info("No More transactions in the Queue to Process");
                        }
                    } else {
                        Log.info("No More transactions in the Queue to Process");
                    }
                }

            }
            else{
                Log.info("No files to process");
            }
        } catch (Exception e) {
            Log.error("Dispatcher failed: " + e.getMessage());
            UtilClassDispatcher.sendEmail("SystemException",e.getMessage(),
                    Constant.DispatcherprocessName);
            throw new ApplicationException(e.getMessage());
        }
    }
}