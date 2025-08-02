package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import utils.StepLogger;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Inbound Shipment Page - Demonstrates inheritance, collections, and business logic
 * Interview Points: Page Object Model, Collections usage, Stream operations
 */
public class InboundShipmentPage extends BasePage {
    
    // Page elements using Page Factory pattern
    @FindBy(id = "shipment-id-input")
    private WebElement shipmentIdInput;
    
    @FindBy(id = "create-shipment-btn")
    private WebElement createShipmentButton;
    
    @FindBy(id = "pre-receiving-btn")
    private WebElement preReceivingButton;
    
    @FindBy(id = "appointment-number-input")
    private WebElement appointmentNumberInput;
    
    @FindBy(id = "create-appointment-btn")
    private WebElement createAppointmentButton;
    
    @FindBy(id = "lot-number-input")
    private WebElement lotNumberInput;
    
    @FindBy(id = "assign-lot-btn")
    private WebElement assignLotButton;
    
    @FindBy(id = "location-input")
    private WebElement locationInput;
    
    @FindBy(id = "assign-location-btn")
    private WebElement assignLocationButton;
    
    @FindBy(id = "pallet-id-input")
    private WebElement palletIdInput;
    
    @FindBy(id = "check-pallet-btn")
    private WebElement checkPalletButton;
    
    @FindBy(id = "complete-receiving-btn")
    private WebElement completeReceivingButton;
    
    @FindBy(id = "create-receipt-btn")
    private WebElement createReceiptButton;
    
    @FindBy(id = "close-appointment-btn")
    private WebElement closeAppointmentButton;
    
    @FindBy(id = "print-receipt-btn")
    private WebElement printReceiptButton;
    
    @FindBy(id = "shipment-status")
    private WebElement shipmentStatusElement;
    
    @FindBy(className = "item-row")
    private List<WebElement> itemRows;
    
    @FindBy(className = "variance-indicator")
    private List<WebElement> varianceIndicators;
    
    // Collections to store page data - demonstrates various collection types
    private final Map<String, Integer> expectedItems = new LinkedHashMap<>();
    private final Map<String, Integer> receivedItems = new LinkedHashMap<>();
    private final Map<String, Set<String>> lotAssignments = new LinkedHashMap<>();
    private final Map<String, String> locationAssignments = new LinkedHashMap<>();
    private final Set<String> processedPallets = new HashSet<>();
    
    private static final String PENDING = "Pending";
    private static final String IN_PROGRESS = "In Progress";
    private static final String COMPLETED = "Completed";
    private static final String CLOSED = "Closed";
    
    @Override
    protected void validatePageElements() {
        StepLogger.info("Validating Inbound Shipment page elements");
        
        if (!isElementDisplayed(shipmentIdInput)) {
            throw new RuntimeException("Shipment ID input not found");
        }
        
        if (!isElementDisplayed(createShipmentButton)) {
            throw new RuntimeException("Create Shipment button not found");
        }
        
        StepLogger.pass("All required page elements are present");
    }
    
    @Override
    protected boolean executeOperation(String operation, Map<String, Object> parameters) {
        switch (operation.toLowerCase()) {
            case "create_shipment":
                return createShipment(parameters);
            case "navigate_pre_receiving":
                return navigateToPreReceiving();
            case "create_appointment":
                return createAppointment(parameters);
            case "assign_lots":
                return assignLotNumbers(parameters);
            case "assign_locations":
                return assignLocations(parameters);
            case "check_pallets":
                return checkPallets(parameters);
            case "complete_receiving":
                return completeReceiving();
            case "create_receipt":
                return createReceipt(parameters);
            case "close_appointment":
                return closeAppointment(parameters);
            default:
                StepLogger.warning("Unknown operation: " + operation);
                return false;
        }
    }
    
    @Override
    protected String getPageTitle() {
        return "Inbound Shipment Management";
    }
    
    @Override
    protected String getPageUrl() {
        return "/warehouse/inbound";
    }
    
    // Business logic methods demonstrating OOP principles
    public boolean createShipment(Map<String, Object> parameters) {
        try {
            String shipmentId = (String) parameters.get("shipment_id");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) parameters.get("items");
            
            enterText(shipmentIdInput, shipmentId, "Shipment ID");
            clickElement(createShipmentButton, "Create Shipment");
            
            // Store expected items using collections
            for (Map<String, Object> item : items) {
                String sku = (String) item.get("sku");
                Integer quantity = (Integer) item.get("quantity");
                expectedItems.put(sku, quantity);
                
                // Store in data manager for cross-page access
                dataManager.storeShipmentData(shipmentId, "expected_" + sku, quantity);
            }
            
            StepLogger.passWithScreenshot("Shipment created successfully: " + shipmentId);
            storePageData("current_shipment_id", shipmentId);
            
            return validateSuccessMessage("Shipment created");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create shipment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean navigateToPreReceiving() {
        try {
            clickElement(preReceivingButton, "Pre-Receiving");
            waitForPageLoad();
            
            StepLogger.passWithScreenshot("Navigated to pre-receiving screen");
            return true;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to navigate to pre-receiving: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createAppointment(Map<String, Object> parameters) {
        try {
            String appointmentNumber = (String) parameters.get("appointment_number");
            
            enterText(appointmentNumberInput, appointmentNumber, "Appointment Number");
            clickElement(createAppointmentButton, "Create Appointment");
            
            storePageData("appointment_number", appointmentNumber);
            StepLogger.passWithScreenshot("Appointment created: " + appointmentNumber);
            
            return validateSuccessMessage("Appointment created");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create appointment: " + e.getMessage());
            return false;
        }
    }
    
    // Demonstrates Set operations for lot number management
    public boolean assignLotNumbers(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, List<String>> lotAssignmentData = (Map<String, List<String>>) parameters.get("lot_assignments");
            
            for (Map.Entry<String, List<String>> entry : lotAssignmentData.entrySet()) {
                String sku = entry.getKey();
                List<String> lots = entry.getValue();
                
                // Use Set to ensure unique lot numbers
                Set<String> uniqueLots = new HashSet<>(lots);
                lotAssignments.put(sku, uniqueLots);
                
                // Assign each lot number
                for (String lotNumber : uniqueLots) {
                    assignSingleLot(sku, lotNumber);
                    
                    // Store in data manager
                    dataManager.addLotNumber(sku, lotNumber);
                }
            }
            
            StepLogger.passWithScreenshot("Lot numbers assigned successfully");
            return true;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to assign lot numbers: " + e.getMessage());
            return false;
        }
    }
    
    private void assignSingleLot(String sku, String lotNumber) {
        // Find the SKU row and assign lot number
        WebElement skuRow = driver.findElement(By.xpath("//tr[contains(@data-sku, '" + sku + "')]"));
        WebElement lotInput = skuRow.findElement(By.className("lot-input"));
        WebElement assignButton = skuRow.findElement(By.className("assign-lot-btn"));
        
        enterText(lotInput, lotNumber, "Lot Number for " + sku);
        clickElement(assignButton, "Assign Lot Button for " + sku);
        
        StepLogger.info("Assigned lot " + lotNumber + " to SKU " + sku);
    }
    
    // Demonstrates Map operations for location management
    public boolean assignLocations(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> locationData = (Map<String, String>) parameters.get("location_assignments");
            
            for (Map.Entry<String, String> entry : locationData.entrySet()) {
                String sku = entry.getKey();
                String location = entry.getValue();
                
                assignSingleLocation(sku, location);
                locationAssignments.put(sku, location);
                
                // Store in data manager
                dataManager.storeShipmentData(getCurrentShipmentId(), "location_" + sku, location);
            }
            
            StepLogger.passWithScreenshot("Locations assigned successfully");
            return true;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to assign locations: " + e.getMessage());
            return false;
        }
    }
    
    private void assignSingleLocation(String sku, String location) {
        WebElement skuRow = driver.findElement(By.xpath("//tr[contains(@data-sku, '" + sku + "')]"));
        WebElement locationInput = skuRow.findElement(By.className("location-input"));
        WebElement assignButton = skuRow.findElement(By.className("assign-location-btn"));
        
        enterText(locationInput, location, "Location for " + sku);
        clickElement(assignButton, "Assign Location Button for " + sku);
        
        StepLogger.info("Assigned location " + location + " to SKU " + sku);
    }
    
    // Demonstrates Set operations for pallet management
    public boolean checkPallets(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> palletData = (List<Map<String, Object>>) parameters.get("pallets");
            
            for (Map<String, Object> pallet : palletData) {
                String palletId = (String) pallet.get("pallet_id");
                @SuppressWarnings("unchecked")
                List<String> skus = (List<String>) pallet.get("skus");
                String status = (String) pallet.get("status");
                
                checkSinglePallet(palletId, skus, status);
                processedPallets.add(palletId);
                
                // Store pallet assignments
                for (String sku : skus) {
                    dataManager.assignPallet(sku, palletId);
                }
            }
            
            StepLogger.passWithScreenshot("Pallets checked successfully");
            return true;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to check pallets: " + e.getMessage());
            return false;
        }
    }
    
    private void checkSinglePallet(String palletId, List<String> skus, String status) {
        enterText(palletIdInput, palletId, "Pallet ID");
        clickElement(checkPalletButton, "Check Pallet");
        
        // Verify pallet contents
        for (String sku : skus) {
            WebElement skuElement = driver.findElement(By.xpath("//span[contains(@data-sku, '" + sku + "')]"));
            if (isElementDisplayed(skuElement)) {
                StepLogger.pass("SKU " + sku + " found on pallet " + palletId);
            } else {
                StepLogger.warning("SKU " + sku + " not found on pallet " + palletId);
            }
        }
        
        StepLogger.info("Checked pallet " + palletId + " with status: " + status);
    }
    
    public boolean completeReceiving() {
        try {
            clickElement(completeReceivingButton, "Complete Receiving");
            waitForPageLoad();
            
            StepLogger.passWithScreenshot("Receiving completed successfully");
            return validateSuccessMessage("Receiving completed");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to complete receiving: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createReceipt(Map<String, Object> parameters) {
        try {
            String receiptNumber = (String) parameters.get("receipt_number");
            
            clickElement(createReceiptButton, "Create Receipt");
            
            storePageData("receipt_number", receiptNumber);
            StepLogger.passWithScreenshot("Receipt created: " + receiptNumber);
            
            return validateSuccessMessage("Receipt created");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create receipt: " + e.getMessage());
            return false;
        }
    }
    
    public boolean closeAppointment(Map<String, Object> parameters) {
        try {
            String appointmentNumber = (String) parameters.get("appointment_number");
            
            clickElement(closeAppointmentButton, "Close Appointment");
            
            StepLogger.passWithScreenshot("Appointment closed: " + appointmentNumber);
            return validateSuccessMessage("Appointment closed");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to close appointment: " + e.getMessage());
            return false;
        }
    }
    
    // Stream operations for data analysis
    public Map<String, Integer> getVarianceReport() {
        return expectedItems.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> receivedItems.getOrDefault(entry.getKey(), 0) - entry.getValue()
                ));
    }
    
    public List<String> getOverReceivedItems() {
        return getVarianceReport().entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public List<String> getShortReceivedItems() {
        return getVarianceReport().entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    // Status validation
    public String getCurrentShipmentStatus() {
        try {
            String statusText = getText(shipmentStatusElement, "Shipment Status");
            return statusText;
        } catch (Exception e) {
            StepLogger.warning("Could not determine shipment status: " + e.getMessage());
            return PENDING;
        }
    }
    
    public boolean validateShipmentStatus(String expectedStatus) {
        String actualStatus = getCurrentShipmentStatus();
        boolean isValid = actualStatus.equals(expectedStatus);
        
        if (isValid) {
            StepLogger.pass("Shipment status validated: " + actualStatus);
        } else {
            StepLogger.fail("Shipment status mismatch. Expected: " + expectedStatus + ", Actual: " + actualStatus);
        }
        
        return isValid;
    }
    
    // Utility methods
    private String getCurrentShipmentId() {
        return (String) getPageData("current_shipment_id");
    }
    
    // Getters for collections (demonstrating encapsulation)
    public Map<String, Integer> getExpectedItems() {
        return new LinkedHashMap<>(expectedItems);
    }
    
    public Map<String, Integer> getReceivedItems() {
        return new LinkedHashMap<>(receivedItems);
    }
    
    public Map<String, Set<String>> getLotAssignments() {
        return new LinkedHashMap<>(lotAssignments);
    }
    
    public Map<String, String> getLocationAssignments() {
        return new LinkedHashMap<>(locationAssignments);
    }
    
    public Set<String> getProcessedPallets() {
        return new HashSet<>(processedPallets);
    }
}
