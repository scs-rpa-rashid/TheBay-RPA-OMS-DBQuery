package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.BaseFunctions;
import utility.Constants;
public class EnterOTPPage extends BaseFunctions {
    WebDriver driver;
    String otp;
    String verificationcode;
    String splitotp[];
    WebDriverWait wait;
    public EnterOTPPage(WebDriver driver)
    {
        this.driver=driver;
    }
    @FindBy (id = "otp")
    WebElement otptextfield;
    @FindBy (xpath = "//button")
    WebElement verifybtn;
    public void enterotp(String otp) {
        //Split OTP by -
        splitotp= otp.split("-");
        verificationcode=splitotp[1];
        System.out.println("Verification code: "+verificationcode);
        wait = new WebDriverWait(driver, Constants.WAIT_TIME);
        type(otptextfield,verificationcode,wait);
        click(verifybtn,wait);
    }
}
