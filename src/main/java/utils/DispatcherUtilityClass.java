package utils;
import exceptionutil.ApplicationException;
import model.PojoClass;
import org.apache.log4j.xml.DOMConfigurator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.ResultSet;

public class DispatcherUtilityClass {
    public static void initialiseLog4j() {
        DOMConfigurator.configure(utils.Constant.LOG4J_XML);
    }
    public static void setSpecificDataToPojo(String specificData) {
        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();
            // Deserialize JSON string to Java object
            Product product = objectMapper.readValue(specificData, Product.class);
            // Create an instance of PojoClass
            PojoClass pojo = new PojoClass();
            // Set values from Product object to PojoClass using setters
            pojo.setStrFileName(product.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static class Product {
        @JsonProperty("FileToProcess")
        public String fileName;
        public String getFileName() {
            return fileName;
        }
    }

    public static void updateDbStatusAndRetry(ResultSet resultSet, int retry,
                                              int id,
                                              String workitemId,
                                              String queueName, Exception e,
                                              String specificData,
                                              String state, WebDriver driver) throws ApplicationException, Exception {
        if (resultSet != null) {
            if (retry < utils.Constant.MAX_RETRY) {
                utils.DatabaseUtil.updateDatabase("status", "Retried", id);
                utils.DatabaseUtil.updateDatabase("reason", e.getMessage().replace(
                        "'", ""), id);
                retry = retry + 1;
                utils.DatabaseUtil.insertDataIntoDb(utils.Constant.SQL_WORKITEM,
                        workitemId, queueName, state, "New", specificData, retry);
                driver.quit();
                //new Dispatcher();
                Log.error(e.getMessage());
                /*throw new ApplicationException(e.getMessage());*/
            } else {
                utils.DatabaseUtil.updateDatabase("status", "Failed", id);
                utils.DatabaseUtil.updateDatabase("reason", e.getMessage(), id);
                driver.quit();
                //new Dispatcher();
                Log.error(e.getMessage());
                /*throw new ApplicationException(e.getMessage());*/
            }
        } else {
            Log.info("Not Retrying as the Queue Item is not yet Fetched");
            throw new ApplicationException(e.getMessage());
        }
    }
}
