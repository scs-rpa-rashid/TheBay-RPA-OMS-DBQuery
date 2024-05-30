package pages;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.json.JSONObject;
import utils.Constants;
import utils.Log;
import utils.SystemException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import static utils.UtilClass.sendEmail;

public class SheetsQuickstart {
    private static final String APPLICATION_NAME = "SCS-RPA-BOT-ACCOUNT";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_FILE_PATH = "C:\\IAM_Deepnet\\DeepNet_Performer\\src\\main\\resources\\credentials.json";
    static Sheets.Spreadsheets spreadsheets;
    public static String strExceptionMessege;

    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);
    static NetHttpTransport HTTP_TRANSPORT = null;
    public static String spreadsheetId;
    public static String range;
    public static ValueRange response;
    static ArrayList<String> unavailableEmailsInGoogleSheet = new ArrayList<>();
    static boolean unavailableEmailFound = false;
    static Constants constant = new Constants();
    static HashMap<String, Boolean> tokenFound = new HashMap<>();
    static HashMap<String, Boolean> emailFound = new HashMap<>();
    static ArrayList<JSONObject> listOferror = new ArrayList<>();

    public SheetsQuickstart(NetHttpTransport httpTransport) throws GeneralSecurityException, IOException {
        this.HTTP_TRANSPORT = httpTransport;
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        spreadsheets = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build().spreadsheets();
    }

    public static void getSpreadSheetInstance() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        spreadsheets = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build().spreadsheets();
    }

    public static void writeSheet(List<List<Object>> inputData, String sheetAndRange, String SheetId) throws IOException, GeneralSecurityException, SystemException {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            spreadsheets = new Sheets.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME).build().spreadsheets();
            ValueRange body = new ValueRange().setValues(inputData);
            UpdateValuesResponse result = spreadsheets.values().update(SheetId, sheetAndRange, body).setValueInputOption("RAW").execute();
            System.out.println("Cell updated:" + result.getUpdatedCells());
            if (result.getUpdatedCells() > 0) {
                Log.info("Google sheet was updated successfully");
            } else {
                throw new SystemException("Zero rows updated in Google Sheet");
            }
        } catch (Exception e) {
            throw new SystemException("System Exception:" + e.getMessage());
        }
    }

//    public static void createNewSpreadSheet() throws GeneralSecurityException, IOException {
//        Spreadsheet createdResponse = null;
//        try {
//            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                    .setApplicationName(APPLICATION_NAME).build();
//            SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
//            spreadsheetProperties.setTitle("Practice_11");
//            SheetProperties sheetProperties = new SheetProperties();
//            sheetProperties.setTitle("Page1");
//            Sheet sheet = new Sheet().setProperties(sheetProperties);
//
//            Spreadsheet spreadsheet = new Spreadsheet().setProperties(spreadsheetProperties).setSheets(Collections.singletonList(sheet));
//            createdResponse = service.spreadsheets().create(spreadsheet).execute();
//
//            System.out.println("URL:" + createdResponse.getSpreadsheetUrl());
//
//            List<List<Object>> data = new ArrayList<>();
//            List<Object> list2 = new ArrayList<>();
//            list2.add("Good");
//            list2.add("morning");
//            list2.add("=1+1");
//            data.add(list2);
//            ArrayList<Object> data1 = new ArrayList<>(
//                    Arrays.asList("source", "destination", "status code", "test status")
//            );
//            // writeSheet(data1, "A1", createdResponse.getSpreadsheetId());
//        } catch (Exception e) {
//            System.out.println("Error:" + e);
//        }
//    }

    private static GoogleCredential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        FileInputStream serviceAccountStream = new FileInputStream(SERVICE_ACCOUNT_KEY_FILE_PATH);
        return GoogleCredential.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> emailPhoneMap = new HashMap<>();
        ArrayList<JSONObject> data;
        // Add dummy data to the HashMap
        emailPhoneMap.put("12345678", "john.doe@example.com");
        emailPhoneMap.put("34545678", "john.doe@example.com");
        emailPhoneMap.put("87654321", "jane.smith@example.com");
        emailPhoneMap.put("13579246", "sam.wilson@example.com");
        data = removeEmailFromGoogleSheet(emailPhoneMap);
        System.out.println(data);
    }
    static ArrayList<String> tokenSuccessfullyremoved=new ArrayList<>();
    public static ArrayList<JSONObject> removeEmailFromGoogleSheet(Map<String, String> mailList) throws Exception {
        constant.processName = "SCS_IAM_DeepNet_Google Sheet Updation";
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        spreadsheetId="1AOTthjxXN1UIuBIJSAAny3MCkw4Fvvvt9fKVMTeU1xM";
       // spreadsheetId = "1KGpVGc6m_yG2zcS9NOxObvanpl0jqY4I5koN6gGqAyc";// put it in constant
        range = "Active Codes!A1:B";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        int i = 0;
        int count=0;
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("email    otp");
            System.out.println(mailList);

            for (List row : values) {
                count++;
                tokenSuccessfullyremoved.clear();
                if (i != 0) {
                    for (Map.Entry<String, String> entry : mailList.entrySet()) {
                        String token = entry.getKey();
                        String mail = entry.getValue();

                        if (row.get(0).toString().trim().equals(mail.trim())) {
                            emailFound.put(mail, true);
                            if (row.get(1).toString().trim().equals(token.trim())) {
                                System.out.println(row.get(0) + ":" + row.get(1));
                                row.set(0, "");
                                tokenSuccessfullyremoved.add(token);
                            }
                        } else if (row.get(1).toString().trim().equals(token.trim())) {
                            tokenFound.put(mail, true);
                        }
                    }
                    for(String token:tokenSuccessfullyremoved){
                        mailList.remove(token);
                        System.out.println("removed---------------------------"+token);
                        System.out.println(mailList);
                    }
                }
                i++;
            }
        }
        System.out.println("Total rows:"+count);

        for (Map.Entry<String, String> entry : mailList.entrySet()) {
            JSONObject googleSheetFailure = new JSONObject();
            String key = entry.getKey();
            String value = entry.getValue();
            if (emailFound.containsKey(value)) {
                if (emailFound.get(value)) {
                    googleSheetFailure.put("Email", value);
                    googleSheetFailure.put("FailureReason", "Serial Number was not found in Google sheet");
                    googleSheetFailure.put("Token", key);
                    System.out.println("Email not found:"+key+" "+value);
                }
            } else if (tokenFound.containsKey(value)) {
                if (tokenFound.get(value)) {
                    googleSheetFailure.put("Email", value);
                    googleSheetFailure.put("FailureReason", "Email was not found in Google sheet");
                    googleSheetFailure.put("Token", key);
                    googleSheetFailure.put("FailureReason", "Serial Number was not found in Google sheet");
                    System.out.println("token not found:"+key+" "+value);
                }
            } else {
                googleSheetFailure.put("Email", value);
                googleSheetFailure.put("FailureReason", "Both Email and Token was not found in Google sheet");
                googleSheetFailure.put("Token", key);
            }
            listOferror.add(googleSheetFailure);
            unavailableEmailFound = true;
        }
        if (unavailableEmailFound) {
            strExceptionMessege = "Email or token not found for " + listOferror + " these transactions in the Google Sheet.";
            Log.error(strExceptionMessege);
            // sendEmail(constant.BUSINESS_EXCEPTION, strExceptionMessege, constant.processName, 0, "", 0);
        }
        writeSheet(values, range, spreadsheetId);
        return listOferror;
    }
}
