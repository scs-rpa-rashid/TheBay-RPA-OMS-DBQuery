package bots;

import org.openqa.selenium.support.PageFactory;
import pages.*;
import org.openqa.selenium.WebDriver;
import utility.*;
public class HB_OMS_DbQuery_Performer {
    static WebDriver driver;
    static String partialotp;
    static String otp;
    static String[][] strarrayinputdata;
    static String[][] strarrayoutputdata;
    static String[][] strarraywarningdata;
    static String[][] strarraycriticaldata;
    static int warningindex;
    static int criticalindex;
    static UserNamePage userNamePage;
    static PasswordPage passwordPage;
    public static void main(String args[]) throws Exception {
        //Initailise Log 4j
        UtilClass.initialiseLog4j();
        //Kill Process
        KillAllProcess kill = new KillAllProcess();
        UtilClass util = new UtilClass();
        //Get Driver
        driver = util.getDriver();
        //Load Page factory for username page
        userNamePage = PageFactory.initElements(driver, UserNamePage.class);
        UserNamePage.enterusername(driver);
        //Load Page factory for password page
        passwordPage = PageFactory.initElements(driver, PasswordPage.class);
        PasswordPage.enterPassword(driver);
        //Load Page factory for OTP verification page
        OTPVerificationPage otppage = PageFactory.initElements(driver, OTPVerificationPage.class);
        //Capture partial OTP
        partialotp = otppage.capturePartialOTP();
        //Get OTP from email service account
        otp = GetEmailFromServiceAccount.GetMailOTP(partialotp);
        System.out.println("OTP: " + otp);
       //Load Page factory for Enter OTP page
        EnterOTPPage enterOTP = PageFactory.initElements(driver, EnterOTPPage.class);
        //Enter OTP
        enterOTP.enterotp(otp);
        //Read input data
        strarrayinputdata = ReadExcelInput.getDataFromSheet();
        //Load page factory for Execute Db query
        ExecuteDbQueryPage executeDbQuery = PageFactory.initElements(driver, ExecuteDbQueryPage.class);
        //Get the output data after executing Db query
        strarrayoutputdata = executeDbQuery.entersqlquery(strarrayinputdata);
    }
}
