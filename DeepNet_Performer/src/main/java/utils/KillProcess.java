package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class KillProcess {

    String username;
    String command;
    Process process;
    BufferedReader reader;
    String[] parts;
    String pid;


    public KillProcess() {
        // Get the username of the current user
        username = System.getProperty("user.name");
        Log.info("BOT user: "+username);

    }


    public void killExcel() throws Exception {

        try {
            Log.info("Killing Excel process");
            // Command to find Excel process for the current user
            command = "tasklist /FI \"USERNAME eq " + username + "\" /FI \"IMAGENAME eq EXCEL.EXE\"";

            process = Runtime.getRuntime().exec(command);

            // Read the output of the command
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Extract the PID of the Excel process
                if (line.contains("EXCEL.EXE")) {
                    parts = line.trim().split("\\s+");
                     pid = parts[1];
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

    public void killChrome() throws Exception {

        try {
            Log.info("Killing Chrome process");
            // Command to find Excel process for the current user
            command = "tasklist /FI \"USERNAME eq " + username + "\" /FI " +
                    "\"IMAGENAME eq CHROME.EXE\"";

             process = Runtime.getRuntime().exec(command);

            // Read the output of the command
             reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Extract the PID of the Excel process
                if (line.contains("CHROME.EXE")) {
                    parts = line.trim().split("\\s+");
                     pid = parts[1];
                    command = "taskkill /F /PID " + pid;
                    Runtime.getRuntime().exec(command);
                        Log.info("Chrome process with PID " + pid + " killed " +
                                "successfully.");
                }
            }

            // Close the reader
            reader.close();
        } catch (IOException e) {
            Log.warn("Failure in killing chrome process due to: " + e.getMessage());
        }
    }
}

