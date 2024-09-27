package utility;

import exceptionutil.ApplicationException;
import logutil.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ReadPropertyFile {

   static String strExceptionMessage;
   static Properties properties;

    static {
        properties=new Properties();
    }

    public static void loadProperties(String filepath) throws ApplicationException {
        try {
            FileInputStream filestream = new FileInputStream(filepath);
            properties.load(filestream);
            Log.info("Property file loaded: "+filepath);
        }
        catch (IOException e)
        {
            strExceptionMessage="Failed to load property file due to: "+e.getMessage();
            Log.error(strExceptionMessage);
            throw new ApplicationException(strExceptionMessage);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}



