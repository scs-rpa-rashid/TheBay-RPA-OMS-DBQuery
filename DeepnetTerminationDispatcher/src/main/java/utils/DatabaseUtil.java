package utils;

import org.json.JSONObject;

import java.sql.*;

public class DatabaseUtil {

    public static ResultSet fetchDataFromDb(String jdbcUrl, String userName,
                                            String password, String sql) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, userName, password);
            PreparedStatement statement = connection.prepareStatement(sql);
            // Execute the query and return the ResultSet
            return statement.executeQuery();
        } catch (SQLException e) {
            System.err.println("SQL Error: " +e.getMessage());
            return null;
        }
    }
    public static void updateDatabase(String column,String value,int id) {
        String updateQuery =
                "UPDATE RPADev.SCS_IAM_Deepnet.interim Set "+column+" = '"+value+"' WHERE id ='" + id + "'";
        try (Connection connection =
                     DatabaseUtil.connectDB(Constant.SQL_JDBC_URL,
                             Constant.SQL_USER_NAME, Constant.DB_KEY);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            // Execute the insert statement
            int rowsUpdated  = statement.executeUpdate();
            System.out.println(statement.toString());
            if (rowsUpdated  > 0) {
                System.out.println("Data updated successfully");
            } else {
                System.out.println("Failed to update the data data.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    public static Connection connectDB(String jdbcUrl, String userName, String passWord) {
        Connection connection = null;
        try {
            // Load the JDBC driver class
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Establish database connection
            connection = DriverManager.getConnection(jdbcUrl, userName, passWord);
            if (connection != null) {
                System.out.println("Connected to the database!");
                Log.info("Connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
                Log.error("Failed to connect to the database.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load JDBC driver: " + e.getMessage());
            Log.error("Failed to load JDBC driver: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            Log.error("Database connection error: " + e.getMessage());

        }
        return connection;
    }
    public static void insertDataIntoDb (String sql, String
            workItemId, String queueName, String state, String status, Object detail,
                                         int retry){
        try (Connection connection = DatabaseUtil.connectDB(Constant.SQL_JDBC_URL,
                Constant.SQL_USER_NAME, Constant.DB_KEY);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set values for the prepared statement
            statement.setString(1, workItemId);
            statement.setString(2, queueName);
            statement.setString(3, state);
            statement.setString(4, status);
            statement.setString(5, String.valueOf(detail));
            statement.setInt(6, retry);
            // Execute the insert statement
            int rowsInserted = statement.executeUpdate();
            System.out.println(statement.toString());
            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully.");
            } else {
                System.out.println("Failed to insert data.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }
    public static void setTransactionFailed(String fileName, String reason,
                                            int intId) throws Exception {
        // Logic to set transaction as failed
        System.out.println(reason);
        Log.info(reason);
        DatabaseUtil.updateDatabase("status", "Failed", intId);
        JSONObject failuereJson = new JSONObject();
        failuereJson.put("ExceptionType:","Business");
        failuereJson.put("FailureReason",reason);
        String detail = failuereJson.toString();
        DatabaseUtil.updateDatabase("reason",detail,intId);
        System.out.println("Transaction failed for file: " + fileName);
        UtilClass.sendEmail("Business"+"Exception",reason,
                Constant.DispatcherprocessName);
        throw new BusinessException(reason);
    }
}

