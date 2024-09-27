package pages;

import exceptionutil.ApplicationException;
import logutil.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utility.Constants;
import utility.*;
import java.time.LocalDateTime;
import java.util.Arrays;
public class ExecuteDbQueryPage extends BaseFunctions {
    WebDriver driver;
    Boolean runquery;
    String resultcount;
    String[][] inputdata;
    WebDriverWait wait;
    WebDriverWait loaderelementWait;
    LocalDateTime startTime;
    LocalDateTime endTime;
    LocalDateTime currentTime;
    String sqlquery;
    Boolean isLoaderDisplayed;
    Boolean doesNotExist;
    @FindBy (id="SQLStatement")
    WebElement sqleditorelement;
    @FindBy (id="submitButton")
    WebElement runquerybtnelement;
//    @FindBy (xpath = "/html/body/div[2]/form[2]/div/div[3]/table/tbody/tr[2]/td")
//    WebElement countcolumn;

    @FindBy (xpath="//table[@id='QueryResult']//tr[contains(@class, 'headerRow')]/th[normalize-space()='COUNT']/ancestor::table//tr[not(contains(@class, 'headerRow'))]/td[2]")
    WebElement countcolumnAlternative;

    @FindBy (xpath="//table[@id='QueryResult']//tr[contains(@class, 'headerRow')]/th[translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'count']/following::td[@class='nowrapsearchlabel']")
    WebElement countcolumn;
@FindBy (id="loader")
    WebElement loaderElement;

@FindBy (id="errorDiv")
WebElement errorElement;
    public ExecuteDbQueryPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver,this);
    }
    public String[][] entersqlquery(String[][] argInputdata) throws InterruptedException, ApplicationException {
        argInputdata[0][11] = "ResultCount";
        wait = new WebDriverWait(driver, Constants.WAIT_TIME);

        for (int i = 1; i < Arrays.stream(argInputdata).count(); i++) {
            if (argInputdata[i][2].equals("24/7")) {
                runquery = true;
            } else {
                startTime = DateTimeParse.GetTime(argInputdata[i][2]);

                endTime = DateTimeParse.GetTime(argInputdata[i][3]);


                currentTime = DateTimeParse.GetCurrentDateTime();


                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                    runquery = true;
                } else {
                    runquery = false;

                }
            }
            if (runquery) {
                try {
                    Log.info("Query: " + argInputdata[i][0]);
                    Log.info("Query " + argInputdata[i][0] + " is in runtime window");
                    click(sqleditorelement, wait);
                    clearTextField(sqleditorelement, wait);
                    sqlquery = argInputdata[i][7];
                    type(sqleditorelement, sqlquery, wait);
                    click(runquerybtnelement, wait);
                    loaderelementWait = new WebDriverWait(driver, Constants.LOADER_WAIT_TIME);
                    doesNotExist = waitUntilElementDisappear(loaderElement, loaderelementWait);
                    Log.info("Loader disappeared: " + doesNotExist);
                    boolean boolResultCountExists = waitForElementToAppear(countcolumnAlternative, wait);
                    if (boolResultCountExists) {
                        resultcount = getText(countcolumnAlternative, wait);
                        argInputdata[i][11] = resultcount;
                        Log.info("Checking for Result count with multiple column");
                        Log.info("count value: " + resultcount);
                    } else if (waitForElementToAppear(countcolumn, wait)) {
                        resultcount = getText(countcolumn, wait);
                        argInputdata[i][11] = resultcount;
                        Log.info("Checking for Result count with one column");
                        Log.info("count value: " + resultcount);

                    } else if (waitForElementToAppear(errorElement, wait)) {
                        argInputdata[i][11] = getText(errorElement, wait);
                        Log.info("Checking for error message with one column");
                        Log.error("count value: " + resultcount);

                    } else{
                        argInputdata[i][11] = "Count Column not Found for AlertName "+argInputdata[i][0];
                    }
                    //System.out.println("Printing from table: " + argInputdata[i][11]);
                } catch (Exception e) {
                    String strExceptionMessage = "Failure in executing SQL query due to: " + e.getMessage() + '\n' + " Exception source: " + e.getCause();
                    throw new ApplicationException(strExceptionMessage);
                }
            } else {
                Log.info("Query " + argInputdata[i][0] + " is not in runtime window");
                argInputdata[i][11] = "";
            }
        }


        return argInputdata;
    }

}

