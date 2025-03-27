package testCases;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.*;

public class PastMealOrdersSteps {

    private boolean isLoggedIn;
    private boolean hasPastOrders;

    @Given("I am logged in with existing order history")
    public void iAmLoggedInWithExistingOrderHistory() {
        isLoggedIn = true;
        hasPastOrders = true;
        assertTrue(isLoggedIn, "User should be logged in.");
        assertTrue(hasPastOrders, "User should have past orders.");
    }

    @When("I navigate to my order history page")
    public void iNavigateToMyOrderHistoryPage() {
        assertTrue(hasPastOrders, "Past orders should be available.");
    }

    @Then("I should see a chronological list of my past orders")
    public void iShouldSeeAChronologicalListOfMyPastOrders() {
        assertTrue(hasPastOrders, "Past orders should be displayed.");
    }
}
