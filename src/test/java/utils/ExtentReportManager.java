package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentReportManager - Comprehensive utility for Extent Reports integration
 * Provides easy-to-use methods for logging with screenshots
 */
public class ExtentReportManager extends WebDriverConfig {
    
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static String reportPath;
    private static String screenshotDir;
    
    /**
     * Initialize Extent Reports
     */
    public static void initializeReport() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportPath = System.getProperty("user.dir") + "/Test-Results/ExtentReport_" + timestamp + ".html";
            screenshotDir = System.getProperty("user.dir") + "/Test-Results/Screenshots/";
            
            // Create directories if they don't exist
            new File(screenshotDir).mkdirs();
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Cucumber Test Automation Report");
            sparkReporter.config().setReportName("Test Execution Report");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
        }
    }
    
    /**
     * Create a new test in the report
     */
    public static void createTest(String testName, String description) {
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
    }
    
    /**
     * Get current test instance
     */
    public static ExtentTest getTest() {
        return test.get();
    }
    
    /**
     * Log PASS with message
     */
    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().log(Status.PASS, message);
        }
    }
    
    /**
     * Log PASS with message and screenshot
     */
    public static void logPassWithScreenshot(String message) {
        if (getTest() != null) {
            String screenshotPath = captureScreenshot("PASS");
            if (screenshotPath != null) {
                getTest().log(Status.PASS, message, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else {
                getTest().log(Status.PASS, message);
            }
        }
    }
    
    /**
     * Log FAIL with message
     */
    public static void logFail(String message) {
        if (getTest() != null) {
            getTest().log(Status.FAIL, message);
        }
    }
    
    /**
     * Log FAIL with message and screenshot
     */
    public static void logFailWithScreenshot(String message) {
        if (getTest() != null) {
            String screenshotPath = captureScreenshot("FAIL");
            if (screenshotPath != null) {
                getTest().log(Status.FAIL, message, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else {
                getTest().log(Status.FAIL, message);
            }
        }
    }
    
    /**
     * Log INFO with message
     */
    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().log(Status.INFO, message);
        }
    }
    
    /**
     * Log INFO with message and screenshot
     */
    public static void logInfoWithScreenshot(String message) {
        if (getTest() != null) {
            String screenshotPath = captureScreenshot("INFO");
            if (screenshotPath != null) {
                getTest().log(Status.INFO, message, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else {
                getTest().log(Status.INFO, message);
            }
        }
    }
    
    /**
     * Log WARNING with message
     */
    public static void logWarning(String message) {
        if (getTest() != null) {
            getTest().log(Status.WARNING, message);
        }
    }
    
    /**
     * Log SKIP with message
     */
    public static void logSkip(String message) {
        if (getTest() != null) {
            getTest().log(Status.SKIP, message);
        }
    }
    
    /**
     * Capture screenshot and return the relative path
     */
    private static String captureScreenshot(String status) {
        try {
            WebDriver driver = getDriver();
            if (driver != null) {
                TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
                byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
                
                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
                String fileName = status + "_" + timestamp + ".png";
                String fullPath = screenshotDir + fileName;
                
                FileUtils.writeByteArrayToFile(new File(fullPath), screenshot);
                
                // Return relative path for the report
                return "./Screenshots/" + fileName;
            }
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Capture screenshot on failure (for hooks)
     */
    public static void captureScreenshotOnFailure(String scenarioName) {
        if (getTest() != null) {
            String screenshotPath = captureScreenshot("FAILURE");
            if (screenshotPath != null) {
                getTest().log(Status.FAIL, "Screenshot captured on failure for: " + scenarioName, 
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        }
    }
    
    /**
     * Flush the report
     */
    public static void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }
    
    /**
     * Get report path
     */
    public static String getReportPath() {
        return reportPath;
    }
    
    /**
     * Clean up thread local
     */
    public static void removeTest() {
        test.remove();
    }
}
