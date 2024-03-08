package pages;

import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.BaseFunctions;
import utility.Log;
import utility.Constants;
import org.openqa.selenium.support.ui.WebDriverWait;


public class UserNamePage extends BaseFunctions {



    @FindBy (id="username")
    static WebElement usernamefield;

    @FindBy (id="continue-button")
    static WebElement continuebtn;

    @FindBy (xpath="//*[@id=\"password\"]")
    static WebElement pswrdfield;

    @FindBy (id="signinbutton")
    static WebElement signinfield;


    public static void enterusername(WebDriver driver) throws InterruptedException {

        //Launch the URL in the chrome driver
        driver.manage().deleteAllCookies();
        driver.get(Constants.OMS_URL);
        driver.manage().window().maximize();
        Log.info("Launched IBM application");
        String username = Constants.USER_NAME;

        WebDriverWait wait =new WebDriverWait(driver,Constants.WAIT_TIME);

        type(usernamefield,username,wait);
        click(continuebtn,wait);

    }
}
