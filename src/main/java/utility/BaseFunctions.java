package utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
public class BaseFunctions extends UtilClass {
    WebDriver driver;
    static String text;
    public static void click(WebElement webElement,WebDriverWait waitTime) {
        try {
            waitTime.until(ExpectedConditions.elementToBeClickable(webElement)).click();
        }
        catch (Exception e){
             throw new RuntimeException("Click failed due to exception: "+e.getMessage());
        }
    }
    public static void  type(WebElement webElement, String input,
                        WebDriverWait waitTime) {
        try {
            waitTime.until(ExpectedConditions.elementToBeClickable(webElement)).sendKeys(input);
        }
        catch (Exception e){
            throw new RuntimeException("Type into failed due to exception: "+e.getMessage());
        }
    }
    public static String getText(WebElement webElement,
                        WebDriverWait waitTime) {
        text = waitTime.until(ExpectedConditions.visibilityOf(webElement)).getText();
                return text;
    }
    public static  void clearTextField(WebElement webElement,
                                 WebDriverWait waitTime) {
            waitTime.until(ExpectedConditions.elementToBeClickable(webElement)).clear();
    }
    public static boolean waitUntilElementDisappear(WebElement webElement,
                                         WebDriverWait waitTime) {
        return waitTime.until(ExpectedConditions.invisibilityOf(webElement));
    }
    public void wait(WebDriverWait wait){
        driver=getDriver();
        wait = new WebDriverWait(driver,Constants.WAIT_TIME);
    }

    public boolean waitForElementToAppear(WebElement webElement, WebDriverWait waitTime){

        try {
            waitTime.until(ExpectedConditions.visibilityOf(webElement));
            return true;
        }
        catch(Exception e){
            return false;
        }

    }
}
