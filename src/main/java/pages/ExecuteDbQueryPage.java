package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
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
    @FindBy (xpath = "/html/body/div[2]/form[2]/div/div[3]/table/tbody/tr[2]/td")
    WebElement countcolumn;
@FindBy (id="loader")
    WebElement loaderElement;
    public ExecuteDbQueryPage(WebDriver driver) {
        this.driver = driver;
    }
    public String[][] entersqlquery(String[][] argInputdata) throws InterruptedException {
        argInputdata[0][11] = "ResultCount";
        wait = new WebDriverWait(driver, Constants.WAIT_TIME);
        for (int i = 1; i < Arrays.stream(argInputdata).count(); i++) {
            if (argInputdata[i][2].equals("24/7")) {
                runquery = true;
            }
            else {
                startTime = DateTimeParse.GetTime(argInputdata[i][2]);
                System.out.println("Start time: " + startTime);
                endTime = DateTimeParse.GetTime(argInputdata[i][3]);

                System.out.println("End time: " + endTime);
                currentTime = DateTimeParse.GetCurrentDateTime();

                System.out.println("Current time: " + currentTime);
                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                    runquery = true;
                }
                else {
                    runquery = false;

                }
            }
            if (runquery) {
                Log.info("Query "+argInputdata[i][1]+" is in runtime window");
                click(sqleditorelement,wait);
                clearTextField(sqleditorelement,wait);
                sqlquery = argInputdata[i][7];
                type(sqleditorelement,sqlquery,wait);
                click(runquerybtnelement,wait);
                doesNotExist = waitUntilElementDisappear(loaderElement,wait);
                System.out.println("Loader disappeared: " + doesNotExist);
                resultcount = getText(countcolumn,wait);
                argInputdata[i][11] = resultcount;
                //System.out.println("Printing from table: " + argInputdata[i][11]);
            }
            else {
                Log.info("Query "+argInputdata[i][1]+" is not in runtime window");
                argInputdata[i][11]="";
            }
        }
        return argInputdata;
    }
}

