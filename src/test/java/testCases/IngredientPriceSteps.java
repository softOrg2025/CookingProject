package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

import java.util.*;

public class IngredientPriceSteps {

    private boolean isLoggedIn = false;
    private boolean pricesFetched = false;
    private boolean pricesUpdated = false;
    private Map<String, Double> supplierPrices = new HashMap<>();
    private String suggestedSupplier = "";
    private final TestContext context;


    public IngredientPriceSteps(TestContext context) {
        this.context = context;
    }
    @When("the system fetches real-time prices")
    public void systemFetchesRealTimePrices() {
        if (context.isLoggedIn) {
            supplierPrices.put("Tomato", 2.5);
            supplierPrices.put("Cheese", 5.0);
            pricesFetched = true;
        }
        Assertions.assertTrue(pricesFetched, "Prices should be fetched");
    }

    @Then("the system should display the updated prices")
    public void systemDisplaysUpdatedPrices() {
        Assertions.assertFalse(supplierPrices.isEmpty(), "Prices must be displayed");
        System.out.println("Fetched prices: " + supplierPrices);
    }

    // Scenario 2
    @Given("the system has fetched new prices")
    public void systemHasFetchedNewPrices() {
        pricesFetched = true;
        supplierPrices.put("Tomato", 2.5);
        supplierPrices.put("Cheese", 5.0);
        Assertions.assertTrue(pricesFetched, "Prices should have been fetched");
    }

    @When("the system updates the prices")
    public void systemUpdatesPrices() {
        if (pricesFetched) {
            pricesUpdated = true; // simulate updating internal DB or UI
        }
        Assertions.assertTrue(pricesUpdated, "Prices should be updated");
    }

    @Then("the kitchen manager should see the updated prices")
    public void kitchenManagerSeesUpdatedPrices() {
        Assertions.assertTrue(pricesUpdated, "Updated prices should be visible");
        System.out.println("Updated prices shown to kitchen manager");
    }

    // Scenario 3
    @Given("the system has multiple supplier options")
    public void systemHasMultipleSuppliers() {
        supplierPrices.clear();
        supplierPrices.put("SupplierA", 4.5);
        supplierPrices.put("SupplierB", 3.8);
        supplierPrices.put("SupplierC", 4.0);
        Assertions.assertTrue(supplierPrices.size() > 1, "There should be multiple suppliers");
    }

    @When("the system compares prices")
    public void systemComparesPrices() {
        // Find the cheapest supplier
        suggestedSupplier = supplierPrices.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
        Assertions.assertFalse(suggestedSupplier.isEmpty(), "A supplier should be suggested");
    }

    @Then("the system should suggest the most cost-effective supplier")
    public void systemSuggestsBestSupplier() {
        System.out.println("Suggested supplier: " + suggestedSupplier);
        Assertions.assertEquals("SupplierB", suggestedSupplier, "SupplierB should be the cheapest");
    }
}
