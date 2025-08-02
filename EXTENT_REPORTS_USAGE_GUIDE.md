# Extent Reports Integration Guide

## Overview
This Cucumber Maven project has been enhanced with comprehensive Extent Reports integration, providing detailed logging capabilities with screenshot support for Pass, Fail, and Info scenarios.

## Features Added
- ✅ **Extent Reports Integration** - Beautiful HTML reports with detailed test execution information
- ✅ **Screenshot Capture** - Automatic screenshots on failure and manual screenshot capture
- ✅ **Comprehensive Logging** - Pass, Fail, Info, Warning, Skip logging with emojis
- ✅ **Thread-Safe Implementation** - Supports parallel execution
- ✅ **Easy-to-Use APIs** - Simple methods for step definitions

## Project Structure
```
src/test/java/
├── utils/
│   ├── ExtentReportManager.java    # Core Extent Reports functionality
│   ├── StepLogger.java            # Easy-to-use logging utilities
│   ├── BaseUtil.java              # Existing base utilities
│   └── WebDriverConfig.java       # WebDriver configuration
├── hooks/
│   └── Hooks.java                 # Updated with Extent Reports integration
├── runners/
│   └── TestRunner.java            # Updated with Extent Reports plugin
└── stepdefs/
    └── ExampleStepDefinitions.java # Example usage of logging

src/test/resources/
├── extent.properties              # Extent Reports configuration
├── extent-config.xml             # Extent Reports theme configuration
└── features/
    └── ExtentReportingExample.feature # Example feature file
```

## How to Use in Your Step Definitions

### 1. Basic Logging (No Screenshots)
```java
import utils.StepLogger;

// Log successful steps
StepLogger.pass("User successfully logged in");

// Log failed steps
StepLogger.fail("Login failed with invalid credentials");

// Log information
StepLogger.info("Navigating to login page");

// Log warnings
StepLogger.warning("Element took longer than expected to load");

// Log skipped steps
StepLogger.skip("Test skipped due to environment issue");
```

### 2. Logging with Screenshots
```java
// Log success with screenshot
StepLogger.passWithScreenshot("Login page displayed correctly");

// Log failure with screenshot
StepLogger.failWithScreenshot("Error message not displayed");

// Log info with screenshot
StepLogger.infoWithScreenshot("Current page state captured");

// Custom screenshot with message
StepLogger.logWithScreenshot("Custom verification point");
```

### 3. Step Tracking
```java
// Track step start and completion
StepLogger.stepStart("Given user is on login page");
// ... your step logic here ...
StepLogger.stepComplete("Given user is on login page");
```

### 4. Example Step Definition
```java
@When("user enters username {string}")
public void userEntersUsername(String username) {
    StepLogger.stepStart("When user enters username: " + username);
    
    try {
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys(username);
        
        StepLogger.pass("Username entered successfully: " + username);
        StepLogger.stepComplete("When user enters username: " + username);
        
    } catch (Exception e) {
        StepLogger.failWithScreenshot("Failed to enter username: " + e.getMessage());
        throw e;
    }
}
```

## Report Generation

### Automatic Report Generation
- Reports are automatically generated after test execution
- Location: `Test-Results/ExtentSparkReport.html`
- Screenshots: `Test-Results/Screenshots/`

### Manual Screenshot Capture
```java
// In your step definitions
@When("user takes a screenshot with message {string}")
public void userTakesScreenshotWithMessage(String message) {
    StepLogger.logWithScreenshot(message);
}
```

### Failure Screenshots
- Automatically captured on scenario failure
- Automatically captured on step failure
- Attached to both Extent Reports and Cucumber reports

## Configuration Files

### extent.properties
```properties
extent.reporter.spark.start=true
extent.reporter.spark.out=Test-Results/ExtentSparkReport.html
extent.reporter.spark.config=src/test/resources/extent-config.xml
screenshot.dir=Test-Results/Screenshots/
screenshot.rel.path=Screenshots/
```

### extent-config.xml
- Configures report theme, colors, and styling
- Customizable document title and report name
- Date/time format configuration

## Running Tests

### Command Line
```bash
mvn clean test
```

### IDE
- Run TestRunner.java directly
- Run individual feature files
- Reports will be generated automatically

## Report Features

### What You'll See in Reports
1. **Test Summary** - Pass/Fail counts, execution time
2. **Detailed Steps** - Each step with status and timestamps
3. **Screenshots** - Embedded screenshots for visual verification
4. **Error Details** - Stack traces and error messages
5. **System Information** - OS, Java version, user details
6. **Timeline View** - Execution timeline with parallel thread support

### Report Sections
- **Dashboard** - Overview with charts and statistics
- **Test Details** - Detailed view of each test execution
- **Categories** - Tests grouped by tags/categories
- **Exception Details** - Detailed error information
- **Timeline** - Chronological execution view

## Best Practices

### 1. Use Appropriate Log Levels
```java
// Use pass for successful verifications
StepLogger.pass("Element found and clicked successfully");

// Use fail for actual failures
StepLogger.fail("Expected element not found");

// Use info for general information
StepLogger.info("Starting test data setup");

// Use warning for non-critical issues
StepLogger.warning("Element took 5 seconds to load (expected 2 seconds)");
```

### 2. Screenshot Strategy
```java
// Take screenshots for important verifications
StepLogger.passWithScreenshot("Login successful - dashboard displayed");

// Always take screenshots on failures
StepLogger.failWithScreenshot("Login failed - error message shown");

// Use info screenshots for debugging
StepLogger.infoWithScreenshot("Current page state before action");
```

### 3. Step Tracking
```java
// Track important steps for better reporting
StepLogger.stepStart("Given user navigates to login page");
// ... step implementation ...
StepLogger.stepComplete("Given user navigates to login page");
```

## Troubleshooting

### Common Issues
1. **Screenshots not appearing**: Check if WebDriver is properly initialized
2. **Report not generated**: Ensure all hooks are properly configured
3. **Thread issues**: Make sure ExtentReportManager is used correctly

### Debug Mode
- Enable debug logging in extent.properties
- Check console output for initialization messages
- Verify file permissions for report directory

## Advanced Usage

### Custom Report Configuration
- Modify extent-config.xml for custom styling
- Add custom JavaScript/CSS for enhanced UI
- Configure additional system information

### Integration with CI/CD
- Reports are generated in Test-Results directory
- Can be archived as build artifacts
- Screenshots included for failure analysis

## Example Feature File Usage
```gherkin
Feature: Login Functionality
  
  Scenario: Successful Login
    Given user navigates to the application
    When user enters username "validuser"
    And user enters password "validpass"
    And user takes a screenshot with message "Credentials entered"
    When user clicks on login button
    Then user should be logged in successfully
    And user takes a screenshot with message "Login successful"
```

This comprehensive integration provides you with a powerful testing framework that generates beautiful, detailed reports with full screenshot support for all your Cucumber tests.
