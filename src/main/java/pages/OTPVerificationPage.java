package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.BaseFunctions;
import utility.Constants;
public class OTPVerificationPage extends BaseFunctions {
    WebDriver driver;
    public String partialotp;
    WebDriverWait wait;
    public OTPVerificationPage(WebDriver driver){
       this.driver=driver;
    }
    @FindBy (xpath = "//span[1]")
    WebElement otpfield;
    public String capturePartialOTP(){
        wait = new WebDriverWait(driver, Constants.WAIT_TIME);
        partialotp=getText(otpfield,wait);
        return partialotp;
    }
}
