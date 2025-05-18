Feature: Customer selects ingredients for custom meal

  Scenario: Customer selects ingredients
    Given the customer is logged into the system
    When the customer selects "Create Custom Meal"
    And the customer chooses ingredients:
      | ingredientName |
      | Tomato         |
      | Cheese         |
      | Basil          |
    Then the system should save the selected ingredients as a meal named "Pizza Toppings"

  Scenario: System validates ingredient combinations
    Given the customer has selected ingredients:
      | ingredientName |
      | Milk           |
      | Lemon          |
    When the customer tries to combine incompatible ingredients
    Then the system should display an error message

  Scenario: Customer saves custom meal
    Given the customer has selected ingredients:
      | ingredientName |
      | Chicken        |
      | Rice           |
      | Broccoli       |
    When the customer saves the custom meal as "Healthy Chicken Bowl"
    Then the system should store the meal "Healthy Chicken Bowl" for future orders