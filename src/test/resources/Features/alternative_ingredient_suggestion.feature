Feature: System suggests alternative ingredients

  Scenario: System suggests alternatives for unavailable ingredients
    Given the customer has selected an unavailable ingredient
    When the system identifies the issue
    Then the system should suggest alternative ingredients

  Scenario: System suggests alternatives for dietary restrictions
    Given the customer has a dietary restriction
    When the customer selects an incompatible ingredient
    Then the system should suggest alternative ingredients

  Scenario: System notifies chef of substitutions
    Given the system has suggested an alternative ingredient
    When the substitution is applied
    Then the system should notify the chef