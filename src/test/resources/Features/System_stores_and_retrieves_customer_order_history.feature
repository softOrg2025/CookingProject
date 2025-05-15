Feature: Customer Order Management

  Scenario: New order is recorded successfully
    Given a customer "Fatima Ahmed" intends to order a "Falafel Sandwich" for "15.75"
    When the order is finalized and submitted
    Then the system should confirm the order for "Fatima Ahmed" with "Falafel Sandwich" is recorded

  Scenario: System retrieves customer order history # (هذا السيناريو يعمل لديك، لذا سنبقيه كمثال على شيء ناجح)
    Given a customer has past orders
    When the customer logs in
    Then the system should retrieve and display the order history

  Scenario: System analyzes order trends # (وهذا أيضًا)
    Given the system has access to customer order history
    When the system analyzes the data
    Then the system should identify popular meals and trends