package utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class AddTransactionToQueue {
    String insertTransactionsQuery;
    int rowsAffected;
    LocalDateTime localDateTime;
    String workitemId, detail, state, status, specific_data, reason, output, retry, comment;
    LocalDate localDate;
    Connection connection;
    public void addQueueItem(String filename, String filepath) throws SQLException {
        connection = SQLServerConnection.connectToSQLDb();
        PreparedStatement statement = null;
        detail = "{\"FileToUpload\": \"" + filepath + "\"}";
        reason = "";
        comment = "";
        output = "";
        retry = "";
        insertTransactionsQuery = Constant.INSERT_INTODB_QUERY;
        localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "_uuuu-MM-dd HH:mm:ss");
        workitemId = filename + localDateTime.format(formatter);
        localDate = LocalDate.now();
        //detail = filepath;
        state = Constant.QUEUE_STATE;
        status = Constant.QUEUE_STATUS;
        try {
            statement = connection.prepareStatement(insertTransactionsQuery);
            statement.setString(1, workitemId);
            statement.setString(2, Constant.QUEUE_NAME);
            //  statement.setString(3, specific_data);
            statement.setString(3, state);
            statement.setString(4, status);
            statement.setString(5, detail);
            statement.setString(6, reason);
            statement.setString(7, comment);
            statement.setString(8, output);
            statement.setString(9, retry);
            rowsAffected = statement.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            if (rowsAffected == 1) {
                Log.info("Transaction added to queue with work_item_id: " + workitemId + specific_data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }/*
    public void getQueueItem() throws SQLException {
        PojoClass queueItemFields = new PojoClass();
        connection=SQLServerConnection.connectToSQLDb();
        PreparedStatement statement2;

        Log.info("Getting Transaction item");
        queueItemFields.setStatus("InProgress");

        try {
            String UpdateStatusQuery = Constants.UPDATEQUERY_GETQUEUEITEM;
            System.out.println(UpdateStatusQuery);
            statement2 = connection.prepareStatement(UpdateStatusQuery);
            statement2.setString(1, queueItemFields.getStatus());
            statement2.setString(2, "New");
            statement2.setString(3, workitemId);
            System.out.println(statement2);
            rowsAffected = statement2.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            if (rowsAffected == 1) {
                Log.info("Transaction set to InProgress for work_item_id: " + workitemId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {


        }

    }



*/
}