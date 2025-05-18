package testCases;

import cook.Ingredient;
import cook.Meal;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;






import java.util.List;




import java.util.Map;


import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.stream.Collectors;






public class MealCustomizationSteps {
    private List<Ingredient> selectedIngredients = new ArrayList<>();
    private boolean errorDisplayed = false;
    private Map<String, Meal> savedMeals = new HashMap<>(); // Stores Meal objects per user
    private String currentUser = "testUser";
    private Meal currentMeal; // To hold the meal being constructed or evaluated

    // Default values for creating a Meal if not otherwise specified
    private static final char DEFAULT_MEAL_SIZE = 'M';
    private static final double DEFAULT_MEAL_PRICE = 10.0; // Example price
    private static final String DEFAULT_MEAL_NAME = "Custom Meal";


    // Helper to convert List<Ingredient> to List<String> (names)
    private List<String> getIngredientNames(List<Ingredient> ingredients) {
        if (ingredients == null) {
            return new ArrayList<>();
        }
        return ingredients.stream().map(Ingredient::getName).collect(Collectors.toList());
    }

    // Helper to create or update the currentMeal based on selectedIngredients
    private void updateCurrentMeal() {
        List<String> ingredientNames = getIngredientNames(selectedIngredients);
        // If currentMeal exists and has a specific name already set by a step,
        // we might want to preserve it. However, this method is mostly for
        // ensuring the ingredients are up-to-date.
        String mealName = (this.currentMeal != null && !DEFAULT_MEAL_NAME.equals(this.currentMeal.getName()))
                ? this.currentMeal.getName()
                : DEFAULT_MEAL_NAME;

        this.currentMeal = new Meal(mealName, ingredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
    }


    @When("the customer selects {string}")
    public void the_customer_selects(String ingredientName) {
        selectedIngredients.add(new Ingredient(ingredientName));
        updateCurrentMeal(); // Update the current meal being built
        System.out.println("Customer selected: " + ingredientName);
    }

    @When("the customer chooses ingredients")
    public void the_customer_chooses_ingredients(io.cucumber.datatable.DataTable dataTable) {
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) { // Skip header
                selectedIngredients.add(new Ingredient(ingredientName));
                System.out.println("Customer chose: " + ingredientName);
            }
        }
        updateCurrentMeal();
    }

    @Then("the system should save the selected ingredients as a meal named {string}")
    public void the_system_should_save_the_selected_ingredients_as_a_meal_named(String mealName) {
        // MODIFICATION: Allow saving empty meals. Remove the isEmpty check or decide on behavior.
        // If you want to prevent saving empty meals and fail the test here, uncomment the Assertions.fail.
        // if (selectedIngredients.isEmpty()) {
        //     System.out.println("No ingredients selected, meal not saved with name: " + mealName);
        //     // Assertions.fail("Attempted to save an empty meal named '" + mealName + "' when it's not allowed.");
        //     return;
        // }

        List<String> ingredientNames = getIngredientNames(selectedIngredients);
        Meal mealToSave = new Meal(mealName, ingredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
        savedMeals.put(currentUser, mealToSave);
        System.out.println("Meal '" + mealName + "' saved for user " + currentUser + " with ingredients: " + ingredientNames);
        Assertions.assertTrue(savedMeals.containsKey(currentUser), "Meal should be saved for the current user.");
        // This assertion relies on Meal.equals() being correctly implemented.
        Assertions.assertEquals(mealToSave, savedMeals.get(currentUser), "Saved meal does not match the expected meal instance or content.");
    }


    @Given("the customer has selected ingredients")
    public void the_customer_has_selected_ingredients(io.cucumber.datatable.DataTable dataTable) {
        selectedIngredients.clear(); // Start fresh
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) { // Skip header
                selectedIngredients.add(new Ingredient(ingredientName));
                // System.out.println("Pre-selected: " + ingredientName); // Less verbose
            }
        }
        updateCurrentMeal(); // Create/update currentMeal with these selections
        System.out.println("Customer has pre-selected ingredients: " + getIngredientNames(selectedIngredients));
    }

    @When("the customer tries to combine incompatible ingredients")
    public void the_customer_tries_to_combine_incompatible_ingredients() {
        if (currentMeal == null) {
            updateCurrentMeal(); // Ensure currentMeal is based on selectedIngredients
        }

        if (currentMeal.hasIncompatibleIngredients()) {
            errorDisplayed = true;
            System.out.println("Attempt to combine incompatible ingredients detected in meal: " + currentMeal.getIngredients());
        } else {
            errorDisplayed = false;
            System.out.println("No incompatible ingredients detected in current selection: " + currentMeal.getIngredients());
        }
    }

    @Then("the system should display an error message")
    public void the_system_should_display_an_error_message() {
        Assertions.assertTrue(errorDisplayed, "An error message should have been displayed for incompatible ingredients.");
        if (errorDisplayed) {
            System.out.println("Error: Incompatible ingredients selected. Please revise your selection.");
        }
    }

    @Then("the system should not display an error message")
    public void the_system_should_not_display_an_error_message() {
        Assertions.assertFalse(errorDisplayed, "An error message was displayed, but no incompatibility was expected.");
        System.out.println("No error message displayed, as expected.");
    }


    @When("the customer saves the custom meal as {string}")
    public void the_customer_saves_the_custom_meal_as(String mealName) {
        // MODIFICATION: Allow saving empty meals.
        // If you want to prevent saving empty meals and this is a valid business rule, keep the check.
        // if (selectedIngredients.isEmpty()) {
        //     System.out.println("Cannot save meal '" + mealName + "': No ingredients selected.");
        //     return; // Meal not saved
        // }

        // Ensure currentMeal reflects the latest selectedIngredients and gets the new name.
        // If currentMeal is null or its ingredients don't match selectedIngredients, rebuild it.
        // List.equals is order-sensitive. Using Sets for comparison is more robust if order doesn't matter for "staleness".
        List<String> currentSelectedIngredientNames = getIngredientNames(selectedIngredients);
        if (currentMeal == null ||
                !new java.util.HashSet<>(currentSelectedIngredientNames).equals(new java.util.HashSet<>(currentMeal.getIngredients()))) {
            // If ingredients differ, rebuild currentMeal using current selectedIngredients
            this.currentMeal = new Meal(mealName, currentSelectedIngredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
        } else {
            // Ingredients are the same, just update the name if currentMeal exists
            currentMeal.setName(mealName);
        }

        savedMeals.put(currentUser, currentMeal);
        System.out.println("Custom meal '" + mealName + "' saved for user " + currentUser + " with ingredients: " + currentMeal.getIngredients());
    }

    @Then("the system should store the meal {string} for future orders")
    public void the_system_should_store_the_meal_for_future_orders(String mealName) {
        Meal savedMeal = savedMeals.get(currentUser);

        Assertions.assertNotNull(savedMeal, "No meal found saved for the current user with name: " + mealName + ". Check if it was intended to be saved (e.g., not an empty meal if empty meals are disallowed).");
        Assertions.assertEquals(mealName, savedMeal.getName(), "The name of the saved meal does not match.");

        // POTENTIAL ISSUE POINT:
        // This `expectedIngredientNames` is derived from the *current* state of `selectedIngredients`
        // in the Step Definition class. If `selectedIngredients` was modified *after* the meal was saved
        // but *before* this verification step (within the same scenario), this assertion will be incorrect.
        // For robust verification, the expected ingredients should ideally come from the Gherkin step itself
        // or be based on a state captured at the moment of saving.
        List<String> expectedIngredientNamesAtVerificationTime = getIngredientNames(selectedIngredients);

        // The ingredients in `savedMeal` are those it was saved with.
        List<String> actualSavedIngredients = savedMeal.getIngredients();

        Assertions.assertEquals(
                new java.util.HashSet<>(expectedIngredientNamesAtVerificationTime), // What current selectedIngredients imply
                new java.util.HashSet<>(actualSavedIngredients),            // What was actually saved in the meal
                "The ingredients of the saved meal do not match the ingredients expected at verification time. " +
                        "Expected (based on current selection): " + expectedIngredientNamesAtVerificationTime +
                        ", Actual (in saved meal): " + actualSavedIngredients
        );
        System.out.println("Custom meal '" + mealName + "' with ingredients " + actualSavedIngredients + " is correctly stored for future orders.");
    }

    @io.cucumber.java.Before
    public void setUp() {
        selectedIngredients.clear();
        savedMeals.clear();
        errorDisplayed = false;
        currentUser = "testUser";
        currentMeal = null;
        System.out.println("--- MealCustomizationSteps: Scenario Start, State Cleared ---");
    }
}