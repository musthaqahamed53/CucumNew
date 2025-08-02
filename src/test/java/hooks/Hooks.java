package hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import utils.BaseUtil;
import utils.ExtentReportManager;
import utils.StepLogger;
import utils.WebDriverConfig;

public class Hooks extends WebDriverConfig {

    BaseUtil baseUtil = new BaseUtil();
    
    /**
     * Initialize Extent Reports before all tests
     */
    @BeforeAll
    public static void setupReport() {
        ExtentReportManager.initializeReport();
        System.out.println("Extent Reports initialized successfully");
    }
    
    /**
     * @Before perform before operations which is to open browser and create test in report.
     */
    @Before
    public void invokeURL(Scenario scenario){
        // Create test in Extent Report
        ExtentReportManager.createTest(scenario.getName(), "Cucumber Scenario: " + scenario.getName());
        StepLogger.info("Starting scenario: " + scenario.getName());
        
        // Open browser
        baseUtil.invokeBrowser();
        StepLogger.info("Browser launched successfully");
    }

    /**
     * @After hooks to perform after operations, capture screenshot on failure, and close browser.
     */
    @After
    public void tearDown(Scenario scenario) {
        try {
            // Check if scenario failed and capture screenshot
            if (scenario.isFailed()) {
                StepLogger.failWithScreenshot("Scenario failed: " + scenario.getName());
                ExtentReportManager.captureScreenshotOnFailure(scenario.getName());
                
                // Also attach screenshot to Cucumber report
                if (getDriver() != null) {
                    byte[] screenshot = ((org.openqa.selenium.TakesScreenshot) getDriver()).getScreenshotAs(org.openqa.selenium.OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Screenshot on Failure");
                }
            } else {
                StepLogger.pass("Scenario completed successfully: " + scenario.getName());
            }
            
            StepLogger.info("Closing browser");
        } catch (Exception e) {
            StepLogger.fail("Error during teardown: " + e.getMessage());
        } finally {
            // Close browser
            baseUtil.closeBrowser();
            
            // Clean up thread local
            ExtentReportManager.removeTest();
        }
    }
    
    /**
     * @AfterStep to capture screenshot on step failure
     */
    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {
            StepLogger.failWithScreenshot("Step failed in scenario: " + scenario.getName());
        }
    }
    
    /**
     * Flush Extent Reports after all tests
     */
    @AfterAll
    public static void tearDownReport() {
        ExtentReportManager.flushReport();
        System.out.println("Extent Reports generated at: " + ExtentReportManager.getReportPath());
    }
}
