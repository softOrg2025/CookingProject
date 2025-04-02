
Feature: : Customer selects ingredients for custom meal                                                                                                          Feature: Customer selects ingredients for custom meal

Scenario: Customer selects ingredients
Given the customer is logged into the system
When the customer selects "Create Custom Meal"
And the customer chooses ingredients
Then the system should save the selected ingredients

Scenario: System validates ingredient combinations
Given the customer has selected ingredients
When the customer tries to combine incompatible ingredients
Then the system should display an error message

Scenario: Customer saves custom meal
Given the customer has selected ingredients
When the customer saves the custom meal
Then the system should store the meal for future orders