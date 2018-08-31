import models.base.testBase;
import org.openqa.selenium.By;


//import static configurations.config.wd;

public class test {
    public static void main(String argr[]) throws Exception {
        testBase.user_calls_Driver__("chrome");
        testBase.user_navigates_to_url__("https://www.google.com/?gws_rd=ssl");
        testBase.user_getsElement__(By.xpath("//*[@id=\"tsf\"]/div[2]/div[3]/center/input[1]")).click();
    }
}
