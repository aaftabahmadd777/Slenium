package models.base;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cucumber.api.java.Before;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;

import com.cucumber.listener.Reporter;
import com.paulhammant.ngwebdriver.NgWebDriver;

import configurations.config;
import cucumber.api.Scenario;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class testBase extends config {

    @Before
    public void run() {
   	 disableWarning();
    }

    public static WebDriverWait driverWaitFor__(int seconds) {
        return new WebDriverWait(wd, seconds);
    }
    /*===================================//>> METHODS / FUNCTIONS <<//===============================================*/

    @Given("^user calls \"([^\"]*)\" driver$")
    public static void user_calls_Driver__(String driverName) throws InterruptedException {
        disableWarning();
        if (driverName.contains("chrome")) {
            user_prints__("| NEW TEST |");
            user_opens_new_chromeDriver_If_notAvailable();
        } else if (driverName.contains("firefox")) {
            user_prints__("| NEW TEST |");
            user_opens_new_forefoxDriver_If_notAvailable();
        }
    }

    @And("^user navigates to \"([^\"]*)\"$")
    public static void user_navigates_to_url__(String navigationUrl) {
        ilog.debug("Navigating to: " + navigationUrl);
        wd.navigate().to(navigationUrl);
    }

    @When("^user opens stage website$")
    public static void user_opens_stage_webSite__() throws Throwable {
        final String TestingEnv = stageURL;
        user_prints__("Driver Started!");
        wd.get(TestingEnv);
        user_prints__("Navigated to site:" + TestingEnv);
    }

    public static void user_waits_for_loader() throws Throwable {
     do{
         user_waits_for_seconds__(1);
     }while(  findElement__(By.id("loading")).isEnabled());
    }

    @When("^user opens dev website$")
    public void user_opens_dev_webSite__() throws Throwable {
        final String TestingEnv = configureProp.getProperty("DevUrl");
        user_prints__("Driver Started!");
        wd.get(TestingEnv);
        user_prints__("Navigated to site:" + TestingEnv);
    }

    public static void user_verifies_element__(By locator) throws Exception {
        final WebDriverWait wait = new WebDriverWait(wd, 15);
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        Assert.assertTrue(wd.findElement(locator).isDisplayed());
        ilog.info("Element verified as: "+ locator);
    }

    public static WebElement user_getsElement__(By locator) throws Exception {
        boolean result = false;

        if (result == false) {
            try {
                ((JavascriptExecutor) wd).executeScript("arguments[0].style.border='1.3px solid red'", new WebDriverWait(wd, 10).until(ExpectedConditions.elementToBeClickable(locator)));
                final WebElement e = wd.findElement(locator);
                final Actions actions = new Actions(wd);
                actions.moveToElement(e).build().perform();
                result = true;
                return e;
            } catch (final Exception ee) {
                try {
                    if (result == false) {
                        final WebElement e = new WebDriverWait(wd, 10).until(ExpectedConditions.elementToBeClickable(locator));
                        final Actions actions = new Actions(wd);
                        actions.moveToElement(e).build().perform();
                        ((JavascriptExecutor) wd).executeScript("arguments[0].style.border='1.3px solid blue'", e);
                        result = true;
                        return e;
                    } else if (result == false) {
                        jse.executeScript(
                                "window.scrollTo(0,Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight));");
                        ((JavascriptExecutor) wd).executeScript("arguments[0].style.border='1.3px solid blue'", wd.findElement(locator));
                        result = true;
                        return wd.findElement(locator);
                    } else if (result == false) {
                        jse.executeScript("arguments[0].scrollIntoView(true);", locator);
                    }
                } catch (final Exception e) {
                    ilog.error("BUG |> ELEMENT NOT FOUND: " + "\n" + e.getMessage());
                }
            }
        } else {
            ilog.error("ELEMENT NOT FOUND WITH ANY METHOD!");
            //Assert.fail("ELEMENT NOT FOUND WITH ANY METHOD!");
        }
        return null;
    }



    protected void user_validatesEmail__(By emailFieldPath) throws Exception {
        final String emailToTest = user_gets_Text(emailFieldPath);
        Assert.assertTrue(emailToTest.matches("^[A-Za-z]\"([^\"]*)\"([@]{1})(.{1,})(\\.)(.{1,})"));
    }

    @And("^user logs \"([^\"]*)\"$")
    public static void user_prints__(String text) {
        ilog.info(text);
    }

    public static void user_scrolls_ToElement__(WebElement Element) {
        final JavascriptExecutor jse = (JavascriptExecutor) wd;
        ((JavascriptExecutor) wd).executeScript("arguments[0].style.border='5px solid red'", Element);
        jse.executeScript("arguments[0].scrollIntoView(true);", Element);
    }

    public static boolean user_checks_If_element_isAvailable__(WebElement element) {
        boolean isElementOnPage = true;
        try {
            element.getLocation();
        } catch (final WebDriverException ex) {
            isElementOnPage = false;
        }
        return isElementOnPage;
    }

    public static boolean user_checks_If_element_isEnable__(WebElement element) {
        return !"true".equals(element.getAttribute("disabled"));
    }

    public static boolean user_checks_If_element_isDisplayed__(By element) throws Exception {
        final WebElement ele = user_getsElement__(element);
        try {
            return ele.isDisplayed();
        } catch (final NoSuchElementException e) {
            return false;
        }
    }

    public static void user_hovers_at__(WebElement element) {
        new Actions(wd).moveToElement(element).build().perform();
    }

    public static void user_switches_toFrame__(int frame, String tagname, String id, String text) throws Exception {
        wd.findElements(By.tagName(tagname)).size();
        wd.switchTo().frame(frame);
        wd.findElement(By.id(id)).sendKeys(text);
        wd.switchTo().parentFrame();
    }

    public static void user_selects_valueAs__(By from, String selection) throws Exception {
        user_getsElement__(from).click();
        new Select(user_getsElement__(from)).selectByVisibleText(selection);
        ilog.info("Selected: " + selection);
    }

    public static void user_selects__(By locator) throws Exception {
        if (!user_getsElement__(locator).isSelected()) {
            user_getsElement__(locator).click();
        }
    }

    public static void user_entersText__(String text, By locator) throws Exception {
        user_getsElement__(locator).clear();
        user_getsElement__(locator).sendKeys(text);
        user_prints__("Text Entered in " + locator + " As > " + text);
    }

    public static void user_entersTextIN__(String Label,String text) {
        try {
            wd.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            try {
                wd.findElement(By.xpath("//label[contains(text(),'" + Label + "')]/following::input[1]")).sendKeys(text);
            } catch ( Exception e) {
                try {
                    wd.findElement(By.xpath("//td[contains(text(),'" + Label + "')]/following::input[1]")).sendKeys(text);
                } catch ( Exception e2) {
                    try {
                        wd.findElement(By.xpath("//tr[contains(text(),'" + Label + "')]/following::input[1]")).sendKeys(text);
                    } catch ( Exception e3) {
                        try {
                            wd.findElement(By.xpath("//li[contains(text(),'" + Label + "')]/following::input[1]")).sendKeys(text);
                        }catch (final Exception e4) {
                            wd.findElement(By.xpath(".//*[@aria-label='"+Label+"']")).sendKeys(text);
                        }
                    }
                }
                ilog.info("Text ["+ text + "] entered successfully in "+Label);
            }

        }catch (Exception e)
        {
            ilog.error("Element could not be found! Check src/test/output");
            org.junit.Assert.fail(e.getMessage());
        }
    }


    @And("^user waits for (\\d+) seconds$")
    public static void user_waits_for_seconds__(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
        user_prints__("Waited for: " + seconds);
    }

    public static void user_entersNumber__(int number, By locator) throws Exception {
        user_getsElement__(locator).sendKeys("" + number);
        user_prints__("Text Entered in: " + locator + " As > " + "" + number);
    }

    public static void user_clicksOn__(By locator) throws Exception {
        try {
            user_getsElement__(locator).click();
            user_prints__("Element Clicked > " + locator);
        } catch (final Exception e) {
            final JavascriptExecutor jse = (JavascriptExecutor) wd;
            jse.executeScript("arguments[0].click();", wd.findElement(locator));
            user_prints__("Element Clicked With JS > " + locator);
        }
    }

    public static void user_clicksOnElement_withText(String text) {
        boolean found = false;
        ///WebElement button;
        if (found == false) {
            try {
                wd.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
                final List<WebElement> buttons = wd.findElements(By.xpath("//*[text()='" + text + "']"));
                final int size = buttons.size();
                if (size == 2) {
                    System.out.print("There are 2 buttons available, i'm confuse please Get unique xpath!");
                } else {
                    wd.findElement(By.xpath("//*[text()='" + text + "']")).click();
                    ilog.info("Clicked: "+ text);
                    found = true;
                }
            } catch (final Exception e) {
                try {
                    if (found == false) {
                        final List<WebElement> buttons = wd.findElements(By.xpath(".//*[@value=\"" + text + "\"]"));
                        final int size = buttons.size();
                        if (size == 2) {
                            System.out.print("There are 2 buttons available, i'm confuse please Get unique xpath!");
                        }else{
                            wd.findElement(By.xpath(".//*[@value=\"" + text + "\"]")).click();
                            ilog.info("Clicked: "+ text);
                            found = true;
                        }
                    }
                } catch (final Exception e2) {
                    if (found == false) {
                        final List<WebElement> buttons = wd.findElements(By.xpath(".//*[@aria-label==\"" + text + "\"]"));
                        final int size = buttons.size();
                        if (size == 2) {
                            System.out.print("There are 2 buttons available, i'm confuse please Get unique xpath!");
                        }else{
                            wd.findElement(By.xpath(".//*[@aria-label==\"" + text + "\"]")).click();
                            //log.info("3 try");
                            ilog.info("Clicked: "+ text);
                            found = true;
                        }
                    }
                    else {
                        ilog.error("Element Clicked Failed");
                    }
                }
            }
        }
    }

    public static void user_verifyText__(String expectedText, By locator) throws Exception {

        final String results = user_getsElement__(locator).getText();
        if (results.equals("")) {
            user_prints__("Expected: " + expectedText + " | Actual: " + user_getsElement__(locator).getAttribute("value"));
            user_getsElement__(locator).getAttribute("value");
            Assert.assertEquals(expectedText,user_getsElement__(locator).getAttribute("value"));
        } else {
            user_prints__("Expected: " + expectedText + " | Actual: " + user_getsElement__(locator).getText());
            Assert.assertEquals(expectedText,user_getsElement__(locator).getText());
        }
    }

    public static void user_verify_errorText__(String expectedText) throws Exception {
        WebElement Message = driverWaitFor__(10).until(ExpectedConditions.elementToBeClickable(By.tagName("html")));
        driverWaitFor__(10).until(ExpectedConditions.textToBePresentInElement(Message, expectedText));
        if (Message.isDisplayed() && Message.getText().contains(expectedText)) {
            user_prints__("Text [" + expectedText + "] verified!");
        } else {
            user_prints__("Text [" + expectedText + "] verification Failed!");
            Assert.fail("Text verification Failed!");
        }
    }

    public static void user_verify_pageText__(String expectedText) throws Exception {
        WebElement Message = driverWaitFor__(10).until(ExpectedConditions.elementToBeClickable(By.tagName("html")));
        driverWaitFor__(10).until(ExpectedConditions.textToBePresentInElement(Message, expectedText));
        if (Message.isDisplayed() && Message.getText().contains(expectedText)) {
            user_prints__("Text [" + expectedText + "] verified!");
        } else {
            user_prints__("Text [" + expectedText + "] verification Failed!");
            Assert.fail("Text verification Failed!");
        }
    }

    @And("^user checks page spellings$")
    public static void user_checksSpelling() {
        try {
            final WebElement body = wd.findElement(By.tagName("body"));
            final String bodyText = body.getText();

            // Comment / uncomment British or American Dictionary as per requirement
            final JLanguageTool langTool = new JLanguageTool(new BritishEnglish());
            // JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
            for (final Rule rule : langTool.getAllActiveRules()) {
                if (rule instanceof SpellingCheckRule) {
                    final List<String> wordsToIgnore = Arrays.asList("Vowpay");
                    ((SpellingCheckRule) rule).addIgnoreTokens(wordsToIgnore);
                }
            }
            final List<RuleMatch> matches = langTool.check(bodyText);
            for (final RuleMatch match : matches) {
                user_prints__("Potential error at characters " + match.getFromPos() + "-" + match.getToPos() + ": "
                        + match.getMessage());
                //System.out.println("Suggested correction(s): " + match.getSuggestedReplacements());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    @And("^user clicks on pagination next button as \"([^\"]*)\"$")
    public void user_clicks_on_pagination_nextPage(By locator) {
        try {
            user_getsElement__(locator).click();
        } catch (final Exception e) {
            user_prints__("Page not found!");
        }
    }

    @And("^user accepts browser alert$")
    public void user_acceptAlert() {
        final Alert alert = wd.switchTo().alert();
        alert.accept();
        user_prints__("Browser alert accepted!");
    }

    @And("^user dismiss browser alert$")
    public void user_rejectAlert() {
        final Alert alert = wd.switchTo().alert();
        alert.dismiss();
        user_prints__("Browser alert rejected!");
    }

    @And("^user uploads file \"([^\"]*)\" in \"([^\"]*)\"$")
    public static void user_uploads(String filename, By locator) {
        try {
            final WebElement upload = user_getsElement__(locator);
            upload.sendKeys(filename);
        } catch (final Exception e) {
            user_prints__("Exception in file uploading: " + e.getMessage());
        }
    }

    @And("^user opens in new tab by clicking \"([^\"]*)\"$")
    public void user_opensIn_newTab(By locator) throws Exception {
        final Actions builder = new Actions(wd);
        final WebElement subMenu = user_getsElement__(locator);
        final Action ctrlClick = builder.keyDown(Keys.CONTROL).moveToElement(subMenu).click().build();
        ctrlClick.perform();
    }

    @And("^user switches to tab \"([^\"]*)\"$")
    public void user_switch_toTab(int tabIndex) {
        ilog.debug("Switching to tab: " + tabIndex);
        final ArrayList<String> tabs = new ArrayList<String>(wd.getWindowHandles());
        wd.switchTo().window(tabs.get(tabIndex));
    }

    @And("^user waits for \"([^\"]*)\" seconds$")
    public void user_sleeps_for(int seconds) throws InterruptedException {
        Thread.sleep(seconds * 1000);
    }

    @And("^user drags from \"([^\"]*)\" and drop to \"([^\"]*)\"$")
    public static void user_dragDrop(WebElement from, WebElement to) throws InterruptedException {
        final Actions actions = new Actions(wd);
        actions.moveToElement(to).perform();
        final Action dragAndDropPromotion = actions.clickAndHold(from).moveToElement(to).release(to).build();
        dragAndDropPromotion.perform();
    }



    @And("^user gets console error and logs$")
    public static void user_log_consoleError() throws Exception {
        Thread.sleep(2000);
        final LogEntries logEntries = wd.manage().logs().get(LogType.BROWSER);
        for (final LogEntry entry : logEntries) {
            System.out.println("Console Errors:" + "\n");
            ilog.error(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            System.out.println("");
        }
    }

    @And("^user ends session$")
    public void user_quits_Browser() {
        wd.quit();
    }

    @Then("^user closes browser$")
    public static void user_closes_Browser() {
        wd.close();
    }


    public static void user_opens_new_chromeDriver_If_notAvailable() {
        try {
            wd.manage().window().maximize();
        } catch (final Exception e) {
            wd = new ChromeDriver();
            wd.manage().window().maximize();

        }
    }

    public static void user_opens_new_forefoxDriver_If_notAvailable() {
        wd = new FirefoxDriver();

    }

    public static void user_createReport() {
        Reporter.loadXMLConfig(new File("extent-config.xml"));
        Reporter.setSystemInfo("user", System.getProperty("user.name"));
    }


    @Then("^Test is completed successfully")
    public static boolean test_isCompleted_succesfully() {
        return true;
    }




    public static String user_gets_Text(By locator) throws Exception {
        final String results = user_getsElement__(locator).getText();
        if (results.equals("")) {
            return user_getsElement__(locator).getAttribute("value");
        } else {
            return results;
        }
    }


    //@After
    public void embedScreenshot(Scenario scenario) throws Exception {
        try {

        } catch (final Exception e) {
        }
        try {

        } catch (final Exception e) {
        }
        try {
        } catch (final Exception e) {

        }
        //user_createReport();
    }

    @BeforeTest
    public static void disableWarning() {
        System.err.close();
    }

    public static WebElement findElement__(By by) throws Throwable {
        try {
            WebDriverWait wait = new WebDriverWait(wd, 15);
            //System.out.println("Found | " + by);
            return wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (Exception e) {
            ((JavascriptExecutor) wd).executeScript("arguments[0].style.border='1.3px solid red'", new WebDriverWait(wd, 10).until(ExpectedConditions.elementToBeClickable(by)));
            WebElement element = wd.findElement(by);
            Actions actions = new Actions(wd);
            actions.moveToElement(element).build().perform();
            return element;
        }
    }

    public static WebElement find_NG_Element__(By by) throws Throwable {
        ngdriver = new NgWebDriver((JavascriptExecutor) wd);
        ngdriver.waitForAngularRequestsToFinish();
        return (driverWaitFor__(10).until(ExpectedConditions.elementToBeClickable(by)));
    }
}