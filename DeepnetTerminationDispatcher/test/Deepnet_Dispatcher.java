package bot;

import utils.*;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static utils.CSVUtililty.*;
import static utils.utilityCSV.validateBlankColumns;

public class Deepnet_Dispatcher {

    //*************************Declaration*****************************//
    static String inputFolder, errorFolder, successfulFolder, reportFolder,
            unassignedFolder, assignedFolder, assignedFileName,
            unassignedFileName, sheetAssigned, sheetUnassigned,
            fileToBeUploadedFolder,
            inProgressFolder, fileNameKeyword, strSpecificData, strWorkitemId, strState, strQueueName, output;
    static ResultSet resultSet;
    static int intRetry;
    static String[] headers;
    static LocalDateTime localDateTime;

    public static void main(String[] args) throws Exception {
        try {

            //*************************Initialization*************************//

            // Constants
            inputFolder = Constants.FILESERVER_PATH + Constants.INPUT_FOLDER;
            errorFolder = Constants.FILESERVER_PATH + Constants.ERROR_FOLDER;
            successfulFolder = Constants.FILESERVER_PATH + Constants.SUCCESSFUL_FOLDER;
            reportFolder = Constants.FILESERVER_PATH + Constants.REPORT_FOLDER;
            fileToBeUploadedFolder =
                    Constants.FILESERVER_PATH + Constants.Completed;
            unassignedFolder = Constants.FILESERVER_PATH + Constants.UNASSIGNED_FOLDER;
            assignedFolder = Constants.FILESERVER_PATH + Constants.ASSIGNED_FOLDER;
            assignedFileName = Constants.ASSIGNED_FILE_NAME;
            unassignedFileName = Constants.UNASSIGNED_FILE_NAME;
            headers = Constants.EXPECTED_HEADERS;
            sheetAssigned = Constants.ASSIGNED_SHEET_NAME;
            sheetUnassigned = Constants.UNASSIGNED_SHEET_NAME;
            inProgressFolder = Constants.FILESERVER_PATH + Constants.INPROGRESS_FOLDER;
            localDateTime = LocalDateTime.now();
            fileNameKeyword = Constants.FILENAME;

            // Initialize logging
            UtilityClass.initialiseLog4j();
            Log.info("Starting DeepnetTerminationDispatcher...");

            // Kill CSV
            KillAllProcess killprocess = new KillAllProcess();
            killprocess.killCSV();

            // Folder Validation
            FolderValidator.validateFolders(inputFolder, inProgressFolder,
                    errorFolder, reportFolder, successfulFolder,
                    unassignedFolder, assignedFolder, fileToBeUploadedFolder);

            // File Validation
            FileValidation fileValidation = new FileValidation();

            // Get list of  csv files from unassigned folder
            List<File> unassignedFiles =
                    fileValidation.getFiles(unassignedFolder, ".csv");

            // Get list of  files from assigned folder
            List<File> assignedFiles =
                    fileValidation.getFiles(assignedFolder, ".csv");

            //If List of files is not empty then only proceed else no file to
            // process

            // Move files from assigned folder to inProgressFolder
            for (File file : assignedFiles) {
                fileValidation.moveFile(file.getPath(), inProgressFolder +
                        "\\" + file.getName());
            }

            // Move files from unassigned folder to inProgressFolder
            for (File file : unassignedFiles) {
                fileValidation.moveFile(file.getPath(), inProgressFolder +
                        "\\" + file.getName());
            }

            // Validate headers in CSV files in the "inProgress" folder
            File inProgress = new File(inProgressFolder);
            File[] csvFiles = inProgress.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            //Add transaction to queue and set to inprogress
            for (File csvFile : csvFiles) {
                validateCSVHeader(csvFile, headers);
                validateBlankRows(csvFile);
                validateBlankColumns(csvFile,"upn",errorFolder);

                //capture exception
                //move files to error folder in case of exception
                //email in case of exception
                fileValidation.moveFile(csvFile.getPath(),
                        fileToBeUploadedFolder + "\\" +
                                csvFile.getName());

                /* *************************** SQL CONNECTION ***************************/
                SQLServerConnection sqlServerConnection = new SQLServerConnection();
                sqlServerConnection.connectToSQLDb();

                // Pass the filename to the addQueueItem method
                AddTransactionToQueue transactionQueue = new AddTransactionToQueue();
                try {
                    transactionQueue.addQueueItem(csvFile.getName(), fileToBeUploadedFolder + "\\" +
                            csvFile.getName());
                } catch (SQLException e) {
                    e.printStackTrace();

                //Read the queue item
                //Fetch transaction from Queue
                resultSet = DatabaseUtil.fetchDataFromDb(Constants.SQL_JDBC_URL,
                        Constants.SQL_USER_NAME,
                        Constants.DB_KEY, Constants.FETCH_QUEUE_ITEM_QUERY);

                //If data is present in queue
                if (resultSet != null) {
                    try {
                        //Process the ResultSet here , for each row in
                        // database until all the items are processed
                        while (resultSet.next()) {
                            // Retrieve column values from the Database
                            int intId = resultSet.getInt(
                                    "id");
                            strSpecificData = resultSet.getString(
                                    "detail");
                            intRetry = resultSet.getInt("retry");
                            strWorkitemId = resultSet.getString(
                                    "work_item_id");
                            strQueueName = resultSet.getString(
                                    "queue_name");
                            strState = resultSet.getString("state");
                            output = resultSet.getString("output");


                            //Set status to inprogress if output value is null
                            System.out.println("update database: " + intId);
                            DatabaseUtil.updateDatabase("status", "InProgress", intId);


                            // Read CSV files
                            System.out.println(new File(fileToBeUploadedFolder));
                                    CSVUtililty.convertCSVToJSON(new File(fileToBeUploadedFolder),strWorkitemId,strQueueName);


                            //Insert data to workitem queue
                            DatabaseUtil.insertDataIntoDb(Constants.SQL_WORKITEM, strWorkitemId,
                                    strQueueName,
                                    "Deepnet_Performer", "New", strSpecificData, 0);

                        }
                    } catch (
                            SQLException f) {
                        e.printStackTrace();
                    }
                }
            }

            {

            }
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
    }
}}
