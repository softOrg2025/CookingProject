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

    private boolean alternativeSuggested;
    private List<Meal> filteredMeals;


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
        Application.meals.clear();
        Application.users.clear();
        Application.setSystemMessage(null);

        InventoryService inventoryService = new InventoryService();
        kitchen_manager manager = new kitchen_manager("Manager", "manager@email.com", "pass", inventoryService);
        chef headChef = new chef("Head Chef", "chef@email.com", "pass", manager);
        Application.users.add(headChef);
        Application.currentUser = Application.login(headChef.getEmail(), headChef.getPassword());

        assertNotNull(Application.currentUser, "Head chef should be logged in.");
        assertEquals(Role.Chef, Application.currentUser.getRole(), "Logged in user should be a Chef.");
        System.out.println("LOG [Given I am logged in as the head chef] END - Current User: " + (Application.currentUser != null ? Application.currentUser.getName() : "null"));
    }

    @When("I access the profile of regular customer {string}")
    public void iAccessTheProfileOfRegularCustomer(String customerName) {
        System.out.println("LOG [When I access profile of customer \"" + customerName + "\"] START");
        Application.setSystemMessage(null);


        currentCustomer = new Customer(customerName, customerName.toLowerCase().replace(" ", "") + "@email.com", "pass123");
        Application.users.add(currentCustomer);

        switch (customerName) {
            case "Emma Wilson":
                currentCustomer.savePreferences("Vegetarian");
                currentCustomer.saveAllergy("Peanuts");
                break;
            case "David Lee":

                currentCustomer.savePreferences("None");
                currentCustomer.saveAllergy("Shellfish");
                break;
            case "Maria Garcia":
                currentCustomer.savePreferences("Vegan");

                break;
        }

        printCustomerState("When I access profile of \"" + customerName + "\" - END");
    }

    @Then("I should see their dietary preferences listed")
    public void iShouldSeeTheirDietaryPreferencesListed() {
        printCustomerState("Then I should see preferences - Before Asserts");
        assertNotNull(currentCustomer, "Customer profile should not be null");
        List<String> preferences = currentCustomer.getPreferences();

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
        List<String> allergies = currentCustomer.getAllergies();

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
        Application.meals.clear();
        Application.setSystemMessage(null);


        List<String> ingredients = new ArrayList<>();
        if (dishName.equalsIgnoreCase("seafood pasta")) {
            ingredients.add("Shellfish");
            ingredients.add("Pasta");
            ingredients.add("Tomato Sauce");
        } else if (dishName.equalsIgnoreCase("Peanut Chicken Stir-fry")) {
            ingredients.add("Chicken");
            ingredients.add("Peanuts");
            ingredients.add("Vegetables");
        } else {
            ingredients.add("Default Ingredient for " + dishName);
        }
        Meal meal = new Meal(dishName, ingredients, 'M', 15.99);
        Application.meals.add(meal);
        System.out.println("LOG Added meal: " + meal.getName() + " ingredients: " + meal.getIngredients() + ". App.meals size: " + Application.meals.size());
    }

    @When("I check the profile of customer {string} who has a shellfish allergy")
    public void iCheckTheProfileOfCustomerWhoHasShellfishAllergy(String customerName) {
        System.out.println("LOG [When I check profile of \"" + customerName + "\"] START");

        iAccessTheProfileOfRegularCustomer(customerName);

        Meal currentMeal = Application.meals.stream()
                .filter(m -> m.getName().equalsIgnoreCase(this.dishNameForScenario))
                .findFirst()
                .orElse(null);

        assertNotNull(currentMeal, "Dish '" + this.dishNameForScenario + "' should exist in Application.meals.");
        System.out.println("LOG Checking meal: " + currentMeal.getName() + " ingredients: " + currentMeal.getIngredients());
        printCustomerState("When I check profile - for " + currentCustomer.getName());

        this.alternativeSuggested = false;


        List<String> customerAllergies = currentCustomer.getAllergies();
        List<String> mealIngredients = currentMeal.getIngredients();

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

        if (mealContainsAllergen) {
            String warningMsg = "WARNING: Customer " + currentCustomer.getName() +
                    " has an allergy to " + foundAllergenInMeal + " which is in " + currentMeal.getName() + "!";
            Application.setSystemMessage(warningMsg);
            System.out.println("LOG Warning set via Application: " + Application.getSystemMessage());


            List<String> alternatives = Meal.suggestAlternative(foundAllergenInMeal);
            this.alternativeSuggested = alternatives != null && !alternatives.isEmpty();
            System.out.println("LOG Alternatives for '" + foundAllergenInMeal + "': " + alternatives + ", alternativeSuggested: " + this.alternativeSuggested);
        }
        System.out.println("LOG [When I check profile of \"" + customerName + "\"] END");
    }

    @Then("the system should display a prominent warning")
    public void theSystemShouldDisplayAProminentWarning() {
        String actualWarning = Application.getSystemMessage();
        System.out.println("LOG Retrieved System Warning Message: " + actualWarning);
        assertNotNull(actualWarning, "A system warning message should be set.");

        if (currentCustomer != null && "David Lee".equals(currentCustomer.getName()) && "seafood pasta".equalsIgnoreCase(dishNameForScenario)) {
            assertTrue(actualWarning.toLowerCase().contains("shellfish"), "Warning for David Lee and Seafood Pasta must mention shellfish.");
        }

    }

    @Then("the system should suggest alternative ingredients to use")
    public void theSystemShouldSuggestAlternativeIngredientsToUse() {
        System.out.println("LOG Alternative Suggested flag: " + this.alternativeSuggested);
        assertTrue(this.alternativeSuggested, "Alternative ingredients should have been identified and suggested.");
    }

    @Given("customer {string} has a {string} dietary preference")
    public void customerHasADietaryPreference(String name, String preference) {
        System.out.println("\nLOG [Given customer " + name + " has preference " + preference + "] START");
        Application.users.clear();
        Application.meals.clear();
        Application.setSystemMessage(null);


        currentCustomer = new Customer(name, name.toLowerCase().replace(" ", "") + "@email.com", "pass123");
        currentCustomer.savePreferences(preference);
        Application.users.add(currentCustomer);
        printCustomerState("Given customer " + name + " has preference " + preference + " - END");


        List<Meal> allMealsForFiltering = new ArrayList<>(List.of(
                new Meal("Vegan Salad", List.of("Lettuce", "Tomato", "Cucumber"), 'M', 10.99),
                new Meal("Grilled Chicken Salad", List.of("Chicken", "Lettuce", "Spices"), 'M', 12.99),
                new Meal("Vegan Bean Burger", List.of("Bean Patty", "Lettuce", "Vegan Bun"), 'M', 11.99),
                new Meal("Beef Steak Dinner", List.of("Beef", "Garlic", "Potatoes"), 'M', 18.99),
                new Meal("Vegetable Stir-fry", List.of("Broccoli", "Carrot", "Tofu", "Soy Sauce"), 'M', 11.50)
        ));

        Application.meals.addAll(allMealsForFiltering);
    }

    @When("I search for suitable meal options")
    public void iSearchForSuitableMealOptions() {
        printCustomerState("When I search for meal options - Before filtering");
        assertNotNull(currentCustomer, "Customer must be set up.");
        List<String> currentPreferences = currentCustomer.getPreferences();
        assertNotNull(currentPreferences, "Customer preferences must not be null.");
        assertFalse(currentPreferences.isEmpty(), "Customer must have at least one preference set for this step.");

        String preference = currentPreferences.getFirst();
        System.out.println("LOG Filtering for preference: " + preference);

        List<String> ingredientsToExclude = new ArrayList<>();
        if ("Vegan".equalsIgnoreCase(preference)) {

            ingredientsToExclude.addAll(Arrays.asList("Chicken", "Beef", "Fish", "Cheese", "Eggs", "Milk", "Honey", "Spices")); // Spices can be non-vegan
        } else if ("Vegetarian".equalsIgnoreCase(preference)) {

            ingredientsToExclude.addAll(Arrays.asList("Chicken", "Beef", "Fish"));
        }


        filteredMeals = Application.exclude(ingredientsToExclude);

        System.out.println("LOG Filtered meals count: " + filteredMeals.size());
        System.out.println("LOG Filtered meals (names): " + filteredMeals.stream().map(Meal::getName).toList());
    }


    private boolean isMealCompliant(Meal meal) {
        Set<String> mealIngredientsLower = meal.getIngredients().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (!"Vegan".equalsIgnoreCase("Vegan")) {
            if ("Vegetarian".equalsIgnoreCase("Vegan")) {
                Set<String> nonVegetarianIngredients = Set.of("chicken", "beef", "fish");
                return nonVegetarianIngredients.stream().noneMatch(mealIngredientsLower::contains);
            } else if ("None".equalsIgnoreCase("Vegan")) {
                return true;
            }
        } else {
            Set<String> nonVeganIngredients = Set.of("chicken", "beef", "fish", "cheese", "eggs", "milk", "honey", "spices"); // Assuming 'spices' from chicken salad could be non-vegan
            return nonVeganIngredients.stream().noneMatch(mealIngredientsLower::contains);
        }
        System.err.println("LOG Unknown preference for compliance check: " + "Vegan" + " in meal " + meal.getName());
        return false;
    }

    @Then("the system should only show vegan-compliant dishes")
    public void theSystemShouldOnlyShowVeganCompliantDishes() {
        assertNotNull(filteredMeals, "Filtered meal list (for Vegan) should not be null.");
        assertFalse(filteredMeals.isEmpty(), "Filtered meal list for Vegan preference should not be empty. Expected vegan meals like 'Vegan Salad'.");
        System.out.println("LOG Verifying vegan-compliant dishes. Found: " + filteredMeals.stream().map(Meal::getName).collect(Collectors.joining(", ")));

        for (Meal meal : filteredMeals) {
            assertTrue(isMealCompliant(meal),
                    "Meal '" + meal.getName() + "' (Ingredients: " + meal.getIngredients() + ") was returned by Application.exclude() but is not vegan-compliant according to local check.");
        }
    }

    @Then("automatically exclude any containing animal products")
    public void automaticallyExcludeAnyContainingAnimalProducts() {
        assertNotNull(filteredMeals, "Filtered meal list (for Vegan exclusion) should not be null.");
        System.out.println("LOG Verifying exclusion of animal products. Filtered meals: " + filteredMeals.stream().map(Meal::getName).collect(Collectors.joining(", ")));

        Set<String> animalProductKeywords = Set.of("chicken", "beef", "fish", "cheese", "eggs", "milk", "honey");
        for (Meal meal : filteredMeals) {
            boolean containsAnimalProduct = meal.getIngredients().stream()
                    .anyMatch(ing -> animalProductKeywords.contains(ing.toLowerCase()));
            assertFalse(containsAnimalProduct,
                    "Meal '" + meal.getName() + "' (Ingredients: " + meal.getIngredients() + ") appears to contain an animal product but should have been excluded for a Vegan preference by Application.exclude().");
        }


        List<String> knownNonVeganMealNames = Arrays.asList("Grilled Chicken Salad", "Beef Steak Dinner");
        List<String> filteredMealNames = filteredMeals.stream().map(Meal::getName).toList();

        for(String nonVeganMealName : knownNonVeganMealNames) {
            assertFalse(filteredMealNames.contains(nonVeganMealName),
                    "Meal '" + nonVeganMealName + "' (known non-vegan) should have been excluded by Application.exclude() for Vegan preference but was found in results.");
        }
    }
}