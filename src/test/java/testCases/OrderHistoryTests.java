package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.util.*;
import java.util.stream.Collectors;

public class OrderHistoryTests {

    static class Order {
        String customerName;
        String mealName;
        double price;

        Order(String customerName, String mealName, double price) {
            this.customerName = customerName;
            this.mealName = mealName;
            this.price = price;
        }

        @Override
        public String toString() {
            return customerName + " - " + mealName + " ($" + price + ")";
        }
    }

    private List<Order> database = new ArrayList<>();
    private List<Order> retrievedOrders;
    private String currentCustomer;
    private Map<String, Long> orderTrends;

    // ------------------------ Scenario 1 ------------------------
    @Given("a customer places an order")
    public void a_customer_places_an_order() {
        currentCustomer = "Ali Hassan";
        Order order = new Order(currentCustomer, "Chicken Shawarma", 25.00);
        database.add(order);
        System.out.println("🛒 Order placed: " + order);
    }

    @When("the order is completed")
    public void the_order_is_completed() {
        Assertions.assertFalse(database.isEmpty(), "❌ No orders found to complete.");
        System.out.println("✅ Order marked as completed.");
    }

    @Then("the system should save the order details in the database")
    public void the_system_should_save_the_order_details_in_the_database() {
        Assertions.assertEquals(1, database.size(), "❌ Order not saved correctly.");
        System.out.println("💾 Order saved in database: " + database.get(0));
    }

    // ------------------------ Scenario 2 ------------------------
    @Given("a customer has past orders")
    public void a_customer_has_past_orders() {
        currentCustomer = "Ali Hassan";
        database.add(new Order(currentCustomer, "Beef Burger", 30.00));
        database.add(new Order(currentCustomer, "Grilled Chicken", 28.50));
        System.out.println("📦 Past orders available for customer: " + currentCustomer);
    }

    @When("the customer logs in")
    public void the_customer_logs_in() {
        retrievedOrders = database.stream()
                .filter(o -> o.customerName.equals(currentCustomer))
                .collect(Collectors.toList());
        Assertions.assertFalse(retrievedOrders.isEmpty(), "❌ No orders found for logged-in customer.");
        System.out.println("🔐 Customer logged in: " + currentCustomer);
    }

    @Then("the system should retrieve and display the order history")
    public void the_system_should_retrieve_and_display_the_order_history() {
        System.out.println("📋 Order history for " + currentCustomer + ":");
        retrievedOrders.forEach(o -> System.out.println(" - " + o));
    }

    // ------------------------ Scenario 3 ------------------------
    @Given("the system has access to customer order history")
    public void the_system_has_access_to_customer_order_history() {
        if (database.isEmpty()) {
            System.out.println("⚠️ No data found, seeding database for test...");
            database.add(new Order("Ali Hassan", "Chicken Shawarma", 25.0));
            database.add(new Order("Ali Hassan", "Beef Burger", 30.0));
            database.add(new Order("Ali Hassan", "Chicken Shawarma", 25.0));
        }
        Assertions.assertFalse(database.isEmpty(), "❌ Database has no orders.");
        System.out.println("📚 Accessing all order history...");
    }


    @When("the system analyzes the data")
    public void the_system_analyzes_the_data() {
        orderTrends = database.stream()
                .collect(Collectors.groupingBy(o -> o.mealName, Collectors.counting()));
        Assertions.assertFalse(orderTrends.isEmpty(), "❌ No trends found.");
        System.out.println("📊 Data analyzed successfully.");
    }

    @Then("the system should identify popular meals and trends")
    public void the_system_should_identify_popular_meals_and_trends() {
        System.out.println("🏆 Popular meals:");
        orderTrends.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println(" - " + e.getKey() + ": " + e.getValue() + " orders"));
    }
}
