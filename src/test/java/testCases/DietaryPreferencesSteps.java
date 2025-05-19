// testCases.DietaryPreferencesSteps.java
package testCases;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Given;
import cook.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.Collectors;

public class DietaryPreferencesSteps {
    private Customer currentCustomer;
    private String dishNameForScenario;
    // private String warningMessage; // Will use Application.getSystemMessage()
    private boolean alternativeSuggested;
    private List<Meal> allMealsForFiltering; // Meals defined for specific scenarios
    private List<Meal> filteredMeals; // Result of Application.exclude or other filtering

    // Helper to print customer state for debugging (can be kept)
    private void printCustomerState(String stepNameContext) {
        if (currentCustomer != null) {
            System.out.println("LOG [" + stepNameContext + "] Customer: " + currentCustomer.getName() +
                    ", Preferences: " + currentCustomer.getPreferences() +
                    ", Allergies: " + currentCustomer.getAllergies());
        } else {
            System.out.println("LOG [" + stepNameContext + "] currentCustomer is NULL");
        }
    }

    @Given("I am logged in as the head chef")
    public void iAmLoggedInAsTheHeadChef() {
        System.out.println("\nLOG [Given I am logged in as the head chef] START");
        Application.meals.clear(); // Clear meals from previous scenarios
        Application.users.clear(); // Clear users
        Application.setSystemMessage(null); // Clear any system message

        InventoryService inventoryService = new InventoryService(); // Dummy for chef creation
        kitchen_manager manager = new kitchen_manager("Manager", "manager@email.com", "pass", inventoryService);
        chef headChef = new chef("Head Chef", "chef@email.com", "pass", manager);
        Application.users.add(headChef); // Add chef to users list
        Application.currentUser = Application.login(headChef.getEmail(), headChef.getPassword()); // Perform login

        assertNotNull(Application.currentUser, "Head chef should be logged in.");
        assertEquals(Role.Chef, Application.currentUser.getRole(), "Logged in user should be a Chef.");
        System.out.println("LOG [Given I am logged in as the head chef] END - Current User: " + (Application.currentUser != null ? Application.currentUser.getName() : "null"));
    }

    @When("I access the profile of regular customer {string}")
    public void iAccessTheProfileOfRegularCustomer(String customerName) {
        System.out.println("LOG [When I access profile of customer \"" + customerName + "\"] START");
        Application.setSystemMessage(null); // Clear previous messages

        // Create and "register" the customer
        currentCustomer = new Customer(customerName, customerName.toLowerCase().replace(" ", "") + "@email.com", "pass123");
        Application.users.add(currentCustomer); // Add to application's user list

        switch (customerName) {
            case "Emma Wilson":
                currentCustomer.savePreferences("Vegetarian");
                currentCustomer.saveAllergy("Peanuts");
                break;
            case "David Lee":
                // No explicit preference saved means it defaults or is empty, we'll test for "None" or handle empty.
                // For test clarity, let's save "None" if that's a valid state, or ensure preferences list is empty.
                currentCustomer.savePreferences("None"); // Assuming "None" is a valid preference string.
                currentCustomer.saveAllergy("Shellfish");
                break;
            case "Maria Garcia":
                currentCustomer.savePreferences("Vegan");
                // No allergies for Maria
                break;
        }
        // Simulating "accessing profile" by having currentCustomer set.
        // In a real UI, this might involve a lookup from Application.users.
        printCustomerState("When I access profile of \"" + customerName + "\" - END");
    }

    @Then("I should see their dietary preferences listed")
    public void iShouldSeeTheirDietaryPreferencesListed() {
        printCustomerState("Then I should see preferences - Before Asserts");
        assertNotNull(currentCustomer, "Customer profile should not be null");
        List<String> preferences = currentCustomer.getPreferences(); // Call to Customer.getPreferences()

        assertNotNull(preferences, "Preferences list should not be null.");
        if ("Emma Wilson".equals(currentCustomer.getName())) {
            assertTrue(preferences.contains("Vegetarian"), "Emma Wilson's preferences should include 'Vegetarian'.");
        } else if ("David Lee".equals(currentCustomer.getName())) {
            assertTrue(preferences.contains("None") || preferences.isEmpty(), "David Lee's preferences should be 'None' or empty.");
        } else if ("Maria Garcia".equals(currentCustomer.getName())) {
            assertTrue(preferences.contains("Vegan"), "Maria Garcia's preferences should include 'Vegan'.");
        }
    }

    @Then("I should see their food allergies clearly highlighted")
    public void iShouldSeeTheirFoodAllergiesClearlyHighlighted() {
        printCustomerState("Then I should see allergies - Before Asserts");
        assertNotNull(currentCustomer, "Customer profile should not be null");
        List<String> allergies = currentCustomer.getAllergies(); // Call to Customer.getAllergies()

        assertNotNull(allergies, "Allergies list should not be null.");
        if ("Emma Wilson".equals(currentCustomer.getName())) {
            assertTrue(allergies.contains("Peanuts"), "Emma Wilson's allergies should include 'Peanuts'.");
        } else if ("David Lee".equals(currentCustomer.getName())) {
            assertTrue(allergies.contains("Shellfish"), "David Lee's allergies should include 'Shellfish'.");
        } else if ("Maria Garcia".equals(currentCustomer.getName())) {
            assertTrue(allergies.isEmpty(), "Maria Garcia should have no allergies listed.");
        }
    }

    @Given("I am preparing a {string} dish")
    public void iAmPreparingADish(String dishName) {
        System.out.println("LOG [Given I am preparing a \"" + dishName + "\"] START");
        this.dishNameForScenario = dishName;
        Application.meals.clear(); // Clear previous meals for this scenario context
        Application.setSystemMessage(null);


        List<String> ingredients = new ArrayList<>();
        if (dishName.equalsIgnoreCase("seafood pasta")) {
            ingredients.add("Shellfish");
            ingredients.add("Pasta");
            ingredients.add("Tomato Sauce");
        } else if (dishName.equalsIgnoreCase("Peanut Chicken Stir-fry")) { // Example for another allergen
            ingredients.add("Chicken");
            ingredients.add("Peanuts");
            ingredients.add("Vegetables");
        } else {
            ingredients.add("Default Ingredient for " + dishName);
        }
        Meal meal = new Meal(dishName, ingredients, 'M', 15.99); // Call to Meal constructor
        Application.meals.add(meal); // Add to application's meal list
        System.out.println("LOG Added meal: " + meal.getName() + " ingredients: " + meal.getIngredients() + ". App.meals size: " + Application.meals.size());
    }

    @When("I check the profile of customer {string} who has a shellfish allergy")
    public void iCheckTheProfileOfCustomerWhoHasShellfishAllergy(String customerName) {
        System.out.println("LOG [When I check profile of \"" + customerName + "\"] START");
        // Setup customer (re-using existing step logic)
        iAccessTheProfileOfRegularCustomer(customerName); // This sets currentCustomer

        Meal currentMeal = Application.meals.stream()
                .filter(m -> m.getName().equalsIgnoreCase(this.dishNameForScenario))
                .findFirst()
                .orElse(null);

        assertNotNull(currentMeal, "Dish '" + this.dishNameForScenario + "' should exist in Application.meals.");
        System.out.println("LOG Checking meal: " + currentMeal.getName() + " ingredients: " + currentMeal.getIngredients());
        printCustomerState("When I check profile - for " + currentCustomer.getName());

        this.alternativeSuggested = false;
        // this.warningMessage = null; // Will use Application.setSystemMessage

        List<String> customerAllergies = currentCustomer.getAllergies(); // Call to Customer.getAllergies()
        List<String> mealIngredients = currentMeal.getIngredients(); // Call to Meal.getIngredients()

        boolean mealContainsAllergen = false;
        String foundAllergenInMeal = null;

        if (customerAllergies != null && !customerAllergies.isEmpty() &&
                mealIngredients != null && !mealIngredients.isEmpty()) {
            for (String allergy : customerAllergies) {
                if (mealIngredients.stream().anyMatch(mi -> mi.equalsIgnoreCase(allergy))) {
                    mealContainsAllergen = true;
                    foundAllergenInMeal = allergy;
                    break;
                }
            }
        }
        System.out.println("LOG mealContainsAllergen: " + mealContainsAllergen);

        if (mealContainsAllergen && foundAllergenInMeal != null) {
            String warningMsg = "WARNING: Customer " + currentCustomer.getName() +
                    " has an allergy to " + foundAllergenInMeal + " which is in " + currentMeal.getName() + "!";
            Application.setSystemMessage(warningMsg); // Call to Application.setSystemMessage()
            System.out.println("LOG Warning set via Application: " + Application.getSystemMessage());

            // Check for alternatives for the specific allergen found
            List<String> alternatives = Meal.suggestAlternative(foundAllergenInMeal); // Call to Meal.suggestAlternative()
            this.alternativeSuggested = alternatives != null && !alternatives.isEmpty();
            System.out.println("LOG Alternatives for '" + foundAllergenInMeal + "': " + alternatives + ", alternativeSuggested: " + this.alternativeSuggested);
        }
        System.out.println("LOG [When I check profile of \"" + customerName + "\"] END");
    }

    @Then("the system should display a prominent warning")
    public void theSystemShouldDisplayAProminentWarning() {
        String actualWarning = Application.getSystemMessage(); // Call to Application.getSystemMessage()
        System.out.println("LOG Retrieved System Warning Message: " + actualWarning);
        assertNotNull(actualWarning, "A system warning message should be set.");

        if (currentCustomer != null && "David Lee".equals(currentCustomer.getName()) && "seafood pasta".equalsIgnoreCase(dishNameForScenario)) {
            assertTrue(actualWarning.toLowerCase().contains("shellfish"), "Warning for David Lee and Seafood Pasta must mention shellfish.");
        }
        // Optionally clear message after check, or ensure @Given steps clear it
        // Application.setSystemMessage(null);
    }

    @Then("the system should suggest alternative ingredients to use")
    public void theSystemShouldSuggestAlternativeIngredientsToUse() {
        System.out.println("LOG Alternative Suggested flag: " + this.alternativeSuggested);
        // This assertion relies on `this.alternativeSuggested` being set correctly
        // by calling `Meal.suggestAlternative()` in the @When step.
        assertTrue(this.alternativeSuggested, "Alternative ingredients should have been identified and suggested.");
    }

    @Given("customer {string} has a {string} dietary preference")
    public void customerHasADietaryPreference(String name, String preference) {
        System.out.println("\nLOG [Given customer " + name + " has preference " + preference + "] START");
        Application.users.clear(); // Clear users for this scenario
        Application.meals.clear(); // Clear meals for this scenario
        Application.setSystemMessage(null);


        currentCustomer = new Customer(name, name.toLowerCase().replace(" ", "") + "@email.com", "pass123");
        currentCustomer.savePreferences(preference); // Call to Customer.savePreferences()
        Application.users.add(currentCustomer); // Add to application user list
        printCustomerState("Given customer " + name + " has preference " + preference + " - END");

        // Define meals for this specific scenario
        allMealsForFiltering = new ArrayList<>(List.of(
                new Meal("Vegan Salad", List.of("Lettuce", "Tomato", "Cucumber"), 'M', 10.99),
                new Meal("Grilled Chicken Salad", List.of("Chicken", "Lettuce", "Spices"), 'M', 12.99),
                new Meal("Vegan Bean Burger", List.of("Bean Patty", "Lettuce", "Vegan Bun"), 'M', 11.99),
                new Meal("Beef Steak Dinner", List.of("Beef", "Garlic", "Potatoes"), 'M', 18.99),
                new Meal("Vegetable Stir-fry", List.of("Broccoli", "Carrot", "Tofu", "Soy Sauce"), 'M', 11.50)
        ));
        // Add these meals to the application's context
        Application.meals.addAll(allMealsForFiltering);
    }

    @When("I search for suitable meal options")
    public void iSearchForSuitableMealOptions() {
        printCustomerState("When I search for meal options - Before filtering");
        assertNotNull(currentCustomer, "Customer must be set up.");
        List<String> currentPreferences = currentCustomer.getPreferences(); // Call to Customer.getPreferences()
        assertNotNull(currentPreferences, "Customer preferences must not be null.");
        assertFalse(currentPreferences.isEmpty(), "Customer must have at least one preference set for this step.");

        String preference = currentPreferences.get(0); // Assuming one primary preference for simplicity
        System.out.println("LOG Filtering for preference: " + preference);

        List<String> ingredientsToExclude = new ArrayList<>();
        if ("Vegan".equalsIgnoreCase(preference)) {
            // For "Vegan", exclude common animal products.
            ingredientsToExclude.addAll(Arrays.asList("Chicken", "Beef", "Fish", "Cheese", "Eggs", "Milk", "Honey", "Spices")); // Spices can be non-vegan
        } else if ("Vegetarian".equalsIgnoreCase(preference)) {
            // For "Vegetarian", exclude meats and fish.
            ingredientsToExclude.addAll(Arrays.asList("Chicken", "Beef", "Fish"));
        }
        // If preference is "None", ingredientsToExclude remains empty, so Application.exclude should return all meals.

        // The meals are already in Application.meals from the @Given step.
        filteredMeals = Application.exclude(ingredientsToExclude); // Call to Application.exclude()

        System.out.println("LOG Filtered meals count: " + (filteredMeals != null ? filteredMeals.size() : "null"));
        if (filteredMeals != null) {
            System.out.println("LOG Filtered meals (names): " + filteredMeals.stream().map(Meal::getName).collect(Collectors.toList()));
        }
    }

    // Helper method for verifying compliance based on preference (used in assertions)
    private boolean isMealCompliant(Meal meal, String preference) {
        Set<String> mealIngredientsLower = meal.getIngredients().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if ("Vegan".equalsIgnoreCase(preference)) {
            Set<String> nonVeganIngredients = Set.of("chicken", "beef", "fish", "cheese", "eggs", "milk", "honey", "spices"); // Assuming 'spices' from chicken salad could be non-vegan
            return nonVeganIngredients.stream().noneMatch(mealIngredientsLower::contains);
        } else if ("Vegetarian".equalsIgnoreCase(preference)) {
            Set<String> nonVegetarianIngredients = Set.of("chicken", "beef", "fish");
            return nonVegetarianIngredients.stream().noneMatch(mealIngredientsLower::contains);
        } else if ("None".equalsIgnoreCase(preference)) {
            return true; // All meals are compliant if no preference restricts them.
        }
        System.err.println("LOG Unknown preference for compliance check: " + preference + " in meal " + meal.getName());
        return false; // Default to non-compliant for unknown preferences
    }

    @Then("the system should only show vegan-compliant dishes")
    public void theSystemShouldOnlyShowVeganCompliantDishes() {
        assertNotNull(filteredMeals, "Filtered meal list (for Vegan) should not be null.");
        assertFalse(filteredMeals.isEmpty(), "Filtered meal list for Vegan preference should not be empty. Expected vegan meals like 'Vegan Salad'.");
        System.out.println("LOG Verifying vegan-compliant dishes. Found: " + filteredMeals.stream().map(Meal::getName).collect(Collectors.joining(", ")));

        for (Meal meal : filteredMeals) {
            assertTrue(isMealCompliant(meal, "Vegan"),
                    "Meal '" + meal.getName() + "' (Ingredients: " + meal.getIngredients() + ") was returned by Application.exclude() but is not vegan-compliant according to local check.");
        }
    }

    @Then("automatically exclude any containing animal products")
    public void automaticallyExcludeAnyContainingAnimalProducts() { // This step usually follows a "Vegan" preference context
        assertNotNull(filteredMeals, "Filtered meal list (for Vegan exclusion) should not be null.");
        System.out.println("LOG Verifying exclusion of animal products. Filtered meals: " + filteredMeals.stream().map(Meal::getName).collect(Collectors.joining(", ")));

        // Check that no meal in filteredMeals contains common animal products
        Set<String> animalProductKeywords = Set.of("chicken", "beef", "fish", "cheese", "eggs", "milk", "honey");
        for (Meal meal : filteredMeals) {
            boolean containsAnimalProduct = meal.getIngredients().stream()
                    .anyMatch(ing -> animalProductKeywords.contains(ing.toLowerCase()));
            assertFalse(containsAnimalProduct,
                    "Meal '" + meal.getName() + "' (Ingredients: " + meal.getIngredients() + ") appears to contain an animal product but should have been excluded for a Vegan preference by Application.exclude().");
        }

        // Specifically check that known non-vegan meals are NOT in the list
        List<String> knownNonVeganMealNames = Arrays.asList("Grilled Chicken Salad", "Beef Steak Dinner");
        List<String> filteredMealNames = filteredMeals.stream().map(Meal::getName).collect(Collectors.toList());

        for(String nonVeganMealName : knownNonVeganMealNames) {
            assertFalse(filteredMealNames.contains(nonVeganMealName),
                    "Meal '" + nonVeganMealName + "' (known non-vegan) should have been excluded by Application.exclude() for Vegan preference but was found in results.");
        }
    }
}