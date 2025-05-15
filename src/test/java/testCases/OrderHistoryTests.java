package testCases;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderHistoryTests {


    static class Order {
        String customerName;
        String mealName;
        double price;
        boolean recorded = false;

        Order(String customerName, String mealName, double price) {
            this.customerName = customerName;
            this.mealName = mealName;
            this.price = price;
        }

        public void markAsRecorded() {
            this.recorded = true;
        }

        @Override
        public String toString() {
            return customerName + " - " + mealName + " ($" + price + ")" + (recorded ? " [Recorded]" : " [Pending]");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Order order = (Order) o;
            return Double.compare(order.price, price) == 0 &&
                    Objects.equals(customerName, order.customerName) &&
                    Objects.equals(mealName, order.mealName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(customerName, mealName, price);
        }
    }

    private List<Order> database;
    private List<Order> retrievedOrders;
    private Map<String, Long> orderTrends;
    private String currentLoggedInCustomer;


    private Order pendingOrder;


    @Before
    public void setUp() {
        System.out.println("--- Initializing state for new scenario ---");
        database = new ArrayList<>();
        retrievedOrders = null;
        orderTrends = null;
        currentLoggedInCustomer = null;
        pendingOrder = null;
    }


    @Given("a customer {string} intends to order a {string} for {string}")
    public void a_customer_intends_to_order_a_for(String customerName, String mealName, String priceString) {
        System.out.println("STEP: Given a customer \"" + customerName + "\" intends to order a \"" + mealName + "\" for \"" + priceString + "\"");
        try {
            double price = Double.parseDouble(priceString);
            pendingOrder = new Order(customerName, mealName, price);
            System.out.println("  Created pending order: " + pendingOrder);
            Assertions.assertNotNull(pendingOrder, "Pending order should have been created.");
        } catch (NumberFormatException e) {
            Assertions.fail("Invalid price format: " + priceString, e);
        }
    }

    @When("the order is finalized and submitted")
    public void the_order_is_finalized_and_submitted() {
        System.out.println("STEP: When the order is finalized and submitted");
        Assertions.assertNotNull(pendingOrder, "Cannot finalize a null order. 'Given' step might have failed.");

        database.add(pendingOrder);
        pendingOrder.markAsRecorded();
        System.out.println("  Order finalized and added to database: " + pendingOrder);
        Assertions.assertTrue(pendingOrder.recorded, "Order should be marked as recorded.");
        Assertions.assertTrue(database.contains(pendingOrder), "Order should be in the database.");
    }

    @Then("the system should confirm the order for {string} with {string} is recorded")
    public void the_system_should_confirm_the_order_for_with_is_recorded(String expectedCustomerName, String expectedMealName) {
        System.out.println("STEP: Then the system should confirm the order for \"" + expectedCustomerName + "\" with \"" + expectedMealName + "\" is recorded");
        Assertions.assertNotNull(pendingOrder, "Cannot confirm a null order. Previous steps might have failed.");
        Assertions.assertTrue(pendingOrder.recorded, "Order was expected to be recorded, but it is not.");
        Assertions.assertEquals(expectedCustomerName, pendingOrder.customerName, "Recorded order customer name mismatch.");
        Assertions.assertEquals(expectedMealName, pendingOrder.mealName, "Recorded order meal name mismatch.");


        Optional<Order> foundOrderInDb = database.stream()
                .filter(o -> o.customerName.equals(expectedCustomerName) && o.mealName.equals(expectedMealName) && o.recorded)
                .findFirst();
        Assertions.assertTrue(foundOrderInDb.isPresent(),
                "Order for " + expectedCustomerName + " (" + expectedMealName + ") not found as recorded in the database.");
        System.out.println("  Confirmation: Order for " + foundOrderInDb.get().customerName + " (" + foundOrderInDb.get().mealName + ") is indeed recorded.");
    }



    @Given("a customer has past orders")
    public void a_customer_has_past_orders() {

        currentLoggedInCustomer = "Ali Hassan";
        database.add(new Order(currentLoggedInCustomer, "Beef Burger", 30.00));
        database.get(0).markAsRecorded();
        database.add(new Order(currentLoggedInCustomer, "Grilled Chicken", 28.50));
        database.get(1).markAsRecorded();
        System.out.println("📦 Past orders set up for customer: " + currentLoggedInCustomer + ". Total orders in DB: " + database.size());
    }

    @When("the customer logs in")
    public void the_customer_logs_in() {
        Assertions.assertNotNull(currentLoggedInCustomer, "Customer for login must be set in 'Given' step.");
        System.out.println("🔐 Customer attempting to log in: " + currentLoggedInCustomer);
        retrievedOrders = database.stream()
                .filter(o -> o.customerName.equals(currentLoggedInCustomer) && o.recorded)
                .collect(Collectors.toList());
        if (retrievedOrders.isEmpty()) {
            System.out.println("⚠️ No recorded orders found for logged-in customer: " + currentLoggedInCustomer);
        } else {
            System.out.println("  Logged in: " + currentLoggedInCustomer + ". Found " + retrievedOrders.size() + " recorded order(s).");
        }
        Assertions.assertFalse(retrievedOrders.isEmpty(), "❌ No recorded orders found for logged-in customer: " + currentLoggedInCustomer);
    }

    @Then("the system should retrieve and display the order history")
    public void the_system_should_retrieve_and_display_the_order_history() {
        Assertions.assertNotNull(retrievedOrders, "❌ Order history not retrieved.");
        Assertions.assertFalse(retrievedOrders.isEmpty(), "❌ Retrieved order history is empty for " + currentLoggedInCustomer);
        System.out.println("📋 Order history for " + currentLoggedInCustomer + ":");
        retrievedOrders.forEach(o -> System.out.println(" - " + o));
        long expectedCount = database.stream().filter(o -> o.customerName.equals(currentLoggedInCustomer) && o.recorded).count();
        Assertions.assertEquals(expectedCount, retrievedOrders.size(), "❌ Mismatch in retrieved order count.");
    }


    @Given("the system has access to customer order history")
    public void the_system_has_access_to_customer_order_history() {
        if (database.isEmpty()) {
            System.out.println("⚠️ Database is empty, seeding for trend analysis...");
            Order o1 = new Order("Ali Hassan", "Chicken Shawarma", 25.0); o1.markAsRecorded(); database.add(o1);
            Order o2 = new Order("Fatima Omer", "Beef Burger", 30.0); o2.markAsRecorded(); database.add(o2);
            Order o3 = new Order("Ali Hassan", "Chicken Shawarma", 25.0); o3.markAsRecorded(); database.add(o3);
            Order o4 = new Order("Youssef Said", "Falafel Plate", 15.0); o4.markAsRecorded(); database.add(o4);
            Order o5 = new Order("Fatima Omer", "Chicken Shawarma", 25.0); o5.markAsRecorded(); database.add(o5);
            Order o6 = new Order("Ali Hassan", "Falafel Plate", 15.0); o6.markAsRecorded(); database.add(o6);
        }
        Assertions.assertFalse(database.isEmpty(), "❌ Database should have orders for analysis.");
        System.out.println("📚 Accessing all order history (" + database.size() + " orders).");
    }

    @When("the system analyzes the data")
    public void the_system_analyzes_the_data() {
        Assertions.assertFalse(database.isEmpty(), "❌ Cannot analyze empty database.");
        orderTrends = database.stream()
                .filter(o -> o.recorded)
                .collect(Collectors.groupingBy(o -> o.mealName, Collectors.counting()));

        System.out.println("📊 Data analyzed successfully. Trends found for " + (orderTrends != null ? orderTrends.size() : 0) + " meal(s).");
    }

    @Then("the system should identify popular meals and trends")
    public void the_system_should_identify_popular_meals_and_trends() {
        Assertions.assertNotNull(orderTrends, "❌ Order trends not generated.");
        if (orderTrends.isEmpty()) {
            System.out.println("ℹ️ No specific meal trends identified (perhaps not enough data or variety).");
            return;
        }
        System.out.println("🏆 Popular meals and trends:");
        orderTrends.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> System.out.println(" - " + e.getKey() + ": " + e.getValue() + " orders"));


    }
}