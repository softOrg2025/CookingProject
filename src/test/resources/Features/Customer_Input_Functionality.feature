Feature: Allow customers to create custom meal requests

  As a customer,
  I want to input my dietary preferences and allergies so that the system
  can recommend appropriate meals and prevent unwanted ingredients.

  Scenario: Customer adds dietary preferences
    Given the customer is logged into the system
    When the customer selects "Dietary Preferences" from the profile menu
    And the customer inputs "Vegan" as their dietary preference
    Then the system should save the preference
    And the system should display a confirmation message

  Scenario: Customer adds allergies
    Given the customer is logged into the system
    When the customer selects "Allergies" from the profile menu
    And the customer inputs "Peanuts" as an allergy
    Then the system should save the allergy information
    And the system should display a confirmation message

  Scenario: System prevents incompatible meal suggestions
    Given the customer has "Peanuts" listed as an allergy
    When the system suggests meals
    Then the system should exclude any meals containing "Peanuts"