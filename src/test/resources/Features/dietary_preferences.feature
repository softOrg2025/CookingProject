Feature: Customer Dietary Preferences Access
  As a chef
  I want to view customer dietary preferences and allergies
  So I can ensure meals meet their requirements

  Scenario: View basic dietary restrictions for a customer
    Given I am logged in as the head chef
    When I access the profile of regular customer "Emma Wilson"
    Then I should see their dietary preferences listed
    And I should see their food allergies clearly highlighted

  Scenario: Alert when preparing meal with allergen
    Given I am preparing a seafood pasta dish
    When I check the profile of customer "David Lee" who has a shellfish allergy
    Then the system should display a prominent warning
    And the system should suggest alternative ingredients to use


  Scenario: Filter meal options by dietary preference
    Given customer "Maria Garcia" has a "Vegan" dietary preference
    When I search for suitable meal options
    Then the system should only show vegan-compliant dishes
    And automatically exclude any containing animal products
