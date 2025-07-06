package hooks;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.BaseUtil;
import utils.WebDriverConfig;

public class Hooks extends WebDriverConfig {

    BaseUtil baseUtil = new BaseUtil();
    /**
     * @Before perform before operations which is to open browser.
     */
    @Before
    public void invokeURL(){
        baseUtil.invokeBrowser();
    }

    /**
     * @After hooks to perform after operations to close browser.
     */
    @After
    public void tearDown() {
        baseUtil.closeBrowser();
    }

    /**
     * @AfterStep method is used to add screenshots to Extent reports
     * @param scenario takes after each step in a scenario
     */
    @AfterStep
    public void attach_screenshot(Scenario scenario){
        baseUtil.takeScreenshot(scenario);
    }
}
