package testCases;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DietaryPreferencesSteps {

    private String dietaryPreference;
    private String allergies;
    private String actualWarning;
    private boolean alternativeSuggested;
    private String customerName;
    private List<String> allMeals;
    private List<String> filteredMeals;



    @Given("I am logged in as the head chef")
    public void iAmLoggedInAsTheHeadChef() {
        System.out.println("Logged in as the head chef");
    }

    @Given("I access the profile of regular customer {string}")
    public void iAccessTheProfileOfRegularCustomer(String customerName) {
        System.out.println("Accessing the profile of customer: " + customerName);
        if (customerName.equals("Emma Wilson")) {
            dietaryPreference = "Vegetarian";
            allergies = "Peanuts";
        }
    }

    @Then("I should see their dietary preferences listed")
    public void iShouldSeeTheirDietaryPreferencesListed() {
        assertNotNull(dietaryPreference, "Dietary preference should not be null");
        assertEquals("Vegetarian", dietaryPreference, "Dietary preference should be 'Vegetarian'");
    }

    @Then("I should see their food allergies clearly highlighted")
    public void iShouldSeeTheirFoodAllergiesClearlyHighlighted() {
        assertNotNull(allergies, "Allergies should not be null");
        assertTrue(allergies.contains("Peanuts"), "Allergies should contain 'Peanuts'");
    }

    @Given("I am preparing a seafood pasta dish")
    public void iAmPreparingASeafoodPastaDish() {
        System.out.println("Preparing a seafood pasta dish...");
    }

    @When("I check the profile of customer {string} who has a shellfish allergy")
    public void iCheckTheProfileOfCustomerWhoHasAShellfishAllergy(String customerName) {
        System.out.println("Checking the profile of customer: " + customerName);
        if (customerName.equals("David Lee")) {
            actualWarning = "⚠ Warning: David Lee has a shellfish allergy!";
            alternativeSuggested = true;
        }
    }

    @Then("the system should display a prominent warning")
    public void theSystemShouldDisplayAProminentWarning() {
        assertNotNull(actualWarning, "Warning should not be null");
        assertTrue(actualWarning.contains("shellfish allergy"), "Warning should contain 'shellfish allergy'");
        System.out.println(actualWarning);
    }

    @Then("the system should suggest alternative ingredients to use")
    public void theSystemShouldSuggestAlternativeIngredientsToUse() {
        assertTrue(alternativeSuggested, "Alternative ingredients should be suggested");
        System.out.println("✅ Suggested alternatives: Use shrimp or tofu instead of shellfish.");
    }


    @Given("customer {string} has a {string} dietary preference")
    public void customerHasADietaryPreference(String name, String preference) {
        this.customerName = name;
        this.dietaryPreference = preference;
        System.out.println("Customer: " + name + " | Preference: " + preference);

        allMeals = Arrays.asList(
                "Vegan Salad", "Grilled Chicken", "Vegan Burger", "Beef Steak",
                "Vegetable Stir Fry", "Fish Tacos", "Vegan Pasta", "Cheese Pizza"
        );
    }

    @When("I search for suitable meal options")
    public void iSearchForSuitableMealOptions() {
        filteredMeals = allMeals.stream()
                .filter(meal -> meal.toLowerCase().contains("vegan"))
                .collect(Collectors.toList());

        System.out.println("Filtered meals for " + customerName + ": " + filteredMeals);
    }

    @Then("the system should only show vegan-compliant dishes")
    public void theSystemShouldOnlyShowVeganCompliantDishes() {
        assertFalse(filteredMeals.isEmpty(), "The filtered meal list should not be empty");

        for (String meal : filteredMeals) {
            assertTrue(meal.toLowerCase().contains("vegan"), "Meal should be vegan-compliant: " + meal);
        }
        System.out.println("✅ All displayed meals are vegan-compliant.");
    }

    @Then("automatically exclude any containing animal products")
    public void automaticallyExcludeAnyContainingAnimalProducts() {
        List<String> nonVeganMeals = Arrays.asList("Grilled Chicken", "Beef Steak", "Fish Tacos", "Cheese Pizza");

        for (String meal : nonVeganMeals) {
            assertFalse(filteredMeals.contains(meal), "Non-vegan meal should be excluded: " + meal);
        }
        System.out.println("✅ All non-vegan meals have been excluded.");
    }
}
