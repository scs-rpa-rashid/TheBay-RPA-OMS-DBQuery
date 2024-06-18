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

public class EnterOTPPage extends BaseFunctions {
    WebDriver driver;
    String strOtp;
    String strVerificationCode;
    String [] strArrSplitOtp;
    WebDriverWait wait;
    String strExceptionMessage;
    public EnterOTPPage(WebDriver driver)
    {
        this.driver=driver;
        PageFactory.initElements(driver, this);
    }
    @FindBy (id = "otp")
    WebElement otpWebElement;
    @FindBy (xpath = "//button[contains(text(),'Verify')]")
    WebElement verifyBtn;
    public void enterOtp(String otp) throws Exception {

        try{
            //Split OTP by -
            Log.info("Split OTP by -");
            strArrSplitOtp = otp.split("-");
            strVerificationCode = strArrSplitOtp[1];
            Log.info("Entering verification code in IBM page....");
            Log.info("Verification code: "+ strVerificationCode);

            wait = new WebDriverWait(driver, Constants.WAIT_TIME);
            type(otpWebElement, strVerificationCode,wait);
            click(verifyBtn,wait);
            Log.info("Verification code entered");
        }
        catch(Exception e){
            strExceptionMessage="Failure in entering OTP in IBM page.Exception message: "+e.getMessage()+'\n'+"Exception source: "+e.getCause();
            Log.error(strExceptionMessage);
            throw new ApplicationException(strExceptionMessage);
        }

    }
}
