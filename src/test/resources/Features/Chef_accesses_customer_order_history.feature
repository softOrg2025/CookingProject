Feature: Chef accesses and uses customer order history

  Scenario: View customer's order timeline
    Given the chef is logged into the system
    When the chef selects a customer profile
    Then the system should display the customer's order history

  Scenario: Recommend based on frequently ordered meals
    Given the chef is viewing a customer's order history
    When the chef identifies frequently ordered meals
    Then the chef should suggest a personalized meal plan

  Scenario: Adjust future suggestions based on meal patterns
    Given the chef is viewing a customer's order history
    When the chef notices a pattern in meal choices
    Then the chef should adjust future meal suggestions accordingly

#    DONE