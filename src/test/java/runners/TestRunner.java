package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;


@CucumberOptions(dryRun = false, features = {"src/test/resources/features/LoggedIn.feature",
        "src/test/resources/features/CheckButtons.feature"},
        glue = {"stepdefs", "utils","hooks"},
        plugin = {"pretty", 
                  "html:target/cucumber.html",
                  "json:cucumnber-json",
                  "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                  "timeline:test-output-thread/"
        },
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        monochrome = true)
public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
