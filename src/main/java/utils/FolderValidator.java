package utils;
import exceptionutil.ApplicationException;

import java.io.File;
public class FolderValidator {
    public static void validateFolders(String... folders) throws Exception {
        for (String folderPath : folders) {
            validateFolder(folderPath);
        }
    }
    public static void validateFolder(String folderPath) throws Exception {
        try {
            File folder = new File(folderPath);
            if (folder.exists()) {
                Log.info("Folder exists: " + folder);
            } else {
                String strExceptionMessage = "Failed to navigate to the user " +
                        "directory: "+folderPath + "  folder does not exist.";
                throw new ApplicationException(strExceptionMessage);
            }
        } catch (ApplicationException e) {
            throw e;
        }
    }
}
