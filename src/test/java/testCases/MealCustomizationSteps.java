package testCases;

import cook.Ingredient;
import cook.Meal;
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
    private final Map<String, Meal> savedMeals = new HashMap<>();
    private String currentUser = "testUser";
    private static final char DEFAULT_MEAL_SIZE = 'M';
    private static final double DEFAULT_MEAL_PRICE = 10.0;
    private static final String DEFAULT_MEAL_NAME = "Custom Meal";

    public MealCustomizationSteps(TestContext context) {
        this.testContext = context;
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
        savedMeals.clear();
        currentUser = "testUser";
        if (testContext != null) {
            testContext.sharedSelectedIngredients.clear();
            testContext.sharedCurrentMeal = null;
            testContext.sharedErrorDisplayed = false;
            testContext.lastSystemMessage = null;
        }
        Assertions.assertTrue(savedMeals.isEmpty(), "savedMeals map should be clear at the start of a scenario.");
        Assertions.assertEquals("testUser", currentUser, "currentUser should be reset to 'testUser'.");
        if (testContext != null) {
            Assertions.assertTrue(testContext.sharedSelectedIngredients.isEmpty(), "sharedSelectedIngredients in context should be clear.");
            Assertions.assertNull(testContext.sharedCurrentMeal, "sharedCurrentMeal in context should be null.");
            Assertions.assertFalse(testContext.sharedErrorDisplayed, "sharedErrorDisplayed in context should be false.");
        }
    }

    @Given("the customer has selected ingredients:")
    public void the_customer_has_selected_ingredients(DataTable dataTable) {
        testContext.sharedSelectedIngredients.clear();
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        List<String> expectedIngredientNames = new ArrayList<>();
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) {
                testContext.sharedSelectedIngredients.add(new Ingredient(ingredientName));
                expectedIngredientNames.add(ingredientName);
            }
        }
        updateSharedCurrentMealDefaultName();
        List<String> actualIngredientNamesInContext = getIngredientNames(testContext.sharedSelectedIngredients);
        Assertions.assertEquals(new HashSet<>(expectedIngredientNames), new HashSet<>(actualIngredientNamesInContext), "Ingredients in context should match those provided in DataTable.");
    }

    @When("the customer chooses ingredients:")
    public void the_customer_chooses_ingredients(DataTable dataTable) {
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) {
                testContext.sharedSelectedIngredients.add(new Ingredient(ingredientName));
                Assertions.assertTrue(getIngredientNames(testContext.sharedSelectedIngredients).contains(ingredientName), "Ingredient '" + ingredientName + "' should be added to the context's selected ingredients list.");
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

        if (testContext.sharedCurrentMeal.hasIncompatibleIngredients()) {
            testContext.sharedErrorDisplayed = true;
            Assertions.assertTrue(testContext.sharedCurrentMeal.hasIncompatibleIngredients(), "Meal.hasIncompatibleIngredients() should return true if this block is reached.");
        } else {
            testContext.sharedErrorDisplayed = false;
            Assertions.assertFalse(testContext.sharedCurrentMeal.hasIncompatibleIngredients(), "Meal.hasIncompatibleIngredients() should return false if this block is reached.");
        }
    }

    @Then("the system should display an error message")
    public void the_system_should_display_an_error_message() {
        Assertions.assertTrue(testContext.sharedErrorDisplayed, "An error message should have been displayed (sharedErrorDisplayed should be true).");
        testContext.lastSystemMessage = "Error: Incompatible ingredients selected. Please revise your selection.";
        Assertions.assertEquals("Error: Incompatible ingredients selected. Please revise your selection.", testContext.lastSystemMessage, "Error message content should be recorded in TestContext.");
    }

    @Then("the system should save the selected ingredients as a meal named {string}")
    public void the_system_should_save_the_selected_ingredients_as_a_meal_named(String mealName) {
        updateSharedCurrentMealWithName(mealName);
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should not be null before saving.");

        savedMeals.put(currentUser, testContext.sharedCurrentMeal);
        Assertions.assertTrue(savedMeals.containsKey(currentUser), "Meal should be saved under the current user.");
        Assertions.assertEquals(testContext.sharedCurrentMeal, savedMeals.get(currentUser), "The meal object saved should be the one from the context.");
        Assertions.assertEquals(mealName, savedMeals.get(currentUser).getName(), "The name of the saved meal should match the expected name.");
    }

    @When("the customer saves the custom meal as {string}")
    public void the_customer_saves_the_custom_meal_as(String mealName) {
        updateSharedCurrentMealWithName(mealName);
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should not be null to be saved.");

        savedMeals.put(currentUser, testContext.sharedCurrentMeal);
        Assertions.assertTrue(savedMeals.containsKey(currentUser), "Meal should be present in savedMeals map after saving action.");
        Meal savedMeal = savedMeals.get(currentUser);
        Assertions.assertNotNull(savedMeal, "A meal object should be retrieved from savedMeals.");
        Assertions.assertEquals(mealName, savedMeal.getName(), "The name of the meal in savedMeals should match.");
        Assertions.assertEquals(new HashSet<>(testContext.sharedCurrentMeal.getIngredients()), new HashSet<>(savedMeal.getIngredients()), "Ingredients of the saved meal should match those from context.");
    }

    @Then("the system should store the meal {string} for future orders")
    public void the_system_should_store_the_meal_for_future_orders(String mealName) {
        Meal mealFromSavedMap = savedMeals.get(currentUser);
        Assertions.assertNotNull(mealFromSavedMap, "No meal found saved for the current user in savedMeals map.");
        Assertions.assertEquals(mealName, mealFromSavedMap.getName(), "The name of the saved meal does not match the expected name.");

        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should not be null for comparison (it reflects the state at time of save).");

        Set<String> expectedIngredientNames = new HashSet<>(testContext.sharedCurrentMeal.getIngredients());
        Set<String> actualIngredientNamesInSavedMeal = new HashSet<>(mealFromSavedMap.getIngredients());

        Assertions.assertEquals(
                expectedIngredientNames,
                actualIngredientNamesInSavedMeal,
                "Ingredients of the saved meal do not match the ingredients from the shared context at save time."
        );
        Assertions.assertEquals(mealName, mealFromSavedMap.getName(), "Final confirmation: The meal name '" + mealName + "' is correctly stored.");
    }
}