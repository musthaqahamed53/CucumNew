Feature: Extent Reporting Example
  This feature demonstrates the comprehensive logging capabilities with Extent Reports
  
  Background:
    Given user navigates to the application
  
  @ExtentReportingDemo
  Scenario: Successful Login with Comprehensive Logging
    When user enters username "validuser"
    And user enters password "validpass"
    And user takes a screenshot with message "Login credentials entered"
    When user clicks on login button
    And user waits for 2 seconds
    Then user verifies element "username" is present
    And user takes a screenshot with message "Login process completed"
  
  @ExtentReportingDemo
  Scenario: Form Validation with Error Verification
    When user enters username "invaliduser"
    And user enters password "invalidpass"
    And user logs custom message "Testing with invalid credentials for demo"
    When user clicks on login button
    And user waits for 1 seconds
    Then user verifies element "password" is present
    And user takes a screenshot with message "Form validation completed"
  
  @ExtentReportingDemo
  Scenario: Element Verification with Screenshots
    When user enters username "testuser"
    And user logs warning "This is a demo warning message"
    Then user verifies element "username" is present
    And user verifies element "password" is present
    And user takes a screenshot with message "All form elements verified successfully"
  
  @ExtentReportingDemo
  Scenario: Logging and Screenshot Demo
    Given user waits for 1 seconds
    And user takes a screenshot with message "Initial page state captured"
    When user logs custom message "Demonstrating comprehensive logging capabilities"
    And user logs warning "This demonstrates warning level logging"
    Then user takes a screenshot with message "Logging demonstration completed"
    And user logs custom message "All logging features demonstrated successfully"
