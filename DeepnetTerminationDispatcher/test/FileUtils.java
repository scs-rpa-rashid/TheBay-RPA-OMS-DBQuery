package utils;

import java.io.File;

public class FileUtils {
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ""; // No extension found
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }



}