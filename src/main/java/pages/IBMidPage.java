package pages;

import exceptionutil.ApplicationException;
import logutil.Log;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.BaseFunctions;
import utility.Constants;

public class IBMidPage extends BaseFunctions {

    @FindBy(xpath = "//a[@data-ci-key='email_1']")
    WebElement sendOTPBtn;
    WebDriverWait wait;
    WebDriver driver;
    public IBMidPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }
    public void sendOTP(WebDriver driver) throws ApplicationException {
        try {

            wait = new WebDriverWait(driver, Constants.WAIT_TIME);
            click(sendOTPBtn, wait);

        }
        catch (Exception e){
            String strExceptionMessage = "Failed to login to IBM application due to: " + e.getMessage() + '\n' + "Exception source: " + e;
            throw new ApplicationException(strExceptionMessage);
        }
    }
}


