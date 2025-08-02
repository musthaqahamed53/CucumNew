package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * ExtentReportDemoRunner - Dedicated test runner for demonstrating Extent Reports functionality
 * This runner specifically executes the ExtentReportingExample.feature file with ExampleStepDefinitions
 * 
 * Features:
 * - Runs only the Extent Reports demo feature
 * - Generates comprehensive reports with screenshots
 * - Demonstrates all logging capabilities
 * - Perfect for testing the Extent Reports integration
 */
@CucumberOptions(
        dryRun = false,
        features = {"src/test/resources/features/ExtentReportingExample.feature"},
        glue = {"stepdefs", "utils", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/ExtentDemo.html",
                "json:target/cucumber-reports/ExtentDemo.json",
                "junit:target/cucumber-reports/ExtentDemo.xml",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "timeline:target/test-output-thread/"
        },
        tags = "@ExtentReportingDemo",
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        monochrome = true,
        publish = false
)
public class ExtentReportDemoRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Parallel execution configuration
     * Set to false for demo purposes to see clear sequential execution in reports
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
