package utils;

/**
 * StepLogger - Easy-to-use logging utility for step definitions
 * Provides simple methods to log test steps with or without screenshots
 */
public class StepLogger {
    
    /**
     * Log a PASS step with message
     * Usage: StepLogger.pass("User successfully logged in");
     */
    public static void pass(String message) {
        ExtentReportManager.logPass("✅ " + message);
        System.out.println("PASS: " + message);
    }
    
    /**
     * Log a PASS step with message and screenshot
     * Usage: StepLogger.passWithScreenshot("Login page displayed correctly");
     */
    public static void passWithScreenshot(String message) {
        ExtentReportManager.logPassWithScreenshot("✅ " + message);
        System.out.println("PASS (with screenshot): " + message);
    }
    
    /**
     * Log a FAIL step with message
     * Usage: StepLogger.fail("Login failed with invalid credentials");
     */
    public static void fail(String message) {
        ExtentReportManager.logFail("❌ " + message);
        System.err.println("FAIL: " + message);
    }
    
    /**
     * Log a FAIL step with message and screenshot
     * Usage: StepLogger.failWithScreenshot("Error message not displayed");
     */
    public static void failWithScreenshot(String message) {
        ExtentReportManager.logFailWithScreenshot("❌ " + message);
        System.err.println("FAIL (with screenshot): " + message);
    }
    
    /**
     * Log an INFO step with message
     * Usage: StepLogger.info("Navigating to login page");
     */
    public static void info(String message) {
        ExtentReportManager.logInfo("ℹ️ " + message);
        System.out.println("INFO: " + message);
    }
    
    /**
     * Log an INFO step with message and screenshot
     * Usage: StepLogger.infoWithScreenshot("Current page state");
     */
    public static void infoWithScreenshot(String message) {
        ExtentReportManager.logInfoWithScreenshot("ℹ️ " + message);
        System.out.println("INFO (with screenshot): " + message);
    }
    
    /**
     * Log a WARNING step with message
     * Usage: StepLogger.warning("Element took longer than expected to load");
     */
    public static void warning(String message) {
        ExtentReportManager.logWarning("⚠️ " + message);
        System.out.println("WARNING: " + message);
    }
    
    /**
     * Log a SKIP step with message
     * Usage: StepLogger.skip("Test skipped due to environment issue");
     */
    public static void skip(String message) {
        ExtentReportManager.logSkip("⏭️ " + message);
        System.out.println("SKIP: " + message);
    }
    
    /**
     * Log step start
     * Usage: StepLogger.stepStart("Given user is on login page");
     */
    public static void stepStart(String stepDescription) {
        ExtentReportManager.logInfo("🔄 Starting: " + stepDescription);
        System.out.println("STEP START: " + stepDescription);
    }
    
    /**
     * Log step completion
     * Usage: StepLogger.stepComplete("Given user is on login page");
     */
    public static void stepComplete(String stepDescription) {
        ExtentReportManager.logInfo("✔️ Completed: " + stepDescription);
        System.out.println("STEP COMPLETE: " + stepDescription);
    }
    
    /**
     * Log custom message with screenshot
     * Usage: StepLogger.logWithScreenshot("Custom verification point");
     */
    public static void logWithScreenshot(String message) {
        ExtentReportManager.logInfoWithScreenshot("📸 " + message);
        System.out.println("LOG (with screenshot): " + message);
    }
}
