package testCases;

import cook.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;
import io.cucumber.datatable.DataTable;

public class MealCustomizationSteps {
    private final TestContext testContext;
    private final Customer customer;
    private static final char DEFAULT_MEAL_SIZE = 'M';
    private static final double DEFAULT_MEAL_PRICE = 10.0;
    private static final String DEFAULT_MEAL_NAME = "Custom Meal";

    public MealCustomizationSteps(TestContext context) {
        this.testContext = context;
        this.customer = new Customer("Test User", "test@example.com", "password");
        Application.users.add(customer);
        Application.currentUser = customer;
    }

    private List<String> getIngredientNames(List<Ingredient> ingredients) {
        if (ingredients == null) return new ArrayList<>();
        return ingredients.stream().map(Ingredient::getName).collect(Collectors.toList());
    }

    private void updateSharedCurrentMealWithName(String mealName) {
        List<String> ingredientNames = getIngredientNames(testContext.sharedSelectedIngredients);
        testContext.sharedCurrentMeal = new Meal(mealName, ingredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared current meal should be created/updated.");
        Assertions.assertEquals(mealName, testContext.sharedCurrentMeal.getName(), "Meal name in context should match the provided name.");
        Assertions.assertEquals(new HashSet<>(ingredientNames), new HashSet<>(testContext.sharedCurrentMeal.getIngredients()), "Meal ingredients in context should match.");
    }

    private void updateSharedCurrentMealDefaultName() {
        updateSharedCurrentMealWithName(DEFAULT_MEAL_NAME);
    }

    @Before
    public void setUp() {
        testContext.sharedSelectedIngredients.clear();
        testContext.sharedCurrentMeal = null;
        testContext.sharedErrorDisplayed = false;
        testContext.lastSystemMessage = null;
        Application.setSystemMessage(null);
    }

    @Given("the customer has selected ingredients:")
    public void the_customer_has_selected_ingredients(DataTable dataTable) {
        testContext.sharedSelectedIngredients.clear();
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) {
                testContext.sharedSelectedIngredients.add(new Ingredient(ingredientName));
            }
        }
        updateSharedCurrentMealDefaultName();
    }

    @When("the customer chooses ingredients:")
    public void the_customer_chooses_ingredients(DataTable dataTable) {
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) {
                testContext.sharedSelectedIngredients.add(new Ingredient(ingredientName));
            }
        }
        updateSharedCurrentMealDefaultName();
    }

    @When("the customer selects {string}")
    public void the_customer_selects(String actionOrIngredientName) {
        if (actionOrIngredientName.equalsIgnoreCase("Create Custom Meal")) {
            testContext.sharedSelectedIngredients.clear();
            testContext.sharedCurrentMeal = null;
        }
    }

    @When("the customer tries to combine incompatible ingredients")
    public void the_customer_tries_to_combine_incompatible_ingredients() {
        if (testContext.sharedCurrentMeal == null && !testContext.sharedSelectedIngredients.isEmpty()) {
            updateSharedCurrentMealDefaultName();
        }
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should exist before checking incompatibility.");

        testContext.sharedErrorDisplayed = testContext.sharedCurrentMeal.hasIncompatibleIngredients();
        if (testContext.sharedErrorDisplayed) {
            Application.notificationService.sendNotification(customer.getEmail(), "Incompatible ingredients selected");
            Application.setSystemMessage("Error: Incompatible ingredients selected. Please revise your selection.");
        }
    }

    @Then("the system should display an error message")
    public void the_system_should_display_an_error_message() {
        Assertions.assertTrue(testContext.sharedErrorDisplayed, "An error message should have been displayed.");
        Assertions.assertEquals("Error: Incompatible ingredients selected. Please revise your selection.",
                Application.getSystemMessage(), "System message should match expected error message.");
    }

    @Then("the system should save the selected ingredients as a meal named {string}")
    public void the_system_should_save_the_selected_ingredients_as_a_meal_named(String mealName) {
        updateSharedCurrentMealWithName(mealName);
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should not be null before saving.");

        // Assuming Customer class has a method to save meals
        customer.saveMeal(mealName, testContext.sharedCurrentMeal);
        Meal savedMeal = customer.getSavedMeal(mealName);
        Assertions.assertNotNull(savedMeal, "Meal should be saved in customer's saved meals.");
        Assertions.assertEquals(mealName, savedMeal.getName(), "The name of the saved meal should match the expected name.");
    }

    @When("the customer saves the custom meal as {string}")
    public void the_customer_saves_the_custom_meal_as(String mealName) {
        updateSharedCurrentMealWithName(mealName);
        customer.saveMeal(mealName, testContext.sharedCurrentMeal);
    }

    @Then("the system should store the meal {string} for future orders")
    public void the_system_should_store_the_meal_for_future_orders(String mealName) {
        Meal savedMeal = customer.getSavedMeal(mealName);
        Assertions.assertNotNull(savedMeal, "Meal should be stored for future orders.");
        Assertions.assertEquals(mealName, savedMeal.getName(), "Meal name should match.");
        Assertions.assertEquals(new HashSet<>(testContext.sharedCurrentMeal.getIngredients()),
                new HashSet<>(savedMeal.getIngredients()), "Ingredients should match.");
    }
}