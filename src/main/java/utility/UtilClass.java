package utility;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.opentelemetry.context.Context;
import logutil.Log;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.seleniumhq.jetty9.server.handler.ContextHandler;
import java.util.Collections;



public class UtilClass {

    ChromeOptions chromeOptions;
    String strExceptionMessage;
    WebDriver driver;
    public void setDriver(WebDriver driver1) {

        driver = driver1;

    }
    public WebDriver getDriver() {
        createDriver();
        return driver;
    }
    public UtilClass() {
    }
    public UtilClass(WebDriver driver) {
        createDriver();
        this.driver = driver;
    }
    public void createDriver() {

        try {
            Log.info("Launching chrome driver");

            WebDriverManager.chromedriver().setup();
            //System.setProperty("webdriver.chrome.driver",
                  //  Constants.CHROMEDRIVER_PATH);
             chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("useAutomationExtension", false);
            chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--ignore-ssl-errors=yes");
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.addArguments("--allow-insecure-localhost");
            chromeOptions.addArguments("--allow-running-insecure-content");
            chromeOptions.addArguments("--incognito");
            // chromeOptions.addArguments("--headless=new");
            // chromeOptions.setBinary("C:\\Program Files\\Google\\Chrome" +
            //  "\\Application\\chrome.exe");
            chromeOptions.addArguments("--ignore-certificate-errors,--no-sandbox,--headless,disable-gpu");
            //ChromeOptions.CAPABILITY ("--incognito");
            chromeOptions.setAcceptInsecureCerts(true);
            driver = new ChromeDriver(chromeOptions);
            driver.manage().deleteAllCookies();
            setDriver(driver);
        }
        catch(Exception e){
            strExceptionMessage="Error in opening chrome driver. Exception message: "+e.getMessage()+'\n'+"Exception cause: "+e.getCause();
            Log.error(strExceptionMessage);
            throw new RuntimeException(strExceptionMessage);
        }
    }
    public static void initialiseLog4j() {
        // Provide Log4j configuration settings
        DOMConfigurator.configure(Constants.LOG4J_XML);
    }
}
