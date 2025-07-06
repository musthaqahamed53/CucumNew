package stepdefs;

import io.cucumber.java.en.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import pages.LoginPage;
import utils.BaseUtil;
import utils.WebDriverConfig;

import java.io.IOException;

public class LoginSteps {

    private WebDriver driver;
    private BaseUtil baseUtil;
    private LoginPage loginPage;
    private static final Logger LOGGER = LogManager.getLogger(LoginSteps.class);
    public LoginSteps(){
        this.driver = WebDriverConfig.getDriver();
        this.baseUtil = new BaseUtil();
        loginPage = new LoginPage(this.driver);
    }

    @Given("User navigates to Login Page")
    public void user_navigates_to_login_page() {
        driver.get(baseUtil.readProp("URL"));
        LOGGER.info("Launching the application on the browser");
        System.out.println("qwer5t6y7u8iop");

    }
    @When("User succesfully enters the log in details")
    public void user_succesfully_enters_the_log_in_details() throws IOException {
        loginPage.login_ace("username","Pwsjejd^888#ftvgsh");
        System.out.println("qwer5t6y7u8iop");
    }
    @Then("User should be able to view the product category page")
    public void user_should_be_able_to_view_the_product_category_page() {
        System.out.println("qwer5t6y7u8iop");
    }
}
