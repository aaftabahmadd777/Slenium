package configurations;
import com.github.javafaker.Faker;
import com.paulhammant.ngwebdriver.NgWebDriver;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import models.base.testBase;
import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.testng.asserts.SoftAssert;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

public class config

{
    public static String stageURL= "https://services.vowpay.com/demo/#!/home";

    public static WebDriver wd;
    public static NgWebDriver ngdriver;
    public static Action actions;
	public static Properties WebOR;
	public static Properties configureProp;
	public static FileInputStream fis;
    public static FileInputStream fis2;
    public static ExtentReports extent;
    public static ExtentTest test;
    public static JavascriptExecutor jse;

    public static Faker faker = new Faker();
    public Random random = new Random();
    public int rand = random.nextInt(1000)+1;
    public static SoftAssert softAssertion= new SoftAssert();

	public static Logger ilog =  Logger.getLogger(testBase.class.getName());
	public static String chromeDriver_Path =  "src/main/drivers/chromedriver.exe";
    public static String firefoxDriver_Path = "src/main/drivers/geckodriver.exe";


}
