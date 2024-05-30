package utils;

import java.sql.ResultSet;
import java.sql.SQLException;

class check {
    ResultSet resultSet;
    Constants constant=new Constants();
    public Boolean check() throws SQLException {
//        resultSet = UtilClass.getQueueItemFromDB(constant.SQL_JDBC_URL, constant.SQL_USER_NAME,
//                constant.SQL_PASS_WORD, constant.SQL_QUERY_TO_SEARCH);
        if(resultSet.next())
             return true;
        return false;
    }
}