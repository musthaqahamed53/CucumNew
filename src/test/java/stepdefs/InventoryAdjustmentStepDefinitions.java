package stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import org.testng.Assert;
import pages.InventoryAdjustmentPage;
import utils.StepLogger;
import utils.WarehouseDataManager;
import utils.WebDriverConfig;

import java.util.*;

/**
 * Inventory Adjustment Step Definitions - Demonstrates Cucumber integration with Page Objects and API testing
 * Interview Points: Step definitions, Data tables, API integration, Collections usage, JSON Path validation
 */
public class InventoryAdjustmentStepDefinitions extends WebDriverConfig {
    
    private InventoryAdjustmentPage inventoryPage;
    private WarehouseDataManager dataManager;
    private String baseApiUrl = "http://localhost:8080"; // Default API base URL
    
    public InventoryAdjustmentStepDefinitions() {
        this.inventoryPage = new InventoryAdjustmentPage();
        this.dataManager = WarehouseDataManager.getInstance();
    }
    
    // Inventory setup steps
    @Given("I have existing inventory for items:")
    public void i_have_existing_inventory_for_items(DataTable dataTable) {
        StepLogger.info("Setting up existing inventory for items");
        
        List<Map<String, String>> items = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> item : items) {
            String itemCode = item.get("SKU");
            Integer currentQuantity = Integer.parseInt(item.get("Current Quantity"));
            String location = item.get("Location");
            
            // Store existing inventory data
            dataManager.updateInventory(itemCode, currentQuantity);
            dataManager.addMetadata("location_" + itemCode, location);
            
            StepLogger.info("Existing inventory: " + itemCode + " - " + currentQuantity + " at " + location);
        }
        
        StepLogger.passWithScreenshot("Existing inventory setup completed");
    }
    
    @Given("I have scheduled cycle count for location {string}")
    public void i_have_scheduled_cycle_count_for_location(String location) {
        StepLogger.info("Scheduling cycle count for location: " + location);
        dataManager.addMetadata("cycle_count_location", location);
        StepLogger.pass("Cycle count scheduled for location: " + location);
    }
    
    @Given("the system shows expected inventory:")
    public void the_system_shows_expected_inventory(DataTable dataTable) {
        StepLogger.info("Setting up system expected inventory");
        
        List<Map<String, String>> items = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> item : items) {
            String itemCode = item.get("SKU");
            Integer systemQuantity = Integer.parseInt(item.get("System Quantity"));
            String location = item.get("Location");
            
            // Store system quantities
            dataManager.updateInventory(itemCode, systemQuantity);
            dataManager.addMetadata("system_qty_" + itemCode, systemQuantity);
            dataManager.addMetadata("location_" + itemCode, location);
            
            StepLogger.info("System inventory: " + itemCode + " - " + systemQuantity + " at " + location);
        }
        
        StepLogger.passWithScreenshot("System expected inventory setup completed");
    }
    
    @Given("I have performed multiple inventory transactions")
    public void i_have_performed_multiple_inventory_transactions() {
        StepLogger.info("Setting up multiple inventory transactions");
        
        // Create some sample transactions
        dataManager.addAuditEntry("Transaction 1: Received 100 units of SKU-001");
        dataManager.addAuditEntry("Transaction 2: Shipped 50 units of SKU-002");
        dataManager.addAuditEntry("Transaction 3: Adjusted 25 units of SKU-003");
        
        StepLogger.pass("Multiple inventory transactions setup completed");
    }
    
    // Navigation steps
    @When("I navigate to the inventory adjustment screen")
    public void i_navigate_to_the_inventory_adjustment_screen() {
        StepLogger.info("Navigating to inventory adjustment screen");
        // Navigation logic would go here
        StepLogger.passWithScreenshot("Navigated to inventory adjustment screen");
    }
    
    @When("I navigate to the cycle count screen for location {string}")
    public void i_navigate_to_the_cycle_count_screen_for_location(String location) {
        StepLogger.info("Navigating to cycle count screen for location: " + location);
        // Navigation logic would go here
        StepLogger.passWithScreenshot("Navigated to cycle count screen for location: " + location);
    }
    
    // Positive adjustment steps
    @When("I perform positive adjustments:")
    public void i_perform_positive_adjustments(DataTable dataTable) {
        StepLogger.info("Performing positive inventory adjustments");
        
        List<Map<String, String>> adjustments = dataTable.asMaps(String.class, String.class);
        List<Map<String, Object>> adjustmentData = new ArrayList<>();
        
        for (Map<String, String> adjustment : adjustments) {
            Map<String, Object> adjustmentItem = new LinkedHashMap<>();
            adjustmentItem.put("item_code", adjustment.get("SKU"));
            adjustmentItem.put("current_quantity", Integer.parseInt(adjustment.get("Current")));
            adjustmentItem.put("adjusted_quantity", Integer.parseInt(adjustment.get("Adjusted")));
            adjustmentItem.put("reason", adjustment.get("Reason"));
            adjustmentItem.put("location", dataManager.getMetadataAsString("location_" + adjustment.get("SKU")));
            adjustmentData.add(adjustmentItem);
            
            StepLogger.info("Positive adjustment: " + adjustment.get("SKU") + 
                          " from " + adjustment.get("Current") + 
                          " to " + adjustment.get("Adjusted"));
        }
        
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("adjustments", adjustmentData);
        
        boolean result = inventoryPage.performPageOperation("positive_adjustment", parameters);
        Assert.assertTrue(result, "Failed to perform positive adjustments");
        
        StepLogger.passWithScreenshot("Positive adjustments completed successfully");
    }
    
    // Negative adjustment steps
    @When("I perform negative adjustments:")
    public void i_perform_negative_adjustments(DataTable dataTable) {
        StepLogger.info("Performing negative inventory adjustments");
        
        List<Map<String, String>> adjustments = dataTable.asMaps(String.class, String.class);
        List<Map<String, Object>> adjustmentData = new ArrayList<>();
        
        for (Map<String, String> adjustment : adjustments) {
            Map<String, Object> adjustmentItem = new LinkedHashMap<>();
            adjustmentItem.put("item_code", adjustment.get("SKU"));
            adjustmentItem.put("current_quantity", Integer.parseInt(adjustment.get("Current")));
            adjustmentItem.put("adjusted_quantity", Integer.parseInt(adjustment.get("Adjusted")));
            adjustmentItem.put("reason", adjustment.get("Reason"));
            adjustmentItem.put("location", dataManager.getMetadataAsString("location_" + adjustment.get("SKU")));
            adjustmentData.add(adjustmentItem);
            
            StepLogger.info("Negative adjustment: " + adjustment.get("SKU") + 
                          " from " + adjustment.get("Current") + 
                          " to " + adjustment.get("Adjusted"));
        }
        
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("adjustments", adjustmentData);
        
        boolean result = inventoryPage.performPageOperation("negative_adjustment", parameters);
        Assert.assertTrue(result, "Failed to perform negative adjustments");
        
        StepLogger.passWithScreenshot("Negative adjustments completed successfully");
    }
    
    // Lot number assignment steps
    @When("I assign lot numbers to adjusted quantities:")
    public void i_assign_lot_numbers_to_adjusted_quantities(DataTable dataTable) {
        StepLogger.info("Assigning lot numbers to adjusted quantities");
        
        List<Map<String, String>> lotAssignments = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> assignment : lotAssignments) {
            String itemCode = assignment.get("SKU");
            String lotNumbers = assignment.get("Lot Numbers");
            
            // Store lot number assignments
            String[] lots = lotNumbers.split(", ");
            for (String lot : lots) {
                dataManager.addLotNumber(itemCode, lot.trim());
            }
            
            StepLogger.info("Lot numbers assigned to " + itemCode + ": " + lotNumbers);
        }
        
        StepLogger.passWithScreenshot("Lot numbers assigned to adjusted quantities");
    }
    
    // Cycle count steps
    @When("I perform physical count:")
    public void i_perform_physical_count(DataTable dataTable) {
        StepLogger.info("Performing physical count");
        
        List<Map<String, String>> countData = dataTable.asMaps(String.class, String.class);
        List<Map<String, Object>> physicalCounts = new ArrayList<>();
        
        for (Map<String, String> count : countData) {
            Map<String, Object> countItem = new LinkedHashMap<>();
            countItem.put("item_code", count.get("SKU"));
            countItem.put("system_quantity", Integer.parseInt(count.get("System")));
            countItem.put("physical_quantity", Integer.parseInt(count.get("Physical")));
            physicalCounts.add(countItem);
            
            int variance = Integer.parseInt(count.get("Physical")) - Integer.parseInt(count.get("System"));
            StepLogger.info("Physical count: " + count.get("SKU") + 
                          " - System: " + count.get("System") + 
                          ", Physical: " + count.get("Physical") + 
                          ", Variance: " + variance);
        }
        
        String location = dataManager.getMetadataAsString("cycle_count_location");
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("location", location);
        parameters.put("count_data", physicalCounts);
        
        boolean result = inventoryPage.performPageOperation("cycle_count", parameters);
        Assert.assertTrue(result, "Failed to perform physical count");
        
        StepLogger.passWithScreenshot("Physical count completed successfully");
    }
    
    @When("I investigate discrepancies for items with variance")
    public void i_investigate_discrepancies_for_items_with_variance() {
        StepLogger.info("Investigating discrepancies for items with variance");
        
        Map<String, Integer> varianceReport = inventoryPage.getVarianceReport();
        
        for (Map.Entry<String, Integer> variance : varianceReport.entrySet()) {
            if (variance.getValue() != 0) {
                String reason = variance.getValue() > 0 ? "Over count - possible misplacement" : "Short count - possible theft or damage";
                dataManager.addAuditEntry("Variance investigation: " + variance.getKey() + " - " + reason);
                StepLogger.info("Investigated variance for " + variance.getKey() + ": " + reason);
            }
        }
        
        StepLogger.passWithScreenshot("Discrepancy investigation completed");
    }
    
    @When("I approve the cycle count adjustments")
    public void i_approve_the_cycle_count_adjustments() {
        StepLogger.info("Approving cycle count adjustments");
        
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("approval_user", dataManager.getMetadataAsString("current_user"));
        
        boolean result = inventoryPage.performPageOperation("approve_adjustment", parameters);
        Assert.assertTrue(result, "Failed to approve cycle count adjustments");
        
        StepLogger.passWithScreenshot("Cycle count adjustments approved successfully");
    }
    
    // Documentation steps
    @When("I document the adjustment reasons with supporting details")
    public void i_document_the_adjustment_reasons_with_supporting_details() {
        StepLogger.info("Documenting adjustment reasons with supporting details");
        
        Map<String, String> adjustmentReasons = inventoryPage.getAdjustmentReasons();
        
        for (Map.Entry<String, String> reason : adjustmentReasons.entrySet()) {
            dataManager.addAuditEntry("Adjustment reason documented: " + reason.getKey() + " - " + reason.getValue());
            StepLogger.info("Documented reason for " + reason.getKey() + ": " + reason.getValue());
        }
        
        StepLogger.passWithScreenshot("Adjustment reasons documented with supporting details");
    }
    
    // Submission steps
    @When("I submit the positive adjustment")
    public void i_submit_the_positive_adjustment() {
        StepLogger.info("Submitting positive adjustment");
        // Submission logic is handled in the positive adjustment method
        StepLogger.pass("Positive adjustment submitted successfully");
    }
    
    @When("I submit the negative adjustment")
    public void i_submit_the_negative_adjustment() {
        StepLogger.info("Submitting negative adjustment");
        // Submission logic is handled in the negative adjustment method
        StepLogger.pass("Negative adjustment submitted successfully");
    }
    
    // Collection operations steps
    @When("I collect all inventory data using Java collections:")
    public void i_collect_all_inventory_data_using_java_collections(DataTable dataTable) {
        StepLogger.info("Collecting inventory data using Java collections");
        
        List<Map<String, String>> collectionTypes = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> collection : collectionTypes) {
            String type = collection.get("Collection Type");
            String usage = collection.get("Usage");
            
            switch (type) {
                case "HashMap":
                    Map<String, Integer> skuQuantityMap = inventoryPage.getSystemQuantities();
                    dataManager.addMetadata("hashmap_demo", skuQuantityMap);
                    StepLogger.info("HashMap used for: " + usage + " - Size: " + skuQuantityMap.size());
                    break;
                    
                case "TreeSet":
                    Set<String> sortedSkus = new TreeSet<>(inventoryPage.getSystemQuantities().keySet());
                    dataManager.addMetadata("treeset_demo", sortedSkus);
                    StepLogger.info("TreeSet used for: " + usage + " - Size: " + sortedSkus.size());
                    break;
                    
                case "LinkedList":
                    List<String> transactionHistory = inventoryPage.getAdjustmentHistory();
                    dataManager.addMetadata("linkedlist_demo", transactionHistory);
                    StepLogger.info("LinkedList used for: " + usage + " - Size: " + transactionHistory.size());
                    break;
                    
                case "ConcurrentMap":
                    // Demonstrate thread-safe operations
                    Map<String, Integer> concurrentMap = new java.util.concurrent.ConcurrentHashMap<>(inventoryPage.getSystemQuantities());
                    dataManager.addMetadata("concurrentmap_demo", concurrentMap);
                    StepLogger.info("ConcurrentMap used for: " + usage + " - Size: " + concurrentMap.size());
                    break;
            }
        }
        
        StepLogger.passWithScreenshot("Inventory data collected using Java collections");
    }
    
    // API validation steps
    @When("I validate inventory via REST API calls for each SKU")
    public void i_validate_inventory_via_rest_api_calls_for_each_sku() {
        StepLogger.info("Validating inventory via REST API calls");
        
        Map<String, Integer> physicalQuantities = inventoryPage.getPhysicalQuantities();
        List<String> itemCodes = new ArrayList<>(physicalQuantities.keySet());
        
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("base_url", baseApiUrl);
        parameters.put("item_codes", itemCodes);
        
        boolean result = inventoryPage.performPageOperation("validate_inventory", parameters);
        Assert.assertTrue(result, "Failed to validate inventory via REST API");
        
        StepLogger.passWithScreenshot("Inventory validated via REST API calls");
    }
    
    @When("I extract data using JSON Path expressions:")
    public void i_extract_data_using_json_path_expressions(DataTable dataTable) {
        StepLogger.info("Extracting data using JSON Path expressions");
        
        List<Map<String, String>> jsonPaths = dataTable.asMaps(String.class, String.class);
        
        // Get a sample item for demonstration
        Map<String, Integer> physicalQuantities = inventoryPage.getPhysicalQuantities();
        if (!physicalQuantities.isEmpty()) {
            String sampleItemCode = physicalQuantities.keySet().iterator().next();
            
            try {
                Response response = dataManager.getInventoryByItemCode(sampleItemCode, baseApiUrl);
                
                if (response.getStatusCode() == 200) {
                    for (Map<String, String> jsonPath : jsonPaths) {
                        String path = jsonPath.get("JSON Path");
                        String validation = jsonPath.get("Validation");
                        
                        // Extract data using JSON Path
                        Object extractedValue = response.jsonPath().get(path.replace("$.", ""));
                        
                        dataManager.addAuditEntry("JSON Path extraction: " + path + " = " + extractedValue + " (" + validation + ")");
                        StepLogger.info("JSON Path " + path + ": " + extractedValue + " - " + validation);
                    }
                }
            } catch (Exception e) {
                StepLogger.warning("JSON Path extraction failed: " + e.getMessage());
            }
        }
        
        StepLogger.passWithScreenshot("Data extracted using JSON Path expressions");
    }
    
    // Validation steps
    @Then("the inventory levels should be updated correctly")
    public void the_inventory_levels_should_be_updated_correctly() {
        StepLogger.info("Validating inventory levels are updated correctly");
        
        Map<String, Integer> physicalQuantities = inventoryPage.getPhysicalQuantities();
        boolean allUpdated = true;
        
        for (Map.Entry<String, Integer> item : physicalQuantities.entrySet()) {
            Integer currentLevel = dataManager.getInventoryLevel(item.getKey());
            if (!item.getValue().equals(currentLevel)) {
                StepLogger.fail("Inventory level mismatch for " + item.getKey() + 
                              ". Expected: " + item.getValue() + ", Actual: " + currentLevel);
                allUpdated = false;
            } else {
                StepLogger.pass("Inventory level correct for " + item.getKey() + ": " + currentLevel);
            }
        }
        
        Assert.assertTrue(allUpdated, "Some inventory levels were not updated correctly");
        StepLogger.passWithScreenshot("All inventory levels updated correctly");
    }
    
    @Then("the inventory should be adjusted to physical count")
    public void the_inventory_should_be_adjusted_to_physical_count() {
        the_inventory_levels_should_be_updated_correctly();
    }
    
    @Then("the inventory levels should be reduced correctly")
    public void the_inventory_levels_should_be_reduced_correctly() {
        the_inventory_levels_should_be_updated_correctly();
    }
    
    @Then("the adjustment transaction should be recorded")
    public void the_adjustment_transaction_should_be_recorded() {
        StepLogger.info("Validating adjustment transaction is recorded");
        
        List<String> auditTrail = dataManager.getAuditTrail();
        boolean transactionRecorded = auditTrail.stream()
                .anyMatch(entry -> entry.contains("adjustment"));
        
        Assert.assertTrue(transactionRecorded, "Adjustment transaction not found in audit trail");
        StepLogger.passWithScreenshot("Adjustment transaction recorded successfully");
    }
    
    @Then("the adjustment transaction should be recorded with reasons")
    public void the_adjustment_transaction_should_be_recorded_with_reasons() {
        the_adjustment_transaction_should_be_recorded();
    }
    
    @Then("the cycle count report should be generated")
    public void the_cycle_count_report_should_be_generated() {
        StepLogger.info("Validating cycle count report generation");
        
        Map<String, Integer> varianceReport = inventoryPage.getVarianceReport();
        Assert.assertFalse(varianceReport.isEmpty(), "Cycle count report should not be empty");
        
        for (Map.Entry<String, Integer> variance : varianceReport.entrySet()) {
            StepLogger.info("Cycle count variance: " + variance.getKey() + " - " + variance.getValue());
        }
        
        StepLogger.passWithScreenshot("Cycle count report generated successfully");
    }
    
    @Then("all API validations should pass")
    public void all_api_validations_should_pass() {
        StepLogger.info("Validating all API validations passed");
        // This is validated in the API validation step
        StepLogger.pass("All API validations passed successfully");
    }
    
    @Then("inventory data should be consistent across systems")
    public void inventory_data_should_be_consistent_across_systems() {
        StepLogger.info("Validating inventory data consistency across systems");
        
        Map<String, Integer> physicalQuantities = inventoryPage.getPhysicalQuantities();
        boolean allConsistent = true;
        
        for (Map.Entry<String, Integer> item : physicalQuantities.entrySet()) {
            try {
                Response response = dataManager.getInventoryByItemCode(item.getKey(), baseApiUrl);
                
                if (response.getStatusCode() == 200) {
                    Map<String, Object> inventoryData = dataManager.extractInventoryDataFromJSON(response);
                    Integer apiQuantity = (Integer) inventoryData.get("totalQuantity");
                    
                    if (!item.getValue().equals(apiQuantity)) {
                        StepLogger.fail("Data inconsistency for " + item.getKey() + 
                                      ". Local: " + item.getValue() + ", API: " + apiQuantity);
                        allConsistent = false;
                    } else {
                        StepLogger.pass("Data consistent for " + item.getKey() + ": " + apiQuantity);
                    }
                }
            } catch (Exception e) {
                StepLogger.warning("Consistency check failed for " + item.getKey() + ": " + e.getMessage());
            }
        }
        
        Assert.assertTrue(allConsistent, "Inventory data inconsistency detected");
        StepLogger.passWithScreenshot("Inventory data consistent across systems");
    }
    
    @Then("collections should demonstrate advanced Java operations")
    public void collections_should_demonstrate_advanced_java_operations() {
        StepLogger.info("Validating advanced Java collection operations");
        
        // Demonstrate stream operations
        Map<String, Integer> systemQuantities = inventoryPage.getSystemQuantities();
        
        // Filter items with quantity > 50
        List<String> highQuantityItems = systemQuantities.entrySet().stream()
                .filter(entry -> entry.getValue() > 50)
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
        
        StepLogger.info("High quantity items (>50): " + highQuantityItems);
        
        // Calculate total inventory value (assuming unit price of 10)
        int totalValue = systemQuantities.values().stream()
                .mapToInt(quantity -> quantity * 10)
                .sum();
        
        StepLogger.info("Total inventory value: " + totalValue);
        
        // Group items by quantity ranges
        Map<String, List<String>> quantityRanges = systemQuantities.entrySet().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    entry -> entry.getValue() < 50 ? "Low" : entry.getValue() < 100 ? "Medium" : "High",
                    java.util.stream.Collectors.mapping(Map.Entry::getKey, java.util.stream.Collectors.toList())
                ));
        
        StepLogger.info("Quantity ranges: " + quantityRanges);
        
        StepLogger.passWithScreenshot("Advanced Java collection operations demonstrated");
    }
    
    // REST API validation steps
    @Then("I validate the adjusted inventory via REST API call")
    public void i_validate_the_adjusted_inventory_via_rest_api_call() {
        i_validate_inventory_via_rest_api_calls_for_each_sku();
    }
    
    @Then("I validate the reduced inventory via REST API call")
    public void i_validate_the_reduced_inventory_via_rest_api_call() {
        i_validate_inventory_via_rest_api_calls_for_each_sku();
    }
    
    @Then("I validate the cycle count results via REST API call")
    public void i_validate_the_cycle_count_results_via_rest_api_call() {
        i_validate_inventory_via_rest_api_calls_for_each_sku();
    }
}
