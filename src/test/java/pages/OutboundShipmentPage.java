package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import utils.StepLogger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Outbound Shipment Page - Demonstrates inheritance, collections, and business logic
 * Interview Points: Page Object Model, Collections usage, Stream operations
 */
public class OutboundShipmentPage extends BasePage {
    
    // Page elements using Page Factory pattern
    @FindBy(id = "order-id-input")
    private WebElement orderIdInput;
    
    @FindBy(id = "create-order-btn")
    private WebElement createOrderButton;
    
    @FindBy(id = "order-change-btn")
    private WebElement orderChangeButton;
    
    @FindBy(id = "appointment-number-input")
    private WebElement appointmentNumberInput;
    
    @FindBy(id = "create-appointment-btn")
    private WebElement createAppointmentButton;
    
    @FindBy(id = "pool-number-input")
    private WebElement poolNumberInput;
    
    @FindBy(id = "assign-pool-btn")
    private WebElement assignPoolButton;
    
    @FindBy(id = "print-order-btn")
    private WebElement printOrderButton;
    
    @FindBy(id = "pallet-id-input")
    private WebElement palletIdInput;
    
    @FindBy(id = "assign-pallet-btn")
    private WebElement assignPalletButton;
    
    @FindBy(id = "complete-pick-btn")
    private WebElement completePickButton;
    
    @FindBy(id = "close-appointment-btn")
    private WebElement closeAppointmentButton;
    
    @FindBy(id = "confirm-order-btn")
    private WebElement confirmOrderButton;
    
    @FindBy(id = "order-status")
    private WebElement orderStatusElement;
    
    @FindBy(className = "item-row")
    private List<WebElement> itemRows;
    
    @FindBy(className = "shortage-indicator")
    private List<WebElement> shortageIndicators;
    
    // Collections for managing outbound data
    private final Map<String, Integer> orderedItems = new LinkedHashMap<>();
    private final Map<String, Integer> pickedItems = new LinkedHashMap<>();
    private final Map<String, String> palletAssignments = new LinkedHashMap<>();
    private final Map<String, String> poolAssignments = new LinkedHashMap<>();
    private final Set<String> processedBatches = new HashSet<>();
    private final List<String> pickingSequence = new ArrayList<>();
    
    // Status constants
    private static final String CREATED = "CREATED";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String PICKED = "PICKED";
    private static final String SHIPPED = "SHIPPED";
    private static final String SHORT_PICKED = "SHORT_PICKED";
    private static final String BATCH_SPLIT = "BATCH_SPLIT";
    private static final String PALLET_SPLIT = "PALLET_SPLIT";
    
    @Override
    protected void validatePageElements() {
        StepLogger.info("Validating Outbound Shipment page elements");
        
        if (!isElementDisplayed(orderIdInput)) {
            throw new RuntimeException("Order ID input not found");
        }
        
        if (!isElementDisplayed(createOrderButton)) {
            throw new RuntimeException("Create Order button not found");
        }
        
        StepLogger.pass("All required page elements are present");
    }
    
    @Override
    protected boolean executeOperation(String operation, Map<String, Object> parameters) {
        switch (operation.toLowerCase()) {
            case "create_order":
                return createOrder(parameters);
            case "navigate_order_change":
                return navigateToOrderChange();
            case "create_appointment":
                return createAppointment(parameters);
            case "assign_pool":
                return assignPoolNumber(parameters);
            case "print_order":
                return printOrder(parameters);
            case "complete_pick":
                return completePick(parameters);
            case "close_appointment":
                return closeAppointment(parameters);
            case "confirm_order":
                return confirmOrder(parameters);
            case "split_batches":
                return splitBatches(parameters);
            case "split_pallets":
                return splitPallets(parameters);
            default:
                StepLogger.warning("Unknown operation: " + operation);
                return false;
        }
    }
    
    @Override
    protected String getPageTitle() {
        return "Outbound Shipment Management";
    }
    
    @Override
    protected String getPageUrl() {
        return "/warehouse/outbound";
    }
    
    // Business logic methods demonstrating OOP principles
    public boolean createOrder(Map<String, Object> parameters) {
        try {
            String orderId = (String) parameters.get("order_id");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) parameters.get("items");
            
            enterText(orderIdInput, orderId, "Order ID");
            clickElement(createOrderButton, "Create Order");
            
            // Store ordered items using collections
            for (Map<String, Object> item : items) {
                String sku = (String) item.get("sku");
                Integer quantity = (Integer) item.get("quantity");
                orderedItems.put(sku, quantity);
                
                // Store in data manager for cross-page access
                dataManager.storeOrderData(orderId, "ordered_" + sku, quantity);
            }
            
            StepLogger.passWithScreenshot("Order created successfully: " + orderId);
            storePageData("current_order_id", orderId);
            
            return validateSuccessMessage("Order created");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create order: " + e.getMessage());
            return false;
        }
    }
    
    public boolean navigateToOrderChange() {
        try {
            clickElement(orderChangeButton, "Order Change");
            waitForPageLoad();
            
            StepLogger.passWithScreenshot("Navigated to order change screen");
            return true;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to navigate to order change: " + e.getMessage());
            return false;
        }
    }
    
    public boolean createAppointment(Map<String, Object> parameters) {
        try {
            String appointmentNumber = (String) parameters.get("appointment_number");
            
            enterText(appointmentNumberInput, appointmentNumber, "Appointment Number");
            clickElement(createAppointmentButton, "Create Appointment");
            
            StepLogger.passWithScreenshot("Appointment created: " + appointmentNumber);
            storePageData("current_appointment", appointmentNumber);
            
            return validateSuccessMessage("Appointment created");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create appointment: " + e.getMessage());
            return false;
        }
    }
    
    public boolean assignPoolNumber(Map<String, Object> parameters) {
        try {
            String poolNumber = (String) parameters.get("pool_number");
            
            enterText(poolNumberInput, poolNumber, "Pool Number");
            clickElement(assignPoolButton, "Assign Pool");
            
            // Store pool assignment
            String orderId = getCurrentOrderId();
            poolAssignments.put(orderId, poolNumber);
            dataManager.storeOrderData(orderId, "pool_number", poolNumber);
            
            StepLogger.passWithScreenshot("Pool number assigned: " + poolNumber);
            return validateSuccessMessage("Pool assigned");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to assign pool number: " + e.getMessage());
            return false;
        }
    }
    
    public boolean printOrder(Map<String, Object> parameters) {
        try {
            String poolNumber = (String) parameters.get("pool_number");
            
            clickElement(printOrderButton, "Print Order");
            
            StepLogger.passWithScreenshot("Order printed for pool: " + poolNumber);
            return validateSuccessMessage("Order printed");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to print order: " + e.getMessage());
            return false;
        }
    }
    
    public boolean completePick(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> pickData = (List<Map<String, Object>>) parameters.get("pick_data");
            
            for (Map<String, Object> pick : pickData) {
                String sku = (String) pick.get("sku");
                String palletId = (String) pick.get("pallet_id");
                Integer quantity = (Integer) pick.get("quantity");
                
                // Assign pallet and record picked quantity
                assignPalletToSku(sku, palletId);
                pickedItems.put(sku, quantity);
                pickingSequence.add(sku);
                
                // Store in data manager
                dataManager.storeOrderData(getCurrentOrderId(), "picked_" + sku, quantity);
                dataManager.assignPallet(sku, palletId);
            }
            
            clickElement(completePickButton, "Complete Pick");
            
            StepLogger.passWithScreenshot("Pick completed successfully");
            return validateSuccessMessage("Pick completed");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to complete pick: " + e.getMessage());
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
    
    public boolean confirmOrder(Map<String, Object> parameters) {
        try {
            clickElement(confirmOrderButton, "Confirm Order");
            
            StepLogger.passWithScreenshot("Order confirmed for EDI generation");
            return validateSuccessMessage("Order confirmed");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to confirm order: " + e.getMessage());
            return false;
        }
    }
    
    public boolean splitBatches(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> batchData = (List<Map<String, Object>>) parameters.get("batches");
            
            for (Map<String, Object> batch : batchData) {
                String batchId = (String) batch.get("batch_id");
                String sku = (String) batch.get("sku");
                Integer quantity = (Integer) batch.get("quantity");
                
                processedBatches.add(batchId);
                dataManager.storeOrderData(getCurrentOrderId(), "batch_" + batchId, Map.of("sku", sku, "quantity", quantity));
            }
            
            StepLogger.passWithScreenshot("Batches split successfully");
            return true;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to split batches: " + e.getMessage());
            return false;
        }
    }
    
    public boolean splitPallets(Map<String, Object> parameters) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> palletSplitData = (List<Map<String, Object>>) parameters.get("pallet_splits");
            
            for (Map<String, Object> split : palletSplitData) {
                String originalPallet = (String) split.get("original_pallet");
                String newPallet = (String) split.get("new_pallet");
                String sku = (String) split.get("sku");
                
                // Update pallet assignment
                palletAssignments.put(sku, newPallet);
                dataManager.assignPallet(sku, newPallet);
            }
            
            StepLogger.passWithScreenshot("Pallets split successfully");
            return true;
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to split pallets: " + e.getMessage());
            return false;
        }
    }
    
    // Helper methods
    private void assignPalletToSku(String sku, String palletId) {
        enterText(palletIdInput, palletId, "Pallet ID for " + sku);
        clickElement(assignPalletButton, "Assign Pallet");
        palletAssignments.put(sku, palletId);
    }
    
    private String getCurrentOrderId() {
        return (String) getPageData("current_order_id");
    }
    
    // Stream operations for data analysis
    public Map<String, Integer> getShortageReport() {
        return orderedItems.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue() - pickedItems.getOrDefault(entry.getKey(), 0)
                ))
                .entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    public List<String> getShortPickedItems() {
        return getShortageReport().keySet().stream()
                .collect(Collectors.toList());
    }
    
    // Status validation
    public String getCurrentOrderStatus() {
        try {
            return getText(orderStatusElement, "Order Status");
        } catch (Exception e) {
            StepLogger.warning("Could not determine order status: " + e.getMessage());
            return CREATED;
        }
    }
    
    public boolean validateOrderStatus(String expectedStatus) {
        String actualStatus = getCurrentOrderStatus();
        boolean isValid = actualStatus.equals(expectedStatus);
        
        if (isValid) {
            StepLogger.pass("Order status validated: " + actualStatus);
        } else {
            StepLogger.fail("Order status mismatch. Expected: " + expectedStatus + ", Actual: " + actualStatus);
        }
        
        return isValid;
    }
    
    // Getters for collections (demonstrating encapsulation)
    public Map<String, Integer> getOrderedItems() {
        return new LinkedHashMap<>(orderedItems);
    }
    
    public Map<String, Integer> getPickedItems() {
        return new LinkedHashMap<>(pickedItems);
    }
    
    public Map<String, String> getPalletAssignments() {
        return new LinkedHashMap<>(palletAssignments);
    }
    
    public Map<String, String> getPoolAssignments() {
        return new LinkedHashMap<>(poolAssignments);
    }
    
    public Set<String> getProcessedBatches() {
        return new HashSet<>(processedBatches);
    }
    
    public List<String> getPickingSequence() {
        return new ArrayList<>(pickingSequence);
    }
}
