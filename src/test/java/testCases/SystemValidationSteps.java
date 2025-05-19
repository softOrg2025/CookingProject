package testCases;

import cook.Application;
import cook.Ingredient;
import cook.Meal;
import cook.NotificationService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class SystemValidationSteps {
    private final TestContext testContext;
    private boolean mealSubmissionPrevented = false;
    private final NotificationService notificationService = Application.notificationService;

    private static final char DEFAULT_MEAL_SIZE = 'M';
    private static final double DEFAULT_MEAL_PRICE = 0.0;

    public SystemValidationSteps(TestContext context) {
        this.testContext = context;
    }

    @Before
    public void setUp() {
        testContext.reset();
        mealSubmissionPrevented = false;
        notificationService.clearNotifications("testUser");
    }

    private void ensureSharedMealFromSharedIngredients(String mealName) {
        List<String> ingredientNames = testContext.sharedSelectedIngredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.toList());
        testContext.sharedCurrentMeal = new Meal(mealName, ingredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
    }

    @Given("the customer has selected incompatible ingredients")
    public void the_customer_has_selected_incompatible_ingredients() {
        testContext.sharedSelectedIngredients.clear();
        testContext.sharedSelectedIngredients.add(new Ingredient("Milk"));
        testContext.sharedSelectedIngredients.add(new Ingredient("Lemon"));
        ensureSharedMealFromSharedIngredients("IncompatibleTestMeal");
        notificationService.sendNotification("testUser", "Selected ingredients: Milk, Lemon");
    }

    @Given("the customer has selected invalid ingredients")
    public void the_customer_has_selected_invalid_ingredients() {
        testContext.sharedSelectedIngredients.clear();
        testContext.sharedSelectedIngredients.add(new Ingredient("Milk"));
        testContext.sharedSelectedIngredients.add(new Ingredient("Lemon"));
        ensureSharedMealFromSharedIngredients("InvalidTestMeal");
        notificationService.sendNotification("testUser", "Selected invalid ingredients: Milk, Lemon");
    }

    @When("the system checks the combination")
    public void the_system_checks_the_combination() {
        assertNotNull(testContext.sharedCurrentMeal, "Meal should exist for combination check");
        testContext.sharedErrorDisplayed = testContext.sharedCurrentMeal.hasIncompatibleIngredients();

        if (testContext.sharedErrorDisplayed) {
            notificationService.sendNotification("testUser",
                    "Incompatible combination detected in meal: " + testContext.sharedCurrentMeal.getName());
        }
    }

    @When("the system identifies the issue")
    public void the_system_identifies_the_issue() {
        the_system_checks_the_combination();
        if (testContext.sharedErrorDisplayed) {
            Application.setSystemMessage("Incompatible ingredients found in current meal");
        }
    }

    @Then("the system should flag any incompatible ingredients")
    public void the_system_should_flag_any_incompatible_ingredients() {
        assertTrue(testContext.sharedErrorDisplayed);
        assertFalse(notificationService.getNotifications("testUser").isEmpty());
    }

    @Then("the system should suggest alternative ingredients")
    public void the_system_should_suggest_alternative_ingredients() {
        assertTrue(testContext.sharedErrorDisplayed);
        assertNotNull(testContext.sharedCurrentMeal);

        boolean suggestionsFound = false;
        for (String ingredient : testContext.sharedCurrentMeal.getIngredients()) {
            List<String> alternatives = Meal.suggestAlternative(ingredient);
            if (!alternatives.isEmpty()) {
                suggestionsFound = true;
                notificationService.sendNotification("testUser",
                        "Suggested alternatives for " + ingredient + ": " + String.join(", ", alternatives));
            }
        }
        assertTrue(suggestionsFound);
    }

    @When("the customer tries to submit the meal")
    public void the_customer_tries_to_submit_the_meal() {
        if (testContext.sharedCurrentMeal == null && !testContext.sharedSelectedIngredients.isEmpty()) {
            ensureSharedMealFromSharedIngredients("SubmissionTestMeal");
        }
        assertNotNull(testContext.sharedCurrentMeal);

        mealSubmissionPrevented = testContext.sharedCurrentMeal.hasIncompatibleIngredients();
        if (mealSubmissionPrevented) {
            Application.setSystemMessage("Submission prevented: Meal contains incompatible ingredients");
        }
    }

    @Then("the system should prevent submission and display an error")
    public void the_system_should_prevent_submission_and_display_an_error() {
        assertTrue(mealSubmissionPrevented);
        assertNotNull(Application.getSystemMessage());
    }
}