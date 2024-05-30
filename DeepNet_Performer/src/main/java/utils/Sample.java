package utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Sample {

    static ResultSet resultSet1;
    static Constants constant = new Constants();

    public static void main(String[] args) throws IOException, SQLException, SystemException {
//        String email="nithin@hbc.com";
//        String substring = email.length() > 8 ? email.substring(email.length() - 8) : email;
//        if(substring.equals("@hbc.com")){
//            System.out.println("Email matched");
//        }else{
//            System.out.println("Please enter valid email");
//        }
   /*     System.out.println("hi");
        String file="C:\\Deepnet_Termination\\filesToBeUploaded\\Deepnet_Bulk.csv";
        BufferedReader reader=null;
        String line="";
        JSONObject jsonObject = new JSONObject();
        try{
            reader=new BufferedReader(new FileReader(file));
            while((line= reader.readLine())!=null){
                String[] oneLine=line.split(",");
                String token=oneLine[0];
                String email = oneLine[1];
                String manufactureCode = oneLine[2];
                String productId = oneLine[3];
                jsonObject.put("token",token);
                jsonObject.put("email", email);
                jsonObject.put("manufactureCode", manufactureCode);
                jsonObject.put("productId", productId);

                String jsonString = jsonObject.toString();
                System.out.println(jsonString);
            }
        }catch(Exception e){
          System.out.println(e.getMessage());
        }finally {
            reader.close();
        }*/

        // The string to be written to the CSV file
              /*  String data = "12345,Doe@saks,30,54,67\n12345,Smith@saks,25,43,67\n";

                // Write the string to the CSV file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,true))) {
                    writer.write(data);
                    System.out.println("Data has been written to the CSV file successfully.");
                } catch (IOException e) {
                    System.err.println("Error writing to the CSV file: " + e.getMessage());
                }*/
        // get work item queue

       /* try {
            resultSet1 = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD, constant.SQL_QUERY_TO_SEARCH_FILE_UPLOADER);
            while (resultSet1.next()) {
                System.out.println(resultSet1.getString("detail"));
                String column="status";
                int id=resultSet1.getInt("id");
                UtilClass.updateDatabase(column,"InProgress",id,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                        constant.SQL_PASS_WORD);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }*/


        // update summary details

      /* String queryToPass,updatedJson,column;
        JSONObject jsonToUpdateDB = new JSONObject();
        ArrayList<String> mailList=new ArrayList<>();
        ArrayList<String> errorList=new ArrayList<>();
        mailList.add("Singh.Aanchal@hbc.com");
        errorList.add("No Token Found");
        mailList.add("Fady.Aaraj@hbc.com");
        errorList.add("No Token Found");
        String mail,error;
        column="output";// update in reason column
        for(int i=0;i<mailList.size();i++){
          mail=mailList.get(i);
          error=errorList.get(i);
          jsonToUpdateDB.put("Output",error);
          updatedJson=jsonToUpdateDB.toString();
            UtilClass.updateDatabase(column,error,mail,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                    constant.SQL_PASS_WORD);
        }*/

        // all token and email details add it to new csv file

        /*List<String[]> updatedLines = new ArrayList<>();
        String detail,mail,token;
        JsonObject jsonObject;
        int id;
        String csvFilePath = "C:\\Deepnet_Termination\\filesToBeUploaded\\File_To_Upload.csv"; // Specify the path where you want to save the CSV file
        String[] headers = {"Email", "Token"};
        resultSet1 =UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL,constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD,constant.SQL_QUERY_TO_SEARCH_FILE_UPLOADER);
        while(resultSet1.next()) {
            String[] temp=new String[3];
            detail = resultSet1.getString("detail");
            jsonObject = JsonParser.parseString(detail).getAsJsonObject();
            id = resultSet1.getInt("id");
            mail = jsonObject.get("upn").getAsString();
            temp[0]=mail;
            token = jsonObject.get("token").getAsString();
            temp[1]=token;
            updatedLines.add(temp);

        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            writer.writeNext(headers);
            for (String[] list : updatedLines) {
                writer.writeNext(list);
            }
            System.out.println("Data has been written to " + csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to write data to CSV file: " + e.getMessage());
        }*/

        // successfull scenario
       /* String column,reason;
        JSONObject jsonObject=new JSONObject();
        column="status";
        String mail="karthik@saks.com";
        UtilClass.updateDatabase(column,"Success",mail,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD);
        // for failure scenario and send email
        UtilClass.updateDatabase(column,"Failed",mail,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD);
        jsonObject.put("Exception Type","System");
        jsonObject.put("FailureReason","e.getMessege");
        reason=jsonObject.toString();
        column="reason";
        UtilClass.updateDatabase(column,reason,mail,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD);
        //sendMail("System",e.getMessege());
        //for buissness exception
        jsonObject.put("Exception Type","Business");
        jsonObject.put("FailureReason","e.getMessege");
        reason=jsonObject.toString();
        column="reason";
        UtilClass.updateDatabase(column,reason,mail,constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD);
        //sendMail("System",e.getMessege());

        */
        // get all success status with state='Deepnrt_CSV_Writter'
        /*String detail,upn,token,productid,manufacturecode,serialNumber,modelcode,work_item_id = null,
        queue_name,state;
        JsonObject json;
        JSONArray jsonArray = new JSONArray();
        resultSet1 =UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL,constant.SQL_USER_NAME,
                constant.SQL_PASS_WORD,constant.SQL_QUERY_TO_SEARCH_FILE_UPLOADER2);
        try {
            while (resultSet1.next()) {
                JSONObject updatedDetail=new JSONObject();
                detail = resultSet1.getString("detail");
                work_item_id = resultSet1.getString("work_item_id");

                json = JsonParser.parseString(detail).getAsJsonObject();
                upn = json.get("upn").getAsString();
                serialNumber = json.get("serial Number").getAsString();
                token = json.get("token").getAsString();
                productid = json.get("product Id").getAsString();
                manufacturecode = json.get("manufacture Code").getAsString();
                modelcode = json.get("model code").getAsString();
                updatedDetail.put("upn", upn);
                updatedDetail.put("product Id", productid);
                updatedDetail.put("model code", modelcode);
                updatedDetail.put("serial Number", serialNumber);
                updatedDetail.put("manufacture Code", manufacturecode);
                updatedDetail.put("token", token);
                jsonArray.put(updatedDetail);
                System.out.println("came");
            }
        }catch(Exception e){
            System.out.println("error:"+e.getMessage());
        }
        System.out.println(jsonArray);
        queue_name="Deepnet_FileUploader";
        state="File_Uploader";
        UtilClass.insertDataIntoDb(constant.SQL_INSERT_QUERY_FOR_FILE_UPLOAD,work_item_id,queue_name,state,"New",jsonArray,0,constant.SQL_JDBC_URL,constant.SQL_USER_NAME,constant.SQL_PASS_WORD);

// csv
//        public void updateTokenColumnInCSV(ArrayList<String> tokenList) throws IOException, SystemException {
//            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//                String line;
//                int i=-1;
//                while ((line = reader.readLine()) != null) {
//                    String[] parts = line.split(",");
//                    if(i>=0) {
//                        // Update the token column
//                        parts[0] = String.valueOf(tokenList.get(i));
//                    }
//                    updatedLines.add(String.join(",", parts));
//                    i++;
//                }
//            } catch (Exception e) {
//                Log.error("Error writing to the CSV file: " + e.getMessage());
//                throw new SystemException("System Exception:"+e.getMessage());
//            }
//            // Write the updated content back to the CSV file
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//                for (String updatedLine : updatedLines) {
//                    writer.write(updatedLine);
//                    writer.newLine();
//                }
//                Log.info("CSV file updated successfully.");
//            } catch (IOException e) {
//                Log.error("Error writing to the CSV file: " + e.getMessage());
//                throw new SystemException("System Exception:"+e.getMessage());
//            }
//
//        }

         */


    }
}

