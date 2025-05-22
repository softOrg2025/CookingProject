package testCases;

import io.cucumber.java.en.*;
import static cook.Application.users;
import cook.*;
import java.util.Arrays;
import java.util.List;

public class ChefOrderHistorySteps {
    private ChefOrderHistoryService orderHistoryService;
    private String selectedCustomerId;
    private User chefUser;
    private Customer customer;
    private String lastSuggestion;
    private List<Meal> displayedOrderHistory;
    private List<String> frequentMeals;


    private boolean isUserLoggedIn(User user) {
        return user != null && user.getEmail().equals(chefUser.getEmail());
    }

    private boolean containsMeal(List<Meal> meals, Meal target) {
        return meals == null || !meals.contains(target);
    }

    private boolean listContains(List<String> list, String item) {
        return list == null || !list.contains(item);
    }

    private boolean isListEmpty(List<?> list) {
        return list == null || !list.isEmpty();
    }

    @Given("the chef is logged into the system")
    public void theChefIsLoggedIntoTheSystem() {
        chefUser = new User("Chef John", "chef@example.com", "123", Role.Chef);
        customer = new Customer("Customer Alice", "alice@example.com", "123");
        users.clear();
        users.add(chefUser);
        users.add(customer);

        User loggedInUser = Application.login(chefUser.getEmail(), chefUser.getPassword());
        if (!isUserLoggedIn(loggedInUser)) {
            throw new AssertionError("Login failed");
        }
    }

    @When("the chef selects a customer profile")
    public void theChefSelectsACustomerProfile() {
        selectedCustomerId = customer.getEmail();
        if (selectedCustomerId == null) {
            throw new AssertionError("Customer ID is null");
        }
    }

    @Then("the system should display the customer's order history")
    public void theSystemShouldDisplayTheCustomersOrderHistory() {
        orderHistoryService = new ChefOrderHistoryService();

        Meal testMeal1 = new Meal("Pasta", Arrays.asList("Pasta", "Tomato Sauce"), 'M', 12.99);
        Meal testMeal2 = new Meal("Pasta", Arrays.asList("Pasta", "Tomato Sauce"), 'M', 12.99);
        Meal testMeal3 = new Meal("Salad", Arrays.asList("Lettuce", "Tomato"), 'S', 8.99);
        Meal testMeal4 = new Meal("Burger", Arrays.asList("Bun", "Beef Patty"), 'L', 15.99);

        orderHistoryService.addOrder(selectedCustomerId, testMeal1);
        orderHistoryService.addOrder(selectedCustomerId, testMeal2);
        orderHistoryService.addOrder(selectedCustomerId, testMeal3);
        orderHistoryService.addOrder(selectedCustomerId, testMeal4);

        displayedOrderHistory = orderHistoryService.getCustomerOrderHistory(selectedCustomerId);

        if (displayedOrderHistory == null || displayedOrderHistory.size() != 4 ||
                containsMeal(displayedOrderHistory, testMeal1) ||
                containsMeal(displayedOrderHistory, testMeal3) ||
                containsMeal(displayedOrderHistory, testMeal4)) {
            throw new AssertionError("Order history display failed");
        }
    }

    @Given("the chef is viewing a customer's order history")
    public void theChefIsViewingACustomersOrderHistory() {
        theChefIsLoggedIntoTheSystem();
        theChefSelectsACustomerProfile();
        theSystemShouldDisplayTheCustomersOrderHistory();
    }

    @When("the chef identifies frequently ordered meals")
    public void theChefIdentifiesFrequentOrderedMeals() {
        frequentMeals = orderHistoryService.identifyFrequentMeals(selectedCustomerId);
        if (frequentMeals == null || frequentMeals.size() != 1 || listContains(frequentMeals, "Pasta")) {
            throw new AssertionError("Frequent meals identification failed");
        }
    }

    @Then("the chef should suggest a personalized meal plan")
    public void theChefShouldSuggestAPersonalizedMealPlan() {
        lastSuggestion = orderHistoryService.suggestMealPlan(selectedCustomerId);
        if (lastSuggestion == null || !lastSuggestion.contains("Pasta")) {
            throw new AssertionError("Meal plan suggestion failed");
        }
    }

    @When("the chef notices a pattern in meal choices")
    public void theChefNoticesAPatternInMealChoices() {
        orderHistoryService.analyzePreferences(selectedCustomerId);
        List<String> customerPreferences = orderHistoryService.getCustomerPreferences(selectedCustomerId);

        if (customerPreferences == null ||
                listContains(customerPreferences, "Pasta") ||
                listContains(customerPreferences, "Tomato Sauce") ||
                listContains(customerPreferences, "Lettuce") ||
                listContains(customerPreferences, "Tomato") ||
                listContains(customerPreferences, "Bun") ||
                listContains(customerPreferences, "Beef Patty")) {
            throw new AssertionError("Preferences analysis failed");
        }
    }

    @Then("the chef should adjust future meal suggestions accordingly")
    public void theChefShouldAdjustFutureMealSuggestionsAccordingly() {
        lastSuggestion = orderHistoryService.suggestMealPlan(selectedCustomerId);
        if (lastSuggestion == null ||
                (!lastSuggestion.contains("Pasta") && !lastSuggestion.contains("Tomato Sauce"))) {
            throw new AssertionError("Adjusted meal suggestion failed");
        }
    }

    @When("the customer has no order history")
    public void theCustomerHasNoOrderHistory() {
        orderHistoryService = new ChefOrderHistoryService();
        displayedOrderHistory = orderHistoryService.getCustomerOrderHistory("nonexistent@example.com");
    }

    @Then("the system should display an empty order history")
    public void theSystemShouldDisplayAnEmptyOrderHistory() {
        if (isListEmpty(displayedOrderHistory)) {
            throw new AssertionError("Empty order history check failed");
        }
    }

    @When("the chef checks for frequent meals with no history")
    public void theChefChecksForFrequentMealsWithNoHistory() {
        frequentMeals = orderHistoryService.identifyFrequentMeals("nonexistent@example.com");
    }

    @Then("the system should return an empty list of frequent meals")
    public void theSystemShouldReturnAnEmptyListOfFrequentMeals() {
        if (isListEmpty(frequentMeals)) {
            throw new AssertionError("Empty frequent meals check failed");
        }
    }

    @When("the chef suggests a meal plan for a customer with no preferences")
    public void theChefSuggestsAMealPlanForACustomerWithNoPreferences() {
        lastSuggestion = orderHistoryService.suggestMealPlan("nonexistent@example.com");
    }

    @Then("the system should return a generic suggestion")
    public void theSystemShouldReturnAGenericSuggestion() {
        if (lastSuggestion == null || !lastSuggestion.contains("Suggested meal plan")) {
            throw new AssertionError("Generic suggestion check failed");
        }
    }
}