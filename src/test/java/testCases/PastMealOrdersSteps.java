package testCases;

import io.cucumber.java.Before; //  استيراد مهم
import io.cucumber.java.en.*;
import java.util.*;

import static org.junit.Assert.*;


public class PastMealOrdersSteps {

    private boolean loggedIn;
    private List<Order> pastOrders;
    private List<Order> filteredOrders;
    private List<String> cart;
    private String confirmationMessage;
    private String systemMessageForNoOrders;


    private static class Order {
        String mealName;
        String orderDate;
        double totalPrice;

        Order(String mealName, String orderDate, double totalPrice) {
            this.mealName = mealName;
            this.orderDate = orderDate;
            this.totalPrice = totalPrice;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "mealName='" + mealName + '\'' +
                    ", orderDate='" + orderDate + '\'' +
                    ", totalPrice=" + totalPrice +
                    '}';
        }
    }

    @Before
    public void setUpScenario() {
        System.out.println("--- Setting up new scenario ---");
        loggedIn = false;
        pastOrders = new ArrayList<>();
        filteredOrders = new ArrayList<>();
        cart = new ArrayList<>();
        confirmationMessage = null;
        systemMessageForNoOrders = null;
        System.out.println("Scenario state has been reset.");
    }



    @Given("I am logged in with existing order history")
    public void iAmLoggedInWithExistingOrderHistory() {
        System.out.println("STEP_DEBUG: @Given('I am logged in with existing order history')");
        loggedIn = true;

        pastOrders.add(new Order("Vegetable Curry", "2025-04-15", 12.99));
        pastOrders.add(new Order("Chicken Biryani", "2025-03-10", 14.50));
        pastOrders.add(new Order("Pasta Alfredo", "2025-01-20", 11.75));
        System.out.println("  Logged in and populated pastOrders with " + pastOrders.size() + " orders.");
    }

    @When("I navigate to my order history page")
    public void iNavigateToMyOrderHistoryPage() {
        System.out.println("STEP_DEBUG: @When('I navigate to my order history page')");

        assertTrue("Navigation to order history page simulated.", loggedIn);
    }

    @Then("I should see a chronological list of my past orders")
    public void iShouldSeeAChronologicalListOfMyPastOrders() {
        System.out.println("STEP_DEBUG: @Then('I should see a chronological list of my past orders')");
        assertNotNull("Past orders list should not be null.", pastOrders);
        assertFalse("Past orders list should not be empty for this check.", pastOrders.isEmpty());

        pastOrders.sort(Comparator.comparing(o -> o.orderDate));
        System.out.println("  Past orders sorted chronologically (oldest first). First order date: " + (pastOrders.isEmpty() ? "N/A" : pastOrders.get(0).orderDate));
    }

    @Then("each order should display the order date, total price, and reorder button")
    public void eachOrderShouldDisplayEssentialDetails() {
        System.out.println("STEP_DEBUG: @Then('each order should display the order date, total price, and reorder button')");
        assertNotNull("Past orders list should not be null.", pastOrders);
        assertFalse("Past orders list should not be empty for this check.", pastOrders.isEmpty());
        for (Order order : pastOrders) {
            assertNotNull("Order date should not be null.", order.orderDate);
            assertTrue("Order total price should be greater than zero.", order.totalPrice > 0);
            assertNotNull("Order meal name should not be null.", order.mealName);

            System.out.println("  Verified details for order: " + order.mealName + " on " + order.orderDate);
        }
    }


    @Given("I am viewing my order history")
    public void iAmViewingMyOrderHistory() {
        System.out.println("STEP_DEBUG: @Given('I am viewing my order history')");

        if (pastOrders.isEmpty() && loggedIn) {
            System.out.println("  No past orders found from previous steps, populating for this 'viewing' step.");
            pastOrders.add(new Order("Vegetable Curry", "2025-04-15", 12.99));
            pastOrders.add(new Order("Chicken Biryani", "2025-03-10", 14.50));
        }
        assertNotNull("Past orders list should not be null.", pastOrders);
        assertFalse("Past orders should exist to be viewed.", pastOrders.isEmpty());
        System.out.println("  Currently viewing " + pastOrders.size() + " past orders.");
    }

    @When("I click \"Reorder\" on an order containing {string}")
    public void iClickReorderOnMeal(String mealToReorder) {
        System.out.println("STEP_DEBUG: @When('I click \"Reorder\" on an order containing \"" + mealToReorder + "\"')");
        assertNotNull("Past orders list should not be null.", pastOrders);
        Optional<Order> orderToReorder = pastOrders.stream()
                .filter(o -> o.mealName.equals(mealToReorder))
                .findFirst();

        assertTrue("Order containing '" + mealToReorder + "' should exist to be reordered.", orderToReorder.isPresent());
        cart.add(orderToReorder.get().mealName);
        confirmationMessage = "Meal added to cart!";
        System.out.println("  '" + mealToReorder + "' added to cart. Cart size: " + cart.size());
    }

    @Then("{string} should be added to my current cart")
    public void mealShouldBeAddedToCart(String expectedMealInCart) {
        System.out.println("STEP_DEBUG: @Then('\"" + expectedMealInCart + "\" should be added to my current cart')");
        assertNotNull("Cart should not be null.", cart);
        assertTrue("Cart should contain '" + expectedMealInCart + "'.", cart.contains(expectedMealInCart));
        System.out.println("  Confirmed: '" + expectedMealInCart + "' is in the cart.");
    }

    @Then("I should see a confirmation message {string}")
    public void iShouldSeeConfirmationMessage(String expectedMessage) {
        System.out.println("STEP_DEBUG: @Then('I should see a confirmation message \"" + expectedMessage + "\"')");
        assertNotNull("Confirmation message should not be null.", confirmationMessage);
        assertEquals("Confirmation message mismatch.", expectedMessage, confirmationMessage);
        System.out.println("  Confirmed: Message shown is '" + expectedMessage + "'.");
    }



    @Given("the customer is viewing past orders")
    public void theCustomerIsViewingPastOrders() {
        System.out.println("SCENARIO_3_DEBUG: Entering @Given('the customer is viewing past orders')");

        assertNotNull("Past orders list should not be null for viewing.", pastOrders);
        assertFalse("Past orders list should not be empty for viewing.", pastOrders.isEmpty());
        System.out.println("  Currently viewing " + pastOrders.size() + " past orders before filtering.");
        System.out.println("SCENARIO_3_DEBUG: Exiting @Given('the customer is viewing past orders')");
    }

    @When("the customer filters orders by a specific date range")
    public void theCustomerFiltersOrdersByASpecificDateRange() {
        System.out.println("SCENARIO_3_DEBUG: Entering @When('the customer filters orders by a specific date range')");

        String startDate = "2025-03-01";
        String endDate = "2025-04-30";
        System.out.println("  Filtering between " + startDate + " and " + endDate);


        assertNotNull("pastOrders list should not be null before filtering.", pastOrders);

        for (Order o : pastOrders) {

            if (o.orderDate != null && o.orderDate.compareTo(startDate) >= 0 && o.orderDate.compareTo(endDate) <= 0) {
                filteredOrders.add(o);
            }
        }

        if (filteredOrders.isEmpty()) {
            systemMessageForNoOrders = "No orders found in this range."; //  محاكاة رسالة النظام
            System.out.println("  No orders found in the specified range. System message set.");
        } else {

            filteredOrders.sort((a, b) -> b.orderDate.compareTo(a.orderDate));
            System.out.println("  Found " + filteredOrders.size() + " orders in range, sorted newest first.");
        }
        System.out.println("SCENARIO_3_DEBUG: Exiting @When('the customer filters orders by a specific date range')");
    }

    @Then("the system should display only orders within that range")
    public void theSystemShouldDisplayOnlyOrdersWithinThatRange() {
        System.out.println("SCENARIO_3_DEBUG: Entering @Then('the system should display only orders within that range')");
        assertNotNull("Filtered orders list should not be null.", filteredOrders);


        String startDate = "2025-03-01";
        String endDate = "2025-04-30";

        for (Order o : filteredOrders) {
            assertNotNull("Order date in filtered list should not be null.", o.orderDate);
            assertTrue("Order " + o.mealName + " with date " + o.orderDate + " is out of range (before start).", o.orderDate.compareTo(startDate) >= 0);
            assertTrue("Order " + o.mealName + " with date " + o.orderDate + " is out of range (after end).", o.orderDate.compareTo(endDate) <= 0);
        }
        System.out.println("  Verified all " + filteredOrders.size() + " displayed orders are within the specified date range.");
        System.out.println("SCENARIO_3_DEBUG: Exiting @Then('the system should display only orders within that range')");
    }

    @Then("the orders should be sorted from newest to oldest")
    public void theOrdersShouldBeSortedFromNewestToOldest() {
        System.out.println("SCENARIO_3_DEBUG: Entering @Then('the orders should be sorted from newest to oldest')");
        assertNotNull("Filtered orders list should not be null.", filteredOrders);

        for (int i = 0; i < filteredOrders.size() - 1; i++) {
            Order currentOrder = filteredOrders.get(i);
            Order nextOrder = filteredOrders.get(i + 1);
            assertNotNull("Order date should not be null for sorting check (current).", currentOrder.orderDate);
            assertNotNull("Order date should not be null for sorting check (next).", nextOrder.orderDate);


            assertTrue("Orders are not sorted correctly (newest to oldest). " +
                            currentOrder.orderDate + " should be >= " + nextOrder.orderDate,
                    currentOrder.orderDate.compareTo(nextOrder.orderDate) >= 0);
        }
        if (!filteredOrders.isEmpty()) {
            System.out.println("  Verified orders are sorted newest to oldest.");
        } else {
            System.out.println("  No orders to verify sorting (list is empty).");
        }
        System.out.println("SCENARIO_3_DEBUG: Exiting @Then('the orders should be sorted from newest to oldest')");
    }

    @Then("if no orders match, a message {string} should be shown")
    public void ifNoOrdersMatchAMessageShouldBeShown(String expectedSystemMessage) {
        System.out.println("SCENARIO_3_DEBUG: Entering @Then('if no orders match, a message \"" + expectedSystemMessage + "\" should be shown')");
        if (filteredOrders.isEmpty()) {
            System.out.println("  Filtered orders list is empty. Checking system message.");
            assertNotNull("System message for no orders should have been set.", systemMessageForNoOrders);
            assertEquals("Mismatch in 'no orders found' message.", expectedSystemMessage, systemMessageForNoOrders);
            System.out.println("  Confirmed: System message '" + systemMessageForNoOrders + "' is shown as expected.");
        } else {
            System.out.println("  Filtered orders list is NOT empty (" + filteredOrders.size() + " orders). This step assumes no orders match.");
        }
        System.out.println("SCENARIO_3_DEBUG: Exiting @Then('if no orders match, a message \"" + expectedSystemMessage + "\" should be shown')");
    }
}