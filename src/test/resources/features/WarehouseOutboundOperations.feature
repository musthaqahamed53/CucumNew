@WarehouseManagement @OutboundOperations
Feature: Warehouse Outbound Operations Management
  As a warehouse operator
  I want to manage complete outbound order processes
  So that I can efficiently handle picking, shipping, and order fulfillment

  Background:
    Given the warehouse management system is available
    And I am logged into the warehouse system as "warehouse_operator"

  @Smoke @FullShipping @Regression
  Scenario: Complete Full Shipping Process with 945 EDI Generation
    Given I create a new outbound order "ORD-001" with items:
      | SKU     | Quantity | Description   |
      | SKU-001 | 80       | Widget A      |
      | SKU-002 | 40       | Widget B      |
      | SKU-003 | 20       | Widget C      |
    When I navigate to the order change screen for order "ORD-001"
    And I create appointment "APT-OUT-001" for the order
    And I assign pool number "POOL-001" to the order
    And I print the order by pool number "POOL-001"
    And I complete manual pick with pallet numbers:
      | SKU     | Pallet ID | Quantity |
      | SKU-001 | PLT-OUT-001 | 80     |
      | SKU-002 | PLT-OUT-002 | 40     |
      | SKU-003 | PLT-OUT-002 | 20     |
    And I close appointment "APT-OUT-001"
    And I confirm the order for 945 generation
    Then the order status should be "SHIPPED"
    And the 945 EDI should be generated successfully
    And I validate the shipped inventory via REST API call

  @Smoke @ShortShipping @Regression
  Scenario: Short Shipping Process with Allocation Issues
    Given I create a new outbound order "ORD-002" with items:
      | SKU     | Quantity |
      | SKU-101 | 60       |
      | SKU-102 | 35       |
    When I navigate to the order change screen for order "ORD-002"
    And I create appointment "APT-OUT-002" for the order
    And I assign pool number "POOL-002" to the order
    And I attempt to pick items but encounter shortages:
      | SKU     | Ordered | Available | Picked |
      | SKU-101 | 60      | 45        | 45     |
      | SKU-102 | 35      | 30        | 30     |
    And I document shortage reasons for short picked items
    And I complete pick with available quantities
    Then the order status should be "SHORT_PICKED"
    And the shortage report should be generated
    And I validate the short shipped inventory via API

  @Smoke @BatchSplitting @Regression
  Scenario: Batch Splitting Process for Large Orders
    Given I create a new outbound order "ORD-003" with large quantities:
      | SKU     | Quantity |
      | SKU-201 | 500      |
      | SKU-202 | 300      |
    When I navigate to the order change screen for order "ORD-003"
    And I determine that batch splitting is required
    And I split the order into batches:
      | Batch ID | SKU     | Quantity |
      | BATCH-001 | SKU-201 | 250     |
      | BATCH-002 | SKU-201 | 250     |
      | BATCH-003 | SKU-202 | 150     |
      | BATCH-004 | SKU-202 | 150     |
    And I assign different pool numbers to each batch
    And I process each batch separately
    Then the order status should be "BATCH_SPLIT"
    And all batches should be tracked individually
    And I validate batch inventory movements via API

  @Smoke @PalletSplitting @Regression
  Scenario: Pallet Splitting Process for Mixed Items
    Given I create a new outbound order "ORD-004" with mixed items:
      | SKU     | Quantity |
      | SKU-301 | 100      |
      | SKU-302 | 80       |
      | SKU-303 | 60       |
    When I navigate to the order change screen for order "ORD-004"
    And I assign initial pallet "PLT-MIXED-001" for all items
    And I determine that pallet splitting is required due to weight limits
    And I split the pallet into multiple pallets:
      | Original Pallet | New Pallet | SKUs        | Quantities |
      | PLT-MIXED-001  | PLT-SPLIT-001 | SKU-301   | 100        |
      | PLT-MIXED-001  | PLT-SPLIT-002 | SKU-302   | 80         |
      | PLT-MIXED-001  | PLT-SPLIT-003 | SKU-303   | 60         |
    And I update pallet assignments for each SKU
    Then the order status should be "PALLET_SPLIT"
    And each pallet should be tracked separately
    And I validate pallet split inventory via API

  @Smoke @MixedPallet @Regression
  Scenario: Mixed Item Pallet Management
    Given I create a new outbound order "ORD-005" with compatible items:
      | SKU     | Quantity | Weight | Compatibility |
      | SKU-401 | 50       | Light  | Compatible    |
      | SKU-402 | 30       | Light  | Compatible    |
      | SKU-403 | 25       | Light  | Compatible    |
    When I navigate to the order change screen for order "ORD-005"
    And I create a mixed pallet "PLT-MIXED-002" with all items:
      | Pallet ID | SKUs                    |
      | PLT-MIXED-002 | SKU-401, SKU-402, SKU-403 |
    And I verify item compatibility on the mixed pallet
    And I complete picking for the mixed pallet
    Then the mixed pallet should be created successfully
    And all items should be tracked on the same pallet
    And I validate mixed pallet inventory via API

  @Smoke @OrderRevision @Regression
  Scenario: Order Revision Process with Customer Changes
    Given I create a new outbound order "ORD-006" with original items:
      | SKU     | Quantity |
      | SKU-501 | 100      |
      | SKU-502 | 75       |
    When I navigate to the order change screen for order "ORD-006"
    And I receive a revision request to change quantities:
      | SKU     | Original | Revised | Change |
      | SKU-501 | 100      | 120     | +20    |
      | SKU-502 | 75       | 60      | -15    |
    And I process the order revision with reason "Customer request"
    And I update inventory allocations for revised quantities
    And I complete picking with revised quantities
    Then the order status should show revision history
    And the revised quantities should be reflected in the order
    And I validate revised inventory via API
