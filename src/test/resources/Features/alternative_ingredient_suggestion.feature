Feature: System suggests alternative ingredients and handles substitutions

  Scenario: System suggests alternatives for unavailable ingredients
    Given the customer has selected "Avocado" which is unavailable
    When the system identifies the unavailability
    Then the system should suggest "Guacamole" as an alternative for "Avocado"

  Scenario: System suggests alternatives for ingredients conflicting with dietary restrictions
    Given the customer has a "dairy-free" dietary restriction
    And the customer selects "Milk" which conflicts with the restriction
    When the system identifies the dietary conflict
    Then the system should suggest "Soy Milk" as an alternative for "Milk"

  Scenario: System notifies chef of substitution for unavailable ingredient
    Given an alternative "Guacamole" has been suggested for unavailable "Avocado"
    When the customer accepts the substitution of "Avocado" with "Guacamole"
    Then the chef should be notified of the substitution from "Avocado" to "Guacamole"

  Scenario: System notifies chef of substitution for dietary restriction
    Given the customer has a "dairy-free" dietary restriction
    And an alternative "Soy Milk" has been suggested for "Milk" due to dietary conflict
    When the customer accepts the substitution of "Milk" with "Soy Milk"
    Then the chef should be notified of the substitution from "Milk" to "Soy Milk"