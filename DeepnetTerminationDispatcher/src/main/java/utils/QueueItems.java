package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class QueueItems {
    Connection conection;
    PreparedStatement statement;
    int rowsaffected;
    ResultSet resultSet;
    String work_item_id;
    String queue_name;
    String state;
    String status;
    String detail;
    String reason;
    String comment;
    String output;
    int retry;
    int id;
    ReadPropertyFile propertyFile;

    public QueueItems(ReadPropertyFile propertyFile) {
        this.propertyFile = propertyFile;

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getWork_item_id() {
        return work_item_id;
    }

    public void setWork_item_id(String work_item_id) {
        this.work_item_id = work_item_id;
    }
    public String getQueue_name() {
        return queue_name;
    }

    public void setQueue_name(String queue_name) {
        this.queue_name = queue_name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getOutput() {
        return output;
    }
    public void setOutput(String output) {
        this.output = output;
    }
    public int getRetry() {
        return retry;
    }
    public void setRetry(int retry) {
        this.retry = retry;
    }
    public PreparedStatement addQueueItem(String dbTable,
                                          Connection dbConnection) throws Exception {
        statement = dbConnection.prepareStatement("INSERT into " + dbTable +
                "(work_item_id,queue_name,state,status,detail,retry) Values " +
                "(?,?,?,?,?,?)");
        statement.setString(0,"1");
        statement.setString(1,"2");
        return statement;
    }
    public ResultSet getQueueItem(String dbTable,
                                  String queue_name,Connection dbConnection) throws SQLException {
        String getnewQueue = "Select * From " + dbTable + " WHERE " +
                "status = 'New' and queue_name='"+queue_name+"'";
        statement = dbConnection.prepareStatement(getnewQueue);

        System.out.println(statement);
        resultSet = statement.executeQuery();
        System.out.println(statement);
        return resultSet;
    }
    public void updateDataBaseValue(String dbTable, String tableColumn,
                                    String value, int id,
                                    Connection dbConnection) throws Exception {
        String setStatusQuery = "UPDATE " + dbTable + " SET "+tableColumn+" = ? WHERE " +
                "id = ?";
        statement = dbConnection.prepareStatement(setStatusQuery);
        statement.setString(1, value);
        statement.setInt(2, id);
        rowsaffected = statement.executeUpdate();
        if (rowsaffected == 1) {
            Log.info("Updated the column "+tableColumn+" to "+value+ "for id: " + id);
        }
    }
}
