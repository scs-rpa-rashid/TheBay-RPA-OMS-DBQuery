package test;
import org.apache.poi.ss.usermodel.Sheet;
import utils.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static utils.FolderValidator.validateFolders;
public class DeepnetTerminationDispatcher {

    //*************************Declaration*****************************//
    static String inputFolder,errorFolder,successfulFolder,reportFolder,
            unassignedFolder, assignedFolder,assignedFileName,
            unassignedFileName, sheetAssigned, sheetUnassigned,
            fileToBeUploadedFolder,
            inProgressFolder;
    static String[] headers;
    static LocalDateTime localDateTime;


    //*************************Initialization*************************//

    public static void main(String[] args) throws SystemException {
        try {

            inputFolder = Constants.FILESERVER_PATH + Constants.INPUT_FOLDER;
            errorFolder = Constants.FILESERVER_PATH + Constants.ERROR_FOLDER;
            successfulFolder = Constants.FILESERVER_PATH + Constants.SUCCESSFUL_FOLDER;
            reportFolder = Constants.FILESERVER_PATH + Constants.REPORT_FOLDER;
            fileToBeUploadedFolder =
                     Constants.FILESERVER_PATH + Constants.FILES_TO_BE_UPLOADED_FOLDER;
            unassignedFolder = Constants.FILESERVER_PATH + Constants.UNASSIGNED_FOLDER;
            assignedFolder = Constants.FILESERVER_PATH + Constants.ASSIGNED_FOLDER;
            assignedFileName = Constants.ASSIGNED_FILE_NAME;
            unassignedFileName = Constants.UNASSIGNED_FILE_NAME;
            headers = Constants.EXPECTED_HEADERS;
            sheetAssigned = Constants.ASSIGNED_SHEET_NAME;
            sheetUnassigned = Constants.UNASSIGNED_SHEET_NAME;
            inProgressFolder = Constants.FILESERVER_PATH + Constants.INPROGRESS_FOLDER;
            localDateTime= LocalDateTime.now();


            // Initialize logging
            UtilityClass.initialiseLog4j();
            Log.info("Starting DeepnetTerminationDispatcher...");

            //Kill Excel
            KillAllProcess kill = new KillAllProcess();
            kill.killExcel();
            Log.info("Killing all Excel processes...");

            //Folder Validation
            FolderValidator.validateFolders(inputFolder, inProgressFolder,
                    errorFolder, reportFolder, successfulFolder,
                    unassignedFolder, assignedFolder, fileToBeUploadedFolder);
            Log.info(" All Folders exist.");

            //File Validation
            FileValidation fileValidation = new FileValidation(inputFolder, assignedFolder,
                    unassignedFolder, inProgressFolder);

            // Check assigned folder extensions and filename
            List<File> assignedFiles = FileValidation.getFiles(assignedFolder
                    , ".csv");

            if(!assignedFiles.isEmpty()){

            }



            // Check unassigned folder contents
            List<File> unassignedFiles =
                    fileValidation.getFiles(unassignedFolder, ".csv");
            if(!unassignedFiles.isEmpty()){

            }

            // Move files from assigned folder to inProgressFolder
            for (File file : assignedFiles) {
                String originalFileName = file.getName();
                fileValidation.moveFile(file.getPath(), inProgressFolder +
                        "\\"+originalFileName);
            }


            /// Move files from unassigned folder to inProgressFolder
            for (File file : unassignedFiles) {
                String originalFileName = file.getName();
                fileValidation.moveFile(file.getPath(), inProgressFolder +
                        "\\"+originalFileName);
            }

            // Create an instance of ExcelUtility
            ExcelUtility excelUtility = new ExcelUtility();
            // Get files in the inProgress folder
            List<File> inProgressFiles =
                    fileValidation.getFiles(inProgressFolder, ".csv");


            //Validate sheets for inProgress files
            for (File file : inProgressFiles) {
                String fileName = file.getName();
                Sheet sheet = excelUtility.readInputExcel(file.getAbsolutePath());

                // Validate sheet name
                excelUtility.validateSheetName(sheet, sheetAssigned);
                Log.info("Sheet 'assigned' in file '" + fileName + "' found and validated successfully.");

                // Validate header sequence
                boolean headerSequenceValid = excelUtility.validateHeaderSequence(sheet, headers);
                if (headerSequenceValid) {
                    Log.info("Header sequence in file '" + fileName + "' is valid.");
                } else {
                    String errorMessage = "Header sequence in file '" + fileName + "' is invalid.";
                    System.out.println(errorMessage);
                    // Log error message
                    Log.error(errorMessage);
                    // Further action if needed
                }

                // Validate number of rows
                try {
                    excelUtility.validateNumberOfRows(sheet, fileName);
                } catch (BusinessException e) {
                    String errorMessage = "Error while validating number of rows in file '" + fileName + "': " + e.getMessage();
                    System.out.println(errorMessage);
                    // Log error message
                    Log.error(errorMessage);
                    throw new SystemException(e.getMessage());
                }

                // Validate blank values in columns
                try {
                    excelUtility.validateBlankValuesInColumns(sheet, fileName);
                } catch (BusinessException e) {
                    String errorMessage = "Error while validating blank values in columns of file '" + fileName + "': " + e.getMessage();
                    System.out.println(errorMessage);
                    // Log error message
                    Log.error(errorMessage);
                    throw new SystemException(e.getMessage());
                }
            }


            Log.info("DeepnetTerminationDispatcher finished successfully.");
        } catch (SystemException e) {
            String errorMessage = "SystemException occurred: " + e.getMessage();
            System.out.println(errorMessage);
            // Log error message
            Log.error(errorMessage);
        } catch (BusinessException e) {
            String errorMessage = "BusinessException occurred: " + e.getMessage();
            System.out.println(errorMessage);
            // Log error message
            Log.error(errorMessage);
        } catch (Exception e) {
            String errorMessage = "IOException occurred: " + e.getMessage();
            System.out.println(errorMessage);
            // Log error message
            Log.error(errorMessage);
            throw new SystemException(e.getMessage());
        }
    }
}