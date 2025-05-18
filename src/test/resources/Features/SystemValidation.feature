Feature: System validates ingredient combinations

  Scenario: System checks for incompatible ingredients
    Given the customer has selected ingredients:
      | ingredientName |
      | Milk           |
      | Lemon          |
    When the system checks the combination
    Then the system should flag any incompatible ingredients

  Scenario: System suggests alternative ingredients
    Given the customer has selected incompatible ingredients
    When the system identifies the issue
    Then the system should suggest alternative ingredients

  Scenario: System prevents invalid meal submissions
    Given the customer has selected invalid ingredients
    When the customer tries to submit the meal
    Then the system should prevent submission and display an error