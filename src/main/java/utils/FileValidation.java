package utils;

import exceptionutil.ApplicationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class FileValidation {
    public static List<File> getFiles(String directoryPath, String extension) throws Exception {
        List<File> csvFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        try {
            // Get list of files in the directory
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (extension.isEmpty() || (file.isFile() && file.getName().endsWith(extension))) {
                        csvFiles.add(file);
                        getFileNames(csvFiles);
                        Log.info(file.getName());
                    } else {
                        throw new ApplicationException("File is not in .csv " +
                                "format. " +
                                " Invalid file extension: " + file.getName());
                    }
                }
            }
            return csvFiles;
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }
    }

    public static void getFileNames(List<File> files) {
        for (File file : files
        ) {
            Log.info("Filename"+file.getName());
        }
    }

    public void moveFile(String sourcePath, String destinationPath) throws Exception {
        // Create Path objects for source and destination files
        Path source = new File(sourcePath).toPath();
        Path destination = new File(destinationPath).toPath();

        try {
            // Move the file to the destination
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            Log.info("Files moved from " +source+ " to "+ destination);
        } catch (Exception e) {
            Log.error("Error while moving file '" + source + " to " + destination + e.getMessage());
            throw new ApplicationException("Error while moving file " + source +
                    " to " + destination);

        }
    }
}

/*//Use this to check for sub folder
public void validateFolder(String folderPath) throws Exception {
    try {
        File folder = new File(folderPath);
        if (folder.exists()) {
            System.out.println("Folder exists: " + folder);
            Log.info("Folder exists: " + folder);
        } else {
            throw new SystemException(folderPath + " does not exist.");
        }

    } catch (Exception e) {
        Log.error(e.getMessage());
        throw e;
    }
}

   /* public void moveValidFilesToInProgressFolder(List<String> validFiles)
   throws SystemException {

        if (validFiles.isEmpty()) {
            System.out.println("No valid files to move to 'in progress' folder.");
            return;
        }
        try {
            File inProgressFolder = new File(inProgressFolderPath);
            if (!inProgressFolder.exists()) {
                throw new SystemException("The 'in progress' folder does not exist.");
            }

            for (String fileName : validFiles) {
                try {
                    Path sourcePath = new File(folderPath, fileName).toPath();
                    Path destinationPath = new File(inProgressFolderPath, fileName).toPath();
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File '" + fileName + "' moved to 'in progress' folder.");
                } catch (IOException e) {
                    Log.error("Error while moving file '" + fileName + "' to 'in progress' folder: " + e.getMessage());
                    throw new SystemException("Error while moving file '" + fileName + "' to 'in progress' folder.");
                }
            }
        } catch (Exception e) {
            Log.error("Error while moving files to 'in progress' folder: " + e.getMessage());
            throw new SystemException("Error while moving files to 'in progress' folder.");
        }
    }
}*/
/*public List<String> validateFolderContents(String folderName) throws
    BusinessException, BusinessException, SystemException {

        try {
            File folderToCheck = new File(folderPath, folderName);
            if (folderToCheck.exists() && folderToCheck.isDirectory()) {
                System.out.println("The '" + folderName + "' folder exists inside the input folder.");
                return checkFilesInFolder(folderToCheck, folderName);
            } else {
                System.out.println("The '" + folderName + "' folder does not exist inside the input folder.");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            Log.error("Error while validating folder contents: " + e.getMessage());
            throw e;
        }
    }*/

/*private List<String> checkFilesInFolder(File folderToCheck,
                                             String folderName) throws BusinessException {
        File[] files = folderToCheck.listFiles();
        if (files == null || files.length == 0) {
            Log.info("No files found in the folder.");
        }
        System.out.println("Number of files in folder: " + files.length);
        Log.info("Number of files in folder: " + files.length);
        List<String> validFiles = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            String fileExtension = FileUtils.getFileExtension(file);
            if (fileExtension.equals("xlsx")) {
                // Validate filename
                String expectedFileName = folderName.equalsIgnoreCase(assignedFolderName) ? assignedFileName : unassignedFileName;
                if (folderName.equalsIgnoreCase(unassignedFolderName) && !fileName.equalsIgnoreCase(expectedFileName)) {
                    System.out.println("Invalid file '" + fileName + "' in 'Unassigned' folder due to incorrect filename.");
                } else {
                    validFiles.add(fileName);
                }
            } else {
                System.out.println("Invalid file '" + fileName + "' due to invalid extension.");
            }
        }
        return validFiles;
    }
*/
