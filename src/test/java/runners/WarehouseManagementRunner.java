package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Warehouse Management Test Runner - Comprehensive test execution for all warehouse operations
 * Demonstrates Cucumber integration with TestNG and comprehensive reporting
 */
@CucumberOptions(
        dryRun = false,
        features = {
                "src/test/resources/features/WarehouseInboundOperations.feature",
                "src/test/resources/features/WarehouseOutboundOperations.feature",
                "src/test/resources/features/WarehouseInventoryAdjustment.feature"
        },
        glue = {"stepdefs", "utils", "hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/WarehouseManagement.html",
                "json:target/cucumber-reports/WarehouseManagement.json",
                "junit:target/cucumber-reports/WarehouseManagement.xml",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "timeline:target/test-output-thread/"
        },
        tags = "@WarehouseManagement",
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        monochrome = true,
        publish = false
)
public class WarehouseManagementRunner extends AbstractTestNGCucumberTests {
    
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
