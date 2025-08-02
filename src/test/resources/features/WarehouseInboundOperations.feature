@WarehouseManagement @InboundOperations
Feature: Warehouse Inbound Operations Management
  As a warehouse operator
  I want to manage complete inbound shipment processes
  So that I can efficiently handle receiving, appointments, and documentation

  Background:
    Given the warehouse management system is available
    And I am logged into the warehouse system as "warehouse_operator"

  @Smoke @FullReceiving @Regression
  Scenario: Complete Full Receiving Process with 944 EDI Generation
    Given I create a new inbound shipment "SHP-001" with expected items:
      | SKU     | Quantity | Description   |
      | SKU-001 | 100      | Widget A      |
      | SKU-002 | 50       | Widget B      |
      | SKU-003 | 25       | Widget C      |
    When I navigate to the pre-receiving change screen for shipment "SHP-001"
    And I create appointment "APT-001" for the shipment
    And I assign lot numbers to received items:
      | SKU     | Lot Numbers        |
      | SKU-001 | LOT-001, LOT-002  |
      | SKU-002 | LOT-003           |
      | SKU-003 | LOT-004, LOT-005  |
    And I assign storage locations for items:
      | SKU     | Location |
      | SKU-001 | A-01-01  |
      | SKU-002 | A-01-02  |
      | SKU-003 | A-01-03  |
    And I check pallets with customer sent pallet as full receiving:
      | Pallet ID | SKUs              | Status |
      | PLT-001   | SKU-001          | Full   |
      | PLT-002   | SKU-002, SKU-003 | Full   |
    And I complete the receiving process for all items
    And I create receipt "RCP-001" for the shipment
    And I close appointment "APT-001"
    Then the receipt should be printed successfully
    And the 944 EDI should be generated and ready to send
    And the shipment status should be "RECEIVED"
    And I validate inventory levels via REST API call

  @Smoke @OverReceiving @Regression
  Scenario: Over Receiving Process with Variance Management
    Given I create a new inbound shipment "SHP-002" with expected items:
      | SKU     | Quantity |
      | SKU-101 | 75       |
      | SKU-102 | 40       |
    When I navigate to the pre-receiving change screen for shipment "SHP-002"
    And I create appointment "APT-002" for the shipment
    And I receive items with over quantities:
      | SKU     | Expected | Received | Variance |
      | SKU-101 | 75       | 85       | +10      |
      | SKU-102 | 40       | 45       | +5       |
    And I handle the over receiving variance for each item
    And I assign lot numbers for over received quantities
    And I create receipt for over received items
    Then the shipment status should be "OVER_RECEIVED"
    And the variance report should show over received quantities
    And I validate the over received inventory via API

  @Smoke @ShortReceiving @Regression  
  Scenario: Short Receiving Process with Documentation
    Given I create a new inbound shipment "SHP-003" with expected items:
      | SKU     | Quantity |
      | SKU-201 | 60       |
      | SKU-202 | 30       |
    When I navigate to the pre-receiving change screen for shipment "SHP-003"
    And I create appointment "APT-003" for the shipment
    And I receive items with short quantities:
      | SKU     | Expected | Received | Shortage |
      | SKU-201 | 60       | 45       | 15       |
      | SKU-202 | 30       | 25       | 5        |
    And I document shortage reasons:
      | SKU     | Reason              |
      | SKU-201 | Damaged in transit  |
      | SKU-202 | Short shipped       |
    And I create receipt for short received items
    Then the shipment status should be "SHORT_RECEIVED"
    And the shortage report should be generated
    And I validate the short received inventory via API
