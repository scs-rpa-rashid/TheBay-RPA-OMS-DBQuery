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

public class OTPVerificationPage extends BaseFunctions {
    WebDriver driver;
    String strPartialOtp;
    WebDriverWait wait;
    String strExceptionMessage;
    public OTPVerificationPage(WebDriver driver){
       this.driver=driver;
        PageFactory.initElements(driver,this);
    }
    @FindBy (xpath = "//*[@id=\"mfa-text-wrap\"]/div/span[1]")
    WebElement partialOtpElement;
    public String capturePartialOTP() throws Exception {
        try {
            Log.info("Capturing partial OTP from IBM page");
            wait = new WebDriverWait(driver, Constants.WAIT_TIME);
            strPartialOtp = getText(partialOtpElement, wait);
            Log.info("Partial OTP captured: "+ strPartialOtp);
            return strPartialOtp;
        }
        catch (Exception e)
        {
            strExceptionMessage="Failure in capturing partial OTP. Exception message: "+e.getMessage()+'\n'+"Exception source: "+e.getCause();
            Log.error(strExceptionMessage);
            throw new ApplicationException(strExceptionMessage);
        }
    }
}
