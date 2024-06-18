package pages;

import exceptionutil.ApplicationException;
import logutil.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import utility.BaseFunctions;
import utility.Constants;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BaseFunctions {

    @FindBy(id = "username")
    WebElement usernameWebElement;

    @FindBy(id = "continue-button")
    WebElement continueBtn;

    @FindBy(id = "password")
    WebElement pswdWebElement;
    @FindBy(id = "signinbutton")
    WebElement signinBtn;
    String strExceptionMessage;
    String username;

    WebDriverWait wait;
    String pass;

    @FindBy(id = "password-error-msg")
    WebElement loginErrorWebElement;

    boolean boolLoginErr;
    WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }

    public void login(WebDriver driver) throws ApplicationException {

        try {
            //Clear cookies
            driver.manage().deleteAllCookies();
            Log.info("Deleted cookies");
            //Launch the URL in the chrome driver
            driver.get(Constants.OMS_URL);
            //Maximize window
            driver.manage().window().maximize();
            Log.info("Launched IBM application in chrome");
            username = Constants.USER_NAME;
            wait = new WebDriverWait(driver, Constants.WAIT_TIME);

            //Type into username field
            type(usernameWebElement, username, wait);
            //Click continue
            click(continueBtn, wait);
            Log.info("Entered username");

            pass = Constants.PASSWORD;
            //Type password
            type(pswdWebElement, pass, wait);
            //Click signin
            click(signinBtn, wait);
            Log.info("Clicked on Sign-in");
            wait = new WebDriverWait(driver, Constants.LOGIN_ERROR_WAIT_TIME);
            boolean boolLoginErr = waitForElementToAppear(loginErrorWebElement, wait);

            if (boolLoginErr) {

                strExceptionMessage = getText(loginErrorWebElement, wait);
                Log.error("Login error: " + strExceptionMessage);
                throw new ApplicationException(strExceptionMessage);
            }
        }
        catch (Exception e){
            strExceptionMessage="Failed to login to IBM application due to: "+e.getMessage()+'\n'+"Exception source: "+e;
            throw new ApplicationException(strExceptionMessage);
        }


    }


}

