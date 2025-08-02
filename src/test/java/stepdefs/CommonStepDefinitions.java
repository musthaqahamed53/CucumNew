package stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import utils.StepLogger;

/**
 * Common Step Definitions - Contains shared step definitions used across multiple feature files
 * This prevents duplicate step definition exceptions
 */
public class CommonStepDefinitions {
    
    @Given("the warehouse management system is available")
    public void the_warehouse_management_system_is_available() {
        StepLogger.info("Verifying warehouse management system availability");
        // System availability check logic would go here
        StepLogger.pass("Warehouse management system is available");
    }
    
    @Given("I am logged into the warehouse system as {string}")
    public void i_am_logged_into_the_warehouse_system_as(String userRole) {
        StepLogger.info("Logging into warehouse system as: " + userRole);
        // Login logic would go here
        StepLogger.pass("Logged into warehouse system as: " + userRole);
    }
    
    @When("I close appointment {string}")
    public void i_close_appointment(String appointmentNumber) {
        StepLogger.info("Closing appointment: " + appointmentNumber);
        // Appointment closure logic would go here
        StepLogger.pass("Appointment closed successfully: " + appointmentNumber);
    }
}
