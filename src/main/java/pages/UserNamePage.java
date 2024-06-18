package pages;

import logutil.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utility.BaseFunctions;
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
static String strExceptionMessage;

    public static void enterusername(WebDriver driver) throws InterruptedException {

        //Launch the URL in the chrome driver
            driver.manage().deleteAllCookies();
            Log.info("Deleted cookies");
            driver.get(Constants.OMS_URL);
            driver.manage().window().maximize();
            Log.info("Launched IBM application in chrome");
            String username = Constants.USER_NAME;

            WebDriverWait wait = new WebDriverWait(driver, Constants.WAIT_TIME);

            type(usernamefield, username, wait);
            click(continuebtn, wait);
            Log.info("Entered username");


        }

    }

