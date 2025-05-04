package testCases;

import io.cucumber.java.en.*;
import java.util.*;


public class PastMealOrdersSteps {

    private boolean loggedIn;
    private List<Order> pastOrders = new ArrayList<>();
    private List<Order> filteredOrders = new ArrayList<>();
    private List<String> cart = new ArrayList<>();
    private String confirmationMessage;

    private static class Order {
        String mealName;
        String orderDate; // ISO format: yyyy-MM-dd
        double totalPrice;

        Order(String mealName, String orderDate, double totalPrice) {
            this.mealName = mealName;
            this.orderDate = orderDate;
            this.totalPrice = totalPrice;
        }
    }



    @Given("I am logged in with existing order history")
    public void iAmLoggedInWithExistingOrderHistory() {
        loggedIn = true;
        pastOrders = Arrays.asList(
                new Order("Vegetable Curry", "2025-04-15", 12.99),
                new Order("Chicken Biryani", "2025-03-10", 14.50),
                new Order("Pasta Alfredo", "2025-01-20", 11.75)
        );
    }

    @When("I navigate to my order history page")
    public void iNavigateToMyOrderHistoryPage() {
        // No-op for simulation
    }

    @Then("I should see a chronological list of my past orders")
    public void iShouldSeeAChronologicalListOfMyPastOrders() {
        pastOrders.sort(Comparator.comparing(o -> o.orderDate));
    }

    @Then("each order should display the order date, total price, and reorder button")
    public void eachOrderShouldDisplayEssentialDetails() {
        for (Order order : pastOrders) {
            if (order.orderDate == null || order.totalPrice <= 0 || order.mealName == null) {
                throw new IllegalStateException("Order details are incomplete.");
            }
        }
    }

    @Given("I am viewing my order history")
    public void iAmViewingMyOrderHistory() {
        if (pastOrders.isEmpty()) {
            throw new IllegalStateException("No past orders to display.");
        }
    }

    @When("I click \"Reorder\" on an order containing \"Vegetable Curry\"")
    public void iClickReorderOnVegetableCurry() {
        Order order = pastOrders.stream()
                .filter(o -> o.mealName.equals("Vegetable Curry"))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Order not found."));
        cart.add(order.mealName);
        confirmationMessage = "Meal added to cart!";
    }

    @Then("\"Vegetable Curry\" should be added to my current cart")
    public void vegetableCurryShouldBeAddedToCart() {
        if (!cart.contains("Vegetable Curry")) {
            throw new AssertionError("Meal not added to cart.");
        }
    }

    @Then("I should see a confirmation message \"Meal added to cart!\"")
    public void iShouldSeeConfirmationMessage() {
        if (!"Meal added to cart!".equals(confirmationMessage)) {
            throw new AssertionError("Confirmation message incorrect.");
        }
    }

    @Given("the customer is viewing past orders")
    public void theCustomerIsViewingPastOrders() {
        if (pastOrders.isEmpty()) {
            throw new IllegalStateException("No orders to filter.");
        }
    }

    @When("the customer filters orders by a specific date range")
    public void theCustomerFiltersOrdersByASpecificDateRange() {
        String start = "2025-03-01";
        String end = "2025-04-30";

        filteredOrders = new ArrayList<>();
        for (Order o : pastOrders) {
            if (o.orderDate.compareTo(start) >= 0 && o.orderDate.compareTo(end) <= 0) {
                filteredOrders.add(o);
            }
        }
        filteredOrders.sort((a, b) -> b.orderDate.compareTo(a.orderDate)); // Newest first
    }

    @Then("the system should display only orders within that range")
    public void theSystemShouldDisplayOnlyOrdersWithinThatRange() {
        for (Order o : filteredOrders) {
            if (o.orderDate.compareTo("2025-03-01") < 0 || o.orderDate.compareTo("2025-04-30") > 0) {
                throw new AssertionError("Order out of range.");
            }
        }
    }

    @Then("the orders should be sorted from newest to oldest")
    public void theOrdersShouldBeSortedFromNewestToOldest() {
        for (int i = 0; i < filteredOrders.size() - 1; i++) {
            String current = filteredOrders.get(i).orderDate;
            String next = filteredOrders.get(i + 1).orderDate;
            if (current.compareTo(next) < 0) {
                throw new AssertionError("Orders not sorted correctly.");
            }
        }
    }

    @Then("if no orders match, a message {string} should be shown")
    public void ifNoOrdersMatchAMessageShouldBeShown(String expectedMessage) {
        if (filteredOrders.isEmpty() && !"No orders found in this range.".equals(expectedMessage)) {
            throw new AssertionError("Expected message not shown.");
        }
    }
}
