package utils;

import exceptionutil.ApplicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class KillAllProcess {
    String username;
    String command;
    public KillAllProcess() {
        // Get the username of the current user
        username = System.getProperty("user.name");
        System.out.println(username);
    }

    public void killExcel() throws Exception {
        try {
            // Command to find Excel process for the current user
            command = "tasklist /FI \"USERNAME eq " + username + "\" /FI \"IMAGENAME eq EXCEL.EXE\"";
            // Execute the command
            Runtime.getRuntime().exec(command);
            Process process = Runtime.getRuntime().exec(command);

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Extract the PID of the Excel process
                if (line.contains("EXCEL.EXE")) {
                    String[] parts = line.trim().split("\\s+");
                    String pid = parts[1];
                    command = "taskkill /F /PID " + pid;
                    Runtime.getRuntime().exec(command);
                    Log.info("Excel process with PID " + pid + " killed successfully.");
                }
            }
            // Close the reader
            reader.close();
        } catch (IOException e) {
            Log.warn("Failure in killing excel process due to: " + e.getMessage());
        }
    }

    public void killCSV() throws Exception {
        try {
            // Command to find CSV-related processes for the current user
            command = "tasklist /FI \"USERNAME eq " + username + "\" /FI \"IMAGENAME eq *.csv\"";
            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Extract the PID of the CSV-related process
                if (line.toLowerCase().contains(".csv")) {
                    String[] parts = line.trim().split("\\s+");
                    String pid = parts[1];
                    // Command to kill the process
                    command = "taskkill /F /PID " + pid;
                    Runtime.getRuntime().exec(command);
                    Log.info("CSV-related process with PID " + pid + " killed successfully.");
                }
            }
            // Close the reader
            reader.close();
        } catch (Exception e) {
            Log.warn("Failure in killing CSV-related processes due to: " + e.getMessage());
            throw new ApplicationException(e.getMessage());
        }
    }
}
