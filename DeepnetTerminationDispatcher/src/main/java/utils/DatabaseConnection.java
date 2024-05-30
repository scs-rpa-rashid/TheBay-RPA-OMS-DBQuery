package utils;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    public static void main (String[] args) throws SQLException {
        // Initialize logging
        UtilityClass.initialiseLog4j();
        SQLServerConnection sqlServerConnection = new SQLServerConnection();
        Connection connection = sqlServerConnection.connectToSQLDb();
        System.out.println(connection);
        AddTransactionToQueue queueItemUtils =
                new AddTransactionToQueue();
        String filename = null;
        String filepath = null;
        queueItemUtils.addQueueItem(filename, filepath);

    }
}
