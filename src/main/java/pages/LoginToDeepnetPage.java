package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.*;

import java.time.Duration;

import static utils.BaseFunctions.click;
import static utils.BaseFunctions.sendKeys;


public class LoginToDeepnetPage {
    public static WebDriver driver;
    public static WebDriverWait waitTime;
    public Constants constant=new Constants();
    public String strExceptionmessage;
    @FindBy(id="EmailAddress")
    WebElement userIdForm;
    @FindBy(xpath = "//div/input[@class='btn btn-primary']")
    WebElement useridSubmitbtn;
    @FindBy(id= "i0118")
    WebElement passwordField;
    @FindBy(id="idSIButton9")
    WebElement passwordSubmitbtn;
    @FindBy(id="idBtn_Back")
    WebElement pressNobtn;

   KillProcess kill =new KillProcess();

    public LoginToDeepnetPage(WebDriver driver) {
        PageFactory.initElements(driver,this);
        this.driver = driver;
        waitTime = new WebDriverWait(driver, Duration.ofSeconds(constant.INT_TIME_TO_WAIT));
    }
    public void loginPage() throws Exception {
        try {
            constant.processName="IAM_DeepNet_Capture_Token";
            //Kill All Process
            kill.killChrome();
            driver.get(constant.STR_URL_TO_LOGIN_PAGE);
            //Login to Deepnet Application
            driver.manage().window().maximize();
            waitTime.until(ExpectedConditions.elementToBeClickable(userIdForm));
            Log.info("DeepNet application was launched in chrome browser successfully");
            sendKeys(userIdForm, waitTime, constant.STR_USER_EMAIL_ADDRESS);
            Log.info("User Id was entered");
            click(useridSubmitbtn, waitTime);
            Log.info("User Id was submitted");
            click(passwordField, waitTime);
            sendKeys(passwordField, waitTime, constant.STR_PASSWORD);
            Log.info("Password was entered");
            click(passwordSubmitbtn, waitTime);
            Log.info("Password was submitted");
            click(pressNobtn, waitTime);
            Log.info("Password remember for next time login,'no' button was clicked");
            Log.info("Successfully logged into DeepNet Application");
        }catch (Exception e) {
            strExceptionmessage = "Failed to login to Deepnet Application due to :"+e.getMessage();
            Log.error(strExceptionmessage);
            UtilClass.sendEmail(constant.SYSTEM_EXCEPTION,strExceptionmessage,constant.processName,0,constant.tableNameWorkItem,0);
            throw new SystemException(strExceptionmessage);
        }

    }
}
