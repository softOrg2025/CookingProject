Feature: View Past Meal Orders
  As a registered customer
  I want to view my past meal orders
  So that I can reorder meals I liked

  Scenario: View list of past orders
    Given I am logged in with existing order history
    When I navigate to my order history page
    Then I should see a chronological list of my past orders
    And each order should display:
      | Order date |
      | Meal images |
      | Total price |
      | Reorder button |

  Scenario: Reorder a previous meal
    Given I am viewing my order history
    When I click "Reorder" on an order containing "Vegetable Curry"
    Then "Vegetable Curry" should be added to my current cart
    And I should see a confirmation message "Meal added to cart!"

  Scenario: No orders placeholder for new customers
    Given I am logged in as a new customer with no order history
    When I navigate to my order history page
    Then I should see the message "No past orders found"
    And I should see a "Browse Menu" call-to-action button