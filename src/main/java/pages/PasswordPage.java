package pages;

import logutil.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.BaseFunctions;
import utility.Constants;
import org.openqa.selenium.support.ui.WebDriverWait;
public class PasswordPage extends BaseFunctions {
    @FindBy (id="password")
    static WebElement pswrdfield;
    @FindBy (id="signinbutton")
    static WebElement signinfield;
    public static void enterPassword(WebDriver driver) throws InterruptedException {
        String pass=Constants.PASSWORD;
        WebDriverWait wait = new WebDriverWait(driver,Constants.WAIT_TIME);
        type(pswrdfield,pass,wait);
        click(signinfield,wait);
        Log.info("Clicked on Sign-in");
    }
}
