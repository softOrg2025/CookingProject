Feature: Chef approves ingredient substitutions

  Scenario: Chef receives substitution notification
    Given the system has suggested an alternative ingredient
    When the chef logs in
    Then the system should display the substitution notification

  Scenario: Chef approves substitution
    Given the chef is viewing a substitution notification
    When the chef approves the substitution
    Then the system should update the meal recipe

  Scenario: Chef rejects substitution
    Given the chef is viewing a substitution notification
    When the chef rejects the substitution
    Then the system should notify the customer