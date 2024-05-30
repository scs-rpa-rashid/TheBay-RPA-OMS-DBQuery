package bots;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.json.TypeToken;
import utils.Constants;
import utils.KillProcess;
import utils.Log;
import utils.UtilClass;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.sql.ResultSet;

public class Reporting {
    static Constants constant=new Constants();
    static ResultSet resultSet;
    static String fileUploaded,fileName="";
    public static String cleanJsonData(String json) {
        String bORs=json.substring(18,19);
        String starting = "",substring = "";
        if(bORs.equals("S")) {
            System.out.println(bORs);
            starting = json.substring(0, 43);
            substring = json.substring(43).replace("\"", "'");
        }else if(bORs.equals("B")){
            System.out.println(bORs);
            starting = json.substring(0, 45);
            substring = json.substring(45).replace("\"", "'");
        }
        System.out.println(substring);
        String result = substring.substring(0, substring.length() - 2);
        result=starting+result+"\"}";
        return result;
    }
    static KillProcess kill =new KillProcess();
    static Workbook workbook;
    static Sheet sheet;
    static int serialNumber;
    static String jsonString;
    static String reason;
    static Gson gson;
    static Type mapType;
    static Map<String, Object> data;
    static List<Map<String, Object>> failedEmails;
    static Map<String, String> successEmails;
    static List<Map<String, String>> googleSheetErrors;
    static List<Map<String, String>> dataList;
    static Map<String, String> exceptionDetails;
    static JsonObject jsonObject;
    static String mail,token;
    static String strExceptionMessage;
    static SimpleDateFormat dateFormat;
    static Date currentDate;
    static String formattedDate;
    static boolean isReportGenerated=false;
    static String[] path;

    public static void main(String[] args) throws Exception {

        try {
            constant.processName = "SCS_IAM_DeepNet_Reporter";
            //Kill Excel
            kill.killExcel();
            resultSet = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD, constant.getQueueOfFileUploader(constant.intHoursBack, constant.statusSuccess, constant.stateNameFileUploader));
            // Create a new Excel workbook
            workbook = new XSSFWorkbook();
            // Create a sheet named "Report"
            sheet = workbook.createSheet("Unassigned_Report");
            int rowNum = 0;
            // Create a font with bold style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            // Set the font color to orange
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            // Create a cell style with the bold orange font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            // Set the background color to light blue
            headerCellStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Create headers
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Sl.No", "Email","Serial Number(Token)", "Status", "Exception Type", "Failure Reason", "File Uploaded"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle); // Apply the cell style to the header cells
            }

            serialNumber = 1;

            // Loop through the resultSet
            while (resultSet.next()) {
                isReportGenerated = true;
                jsonString = resultSet.getString("output");
                fileUploaded = resultSet.getString("work_item_id");
                fileUploaded = fileUploaded.replace(".csv_", "_");
                fileUploaded = fileUploaded.replace(":", "");
                fileUploaded = fileUploaded.replace(" ", "_");
                fileUploaded = fileUploaded.replace("-", "_");
                fileName = fileUploaded;
                fileUploaded = fileUploaded + ".csv";
                gson = new Gson();
                // Deserialize the JSON string into a Map
                ObjectMapper mapper = new ObjectMapper();
                if (jsonString != null) {
                    Map<String, Object> data = mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
                    });

                    // Write failed email data to Excel sheet
                    List<Map<String, Object>> failedEmails = (List<Map<String, Object>>) data.get("Failed Email");
                    for (Map<String, Object> item : failedEmails) {
                        System.out.println("Email: " + item.get("Email"));
                        System.out.println("Failure Reason: " + item.get("FailureReason"));
                        System.out.println("Token: " + item.get("Token"));
                        System.out.println();
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(serialNumber++);
                        row.createCell(1).setCellValue((String) item.get("Email"));
                        row.createCell(2).setCellValue((String) item.get("Token"));
                        row.createCell(3).setCellValue("Failed");
                        row.createCell(4).setCellValue(""); // Empty for now, no exception type provided in the data
                        row.createCell(5).setCellValue((String) item.get("FailureReason")); // Empty for now, no failure message provided in the data
                        row.createCell(6).setCellValue(""); // Set file uploaded
                    }

                    // Write success email data to Excel sheet
                    successEmails = (Map<String, String>) data.get("Success Email");

                    List<Map<String, Object>> googleSheetError = (List<Map<String, Object>>) data.get("Google Sheet Error");
                    for (Map<String, Object> item : googleSheetError) {
                        isReportGenerated = true;
                        System.out.println("Email: " + item.get("Email"));
                        System.out.println("Failure Reason: " + item.get("FailureReason"));
                        System.out.println("Token: " + item.get("Token"));
                        System.out.println();
                        successEmails.remove((String) item.get("Token"));
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(serialNumber++);
                        row.createCell(1).setCellValue((String) item.get("Email"));
                        row.createCell(2).setCellValue((String) item.get("Token"));
                        row.createCell(3).setCellValue("Uploaded");
                        row.createCell(4).setCellValue(""); // Empty for now, no exception type provided in the data
                        row.createCell(5).setCellValue((String) item.get("FailureReason")); // Empty for now, no failure message provided in the data
                        row.createCell(6).setCellValue(fileUploaded); // Set file uploaded
                    }

                    // Process remaining success emails (if any)
                    for (Map.Entry<String, String> entry : successEmails.entrySet()) {
                        isReportGenerated = true;
                        String key = entry.getKey();
                        String value = entry.getValue();
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(serialNumber++);
                        row.createCell(1).setCellValue(value);
                        row.createCell(2).setCellValue(key);
                        row.createCell(3).setCellValue("Uploaded");
                        row.createCell(4).setCellValue(""); // Empty for now, no exception type provided in the data
                        row.createCell(5).setCellValue(""); // Empty for now, no failure message provided in the data
                        row.createCell(6).setCellValue(fileUploaded); // Set file uploaded
                    }
                } else {
                Log.info("json was empty");
            }
            }

                //For failed cases of file_uploader
                resultSet = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD, constant.getQueueOfFileUploader(constant.intHoursBack, "Failed", "File_Uploader"));
                while (resultSet.next()) {
                    isReportGenerated = true;
                    reason = resultSet.getString("reason");
                    if(reason!=null) {
                        String emails = resultSet.getString("detail");
                        System.out.println(reason);
                        reason = cleanJsonData(reason);
                        gson = new Gson();
                        Type listType = new TypeToken<List<Map<String, String>>>() {
                        }.getType();
                        dataList = gson.fromJson(emails, listType);

                        // Extract "upn" details and store them in the ArrayList
                        for (Map<String, String> item : dataList) {
                            String upn = item.get("upn");
                            TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>() {
                            };
                            exceptionDetails = gson.fromJson(reason, typeToken.getType());
                            Row row = sheet.createRow(rowNum++);
                            row.createCell(0).setCellValue(serialNumber++);
                            row.createCell(1).setCellValue(upn);
                            row.createCell(2).setCellValue(item.get("token"));
                            row.createCell(3).setCellValue("Failed");
                            row.createCell(4).setCellValue(exceptionDetails.get("ExceptionType")); // Empty for now, no exception type provided in the data
                            row.createCell(5).setCellValue(exceptionDetails.get("FailureReason"));
                            row.createCell(6).setCellValue("");
                        }
                    }else{
                        Log.info("File_Upload status failed Reason was empty");
                    }
                }
                resultSet = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD, constant.getQueueOfFileUploader(constant.intHoursBack, "Failed", "Performer"));
                while (resultSet.next()) {
                    isReportGenerated = true;
                    reason = resultSet.getString("reason");
                    if(reason!=null) {
                        String detail = resultSet.getString("detail");
                        jsonObject = JsonParser.parseString(detail).getAsJsonObject();
                        mail = jsonObject.get("upn").getAsString();
                        token = jsonObject.get("token").getAsString();
                        reason = cleanJsonData(reason);
                        gson = new Gson();
                        TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>() {
                        };
                        exceptionDetails = gson.fromJson(reason, typeToken.getType());
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(serialNumber++);
                        row.createCell(1).setCellValue(mail);
                        row.createCell(2).setCellValue(token);
                        row.createCell(3).setCellValue("Failed");
                        row.createCell(4).setCellValue(exceptionDetails.get("ExceptionType")); // Empty for now, no exception type provided in the data
                        row.createCell(5).setCellValue(exceptionDetails.get("FailureReason"));
                        row.createCell(6).setCellValue("");
                    }else{
                        Log.info("Performer status failed Reason was empty");
                    }
                }
                resultSet = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME, constant.SQL_PASS_WORD, constant.getQueueOfFileUploader(constant.intHoursBack, "Failed", "CSV_Writer"));
                while (resultSet.next()) {
                    isReportGenerated = true;
                    reason = resultSet.getString("reason");
                    if(reason!=null){
                    String detail = resultSet.getString("detail");
                    jsonObject = JsonParser.parseString(detail).getAsJsonObject();
                    mail = jsonObject.get("upn").getAsString();
                    token = jsonObject.get("token").getAsString();
                    reason = cleanJsonData(reason);
                    gson = new Gson();
                    TypeToken<Map<String, String>> typeToken = new TypeToken<Map<String, String>>() {
                    };
                    exceptionDetails = gson.fromJson(reason, typeToken.getType());
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(serialNumber++);
                    row.createCell(1).setCellValue(mail);
                    row.createCell(2).setCellValue(token);
                    row.createCell(3).setCellValue("Failed");
                    row.createCell(4).setCellValue(exceptionDetails.get("ExceptionType")); // Empty for now, no exception type provided in the data
                    row.createCell(5).setCellValue(exceptionDetails.get("FailureReason"));
                    row.createCell(6).setCellValue("");
                    }else{
                        Log.info("CSV_Writer status failed Reason was empty");
                    }
                }
                // Auto-size the columns
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }


                // Write the workbook content to a file

                currentDate = new Date();
                dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                formattedDate = dateFormat.format(currentDate);

                if (isReportGenerated) {
                    try (FileOutputStream outputStream = new FileOutputStream(constant.reportPath + "SCS_IAM_DeepNet_" + formattedDate + ".xlsx")) {
                        workbook.write(outputStream); // SCS_IAM_DeepNet_Report_yyyy_MM_DD_hh_mm_ss// LOgs
                    }
                    strExceptionMessage = "Please find the report attached";
                    path = new String[]{constant.reportPath + "SCS_IAM_DeepNet_" + formattedDate + ".xlsx"};
                    UtilClass.sendEmail(strExceptionMessage, path, formattedDate);
                    Log.info("Report generated succesfully");
                    System.out.println("Report generated successfully.");
                } else {
                    Log.info("No record found to generate the report.");
                    System.out.println("No record found to generate the report.");
                }


        } catch (Exception e) {
            strExceptionMessage = "Unable to write to xl sheet due to:" + e.getMessage();
            UtilClass.sendEmail(constant.SYSTEM_EXCEPTION, strExceptionMessage,constant.processName,0,"",0);
            e.printStackTrace();
        }
        if(!isReportGenerated){
            strExceptionMessage="No new transaction found to generate the report.";
            UtilClass.sendEmail(strExceptionMessage,path,formattedDate);
        }
    }
}
