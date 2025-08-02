package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Warehouse Data Manager - Demonstrates Java Collections, Streams, and REST API integration
 * Interview Points: LinkedHashMap usage, Collections, Stream operations, REST API testing, JSON processing
 */
public class WarehouseDataManager {
    
    // Using LinkedHashMap to maintain insertion order
    private static final Map<String, Map<String, Object>> shipmentData = new LinkedHashMap<>();
    private static final Map<String, Map<String, Object>> orderData = new LinkedHashMap<>();
    private static final Map<String, Set<String>> lotNumbers = new LinkedHashMap<>();
    private static final Map<String, List<String>> palletAssignments = new LinkedHashMap<>();
    private static final Map<String, Integer> inventoryLevels = new LinkedHashMap<>();
    
    // Collections demonstrating different data structures
    private static final List<String> auditTrail = new ArrayList<>();
    private static final Queue<String> processingQueue = new LinkedList<>();
    private static final Set<String> processedItems = new HashSet<>();
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // Singleton pattern demonstration
    private static WarehouseDataManager instance;
    
    private WarehouseDataManager() {
        // Private constructor for singleton
    }
    
    public static WarehouseDataManager getInstance() {
        if (instance == null) {
            instance = new WarehouseDataManager();
        }
        return instance;
    }
    
    // Simple methods for storing and retrieving data
    public void storeShipmentData(String shipmentId, String key, Object value) {
        if (!shipmentData.containsKey(shipmentId)) {
            shipmentData.put(shipmentId, new LinkedHashMap<>());
        }
        shipmentData.get(shipmentId).put(key, value);
        addAuditEntry("Shipment data stored: " + shipmentId + " -> " + key + " = " + value);
    }
    
    public Object getShipmentData(String shipmentId, String key) {
        Map<String, Object> data = shipmentData.get(shipmentId);
        if (data != null) {
            return data.get(key);
        }
        return null;
    }
    
    public String getShipmentDataAsString(String shipmentId, String key) {
        Object value = getShipmentData(shipmentId, key);
        return value != null ? value.toString() : null;
    }
    
    public void storeOrderData(String orderId, String key, Object value) {
        if (!orderData.containsKey(orderId)) {
            orderData.put(orderId, new LinkedHashMap<>());
        }
        orderData.get(orderId).put(key, value);
        addAuditEntry("Order data stored: " + orderId + " -> " + key + " = " + value);
    }
    
    public Object getOrderData(String orderId, String key) {
        Map<String, Object> data = orderData.get(orderId);
        if (data != null) {
            return data.get(key);
        }
        return null;
    }
    
    public String getOrderDataAsString(String orderId, String key) {
        Object value = getOrderData(orderId, key);
        return value != null ? value.toString() : null;
    }
    
    // Set operations for lot numbers
    public void addLotNumber(String sku, String lotNumber) {
        if (!lotNumbers.containsKey(sku)) {
            lotNumbers.put(sku, new HashSet<>());
        }
        lotNumbers.get(sku).add(lotNumber);
        addAuditEntry("Lot number added: " + sku + " -> " + lotNumber);
    }
    
    public Set<String> getLotNumbers(String sku) {
        return lotNumbers.getOrDefault(sku, new HashSet<>());
    }
    
    public Map<String, Set<String>> getAllLotNumbers() {
        return new LinkedHashMap<>(lotNumbers);
    }
    
    // List operations for pallet assignments
    public void assignPallet(String sku, String palletId) {
        if (!palletAssignments.containsKey(sku)) {
            palletAssignments.put(sku, new ArrayList<>());
        }
        palletAssignments.get(sku).add(palletId);
        addAuditEntry("Pallet assigned: " + sku + " -> " + palletId);
    }
    
    public List<String> getPalletAssignments(String sku) {
        return palletAssignments.getOrDefault(sku, new ArrayList<>());
    }
    
    // Inventory operations
    public void updateInventory(String sku, int quantity) {
        if (inventoryLevels.containsKey(sku)) {
            int currentQty = inventoryLevels.get(sku);
            inventoryLevels.put(sku, currentQty + quantity);
        } else {
            inventoryLevels.put(sku, quantity);
        }
        addAuditEntry("Inventory updated: " + sku + " -> " + inventoryLevels.get(sku));
    }
    
    public Integer getInventoryLevel(String sku) {
        return inventoryLevels.getOrDefault(sku, 0);
    }
    
    public Map<String, Integer> getAllInventoryLevels() {
        return new LinkedHashMap<>(inventoryLevels);
    }
    
    // Stream operations for data analysis
    public List<String> getLowStockItems(int threshold) {
        return inventoryLevels.entrySet().stream()
                .filter(entry -> entry.getValue() < threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }
    
    public Map<String, Integer> getInventoryByRange(int min, int max) {
        return inventoryLevels.entrySet().stream()
                .filter(entry -> entry.getValue() >= min && entry.getValue() <= max)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (existing, replacement) -> existing,
                    LinkedHashMap::new
                ));
    }
    
    public long getTotalInventoryValue() {
        return inventoryLevels.values().stream()
                .mapToLong(Integer::longValue)
                .sum();
    }
    
    // Queue operations for processing
    public void addToProcessingQueue(String item) {
        processingQueue.offer(item);
        addAuditEntry("Item added to processing queue: " + item);
    }
    
    public String getNextItemToProcess() {
        String item = processingQueue.poll();
        if (item != null) {
            processedItems.add(item);
            addAuditEntry("Item processed: " + item);
        }
        return item;
    }
    
    public int getQueueSize() {
        return processingQueue.size();
    }
    
    // REST API integration methods
    public Response getInventoryByItemCode(String itemCode, String baseUrl) {
        try {
            Response response = RestAssured.given()
                    .contentType("application/json")
                    .when()
                    .get(baseUrl + "/api/inventory/item/" + itemCode)
                    .then()
                    .extract().response();
            
            addAuditEntry("API call made for inventory by item code: " + itemCode + " -> Status: " + response.getStatusCode());
            return response;
            
        } catch (Exception e) {
            addAuditEntry("API call failed for inventory by item code: " + itemCode + " -> Error: " + e.getMessage());
            throw new RuntimeException("Inventory API call failed", e);
        }
    }
    
    public Response getInventoryByLocation(String locationId, String baseUrl) {
        try {
            Response response = RestAssured.given()
                    .contentType("application/json")
                    .when()
                    .get(baseUrl + "/api/inventory/location/" + locationId)
                    .then()
                    .extract().response();
            
            addAuditEntry("API call made for inventory by location: " + locationId + " -> Status: " + response.getStatusCode());
            return response;
            
        } catch (Exception e) {
            addAuditEntry("API call failed for inventory by location: " + locationId + " -> Error: " + e.getMessage());
            throw new RuntimeException("Location inventory API call failed", e);
        }
    }
    
    public Response adjustInventory(String itemCode, String adjustmentType, int adjustedQuantity, 
                                  String reason, String locationId, String performedBy, 
                                  String referenceId, String baseUrl) {
        try {
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("itemCode", itemCode);
            requestBody.put("adjustmentType", adjustmentType); // "INCREMENT" or "DECREMENT"
            requestBody.put("adjustedQuantity", adjustedQuantity);
            requestBody.put("reason", reason);
            requestBody.put("locationId", locationId);
            requestBody.put("performedBy", performedBy);
            requestBody.put("referenceId", referenceId);
            
            Response response = RestAssured.given()
                    .contentType("application/json")
                    .body(requestBody)
                    .when()
                    .post(baseUrl + "/api/inventory/adjust")
                    .then()
                    .extract().response();
            
            addAuditEntry("API call made for inventory adjustment: " + itemCode + " -> Status: " + response.getStatusCode());
            return response;
            
        } catch (Exception e) {
            addAuditEntry("API call failed for inventory adjustment: " + itemCode + " -> Error: " + e.getMessage());
            throw new RuntimeException("Inventory adjustment API call failed", e);
        }
    }
    
    // REST API validation method for inventory by item code
    public Response validateInventoryViaAPI(String itemCode, String baseUrl) {
        try {
            // Build the API endpoint URL using the specified format
            String apiUrl = baseUrl + "/api/inventory/item/" + itemCode;
            
            // Create REST Assured request specification
            RequestSpecification request = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");
            
            // Make GET request to validate inventory
            Response response = request.get(apiUrl);
            
            // Log the API call for audit trail
            addAuditEntry("API call made to validate inventory for item: " + itemCode + 
                         " | Status: " + response.getStatusCode());
            
            return response;
            
        } catch (Exception e) {
            addAuditEntry("Failed to validate inventory via API for item: " + itemCode + 
                         " | Error: " + e.getMessage());
            throw new RuntimeException("API validation failed for item: " + itemCode, e);
        }
    }

    // JSON data extraction method for inventory by item code response
    public Map<String, Object> extractInventoryDataFromJSON(Response response) {
        try {
            Map<String, Object> inventoryData = new LinkedHashMap<>();
            
            // Extract data based on the specified API response format
            inventoryData.put("itemCode", response.jsonPath().getString("itemCode"));
            inventoryData.put("description", response.jsonPath().getString("description"));
            inventoryData.put("totalQuantity", response.jsonPath().getInt("totalQuantity"));
            inventoryData.put("availableQuantity", response.jsonPath().getInt("availableQuantity"));
            inventoryData.put("allocatedQuantity", response.jsonPath().getInt("allocatedQuantity"));
            inventoryData.put("uom", response.jsonPath().getString("uom"));
            inventoryData.put("locations", response.jsonPath().getList("locations"));
            
            addAuditEntry("Extracted inventory data for item: " + inventoryData.get("itemCode"));
            return inventoryData;
            
        } catch (Exception e) {
            addAuditEntry("Failed to extract inventory data from JSON: " + e.getMessage());
            throw new RuntimeException("JSON extraction failed", e);
        }
    }
    
    // JSON data extraction method for location inventory response
    public Map<String, Object> extractLocationInventoryFromJSON(Response response) {
        try {
            Map<String, Object> locationData = new LinkedHashMap<>();
            
            // Extract data based on the specified location API response format
            locationData.put("locationId", response.jsonPath().getString("locationId"));
            locationData.put("items", response.jsonPath().getList("items"));
            
            addAuditEntry("Extracted location inventory data for: " + locationData.get("locationId"));
            return locationData;
            
        } catch (Exception e) {
            addAuditEntry("Failed to extract location inventory data from JSON: " + e.getMessage());
            throw new RuntimeException("Location JSON extraction failed", e);
        }
    }
    
    public boolean validateInventoryResponse(Response response, String expectedSku, int expectedQuantity) {
        try {
            String actualSku = response.jsonPath().getString("itemCode");
            int actualQuantity = response.jsonPath().getInt("totalQuantity");
            
            boolean isValid = expectedSku.equals(actualSku) && expectedQuantity == actualQuantity;
            
            addAuditEntry("Inventory validation result: " + isValid + 
                         " (Expected: " + expectedSku + "/" + expectedQuantity + 
                         ", Actual: " + actualSku + "/" + actualQuantity + ")");
            
            return isValid;
            
        } catch (Exception e) {
            addAuditEntry("Inventory validation failed: " + e.getMessage());
            return false;
        }
    }
    
    // Audit trail operations
    public void addAuditEntry(String entry) {
        auditTrail.add(LocalDateTime.now() + ": " + entry);
    }
    
    public List<String> getAuditTrail() {
        return new ArrayList<>(auditTrail);
    }
    
    public List<String> getAuditTrailByKeyword(String keyword) {
        return auditTrail.stream()
                .filter(entry -> entry.toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    // Data export methods
    public Map<String, Object> exportAllData() {
        Map<String, Object> exportData = new LinkedHashMap<>();
        exportData.put("shipments", new LinkedHashMap<>(shipmentData));
        exportData.put("orders", new LinkedHashMap<>(orderData));
        exportData.put("lot_numbers", new LinkedHashMap<>(lotNumbers));
        exportData.put("pallet_assignments", new LinkedHashMap<>(palletAssignments));
        exportData.put("inventory_levels", new LinkedHashMap<>(inventoryLevels));
        exportData.put("audit_trail", new ArrayList<>(auditTrail));
        exportData.put("export_timestamp", LocalDateTime.now().toString());
        
        addAuditEntry("Data export completed");
        return exportData;
    }
    
    // Clear methods for test cleanup
    public void clearAllData() {
        shipmentData.clear();
        orderData.clear();
        lotNumbers.clear();
        palletAssignments.clear();
        inventoryLevels.clear();
        auditTrail.clear();
        processedItems.clear();
        processingQueue.clear();
        addAuditEntry("All data cleared");
    }
    
    public void clearShipmentData(String shipmentId) {
        shipmentData.remove(shipmentId);
        addAuditEntry("Shipment data cleared: " + shipmentId);
    }
    
    public void clearOrderData(String orderId) {
        orderData.remove(orderId);
        addAuditEntry("Order data cleared: " + orderId);
    }
    
    // Simple metadata operations
    public void addMetadata(String key, Object value) {
        storeShipmentData("metadata", key, value);
    }
    
    public Object getMetadata(String key) {
        return getShipmentData("metadata", key);
    }
    
    public String getMetadataAsString(String key) {
        return getShipmentDataAsString("metadata", key);
    }
}
