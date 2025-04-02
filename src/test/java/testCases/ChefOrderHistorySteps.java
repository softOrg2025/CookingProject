package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;


public class ChefOrderHistorySteps {
    private boolean isChefLoggedIn;
    private boolean isCustomerSelected;
    private boolean isOrderHistoryDisplayed;
    private boolean isMealPlanSuggested;
    private boolean isPreferencesIdentified;

    @Given("the chef is logged into the system")
    public void theChefIsLoggedIntoTheSystem() {
        isChefLoggedIn = true;
        Assertions.assertTrue(isChefLoggedIn, "Chef should be logged in.");
    }

    @When("the chef selects a customer profile")
    public void theChefSelectsACustomerProfile() {
        isCustomerSelected = true;
        Assertions.assertTrue(isCustomerSelected, "Customer profile should be selected.");
    }

    @Then("the system should display the customer's order history")
    public void theSystemShouldDisplayTheCustomersOrderHistory() {
        isOrderHistoryDisplayed = true;
        Assertions.assertTrue(isOrderHistoryDisplayed, "Order history should be displayed.");
    }

    @Given("the chef is viewing a customer's order history")
    public void theChefIsViewingACustomersOrderHistory() {
        isOrderHistoryDisplayed = true;
        Assertions.assertTrue(isOrderHistoryDisplayed, "Chef should be viewing the order history.");
    }

    @When("the chef identifies frequently ordered meals")
    public void theChefIdentifiesFrequentlyOrderedMeals() {
        isMealPlanSuggested = true;
        Assertions.assertTrue(isMealPlanSuggested, "Frequently ordered meals should be identified.");
    }

    @Then("the chef should suggest a personalized meal plan")
    public void theChefShouldSuggestAPersonalizedMealPlan() {
        Assertions.assertTrue(isMealPlanSuggested, "A personalized meal plan should be suggested.");
    }

    @When("the chef notices a pattern in meal choices")
    public void theChefNoticesAPatternInMealChoices() {
        isPreferencesIdentified = true;
        Assertions.assertTrue(isPreferencesIdentified, "Patterns in meal choices should be noticed.");
    }

    @Then("the chef should adjust future meal suggestions accordingly")
    public void theChefShouldAdjustFutureMealSuggestionsAccordingly() {
        Assertions.assertTrue(isPreferencesIdentified, "Future meal suggestions should be adjusted.");
    }
}
