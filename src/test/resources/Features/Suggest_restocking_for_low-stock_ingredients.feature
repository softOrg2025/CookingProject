Feature: Suggest restocking for low-stock ingredients

  Scenario: Notify when stock is low
    Given an ingredient is below the threshold
    When I view the inventory
    Then the system should suggest restocking

  Scenario: Suggest quantities for restocking
    Given multiple items are low
    When I open the restock suggestions
    Then I should see suggested quantities based on usage rate

  Scenario: Allow manager to approve restock
    Given I see restock suggestions
    When I review them
    Then I can approve or reject the restocking plan