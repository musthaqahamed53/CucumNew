package utils;

import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class BaseUtil extends WebDriverConfig{


    /**
     * Reads Global settings.properties file.
     */
    public static Properties properties(){
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\test\\resources\\Global settings.properties");
            prop.load(fis);
        }
        catch (Exception e){
            System.out.println(e);
        }
        return prop;
    }

    /**
     * Gets the value from property file.
     * @param key value of the key mentioned in property file
     */
    public String readProp(String key){
        return properties().getProperty(key);
    }

    /**
     * Opens the browser mentioned in property file and maximises it.
     */
    public void invokeBrowser(){

        String browser = readProp("Browser").toLowerCase();
        RemoteWebDriver driverInstance;
        if (browser.equals("chrome")){
            driverInstance = new ChromeDriver();
        } else if (browser.equals("edge")) {
            driverInstance = new EdgeDriver();
        }
        else{
            driverInstance = new ChromeDriver();
        }
//        ThreadLocal<RemoteWebDriver> qwer = new ThreadLocal<>();
//        RemoteWebDriver asf = new ChromeDriver();
//        qwer.set(asf);

        driverInstance.manage().window().maximize();
        WebDriverConfig.setDriver(driverInstance);
    }


    /**
     * Opens the browser mentioned in property file and maximises it.
     */
    public void closeBrowser(){
            WebDriver driverInstance = WebDriverConfig.getDriver();
            if(driverInstance !=null){
                driverInstance.quit();
                WebDriverConfig.removeDriver();
            }
    }

    /**
     * Takes screenshot whenever called
     * @param scenario mentions scenario in feature file
     */
    public void takeScreenshot(Scenario scenario){
        SimpleDateFormat datetime = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String date = datetime.format(new Date());
        WebDriver driverInstance = WebDriverConfig.getDriver();
        final byte[] screenshot = ((TakesScreenshot) driverInstance).getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", scenario.getName() + " " + date);
    }

}
