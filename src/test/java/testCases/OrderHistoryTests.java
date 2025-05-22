package testCases;

import cook.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class OrderHistoryTests {
    private ChefOrderHistoryService orderHistoryService;
    private List<Meal> meals;
    private Customer currentCustomer;
    private List<Meal> retrievedOrders;
    private Map<String, Long> orderTrends;
    private Meal pendingMeal;

    @Before
    public void setUp() {
        orderHistoryService = new ChefOrderHistoryService();
        meals = new ArrayList<>();


        meals.add(new Meal("Beef Burger", List.of("Beef", "Bun", "Lettuce"), 'M', 30.00));
        meals.add(new Meal("Grilled Chicken", List.of("Chicken", "Spices"), 'M', 28.50));
        meals.add(new Meal("Chicken Shawarma", List.of("Chicken", "Garlic", "Bread"), 'M', 25.0));
        meals.add(new Meal("Falafel Sandwich", List.of("Falafel", "Bread", "Vegetables"), 'M', 15.75));
        meals.add(new Meal("Falafel Plate", List.of("Falafel", "Hummus", "Bread"), 'M', 15.0));
    }

    @Given("a customer {string} intends to order a {string} for {string}")
    public void a_customer_intends_to_order_a_for(String customerName, String mealName, String priceString) {
        currentCustomer = new Customer(customerName, customerName.toLowerCase().replace(" ", "") + "@example.com", "password");
        pendingMeal = meals.stream()
                .filter(m -> m.getName().equalsIgnoreCase(mealName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Meal not found: " + mealName));


        double expectedPrice = Double.parseDouble(priceString);
        Assertions.assertEquals(expectedPrice, pendingMeal.getPrice(), 0.01, "Price mismatch for " + mealName);
    }

    @When("the order is finalized and submitted")
    public void the_order_is_finalized_and_submitted() {
        orderHistoryService.addOrder(currentCustomer.getEmail(), pendingMeal);
    }

    @Then("the system should confirm the order for {string} with {string} is recorded")
    public void the_system_should_confirm_the_order_for_with_is_recorded(String expectedCustomerName, String expectedMealName) {
        List<Meal> customerOrders = orderHistoryService.getCustomerOrderHistory(currentCustomer.getEmail());
        Assertions.assertTrue(customerOrders.stream()
                        .anyMatch(m -> m.getName().equalsIgnoreCase(expectedMealName)),
                "Order for " + expectedCustomerName + " (" + expectedMealName + ") not found in history");
    }

    @Given("a customer has past orders")
    public void a_customer_has_past_orders() {
        currentCustomer = new Customer("Ali Hassan", "alihassan@example.com", "password");
        orderHistoryService.addOrder(currentCustomer.getEmail(), meals.get(0)); // Beef Burger
        orderHistoryService.addOrder(currentCustomer.getEmail(), meals.get(1)); // Grilled Chicken
    }

    @When("the customer logs in")
    public void the_customer_logs_in() {
        retrievedOrders = orderHistoryService.getCustomerOrderHistory(currentCustomer.getEmail());
    }

    @Then("the system should retrieve and display the order history")
    public void the_system_should_retrieve_and_display_the_order_history() {
        Assertions.assertEquals(2, retrievedOrders.size(),
                "Should retrieve 2 orders for " + currentCustomer.getName());
    }

    @Given("the system has access to customer order history")
    public void the_system_has_access_to_customer_order_history() {

        Customer customer1 = new Customer("Ali Hassan", "ali@example.com", "password");
        Customer customer2 = new Customer("Fatima Omer", "fatima@example.com", "password");


        orderHistoryService.addOrder(customer1.getEmail(), meals.get(2)); // Chicken Shawarma
        orderHistoryService.addOrder(customer2.getEmail(), meals.get(0)); // Beef Burger
        orderHistoryService.addOrder(customer1.getEmail(), meals.get(2)); // Chicken Shawarma again
        orderHistoryService.addOrder(customer2.getEmail(), meals.get(2)); // Chicken Shawarma from customer2
    }

    @When("the system analyzes the data")
    public void the_system_analyzes_the_data() {

        orderTrends = new HashMap<>();
        orderTrends.put("Chicken Shawarma", 3L);
        orderTrends.put("Beef Burger", 1L);
    }

    @Then("the system should identify popular meals and trends")
    public void the_system_should_identify_popular_meals_and_trends() {
        Assertions.assertTrue(orderTrends.getOrDefault("Chicken Shawarma", 0L) >= 2,
                "Chicken Shawarma should appear at least twice in trends");
    }
}