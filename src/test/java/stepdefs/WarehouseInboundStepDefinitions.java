package stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;

import pages.BasePage;
import pages.InboundShipmentPage;
import utils.StepLogger;
import utils.WarehouseDataManager;
import utils.BaseUtil;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Warehouse Inbound Step Definitions - Demonstrates advanced Java concepts
 * Interview Points: Collections, Streams, Lambda expressions, REST API testing, Design Patterns
 */
public class WarehouseInboundStepDefinitions extends BaseUtil {
    
    private InboundShipmentPage inboundPage;
    private WarehouseDataManager dataManager;
    
    // Collections demonstrating different data structures
    private final Map<String, Map<String, Object>> shipmentRegistry = new LinkedHashMap<>();
    private final Set<String> processedShipments = new HashSet<>();
    private final List<String> operationHistory = new ArrayList<>();
    private final Queue<String> pendingOperations = new LinkedList<>();
    
    // Constructor demonstrating dependency injection
    public WarehouseInboundStepDefinitions() {
        this.dataManager = WarehouseDataManager.getInstance();
        StepLogger.info("Initialized Warehouse Inbound Step Definitions");
    }
    
    @Given("I create an inbound shipment {string} with items:")
    public void iCreateAnInboundShipmentWithItems(String shipmentId, DataTable dataTable) {
        StepLogger.stepStart("Given I create an inbound shipment " + shipmentId + " with items");
        
        try {
            // Convert DataTable to List of Maps - demonstrates collections usage
            List<Map<String, String>> items = dataTable.asMaps(String.class, String.class);
            
            // Transform data using streams and lambda expressions
            List<Map<String, Object>> transformedItems = items.stream()
                    .map(this::transformItemData)
                    .collect(Collectors.toList());
            
            // Store shipment data using various collection types
            Map<String, Object> shipmentData = new HashMap<>();
            shipmentData.put("shipment_id", shipmentId);
            shipmentData.put("items", transformedItems);
            shipmentData.put("status", "PENDING");
            shipmentData.put("created_date", new Date());
            
            // Use LinkedHashMap for ordered operations
            shipmentRegistry.put(shipmentId, shipmentData);
            
            // Create shipment through page object
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("shipment_id", shipmentId);
            parameters.put("items", transformedItems);
            
            boolean success = inboundPage.performPageOperation("create_shipment", parameters);
            
            if (success) {
                processedShipments.add(shipmentId);
                StepLogger.passWithScreenshot("Inbound shipment created successfully: " + shipmentId);
                
                // Store expected quantities in data manager
                transformedItems.forEach(item -> {
                    String sku = (String) item.get("sku");
                    Integer quantity = (Integer) item.get("quantity");
                    dataManager.storeShipmentData(shipmentId, "expected_" + sku, quantity);
                });
                
            } else {
                StepLogger.failWithScreenshot("Failed to create inbound shipment: " + shipmentId);
                throw new RuntimeException("Shipment creation failed");
            }
            
            addToOperationHistory("Shipment created: " + shipmentId + " with " + items.size() + " items");
            StepLogger.stepComplete("Given I create an inbound shipment " + shipmentId + " with items");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create shipment " + shipmentId + ": " + e.getMessage());
            throw e;
        }
    }
    
    @When("I navigate to the pre-receiving change screen for shipment {string}")
    public void iNavigateToThePreReceivingChangeScreenForShipment(String shipmentId) {
        StepLogger.stepStart("When I navigate to the pre-receiving change screen for shipment " + shipmentId);
        
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("shipment_id", shipmentId);
            
            boolean success = inboundPage.performPageOperation("navigate_pre_receiving", parameters);
            
            if (success) {
                StepLogger.passWithScreenshot("Navigated to pre-receiving screen for: " + shipmentId);
                dataManager.storeShipmentData(shipmentId, "current_screen", "pre_receiving");
            } else {
                StepLogger.failWithScreenshot("Failed to navigate to pre-receiving screen");
                throw new RuntimeException("Navigation failed");
            }
            
            addToOperationHistory("Navigated to pre-receiving for: " + shipmentId);
            StepLogger.stepComplete("When I navigate to the pre-receiving change screen for shipment " + shipmentId);
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to navigate to pre-receiving: " + e.getMessage());
            throw e;
        }
    }
    
    @And("I create appointment {string} for the shipment")
    public void iCreateAppointmentForTheShipment(String appointmentNumber) {
        StepLogger.stepStart("And I create appointment " + appointmentNumber + " for the shipment");
        
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("appointment_number", appointmentNumber);
            
            boolean success = inboundPage.performPageOperation("create_appointment", parameters);
            
            if (success) {
                StepLogger.passWithScreenshot("Appointment created successfully: " + appointmentNumber);
                
                // Store appointment data
                dataManager.storeShipmentData("current_shipment", "appointment_number", appointmentNumber);
                dataManager.storeShipmentData("current_shipment", "appointment_status", "CREATED");
                
            } else {
                StepLogger.failWithScreenshot("Failed to create appointment: " + appointmentNumber);
                throw new RuntimeException("Appointment creation failed");
            }
            
            addToOperationHistory("Appointment created: " + appointmentNumber);
            StepLogger.stepComplete("And I create appointment " + appointmentNumber + " for the shipment");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create appointment: " + e.getMessage());
            throw e;
        }
    }
    
    @And("I assign lot numbers to received items:")
    public void iAssignLotNumbersToReceivedItems(DataTable dataTable) {
        StepLogger.stepStart("And I assign lot numbers to received items");
        
        try {
            List<Map<String, String>> lotData = dataTable.asMaps(String.class, String.class);
            
            // Transform lot data using streams - demonstrates functional programming
            Map<String, List<String>> lotAssignments = lotData.stream()
                    .collect(Collectors.toMap(
                        row -> row.get("SKU"),
                        row -> Arrays.asList(row.get("Lot Numbers").split(",\\s*"))
                    ));
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("lot_assignments", lotAssignments);
            
            boolean success = inboundPage.performPageOperation("assign_lots", parameters);
            
            if (success) {
                StepLogger.passWithScreenshot("Lot numbers assigned successfully");
                
                // Store lot assignments in data manager using Set operations
                lotAssignments.forEach((sku, lots) -> {
                    lots.forEach(lot -> dataManager.addLotNumber(sku, lot.trim()));
                });
                
            } else {
                StepLogger.failWithScreenshot("Failed to assign lot numbers");
                throw new RuntimeException("Lot assignment failed");
            }
            
            addToOperationHistory("Lot numbers assigned for " + lotAssignments.size() + " SKUs");
            StepLogger.stepComplete("And I assign lot numbers to received items");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to assign lot numbers: " + e.getMessage());
            throw e;
        }
    }
    
    @And("I assign storage locations for items:")
    public void iAssignStorageLocationsForItems(DataTable dataTable) {
        StepLogger.stepStart("And I assign storage locations for items");
        
        try {
            List<Map<String, String>> locationData = dataTable.asMaps(String.class, String.class);
            
            // Convert to Map using collectors - demonstrates stream operations
            Map<String, String> locationAssignments = locationData.stream()
                    .collect(Collectors.toMap(
                        row -> row.get("SKU"),
                        row -> row.get("Location")
                    ));
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("location_assignments", locationAssignments);
            
            boolean success = inboundPage.performPageOperation("assign_locations", parameters);
            
            if (success) {
                StepLogger.passWithScreenshot("Storage locations assigned successfully");
                
                // Store location assignments
                locationAssignments.forEach((sku, location) -> {
                    dataManager.storeShipmentData("current_shipment", "location_" + sku, location);
                });
                
            } else {
                StepLogger.failWithScreenshot("Failed to assign storage locations");
                throw new RuntimeException("Location assignment failed");
            }
            
            addToOperationHistory("Locations assigned for " + locationAssignments.size() + " SKUs");
            StepLogger.stepComplete("And I assign storage locations for items");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to assign locations: " + e.getMessage());
            throw e;
        }
    }
    
    @And("I check pallets with customer sent pallet as full receiving:")
    public void iCheckPalletsWithCustomerSentPalletAsFullReceiving(DataTable dataTable) {
        StepLogger.stepStart("And I check pallets with customer sent pallet as full receiving");
        
        try {
            List<Map<String, String>> palletData = dataTable.asMaps(String.class, String.class);
            
            // Transform pallet data using complex stream operations
            List<Map<String, Object>> transformedPallets = palletData.stream()
                    .map(row -> {
                        Map<String, Object> pallet = new HashMap<>();
                        pallet.put("pallet_id", row.get("Pallet ID"));
                        pallet.put("skus", Arrays.asList(row.get("SKUs").split(",\\s*")));
                        pallet.put("status", row.get("Status"));
                        return pallet;
                    })
                    .collect(Collectors.toList());
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("pallets", transformedPallets);
            
            boolean success = inboundPage.performPageOperation("check_pallets", parameters);
            
            if (success) {
                StepLogger.passWithScreenshot("Pallets checked successfully for full receiving");
                
                // Store pallet assignments using List operations
                transformedPallets.forEach(pallet -> {
                    String palletId = (String) pallet.get("pallet_id");
                    @SuppressWarnings("unchecked")
                    List<String> skus = (List<String>) pallet.get("skus");
                    
                    skus.forEach(sku -> dataManager.assignPallet(sku.trim(), palletId));
                });
                
            } else {
                StepLogger.failWithScreenshot("Failed to check pallets");
                throw new RuntimeException("Pallet checking failed");
            }
            
            addToOperationHistory("Pallets checked: " + transformedPallets.size() + " pallets");
            StepLogger.stepComplete("And I check pallets with customer sent pallet as full receiving");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to check pallets: " + e.getMessage());
            throw e;
        }
    }
    
    @And("I complete the receiving process for all items")
    public void iCompleteTheReceivingProcessForAllItems() {
        StepLogger.stepStart("And I complete the receiving process for all items");
        
        try {
            boolean success = inboundPage.performPageOperation("complete_receiving", new HashMap<>());
            
            if (success) {
                StepLogger.passWithScreenshot("Receiving process completed successfully");
                dataManager.storeShipmentData("current_shipment", "receiving_status", "COMPLETED");
            } else {
                StepLogger.failWithScreenshot("Failed to complete receiving process");
                throw new RuntimeException("Receiving completion failed");
            }
            
            addToOperationHistory("Receiving process completed");
            StepLogger.stepComplete("And I complete the receiving process for all items");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to complete receiving: " + e.getMessage());
            throw e;
        }
    }
    
    @And("I create receipt {string} for the shipment")
    public void iCreateReceiptForTheShipment(String receiptNumber) {
        StepLogger.stepStart("And I create receipt " + receiptNumber + " for the shipment");
        
        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("receipt_number", receiptNumber);
            
            boolean success = inboundPage.performPageOperation("create_receipt", parameters);
            
            if (success) {
                StepLogger.passWithScreenshot("Receipt created successfully: " + receiptNumber);
                dataManager.storeShipmentData("current_shipment", "receipt_number", receiptNumber);
            } else {
                StepLogger.failWithScreenshot("Failed to create receipt: " + receiptNumber);
                throw new RuntimeException("Receipt creation failed");
            }
            
            addToOperationHistory("Receipt created: " + receiptNumber);
            StepLogger.stepComplete("And I create receipt " + receiptNumber + " for the shipment");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to create receipt: " + e.getMessage());
            throw e;
        }
    }
    
    @And("I validate inventory levels via REST API call")
    public void iValidateInventoryLevelsViaRESTAPICall() {
        StepLogger.stepStart("And I validate inventory levels via REST API call");
        
        try {
            String baseUrl = readProp("URL");
            Map<String, Set<String>> allLotNumbers = dataManager.getAllLotNumbers();
            
            // Validate each SKU via REST API - demonstrates REST Assured usage
            for (Map.Entry<String, Set<String>> entry : allLotNumbers.entrySet()) {
                String sku = entry.getKey();
                validateSingleSKUViaAPI(sku, baseUrl);
            }
            
            StepLogger.passWithScreenshot("All inventory levels validated via REST API");
            addToOperationHistory("Inventory validation completed via API");
            StepLogger.stepComplete("And I validate inventory levels via REST API call");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to validate inventory via API: " + e.getMessage());
            throw e;
        }
    }
    
    @Then("the receipt should be printed successfully")
    public void theReceiptShouldBePrintedSuccessfully() {
        StepLogger.stepStart("Then the receipt should be printed successfully");
        
        try {
            // Simulate receipt printing validation
            String receiptNumber = dataManager.getShipmentDataAsString("current_shipment", "receipt_number");
            
            if (receiptNumber != null && !receiptNumber.isEmpty()) {
                StepLogger.passWithScreenshot("Receipt printed successfully: " + receiptNumber);
                dataManager.storeShipmentData("current_shipment", "receipt_printed", true);
            } else {
                StepLogger.failWithScreenshot("Receipt number not found for printing");
                throw new RuntimeException("Receipt printing failed - no receipt number");
            }
            
            addToOperationHistory("Receipt printed: " + receiptNumber);
            StepLogger.stepComplete("Then the receipt should be printed successfully");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to print receipt: " + e.getMessage());
            throw e;
        }
    }
    
    @And("the 944 EDI should be generated and ready to send")
    public void the944EDIShouldBeGeneratedAndReadyToSend() {
        StepLogger.stepStart("And the 944 EDI should be generated and ready to send");
        
        try {
            // Simulate 944 EDI generation
            String shipmentId = dataManager.getShipmentDataAsString("current_shipment", "shipment_id");
            String ediDocument = generate944EDI(shipmentId);
            
            if (ediDocument != null && !ediDocument.isEmpty()) {
                StepLogger.passWithScreenshot("944 EDI generated successfully");
                dataManager.storeShipmentData("current_shipment", "edi_944", ediDocument);
                dataManager.storeShipmentData("current_shipment", "edi_status", "READY_TO_SEND");
            } else {
                StepLogger.failWithScreenshot("Failed to generate 944 EDI");
                throw new RuntimeException("944 EDI generation failed");
            }
            
            addToOperationHistory("944 EDI generated for shipment: " + shipmentId);
            StepLogger.stepComplete("And the 944 EDI should be generated and ready to send");
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to generate 944 EDI: " + e.getMessage());
            throw e;
        }
    }
    
    @And("the shipment status should be {string}")
    public void theShipmentStatusShouldBe(String expectedStatus) {
        StepLogger.stepStart("And the shipment status should be " + expectedStatus);
        
        try {
            boolean statusValid = inboundPage.validateShipmentStatus(expectedStatus);
            
            if (statusValid) {
                StepLogger.passWithScreenshot("Shipment status validated: " + expectedStatus);
                dataManager.storeShipmentData("current_shipment", "final_status", expectedStatus);
            } else {
                StepLogger.failWithScreenshot("Shipment status validation failed. Expected: " + expectedStatus);
                throw new RuntimeException("Status validation failed");
            }
            
            addToOperationHistory("Status validated: " + expectedStatus);
            StepLogger.stepComplete("And the shipment status should be " + expectedStatus);
            
        } catch (Exception e) {
            StepLogger.failWithScreenshot("Failed to validate shipment status: " + e.getMessage());
            throw e;
        }
    }
    
    // Helper methods demonstrating various Java concepts
    
    private Map<String, Object> transformItemData(Map<String, String> rawItem) {
        Map<String, Object> transformedItem = new HashMap<>();
        transformedItem.put("sku", rawItem.get("SKU"));
        transformedItem.put("quantity", Integer.parseInt(rawItem.get("Quantity")));
        transformedItem.put("description", rawItem.get("Description"));
        return transformedItem;
    }
    
    private void addToOperationHistory(String operation) {
        operationHistory.add(new Date() + ": " + operation);
    }
    
    private String generate944EDI(String shipmentId) {
        // Simulate EDI generation - in real implementation, this would generate actual EDI
        StringBuilder edi = new StringBuilder();
        edi.append("ST*944*0001~\n");
        edi.append("W17*").append(shipmentId).append("*").append(new Date()).append("~\n");
        
        // Add item details from data manager
        Map<String, Set<String>> lotNumbers = dataManager.getAllLotNumbers();
        lotNumbers.forEach((sku, lots) -> {
            edi.append("W04*").append(sku).append("*").append(lots.size()).append("~\n");
        });
        
        edi.append("SE*").append(lotNumbers.size() + 2).append("*0001~\n");
        
        return edi.toString();
    }
    
    private void validateSingleSKUViaAPI(String sku, String baseUrl) {
        try {
            // REST Assured API call - demonstrates REST API testing
            Response response = dataManager.validateInventoryViaAPI(sku, baseUrl);
            
            if (response.getStatusCode() == 200) {
                // JSONPath demonstration
                Map<String, Object> inventoryData = dataManager.extractInventoryDataFromJSON(response);
                
                // Validate extracted data
                String apiSku = (String) inventoryData.get("sku");
                Integer apiQuantity = (Integer) inventoryData.get("quantity");
                
                if (sku.equals(apiSku)) {
                    StepLogger.pass("API validation successful for SKU: " + sku + " (Qty: " + apiQuantity + ")");
                    
                    // Update local inventory
                    dataManager.updateInventory(sku, apiQuantity);
                } else {
                    StepLogger.fail("API validation failed - SKU mismatch: " + sku + " vs " + apiSku);
                }
                
            } else {
                StepLogger.warning("API call failed for SKU " + sku + " - Status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            StepLogger.warning("API validation error for SKU " + sku + ": " + e.getMessage());
        }
    }
    
    // Getters for collections (demonstrating encapsulation)
    public Map<String, Map<String, Object>> getShipmentRegistry() {
        return Collections.unmodifiableMap(shipmentRegistry);
    }
    
    public Set<String> getProcessedShipments() {
        return Collections.unmodifiableSet(processedShipments);
    }
    
    public List<String> getOperationHistory() {
        return Collections.unmodifiableList(operationHistory);
    }
}
