package pages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Constants;
import utils.Log;
import utils.SystemException;
import utils.UtilClass;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import static pages.SheetsQuickstart.removeEmailFromGoogleSheet;
import static utils.BaseFunctions.*;
import static utils.UtilClass.getMailFromCSV;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class BulkUploadCSVFile {
    public static WebDriver driver;
    public static WebDriverWait waitTime;
    public Constants constant = new Constants();
    public ResultSet resultSet;
    File uploadFile;
    public String strExceptionmessage;
    public String strTotalProccesed, strTotalSuccesfullyProcessed, strTotalFailed, strTotalWarning;
    @FindBy(xpath = "//button[contains(@data-toggle,'dropdown')]")
    WebElement bulkOpButton;

    @FindBy(id="bulkStatusText")
    WebElement errorMsgWhileFileUploading;
    @FindBy(xpath = "//span[contains(@class,'navi-text')][1]")
    WebElement assignmentButton;
    @FindBy(xpath = "//span[text()='Unassignment']")
    WebElement unassigmentbtn;
    @FindBy(xpath = "//*[@id=\"failedRequestTable\"]/table/tbody/tr")
    List<WebElement> listOfIdentifiers;
    @FindBy(id = "bulkFileInput")
    WebElement chooseFilebtn;
    @FindBy(id = "cbActivateTokens")
    WebElement activateTokenCheckBox;
    @FindBy(id = "cbBulkSetDefaultSignInToCode")
    WebElement sendOtpCheckBox;
    @FindBy(id = "btnStart")
    WebElement startButton;
    @FindBy(xpath = "//*[@id=\"bulkStats\"]/div/button")
    WebElement openReportButton;
    @FindBy(id = "totalProcessed")
    WebElement totalProcessed;
    @FindBy(id = "totalSuccessfullyProcessed")
    WebElement totalSuccessfullyProcessed;
    @FindBy(id = "totalFailed")
    WebElement totalFailed;
    @FindBy(id = "totalWarnings")
    WebElement totalWarnings;

    @FindBy(id="bulkModalClose2")
    WebElement closebtn;
    @FindBy(xpath = "//*[@id=\"kt_aside_menu\"]/ul/li[10]/a/span[2]")
    WebElement userDirectoryLink;
    @FindBy(xpath = "//*[@id=\"tblIDS\"]/table/tbody/tr/td[4]/span/div/button/i")
    WebElement threeDotNavigatorbtn;
    @FindBy(xpath = "//*[@id=\"test\"]/a[1]")
    WebElement userView;

    public ArrayList<String> mailList;
    public BulkUploadCSVFile(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        waitTime = new WebDriverWait(driver, Duration.ofSeconds(constant.INT_TIME_TO_WAIT));
    }

    public void captureProcessData() throws SystemException {
        try {
            strTotalProccesed = getText(totalProcessed, waitTime);
            strTotalSuccesfullyProcessed = getText(totalSuccessfullyProcessed, waitTime);
            strTotalFailed = getText(totalFailed, waitTime);
            strTotalWarning = getText(totalWarnings, waitTime);
        }catch(Exception e){
            strExceptionmessage="System Excepotion"+e.getMessage();
            Log.error(strExceptionmessage);
            throw new SystemException(strExceptionmessage);
        }
        System.out.println("processed:" + strTotalProccesed);
        System.out.println("successfully processed:" + strTotalSuccesfullyProcessed);
        System.out.println("Failed:" + strTotalFailed);
        System.out.println("Fully Processed:" + strTotalWarning);
        Log.info("File Uploaded successfully");
    }

    public String specificData, workitemId, queueName, state,path,child,parent;
    public int failedId, intRetry,numberOfFailedMail;

    public void uploadCSVFile() throws SystemException {

    }
}
