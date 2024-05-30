package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class SQLServerConnection {
    String user;
    String url;
    String password;

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String s) {
        this.url = url;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String s) {
        this.password = password;
    }
    public static Connection connectToSQLDb() {
        Connection connection = null;
        try {
            String url = "jdbc:sqlserver://thebay-rds-uipath-dev" +
                    ".cyeuvydpkw6m.us-east-1.rds.amazonaws.com;" +
                    "databaseName=TheBayUipathOrchestratorDev;encrypt=true;trustServerCertificate=true";
            String user = "bayrpasqladmin";
            String password = "chlp7#r!b=sWa9&7";
            // Load the SQLServer JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // Connect to the database
            connection = DriverManager.getConnection(url,user,
                    password);
        } catch (SQLException | ClassNotFoundException e) {
            Log.error("Failure in connecting to SQL DB due to: "+e);
        }
        return connection;
    }
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed successfully.");
            } catch (SQLException e) {
                // Handle closing error
                e.printStackTrace();
            }
        }
    }
}

