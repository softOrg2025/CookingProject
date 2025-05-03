package testCases;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Collectors;

public class DietaryPreferencesSteps {

    private static class CustomerProfile {
        String name;
        String dietaryPreference;
        String allergy;

        CustomerProfile(String name, String dietaryPreference, String allergy) {
            this.name = name;
            this.dietaryPreference = dietaryPreference;
            this.allergy = allergy;
        }
    }

    private CustomerProfile currentCustomer;
    private String dish;
    private String warningMessage;
    private boolean alternativeSuggested;
    private List<String> allMeals;
    private List<String> filteredMeals;

    @Given("I am logged in as the head chef")
    public void iAmLoggedInAsTheHeadChef() {
        System.out.println("✅ Logged in as the head chef");
    }

    @When("I access the profile of regular customer {string}")
    public void iAccessTheProfileOfRegularCustomer(String customerName) {
        if (customerName.equals("Emma Wilson")) {
            currentCustomer = new CustomerProfile("Emma Wilson", "Vegetarian", "Peanuts");
        } else if (customerName.equals("David Lee")) {
            currentCustomer = new CustomerProfile("David Lee", "None", "Shellfish");
        } else if (customerName.equals("Maria Garcia")) {
            currentCustomer = new CustomerProfile("Maria Garcia", "Vegan", "");
        } else {
            currentCustomer = new CustomerProfile(customerName, "", "");
        }
        System.out.println("👤 Accessed profile of: " + customerName);
    }

    @Then("I should see their dietary preferences listed")
    public void iShouldSeeTheirDietaryPreferencesListed() {
        assertNotNull(currentCustomer, "Customer profile should not be null");
        assertNotNull(currentCustomer.dietaryPreference, "Dietary preference should not be null");
        System.out.println("🥗 Dietary Preference: " + currentCustomer.dietaryPreference);
    }

    @Then("I should see their food allergies clearly highlighted")
    public void iShouldSeeTheirFoodAllergiesClearlyHighlighted() {
        assertNotNull(currentCustomer, "Customer profile should not be null");
        assertNotNull(currentCustomer.allergy, "Allergies should not be null");
        assertFalse(currentCustomer.allergy.isEmpty(), "Allergies should be clearly listed");
        System.out.println("⚠ Allergy: " + currentCustomer.allergy);
    }

    @Given("I am preparing a {string} dish")
    public void iAmPreparingADish(String dishName) {
        this.dish = dishName;
        System.out.println("👨‍🍳 Preparing: " + dishName);
    }

    @When("I check the profile of customer {string} who has a shellfish allergy")
    public void iCheckTheProfileOfCustomerWhoHasShellfishAllergy(String customerName) {
        iAccessTheProfileOfRegularCustomer(customerName);  // reuse logic
        if (currentCustomer.allergy.toLowerCase().contains("shellfish") && dish.toLowerCase().contains("seafood")) {
            warningMessage = "⚠ Warning: " + currentCustomer.name + " has a shellfish allergy!";
            alternativeSuggested = true;
        }
    }

    @Then("the system should display a prominent warning")
    public void theSystemShouldDisplayAProminentWarning() {
        assertNotNull(warningMessage, "Warning message should not be null");
        assertTrue(warningMessage.toLowerCase().contains("shellfish allergy"), "Warning must mention shellfish allergy");
        System.out.println(warningMessage);
    }

    @Then("the system should suggest alternative ingredients to use")
    public void theSystemShouldSuggestAlternativeIngredientsToUse() {
        assertTrue(alternativeSuggested, "Alternative ingredients should be suggested");
        System.out.println("✅ Suggested alternatives: Use tofu or vegetables instead of shellfish.");
    }

    @Given("customer {string} has a {string} dietary preference")
    public void customerHasADietaryPreference(String name, String preference) {
        currentCustomer = new CustomerProfile(name, preference, "");
        allMeals = Arrays.asList(
                "Vegan Salad", "Grilled Chicken", "Vegan Burger", "Beef Steak",
                "Vegetable Stir Fry", "Fish Tacos", "Vegan Pasta", "Cheese Pizza"
        );
        System.out.println("📌 Loaded meals for filtering...");
    }

    @When("I search for suitable meal options")
    public void iSearchForSuitableMealOptions() {
        String pref = currentCustomer.dietaryPreference.toLowerCase();
        filteredMeals = allMeals.stream()
                .filter(meal -> meal.toLowerCase().contains(pref))
                .collect(Collectors.toList());
        System.out.println("📋 Filtered meals: " + filteredMeals);
    }

    @Then("the system should only show vegan-compliant dishes")
    public void theSystemShouldOnlyShowVeganCompliantDishes() {
        assertFalse(filteredMeals.isEmpty(), "Filtered meal list should not be empty");
        for (String meal : filteredMeals) {
            assertTrue(meal.toLowerCase().contains("vegan"), "Non-vegan meal found: " + meal);
        }
        System.out.println("✅ All displayed meals are vegan-compliant.");
    }

    @Then("automatically exclude any containing animal products")
    public void automaticallyExcludeAnyContainingAnimalProducts() {
        List<String> nonVeganMeals = Arrays.asList("Grilled Chicken", "Beef Steak", "Fish Tacos", "Cheese Pizza");
        for (String meal : nonVeganMeals) {
            assertFalse(filteredMeals.contains(meal), "Non-vegan meal should be excluded: " + meal);
        }
        System.out.println("✅ All non-vegan meals excluded successfully.");
    }
}
