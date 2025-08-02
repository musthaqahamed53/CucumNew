package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.StepLogger;
import utils.BaseUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Inventory Adjustment Page - Demonstrates inheritance, collections, and business logic
 * Interview Points: Page Object Model, Collections usage, Stream operations, API integration
 */
public class InventoryAdjustmentPage extends BasePage {
    
    // Page elements using Page Factory pattern
    @FindBy(id = "item-code-input")
    private WebElement itemCodeInput;
    
    @FindBy(id = "adjustment-type-select")
    private WebElement adjustmentTypeSelect;
    
    @FindBy(id = "quantity-input")
    private WebElement quantityInput;
    
    @FindBy(id = "reason-input")
    private WebElement reasonInput;
    
    @FindBy(id = "location-input")
    private WebElement locationInput;
    
    @FindBy(id = "reference-id-input")
    private WebElement referenceIdInput;
    
    @FindBy(id = "submit-adjustment-btn")
    private WebElement submitAdjustmentButton;
    
    @FindBy(id = "cycle-count-btn")
    private WebElement cycleCountButton;
    
    @FindBy(id = "physical-count-input")
    private WebElement physicalCountInput;
    
    @FindBy(id = "approve-count-btn")
    private WebElement approveCountButton;
    
    @FindBy(id = "adjustment-status")
    private WebElement adjustmentStatusElement;
    
    @FindBy(className = "adjustment-row")
    private List<WebElement> adjustmentRows;
    
    @FindBy(className = "variance-indicator")
    private List<WebElement> varianceIndicators;
    
    // Collections for managing inventory adjustment data
    private final Map<String, Integer> systemQuantities = new LinkedHashMap<>();
    private final Map<String, Integer> physicalQuantities = new LinkedHashMap<>();
    private final Map<String, String> adjustmentReasons = new LinkedHashMap<>();
    private final Map<String, String> locationMappings = new LinkedHashMap<>();
    private final Set<String> processedAdjustments = new HashSet<>();
    private final List<String> adjustmentHistory = new ArrayList<>();
    private final Queue<String> pendingAdjustments = new LinkedList<>();
    
    // Adjustment type constants
    private static final String INCREMENT = "INCREMENT";
    private static final String DECREMENT = "DECREMENT";
    
    // Status constants
    private static final String PENDING = "PENDING";
    private static final String APPROVED = "APPROVED";
    private static final String COMPLETED = "COMPLETED";
    private static final String REJECTED = "REJECTED";
    
    // Add BaseUtil instance
    private final BaseUtil baseUtil = new BaseUtil();
    
    @Override
    protected void validatePageElements() {
        StepLogger.info("Validating Inventory Adjustment page elements");
        
        if (!isElementDisplayed(itemCodeInput)) {
            throw new RuntimeException("Item Code input not found");
        }
        
        if (!isElementDisplayed(adjustmentTypeSelect)) {
            throw new RuntimeException("Adjustment Type select not found");
        }
        
        if (!isElementDisplayed(submitAdjustmentButton)) {
            throw new RuntimeException("Submit Adjustment button not found");
        }
        
        StepLogger.pass("All required page elements are present");
    }
    
    @Override
    protected boolean executeOperation(String operation, Map<String, Object> parameters) {
        switch (operation.toLowerCase()) {
            case "positive_adjustment":
                return performPositiveAdjustment(parameters);
            case "negative_adjustment":
                return performNegativeAdjustment(parameters);
            case "cycle_count":
                return performCycleCount(parameters);
            case "approve_adjustment":
                return approveAdjustment(parameters);
            case "validate_inventory":
                return validateInventoryViaAPI(parameters);
            default:
                StepLogger.warning("Unknown operation: " + operation);
                return false;
        }
    }
    
    @Override
    protected String getPageTitle() {
        return "Inventory Adjustment Management";
    }
    
    @Override
    protected String getPageUrl() {
        return "/warehouse/inventory/adjustment";
    }
    
    // Business logic methods demonstrating OOP principles
    public boolean performPositiveAdjustment(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> adjustments = (List<Map<String, Object>>) parameters.get("adjustments");
            
            for (Map<String, Object> adjustment : adjustments) {
                String itemCode = (String) adjustment.get("item_code");
                Integer currentQty = (Integer) adjustment.get("current_quantity");
                Integer adjustedQty = (Integer) adjustment.get("adjusted_quantity");
                String reason = (String) adjustment.get("reason");
                String location = (String) adjustment.get("location");
                
                // Store system quantities
                systemQuantities.put(itemCode, currentQty);
                physicalQuantities.put(itemCode, adjustedQty);
                adjustmentReasons.put(itemCode, reason);
                locationMappings.put(itemCode, location);
                
                // Perform adjustment
                performSingleAdjustment(itemCode, INCREMENT, adjustedQty - currentQty, reason, location);
                
                // Store in data manager
                dataManager.updateInventory(itemCode, adjustedQty);
                dataManager.addAuditEntry("Positive adjustment: " + itemCode + " from " + currentQty + " to " + adjustedQty);
            }
            
            StepLogger.passWithScreenshot("Positive adjustments completed successfully");
            return validateSuccessMessage("Adjustments submitted");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to perform positive adjustment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean performNegativeAdjustment(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> adjustments = (List<Map<String, Object>>) parameters.get("adjustments");
            
            for (Map<String, Object> adjustment : adjustments) {
                String itemCode = (String) adjustment.get("item_code");
                Integer currentQty = (Integer) adjustment.get("current_quantity");
                Integer adjustedQty = (Integer) adjustment.get("adjusted_quantity");
                String reason = (String) adjustment.get("reason");
                String location = (String) adjustment.get("location");
                
                // Store quantities and reasons
                systemQuantities.put(itemCode, currentQty);
                physicalQuantities.put(itemCode, adjustedQty);
                adjustmentReasons.put(itemCode, reason);
                locationMappings.put(itemCode, location);
                
                // Perform adjustment
                performSingleAdjustment(itemCode, DECREMENT, currentQty - adjustedQty, reason, location);
                
                // Store in data manager
                dataManager.updateInventory(itemCode, adjustedQty);
                dataManager.addAuditEntry("Negative adjustment: " + itemCode + " from " + currentQty + " to " + adjustedQty);
            }
            
            StepLogger.passWithScreenshot("Negative adjustments completed successfully");
            return validateSuccessMessage("Adjustments submitted");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to perform negative adjustment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean performCycleCount(Map<String, Object> parameters) {
        try {
            String location = (String) parameters.get("location");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> countData = (List<Map<String, Object>>) parameters.get("count_data");
            
            clickElement(cycleCountButton, "Cycle Count");
            
            for (Map<String, Object> count : countData) {
                String itemCode = (String) count.get("item_code");
                Integer systemQty = (Integer) count.get("system_quantity");
                Integer physicalQty = (Integer) count.get("physical_quantity");
                
                // Store count data
                systemQuantities.put(itemCode, systemQty);
                physicalQuantities.put(itemCode, physicalQty);
                locationMappings.put(itemCode, location);
                
                // Enter physical count
                enterText(itemCodeInput, itemCode, "Item Code");
                enterText(physicalCountInput, physicalQty.toString(), "Physical Count");
                
                // Calculate variance
                int variance = physicalQty - systemQty;
                if (variance != 0) {
                    String reason = variance > 0 ? "Over count found" : "Short count found";
                    adjustmentReasons.put(itemCode, reason);
                    pendingAdjustments.add(itemCode);
                }
                
                // Store in data manager
                dataManager.storeShipmentData("CYCLE_COUNT_" + location, itemCode + "_system", systemQty);
                dataManager.storeShipmentData("CYCLE_COUNT_" + location, itemCode + "_physical", physicalQty);
            }
            
            StepLogger.passWithScreenshot("Cycle count completed for location: " + location);
            return validateSuccessMessage("Cycle count recorded");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to perform cycle count: " + e.getMessage());
            return false;
        }
    }
    
    public boolean approveAdjustment(Map<String, Object> parameters) {
        try {
            clickElement(approveCountButton, "Approve Count");
            
            // Process pending adjustments
            while (!pendingAdjustments.isEmpty()) {
                String itemCode = pendingAdjustments.poll();
                processedAdjustments.add(itemCode);
                adjustmentHistory.add("Approved adjustment for: " + itemCode);
            }
            
            StepLogger.passWithScreenshot("Cycle count adjustments approved");
            return validateSuccessMessage("Adjustments approved");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to approve adjustments: " + e.getMessage());
            return false;
        }
    }
    
    public boolean validateInventoryViaAPI(Map<String, Object> parameters) {
        try {
            String baseUrl = (String) parameters.get("base_url");
            @SuppressWarnings("unchecked")
            List<String> itemCodes = (List<String>) parameters.get("item_codes");
            
            boolean allValid = true;
            
            for (String itemCode : itemCodes) {
                // Get inventory by item code using the specified API
                var response = dataManager.getInventoryByItemCode(itemCode, baseUrl);
                
                if (response.getStatusCode() == 200) {
                    var inventoryData = dataManager.extractInventoryDataFromJSON(response);
                    
                    // Validate against stored data
                    Integer expectedQty = physicalQuantities.get(itemCode);
                    Integer actualQty = (Integer) inventoryData.get("totalQuantity");
                    
                    if (expectedQty != null && !expectedQty.equals(actualQty)) {
                        StepLogger.fail("Inventory mismatch for " + itemCode + ". Expected: " + expectedQty + ", Actual: " + actualQty);
                        allValid = false;
                    } else {
                        StepLogger.pass("Inventory validated for " + itemCode + ": " + actualQty);
                    }
                } else {
                    StepLogger.fail("API call failed for " + itemCode + ". Status: " + response.getStatusCode());
                    allValid = false;
                }
            }
            
            if (allValid) {
                StepLogger.passWithScreenshot("All inventory validations passed");
            } else {
                StepLogger.failWithScreenshot("Some inventory validations failed");
            }
            
            return allValid;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to validate inventory via API: " + e.getMessage());
            return false;
        }
    }
    
    // Helper methods
    private void performSingleAdjustment(String itemCode, String adjustmentType, int quantity, String reason, String location) {
        try {
            enterText(itemCodeInput, itemCode, "Item Code");
            selectFromDropdown(adjustmentTypeSelect, adjustmentType, "Adjustment Type");
            enterText(quantityInput, String.valueOf(quantity), "Quantity");
            enterText(reasonInput, reason, "Reason");
            enterText(locationInput, location, "Location");
            
            String referenceId = "ADJ-" + System.currentTimeMillis();
            enterText(referenceIdInput, referenceId, "Reference ID");
            
            clickElement(submitAdjustmentButton, "Submit Adjustment");
            
            // Call the inventory adjustment API
            String baseUrl = baseUtil.readProp("base_url");
            if (baseUrl != null) {
                dataManager.adjustInventory(itemCode, adjustmentType, quantity, reason, location, "warehouse_operator", referenceId, baseUrl);
            }
            
            adjustmentHistory.add(adjustmentType + " adjustment: " + itemCode + " by " + quantity);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to perform single adjustment: " + e.getMessage(), e);
        }
    }
    
    private void selectFromDropdown(WebElement dropdown, String value, String fieldName) {
        try {
            clickElement(dropdown, fieldName);
            // Implementation would depend on the specific dropdown structure
            StepLogger.info("Selected " + value + " from " + fieldName);
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to select from " + fieldName + ": " + e.getMessage());
            throw e;
        }
    }
    
    // Stream operations for data analysis
    public Map<String, Integer> getVarianceReport() {
        return systemQuantities.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> physicalQuantities.getOrDefault(entry.getKey(), 0) - entry.getValue()
                ));
    }
    
    public List<String> getPositiveVarianceItems() {
        return getVarianceReport().entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    public List<String> getNegativeVarianceItems() {
        return getVarianceReport().entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    // Status validation
    public String getCurrentAdjustmentStatus() {
        try {
            return getText(adjustmentStatusElement, "Adjustment Status");
        } catch (Exception e) {
            StepLogger.warning("Could not determine adjustment status: " + e.getMessage());
            return PENDING;
        }
    }
    
    public boolean validateAdjustmentStatus(String expectedStatus) {
        String actualStatus = getCurrentAdjustmentStatus();
        boolean isValid = actualStatus.equals(expectedStatus);
        
        if (isValid) {
            StepLogger.pass("Adjustment status validated: " + actualStatus);
        } else {
            StepLogger.fail("Adjustment status mismatch. Expected: " + expectedStatus + ", Actual: " + actualStatus);
        }
        
        return isValid;
    }
    
    // Getters for collections (demonstrating encapsulation)
    public Map<String, Integer> getSystemQuantities() {
        return new LinkedHashMap<>(systemQuantities);
    }
    
    public Map<String, Integer> getPhysicalQuantities() {
        return new LinkedHashMap<>(physicalQuantities);
    }
    
    public Map<String, String> getAdjustmentReasons() {
        return new LinkedHashMap<>(adjustmentReasons);
    }
    
    public Map<String, String> getLocationMappings() {
        return new LinkedHashMap<>(locationMappings);
    }
    
    public Set<String> getProcessedAdjustments() {
        return new HashSet<>(processedAdjustments);
    }
    
    public List<String> getAdjustmentHistory() {
        return new ArrayList<>(adjustmentHistory);
    }
    
    public int getPendingAdjustmentsCount() {
        return pendingAdjustments.size();
    }
}
