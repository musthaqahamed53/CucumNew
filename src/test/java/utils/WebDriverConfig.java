package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebDriverConfig {

    private static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver(){
        return driver.get();  //Returns the value in the current thread's copy of this thread-local variable
    }

    public static void setDriver(RemoteWebDriver driverInstance){
        driver.set(driverInstance);  //Sets the current thread's copy of this thread-local variable to the specified value.
    }

    public static void removeDriver() {
        driver.remove();  //Removes the current thread's value for this thread-local variable.
    }

}
