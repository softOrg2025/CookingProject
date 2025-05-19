@PastMealOrdersFeature
Feature: View Past Meal Orders
  As a registered customer
  I want to view my past meal orders
  So that I can reorder meals I liked

  Scenario: View list of past orders
    Given I am logged in with existing order history
    When I navigate to my order history page
    Then I should see a chronological list of my past orders
    And each order should display the order date, total price, and reorder button

  Scenario: Reorder a previous meal
    Given I am logged in with existing order history
    And I am viewing my order history
    When I click "Reorder" on an order containing "Vegetable Curry"
    Then "Vegetable Curry" should be added to my current cart
    And I should see a confirmation message "Meal added to cart!"

  Scenario: Customer filters past orders by date
    Given I am logged in with existing order history
    And the customer is viewing past orders
    When the customer filters orders by a specific date range
    Then the system should display only orders within that range
    And the orders should be sorted from newest to oldest
    And if no orders match, a message "No orders found in this range." should be shown