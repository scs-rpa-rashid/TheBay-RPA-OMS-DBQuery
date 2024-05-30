package utils;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CSVUtililty {

    // Method to validate header row of a CSV file
    static String actualHeaderField;
    static String[] actualHeaders;
    public static String[] validateCSVHeader(File csvFile, String[] expectedHeaders) throws IOException {
        // Read the first line of the CSV file to get the headers
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            actualHeaders = reader.readLine().split(",");
            // Compare the actual headers with the expected headers
            System.out.println("expected:"+expectedHeaders+"\n actualheaders:"+actualHeaders);
            if (expectedHeaders.length != actualHeaders.length) {
                return null; // Return null if lengths don't match
            }
            for (int i = 0; i < expectedHeaders.length; i++) {
                String actualTrimmed = actualHeaders[i].trim();
                String expectedTrimmed = expectedHeaders[i].trim();

                // Print debug information
                System.out.println("Actual: '" + actualTrimmed + "' (Length: " + actualTrimmed.length() + ")");
                System.out.println("Expected: '" + expectedTrimmed + "' (Length: " + expectedTrimmed.length() + ")");

                if (!actualTrimmed.equals(expectedTrimmed)) {
                    System.out.println("Mismatch found:");
                    System.out.println("Actual: '" + actualTrimmed + "' (Length: " + actualTrimmed.length() + ")");
                    System.out.println("Expected: '" + expectedTrimmed + "' (Length: " + expectedTrimmed.length() + ")");
                    return null; // Return null if any header doesn't match
                }
            }
        }
        return actualHeaders; // Return actual headers if they match the expected sequence
    }

// Validate CSV file for blank rows (except for header)
public static boolean validateBlankRows(File csvFile) throws IOException, BusinessException {
    // Read the CSV file skipping the header
    try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
        String header = reader.readLine(); // Read the header
        String line;
        boolean isFirstLine = true;
        boolean hasData = false; // Flag to track if any row has data
        while ((line = reader.readLine()) != null) {
            if (!isFirstLine) {
                String[] fields = line.split(",", -1); // Split line, including trailing empty strings
                // Check if the row has only one non-blank value (excluding the first column)
                boolean rowHasData = false;
                for (int i = 2; i < fields.length; i++) {
                    if (!fields[i].trim().isEmpty()) {
                        rowHasData = true;
                        hasData = true; // Set flag to true if any row has data
                        break;
                    }
                }
                if (!rowHasData) {
                    throw new BusinessException("No data found in CSV file: " + csvFile.getName());
                }
            }
            isFirstLine = false;
        }
        if (!hasData) {
            throw new BusinessException("No data found in CSV file: " + csvFile.getName());
        }
        return false; // Data found after the header
    }
}
// Validate CSV file for blank values in columns
public static boolean validateMissingValues(File csvFile, int columnIndex) throws IOException {
    // Read the CSV file
    try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
        reader.readLine(); // Skip the header
        String line;
        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(",");
            if (columns.length <= columnIndex || columns[columnIndex].trim().isEmpty()) {
                return true; // Missing value found in the specified column
            }
        }
        return false; // No missing values found
    }
}

// Method to process CSV files and convert rows to JSON
public static void convertCSVToJSON(File csvFile,String strWorkitemId,
                                    String strQueueName) {
    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        String line;
        String[] headers = null;

        // Read the CSV file line by line
        while ((line = br.readLine()) != null) {
            // Split the line into columns
            String[] values = line.split(",");

            // If headers are not initialized, it's the first row, so store headers
            if (headers == null) {
                headers = values;
                continue; // Skip further processing for headers
            }

            // Create a JSON object for the row
            JSONObject jsonRow = createJSONObject(headers, values);
            String productCodeValue = jsonRow.getString("product code");
            String manufactureCodeValue=jsonRow.getString("manufacturer code");
            String serialNumberValue=jsonRow.getString("serial number");
            // Remove the old key-value pair
            jsonRow.remove("product code");
            jsonRow.remove("manufacturer code");
            jsonRow.remove("serial number");

            // Add the new key-value pair with the updated key
            jsonRow.put("product Id", productCodeValue);
            jsonRow.put("manufacture Code",manufactureCodeValue);
            jsonRow.put("serial Number",serialNumberValue);


            // Add one more key-value pair
            jsonRow.put("token", "");

            String strSpecificData=jsonRow.toString();

            // Print the JSON object for the row
            System.out.println(strSpecificData);
            DatabaseUtil.insertDataIntoDb(Constant.SQL_WORKITEM, strWorkitemId,
                    strQueueName, "Performer", "New", strSpecificData, 0);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Helper method to create a JSON object from headers and values
public static JSONObject createJSONObject(String[] headers,
                                          String[] values) {
    JSONObject json = new JSONObject();

    // Create a JSON object for the row
    for (int i = 0; i < headers.length; i++) {
        json.put(headers[i].trim(), values[i].trim());
    }

    return json;
}

// Method to move a file to the error folder
public static void moveFileToErrorFolder(File csvFile, String errorFolder) throws IOException {
    Path source = csvFile.toPath();
    Path destination = Paths.get(errorFolder, csvFile.getName());
    Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
}
}

