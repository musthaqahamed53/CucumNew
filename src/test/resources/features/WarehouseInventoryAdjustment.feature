@WarehouseManagement @InventoryAdjustment
Feature: Warehouse Inventory Adjustment Operations
  As a warehouse operator
  I want to manage inventory adjustments and cycle counts
  So that I can maintain accurate inventory levels and handle discrepancies

  Background:
    Given the warehouse management system is available
    And I am logged into the warehouse system as "warehouse_operator"

  @Smoke @PositiveAdjustment @Regression
  Scenario: Positive Inventory Adjustment Process
    Given I have existing inventory for items:
      | SKU     | Current Quantity | Location |
      | SKU-ADJ-001 | 100         | A-01-01  |
      | SKU-ADJ-002 | 50          | A-01-02  |
    When I navigate to the inventory adjustment screen
    And I perform positive adjustments:
      | SKU         | Current | Adjusted | Difference | Reason           |
      | SKU-ADJ-001 | 100     | 115      | +15        | Found additional |
      | SKU-ADJ-002 | 50      | 60       | +10        | Miscount         |
    And I assign lot numbers to adjusted quantities:
      | SKU         | Lot Numbers |
      | SKU-ADJ-001 | LOT-ADJ-001 |
      | SKU-ADJ-002 | LOT-ADJ-002 |
    And I submit the positive adjustment
    Then the inventory levels should be updated correctly
    And the adjustment transaction should be recorded
    And I validate the adjusted inventory via REST API call

  @Smoke @NegativeAdjustment @Regression
  Scenario: Negative Inventory Adjustment Process
    Given I have existing inventory for items:
      | SKU     | Current Quantity | Location |
      | SKU-ADJ-101 | 200         | B-01-01  |
      | SKU-ADJ-102 | 150         | B-01-02  |
    When I navigate to the inventory adjustment screen
    And I perform negative adjustments:
      | SKU         | Current | Adjusted | Difference | Reason    |
      | SKU-ADJ-101 | 200     | 180      | -20        | Damaged   |
      | SKU-ADJ-102 | 150     | 140      | -10        | Expired   |
    And I document the adjustment reasons with supporting details
    And I submit the negative adjustment
    Then the inventory levels should be reduced correctly
    And the adjustment transaction should be recorded with reasons
    And I validate the reduced inventory via REST API call

  @Smoke @CycleCount @Regression
  Scenario: Cycle Count Process with Discrepancy Resolution
    Given I have scheduled cycle count for location "C-01"
    And the system shows expected inventory:
      | SKU     | System Quantity | Location |
      | SKU-CC-001 | 75           | C-01-01  |
      | SKU-CC-002 | 40           | C-01-02  |
      | SKU-CC-003 | 25           | C-01-03  |
    When I navigate to the cycle count screen for location "C-01"
    And I perform physical count:
      | SKU        | System | Physical | Variance |
      | SKU-CC-001 | 75     | 73       | -2       |
      | SKU-CC-002 | 40     | 42       | +2       |
      | SKU-CC-003 | 25     | 25       | 0        |
    And I investigate discrepancies for items with variance
    And I approve the cycle count adjustments
    Then the inventory should be adjusted to physical count
    And the cycle count report should be generated
    And I validate the cycle count results via REST API call

  @APITesting @DataValidation @Collections
  Scenario: Inventory Validation with Advanced Collections and REST API
    Given I have performed multiple inventory transactions
    When I collect all inventory data using Java collections:
      | Collection Type | Usage                           |
      | HashMap        | SKU to current quantity mapping |
      | TreeSet        | Sorted list of adjusted SKUs    |
      | LinkedList     | Transaction history queue       |
      | ConcurrentMap  | Thread-safe inventory updates   |
    And I validate inventory via REST API calls for each SKU
    And I extract data using JSON Path expressions:
      | JSON Path              | Validation               |
      | $.data.sku            | Matches expected SKU     |
      | $.data.quantity       | Matches adjusted quantity |
      | $.data.location       | Matches assigned location |
      | $.data.lot_numbers[*] | Contains expected lots   |
      | $.data.last_updated   | Recent timestamp         |
    Then all API validations should pass
    And inventory data should be consistent across systems
    And collections should demonstrate advanced Java operations
