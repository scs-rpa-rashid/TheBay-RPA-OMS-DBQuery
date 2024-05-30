package utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class utilityCSV {

    // Method to validate header row of a CSV file
    public static boolean validateCSVHeader(File csvFile, String[] expectedHeaders, String errorFolder) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] actualHeaders = reader.readNext();
            if (actualHeaders == null || actualHeaders.length != expectedHeaders.length) {
                moveFile(csvFile.getPath(), errorFolder + File.separator + csvFile.getName());
                return false;
            }
            for (int i = 0; i < actualHeaders.length; i++) {
                if (!actualHeaders[i].trim().equalsIgnoreCase(expectedHeaders[i].trim())) {
                    moveFile(csvFile.getPath(), errorFolder + File.separator + csvFile.getName());
                    return false;
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Validate CSV file for blank rows (except for header)
    public static boolean validateBlankRows(File csvFile, String errorFolder) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] nextLine;
            boolean headerProcessed = false;
            while ((nextLine = reader.readNext()) != null) {
                if (!headerProcessed) {
                    headerProcessed = true;
                    continue;
                }
                boolean blankRow = true;
                for (String field : nextLine) {
                    if (!field.trim().isEmpty()) {
                        blankRow = false;
                        break;
                    }
                }
                if (blankRow) {
                    moveFile(csvFile.getPath(), errorFolder + File.separator + csvFile.getName());
                    return false;
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Validate CSV file for blank values in columns
    public static boolean validateBlankColumns(File csvFile, String columnName, String errorFolder) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            String[] headers = reader.readNext();
            int columnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(columnName.trim())) {
                    columnIndex = i;
                    break;
                }
            }
            if (columnIndex == -1) {
                moveFile(csvFile.getPath(), errorFolder + File.separator + csvFile.getName());
                return false;
            }
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > columnIndex && nextLine[columnIndex].trim().isEmpty()) {
                    moveFile(csvFile.getPath(), errorFolder + File.separator + csvFile.getName());
                    return false;
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void moveFile(String sourcePath, String destinationPath) {
        Path source = new File(sourcePath).toPath();
        Path destination = new File(destinationPath).toPath();
        try {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to process CSV files
    public static List<String> processCSVFiles(File directory) {
        List<String> details = new ArrayList<>();
        if (!directory.isDirectory()) {
            System.err.println("Invalid directory path: " + directory.getPath());
            return details;
        }

        File[] csvFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
        if (csvFiles == null || csvFiles.length == 0) {
            System.out.println("No CSV files found in the directory: " + directory.getPath());
            return details;
        }

        for (File csvFile : csvFiles) {
            try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
                String[] rowData;
                while ((rowData = csvReader.readNext()) != null) {
                    String detail = processRowData(rowData);
                    details.add(detail);
                }
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
        }
        return details;
    }

    // Method to process each row of data
    private static String processRowData(String[] rowData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upn", rowData[0]);
        jsonObject.put("manufacturer_code", rowData[1]);
        jsonObject.put("product_code", rowData[2]);
        jsonObject.put("model_code", rowData[3]);
        return jsonObject.toString();
    }
}
