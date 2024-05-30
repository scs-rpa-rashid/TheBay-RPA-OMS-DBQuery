package test;

import org.apache.poi.ss.usermodel.Sheet;
import utils.*;

import java.io.File;
import java.util.List;

import static utils.FolderValidator.validateFolders;

public class Test {

    //*************************Initialization*****************************//
    static String inputFolder = Constants.FILESERVER_PATH+Constants.INPUT_FOLDER;
    static String errorFolder =
            Constants.FILESERVER_PATH+Constants.ERROR_FOLDER;
    static String successfulFolder =
            Constants.FILESERVER_PATH+Constants.SUCCESSFUL_FOLDER;
    static String reportFolder =
            Constants.FILESERVER_PATH+Constants.REPORT_FOLDER;
    static String unassignedFolder =
            Constants.FILESERVER_PATH+ Constants.UNASSIGNED_FOLDER;
    static String assignedFolder =
            Constants.FILESERVER_PATH+Constants.ASSIGNED_FOLDER;
    static String assignedFileName = Constants.ASSIGNED_FILE_NAME;
    static String unassignedFileName = Constants.UNASSIGNED_FILE_NAME;
    static String[] headers = Constants.EXPECTED_HEADERS;
    static String sheetAssigned = Constants.ASSIGNED_SHEET_NAME;
    static String sheetUnassigned = Constants.UNASSIGNED_SHEET_NAME;
    static String inProgressFolder =
            Constants.FILESERVER_PATH+Constants.INPROGRESS_FOLDER;

    //*************************Initialization*************************//

    public static void main(String[] args) throws Exception {

        try {
            // Initialize logging
            UtilityClass.initialiseLog4j();
            Log.info("Starting DeepnetTerminationDispatcher...");
            KillAllProcess kill = new KillAllProcess();
            kill.killExcel();
            Log.info("Killing all Excel processes...");

            FileValidation fileValidation =
                    new FileValidation(inputFolder, assignedFolder,
                            unassignedFolder , inProgressFolder);

            // Check input folder existence
            fileValidation.validateFolder(inputFolder);
            Log.info("Input folder exists.");

            // Check inProgress folder existence
            fileValidation.validateFolder(inProgressFolder);
            Log.info("InProgress folder exists.");

            // Check errorFolder existence
            fileValidation.validateFolder(errorFolder);
            Log.info("Error Folder exists.");

            // Check reportFolder existence
            fileValidation.validateFolder(reportFolder);
            Log.info("Report Folder exists.");

            // Check successfulFolder existence
            fileValidation.validateFolder(successfulFolder);
            Log.info("Successful Folder exists.");

            // fileValidation.validateFolder(unassignedFolder);
            fileValidation.validateFolder(unassignedFolder);
            Log.info("Unassigned Folder exists inside Input Folder.");

            // fileValidation.validateFolder(assignedFolder);
            fileValidation.validateFolder(assignedFolder);
            Log.info("Assigned Folder exists inside Input Folder.");

            // Check assigned folder contents
            List<File> assignedFiles =
                    fileValidation.getFiles(assignedFolder,
                            ".xlsx");

            // Print the list of Excel files
            for (File file : assignedFiles) {
                System.out.println(file.getName());
            }

            // Check unassigned folder contents
            List<File> unAssignedFiles =
                    fileValidation.getFiles(unassignedFolder,
                            ".xlsx");

            // Print the list of Excel files
            for (File file : unAssignedFiles) {
                System.out.println(file.getName());
            }

            /*
            List<String> assignedFiles = fileValidation.validateFolderContents(assignedFolder);
            Log.info("Assigned folder contents validated.");

            // Check assigned folder contents
            List<String> unassignedFiles =
                    fileValidation.validateFolderContents(assignedFolder);
            Log.info("Assigned folder contents validated.");

            // Create an instance of ExcelUtility
            ExcelUtility excelUtility = new ExcelUtility();

            // Validate sheets for assigned files
            for (String fileName : assignedFiles) {
                File file = new File(inputFolder + File.separator + assignedFolder, fileName);
                if (!file.exists()) {
                    throw new BusinessException("File '" + fileName + "' not found in assigned folder.");
                }

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

            // Validate sheets for unassigned files

            for (String fileName : unassignedFiles) {
                File file = new File(inputFolder + File.separator + unassignedFolder, fileName);
                if (!file.exists()) {
                    throw new BusinessException("File '" + fileName + "' not found in unassigned folder.");
                }

                Sheet sheet = excelUtility.readInputExcel(file.getAbsolutePath());

                // Validate sheet name
                excelUtility.validateSheetName(sheet, sheetUnassigned);
                Log.info("Sheet 'unassigned' in file '" + fileName + "' found and validated successfully.");

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

            // Check if "in progress" folder exists

            // Check if "in progress" folder exists
            boolean inProgressFolderExists = fileValidation.checkInProgressFolderExists();
            if (!inProgressFolderExists) {
                throw new SystemException("The 'in progress' folder does not exist.");
            }

            // Move valid files to "in progress" folder
            List<String> validUnassignedFiles = fileValidation.validateFolderContents(unassignedFolder);
            fileValidation.moveValidFilesToInProgressFolder(validUnassignedFiles);

            List<String> validAssignedFiles = fileValidation.validateFolderContents(assignedFolder);
            fileValidation.moveValidFilesToInProgressFolder(validAssignedFiles);


            Log.info("DeepnetTerminationDispatcher finished successfully.");*/
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