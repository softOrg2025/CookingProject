Feature: System generates automatic purchase orders

  Scenario: Create PO when critical low
    Given an ingredient reaches critical stock
    When the system detects a critical stock level
    Then a purchase order should be generated automatically

  Scenario: Verify details for a known Purchase Order
    Given a purchase order for "PremiumCoffee" has been created with quantity 75, supplier "Beans R Us", and unit price 12.50
    When I view the details for the "PremiumCoffee" purchase order
    Then the purchase order details should show:
      | Field          | Value              |
      | IngredientName | PremiumCoffee      |
      | Quantity       | 75                 |
      | SupplierName   | Beans R Us         |
      | UnitPrice      | 12.50              |

  Scenario: Send PO to supplier
    Given the PO is ready
    When I approve it
    Then the system should send it to the respective supplier

