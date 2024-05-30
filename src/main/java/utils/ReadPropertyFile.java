package utils;
import exceptionutil.ApplicationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class ReadPropertyFile {
    String strExceptionMessage;
    String propertyFilePath;
    Properties properties;
    public ReadPropertyFile(String propertyFilePath) throws Exception {
        this.propertyFilePath = propertyFilePath;
        properties=new Properties(); //Make sure spaces are there in assignment
        try {
            FileInputStream filestream = new FileInputStream(propertyFilePath);
            properties.load(filestream);
            Log.info("Property file loaded: "+propertyFilePath);
        }
        catch (IOException e)
        {
            strExceptionMessage="Failed to load property file due to: "+e.getMessage();
            Log.error(strExceptionMessage);
            throw new ApplicationException(strExceptionMessage);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}



