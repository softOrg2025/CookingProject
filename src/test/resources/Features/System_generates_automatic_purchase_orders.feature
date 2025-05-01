Feature: System generates automatic purchase orders
  Scenario: Create PO when critical low
    Given an ingredient reaches critical stock
    When the system detects it
    Then a purchase order should be generated automatically

  Scenario: Include price and supplier info in PO
    Given a PO is created
    When I view the details
    Then it should include ingredient name, quantity, supplier, and price

  Scenario: Send PO to supplier
    Given the PO is ready
    When I approve it
    Then the system should send it to the respective supplier
