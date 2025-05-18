package testCases;
import cook.Ingredient;
import cook.Meal;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;



// package com.example.yourpackage.steps; // Your package for step definitions

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions; // For assertions

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SystemValidationSteps {

    private List<Ingredient> selectedIngredients = new ArrayList<>();
    private boolean errorDisplayedForIncompatibility = false; // More specific name
    private Meal currentMeal; // To hold the meal being evaluated
    private boolean mealSubmissionPrevented = false;

    // Default values for creating a Meal if not otherwise specified
    private static final char DEFAULT_MEAL_SIZE = 'M';
    private static final double DEFAULT_MEAL_PRICE = 0.0; // Price might not be relevant for validation only

    // Helper to convert List<Ingredient> to List<String> (names)
    private List<String> getIngredientNames(List<Ingredient> ingredients) {
        if (ingredients == null) {
            return new ArrayList<>();
        }
        return ingredients.stream().map(Ingredient::getName).collect(Collectors.toList());
    }

    // Helper to create or update the currentMeal based on selectedIngredients
    private void buildCurrentMealForValidation() {
        List<String> ingredientNames = getIngredientNames(selectedIngredients);
        // For validation purposes, name, size, and price might be minimal
        this.currentMeal = new Meal("ValidationMeal", ingredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
    }

    @io.cucumber.java.Before
    public void setUp() {
        selectedIngredients.clear();
        errorDisplayedForIncompatibility = false;
        currentMeal = null;
        mealSubmissionPrevented = false;
        System.out.println("--- SystemValidationSteps: Scenario Start, State Cleared ---");
    }

    @Given("the customer has selected ingredients:")
    public void the_customer_has_selected_ingredients(io.cucumber.datatable.DataTable dataTable) {
        selectedIngredients.clear();
        List<String> ingredientNames = dataTable.asList(String.class);
        for (String name : ingredientNames) {
            if (!name.equalsIgnoreCase("ingredientName")) { // Skip header if present
                selectedIngredients.add(new Ingredient(name));
            }
        }
        System.out.println("Customer selected for validation: " + getIngredientNames(selectedIngredients));
        buildCurrentMealForValidation(); // Build meal after selection
    }


    @Given("the customer has selected incompatible ingredients")
    public void the_customer_has_selected_incompatible_ingredients() {
        selectedIngredients.clear();
        selectedIngredients.add(new Ingredient("Milk"));
        selectedIngredients.add(new Ingredient("Lemon"));
        System.out.println("Customer selected incompatible: Milk, Lemon");
        buildCurrentMealForValidation();
    }

    // This step can be used if "invalid" means the same as "leading to incompatibility"
    @Given("the customer has selected invalid ingredients")
    public void the_customer_has_selected_invalid_ingredients() {
        selectedIngredients.clear();
        // Assuming "invalid" here means they will form an incompatible combination
        // as per the original test's logic.
        selectedIngredients.add(new Ingredient("Milk"));
        selectedIngredients.add(new Ingredient("Lemon"));
        // If "invalid" meant something else (e.g. ingredient doesn't exist),
        // the setup and Ingredient class would need to reflect that.
        System.out.println("Customer selected (treated as) incompatible: Milk, Lemon");
        buildCurrentMealForValidation();
    }

    @When("the system checks the combination")
    public void the_system_checks_the_combination() {
        if (currentMeal == null) {
            buildCurrentMealForValidation(); // Ensure meal is built if not already
        }
        if (currentMeal.hasIncompatibleIngredients()) {
            errorDisplayedForIncompatibility = true;
            System.out.println("System Check: Incompatible combination detected.");
        } else {
            errorDisplayedForIncompatibility = false;
            System.out.println("System Check: No incompatible combination detected.");
        }
    }

    @When("the system identifies the issue")
    public void the_system_identifies_the_issue() {
        // This is essentially the same as checking the combination for incompatibility
        the_system_checks_the_combination();
        if (errorDisplayedForIncompatibility) {
            System.out.println("System Identified Issue: Incompatibility found.");
        }
    }

    @Then("the system should flag any incompatible ingredients")
    public void the_system_should_flag_any_incompatible_ingredients() {
        Assertions.assertTrue(errorDisplayedForIncompatibility, "System should have flagged incompatible ingredients.");
        if (errorDisplayedForIncompatibility) {
            System.out.println("Flag: Incompatible ingredients detected in the current selection.");
            // In a real UI, this would be a visual flag or message.
        }
    }

    @Then("the system should suggest alternative ingredients")
    public void the_system_should_suggest_alternative_ingredients() {
        Assertions.assertTrue(errorDisplayedForIncompatibility,
                "Alternatives should only be suggested if an incompatibility was found.");

        if (errorDisplayedForIncompatibility && currentMeal != null) {
            System.out.println("Suggesting alternatives due to incompatibility:");
            boolean suggestionsFound = false;
            // We need to iterate through the ingredients that are part of the known incompatible combinations.
            // The Meal.incompatibleCombinations is static and private, but hasIncompatibleIngredients uses it.
            // For suggesting, we can iterate through the current meal's ingredients and see if Meal.suggestAlternative has options.
            for (String ingredientNameInMeal : currentMeal.getIngredients()) {
                List<String> alternatives = Meal.suggestAlternative(ingredientNameInMeal);
                if (!alternatives.isEmpty()) {
                    System.out.println("  Alternatives for " + ingredientNameInMeal + ": " + alternatives);
                    suggestionsFound = true;
                }
            }
            if (!suggestionsFound) {
                System.out.println("  No specific alternatives found via Meal.suggestAlternative for the current incompatible ingredients.");
            }
        } else if (currentMeal == null) {
            System.out.println("Cannot suggest alternatives: current meal not built.");
        }
    }


    @When("the customer tries to submit the meal")
    public void the_customer_tries_to_submit_the_meal() {
        if (currentMeal == null) {
            buildCurrentMealForValidation();
        }

        if (currentMeal.hasIncompatibleIngredients()) {
            errorDisplayedForIncompatibility = true; // Re-confirm error state
            mealSubmissionPrevented = true;
            System.out.println("Submission Attempt: Meal contains incompatible ingredients. Submission will be prevented.");
        } else {
            mealSubmissionPrevented = false; // Assume submission would proceed if no errors
            System.out.println("Submission Attempt: Meal is valid. Submission would proceed.");
            // this.isMealSubmitted = true; // If you had a flag for actual submission
        }
    }

    @Then("the system should prevent submission and display an error")
    public void the_system_should_prevent_submission_and_display_an_error() {
        Assertions.assertTrue(mealSubmissionPrevented, "Meal submission should have been prevented.");
        Assertions.assertTrue(errorDisplayedForIncompatibility, "An error should be displayed for incompatible ingredients during submission.");

        if (mealSubmissionPrevented) {
            System.out.println("Error Displayed: Cannot submit meal with incompatible ingredients. Please revise your selection.");
            // In a real app, assert UI error message.
        }
    }
}
