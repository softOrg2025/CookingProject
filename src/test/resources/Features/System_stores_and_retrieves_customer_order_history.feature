Feature: System stores and retrieves customer order history

  Scenario: System saves customer order history
    Given a customer places an order
    When the order is completed
    Then the system should save the order details in the database

  Scenario: System retrieves customer order history
    Given a customer has past orders
    When the customer logs in
    Then the system should retrieve and display the order history

  Scenario: System analyzes order trends
    Given the system has access to customer order history
    When the system analyzes the data
    Then the system should identify popular meals and trends