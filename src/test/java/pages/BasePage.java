package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.WebDriverConfig;
import utils.StepLogger;
import utils.WarehouseDataManager;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Base Page class demonstrating inheritance, polymorphism, and basic design patterns
 * Interview Points: Abstract classes, Template method pattern, Page Object Model, LinkedHashMap
 */
public abstract class BasePage extends WebDriverConfig {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected WarehouseDataManager dataManager;
    
    // Using LinkedHashMap to maintain insertion order
    protected Map<String, Object> pageData = new LinkedHashMap<>();
    
    // Common elements across all warehouse pages
    @FindBy(id = "user-menu")
    protected WebElement userMenu;
    
    @FindBy(id = "logout-btn")
    protected WebElement logoutButton;
    
    @FindBy(className = "loading-spinner")
    protected WebElement loadingSpinner;
    
    @FindBy(className = "error-message")
    protected WebElement errorMessage;
    
    @FindBy(className = "success-message")
    protected WebElement successMessage;
    
    // Constructor demonstrating dependency injection
    public BasePage() {
        this.driver = getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.dataManager = WarehouseDataManager.getInstance();
        PageFactory.initElements(driver, this);
        StepLogger.info("Initialized " + this.getClass().getSimpleName());
    }
    
    // Template method pattern - defines the skeleton of page operations
    public final boolean performPageOperation(String operation, Map<String, Object> parameters) {
        try {
            waitForPageLoad();
            validatePageElements();
            boolean result = executeOperation(operation, parameters);
            logOperationResult(operation, result);
            return result;
        } catch (Exception e) {
            handleOperationError(operation, e);
            return false;
        }
    }
    
    // Abstract methods to be implemented by concrete pages
    protected abstract void validatePageElements();
    protected abstract boolean executeOperation(String operation, Map<String, Object> parameters);
    protected abstract String getPageTitle();
    protected abstract String getPageUrl();
    
    // Common page operations
    protected void waitForPageLoad() {
        try {
            wait.until(ExpectedConditions.invisibilityOf(loadingSpinner));
            StepLogger.info("Page loaded successfully: " + getPageTitle());
        } catch (Exception e) {
            StepLogger.info("Page load completed (no loading spinner): " + getPageTitle());
        }
    }
    
    protected void waitForElement(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    protected void clickElement(WebElement element, String elementName) {
        try {
            waitForElementToBeClickable(element);
            element.click();
            StepLogger.pass("Clicked on " + elementName);
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to click on " + elementName + ": " + e.getMessage());
            throw e;
        }
    }
    
    protected void enterText(WebElement element, String text, String fieldName) {
        try {
            waitForElement(element);
            element.clear();
            element.sendKeys(text);
            StepLogger.pass("Entered text in " + fieldName + ": " + text);
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to enter text in " + fieldName + ": " + e.getMessage());
            throw e;
        }
    }
    
    protected String getText(WebElement element, String elementName) {
        try {
            waitForElement(element);
            String text = element.getText();
            StepLogger.info("Retrieved text from " + elementName + ": " + text);
            return text;
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to get text from " + elementName + ": " + e.getMessage());
            throw e;
        }
    }
    
    
    // Error handling methods
    protected void handleOperationError(String operation, Exception e) {
        StepLogger.failWithScreenshot("Operation failed: " + operation + " - " + e.getMessage());
        pageData.put("last_error", e.getMessage());
    }
    
    protected void logOperationResult(String operation, boolean result) {
        if (result) {
            StepLogger.pass("Operation completed successfully: " + operation);
        } else {
            StepLogger.fail("Operation failed: " + operation);
        }
    }
    
    // Navigation methods
    public void navigateToPage(String url) {
        try {
            driver.get(url);
            waitForPageLoad();
            StepLogger.passWithScreenshot("Navigated to page: " + url);
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to navigate to page: " + url);
            throw e;
        }
    }
    
    public boolean isOnCorrectPage() {
        try {
            String currentUrl = driver.getCurrentUrl();
            String expectedUrl = getPageUrl();
            boolean isCorrect = currentUrl.contains(expectedUrl);
            
            if (isCorrect) {
                StepLogger.pass("On correct page: " + getPageTitle());
            } else {
                StepLogger.warning("Not on expected page. Current: " + currentUrl + ", Expected: " + expectedUrl);
            }
            
            return isCorrect;
        } catch (Exception e) {
            StepLogger.fail("Failed to verify page: " + e.getMessage());
            return false;
        }
    }
    
    // Simple data management using LinkedHashMap
    protected void storePageData(String key, Object value) {
        pageData.put(key, value);
        StepLogger.info("Stored page data: " + key + " = " + value);
    }
    
    protected Object getPageData(String key) {
        return pageData.get(key);
    }
    
    protected String getPageDataAsString(String key) {
        Object value = pageData.get(key);
        return value != null ? value.toString() : null;
    }
    
    // Common validation methods
    protected boolean validateSuccessMessage(String expectedMessage) {
        try {
            if (isElementDisplayed(successMessage)) {
                String actualMessage = getText(successMessage, "Success Message");
                boolean isValid = actualMessage.contains(expectedMessage);
                
                if (isValid) {
                    StepLogger.passWithScreenshot("Success message validated: " + actualMessage);
                } else {
                    StepLogger.failWithScreenshot("Success message mismatch. Expected: " + expectedMessage + ", Actual: " + actualMessage);
                }
                
                return isValid;
            }
            return false;
        } catch (Exception e) {
            StepLogger.fail("Failed to validate success message: " + e.getMessage());
            return false;
        }
    }
    
    protected boolean validateErrorMessage(String expectedMessage) {
        try {
            if (isElementDisplayed(errorMessage)) {
                String actualMessage = getText(errorMessage, "Error Message");
                boolean isValid = actualMessage.contains(expectedMessage);
                
                if (isValid) {
                    StepLogger.passWithScreenshot("Error message validated: " + actualMessage);
                } else {
                    StepLogger.failWithScreenshot("Error message mismatch. Expected: " + expectedMessage + ", Actual: " + actualMessage);
                }
                
                return isValid;
            }
            return false;
        } catch (Exception e) {
            StepLogger.fail("Failed to validate error message: " + e.getMessage());
            return false;
        }
    }
    
    // Logout functionality
    public void logout() {
        try {
            clickElement(userMenu, "User Menu");
            clickElement(logoutButton, "Logout Button");
            StepLogger.pass("Logged out successfully");
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to logout: " + e.getMessage());
            throw e;
        }
    }
    
    // Cleanup method
    public void cleanup() {
        pageData.clear();
        StepLogger.info("Cleaned up " + this.getClass().getSimpleName());
    }
    
    // Getter for page data map
    public Map<String, Object> getAllPageData() {
        return new LinkedHashMap<>(pageData);
    }
}
