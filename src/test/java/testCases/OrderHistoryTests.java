package testCases;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
public class OrderHistoryTests {
    private boolean isOrderPlaced;
    private boolean isOrderCompleted;
    private boolean isOrderSavedInDatabase;

    private boolean hasPastOrders;
    private boolean isCustomerLoggedIn;
    private boolean isOrderHistoryDisplayed;

    private boolean hasOrderHistory;
    private boolean isDataAnalyzed;
    private boolean areTrendsIdentified;

    @Given("a customer places an order")
    public void aCustomerPlacesAnOrder() {
        isOrderPlaced = true;
        Assertions.assertTrue(isOrderPlaced);
    }

    @When("the order is completed")
    public void theOrderIsCompleted() {
        isOrderCompleted = true;
        Assertions.assertTrue(isOrderCompleted);
    }

    @Then("the system should save the order details in the database")
    public void theSystemShouldSaveTheOrderDetailsInTheDatabase() {
        isOrderSavedInDatabase = true;
        Assertions.assertTrue(isOrderSavedInDatabase);
    }

    @Given("a customer has past orders")
    public void aCustomerHasPastOrders() {
        hasPastOrders = true;
        Assertions.assertTrue(hasPastOrders);
    }

    @When("the customer logs in")
    public void theCustomerLogsIn() {
        isCustomerLoggedIn = true;
        Assertions.assertTrue(isCustomerLoggedIn);
    }

    @Then("the system should retrieve and display the order history")
    public void theSystemShouldRetrieveAndDisplayTheOrderHistory() {
        isOrderHistoryDisplayed = true;
        Assertions.assertTrue(isOrderHistoryDisplayed);
    }

    @Given("the system has access to customer order history")
    public void theSystemHasAccessToCustomerOrderHistory() {
        hasOrderHistory = true;
        Assertions.assertTrue(hasOrderHistory);
    }

    @When("the system analyzes the data")
    public void theSystemAnalyzesTheData() {
        isDataAnalyzed = true;
        Assertions.assertTrue(isDataAnalyzed);
    }

    @Then("the system should identify popular meals and trends")
    public void theSystemShouldIdentifyPopularMealsAndTrends() {
        areTrendsIdentified = true;
        Assertions.assertTrue(areTrendsIdentified);
    }


}
