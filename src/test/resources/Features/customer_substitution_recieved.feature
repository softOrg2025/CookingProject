Feature: Customer receives substitution suggestions

  Scenario: Customer views substitution suggestions
    Given the customer has selected an incompatible ingredient
    When the system suggests alternatives
    Then the system should display the suggestions to the customer

  Scenario: Customer accepts substitution
    Given the customer is viewing substitution suggestions
    When the customer accepts a suggestion
    Then the system should update the meal recipe

  Scenario: Customer rejects substitution
    Given the customer is viewing substitution suggestions
    When the customer rejects a suggestion
    Then the system should notify the chef