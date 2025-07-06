package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import utils.BaseUtil;
import utils.ExcelUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class Contains locators for Login Page along with basic methods used in the page.
 * Page Factory is used to Find Web elements.
 */
public class LoginPage {
    private WebDriver driver;
    private BaseUtil baseUtil;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.baseUtil = new BaseUtil();
        PageFactory.initElements(this.driver,this);

    }

    @FindBy(xpath = "//input[@id='usr']")
    public WebElement usrid;

    @FindBy(xpath = "//input[@id='pwd']")
    public WebElement pwd;

    @FindBy(xpath = "//input[@type='submit']")
    public WebElement login;

    public void login_ace(String username, String pass) throws IOException {
        driver.findElement(By.xpath("//input[@type='submit']"));
//        Assert.fail();
        usrid.sendKeys(username);
        pwd.sendKeys(pass);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", login);
        js.executeScript("arguments[0].click();",login);
        Actions actions = new Actions(driver);

        String usrName = ExcelUtils.readCell(2,"Username","Sheet1");
        System.out.println(usrName+"------------------------------------------------");

        LinkedHashMap<String,String> uidPwd = new LinkedHashMap<>();
        for (int i = 1; i<=4;i++){
            String utemp = ExcelUtils.readCell(i,"Username","Sheet1");
            String ptemp = ExcelUtils.readCell(i,"Password","Sheet1");
            uidPwd.put(utemp,ptemp);
        }
        System.out.println(uidPwd+"------------------------------------------------");

        for(Map.Entry<String,String> entry : uidPwd.entrySet()){
            String usrent = entry.getKey();
            System.out.println(usrent+"------------------------------------------------");

        }

//        actions.contextClick().build();
//        actions.perform();
    }


}
