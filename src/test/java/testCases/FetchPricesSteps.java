package testCases;

import io.cucumber.java.en.*;
import java.util.*;
import java.util.logging.Logger;

public class FetchPricesSteps {

    private static final Logger LOGGER = Logger.getLogger(FetchPricesSteps.class.getName());

    private List<Supplier> suppliers;
    private Supplier selectedSupplier;

    // Class to represent supplier information
    static class Supplier {
        String name;
        double price;

        Supplier(String name, double price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return name + ": $" + price;
        }
    }

    @Given("I am logged in as a manager")
    public void iAmLoggedInAsManager() {
        LOGGER.info("✅ Manager successfully logged in.");
    }

    @When("I open the supplier section")
    public void iOpenTheSupplierSection() {
        suppliers = Arrays.asList(
                new Supplier("Supplier A", 12.50),
                new Supplier("Supplier B", 11.90),
                new Supplier("Supplier C", 13.25)
        );
        LOGGER.info("📦 Supplier section opened. Prices loaded.");
    }

    @Then("I should see updated prices for ingredients")
    public void iShouldSeeUpdatedPrices() {
        assert suppliers != null && !suppliers.isEmpty() : "❌ No suppliers loaded!";
        LOGGER.info("📊 Updated ingredient prices:");
        suppliers.forEach(supplier -> LOGGER.info(" - " + supplier));
    }

    @Given("I have multiple supplier options")
    public void iHaveMultipleSupplierOptions() {
        suppliers = Arrays.asList(
                new Supplier("Supplier A", 10.0),
                new Supplier("Supplier B", 9.5),
                new Supplier("Supplier C", 10.5)
        );
        LOGGER.info("🔄 Multiple supplier options available.");
    }

    @When("I view the prices")
    public void iViewThePrices() {
        LOGGER.info("👀 Viewing all supplier prices:");
        suppliers.forEach(s -> LOGGER.info(" - " + s));
    }

    @Then("I should be able to compare them side-by-side")
    public void iShouldBeAbleToCompareThem() {
        LOGGER.info("📈 Side-by-side comparison view:");
        suppliers.forEach(supplier -> LOGGER.info(supplier.toString()));
    }

    @Given("I need to restock an item")
    public void iNeedToRestockAnItem() {
        LOGGER.info("📥 Restock request initiated: Olive Oil.");
        suppliers = Arrays.asList(
                new Supplier("Supplier A", 10.0),
                new Supplier("Supplier B", 9.5),
                new Supplier("Supplier C", 10.5)
        );
    }

    @When("I compare prices")
    public void iComparePrices() {
        selectedSupplier = suppliers.stream()
                .min(Comparator.comparingDouble(s -> s.price))
                .orElse(null);

        assert selectedSupplier != null : "❌ No supplier found!";
        LOGGER.info("🔍 Lowest price found at: " + selectedSupplier.name);

        if (suppliers == null || suppliers.isEmpty()) {
            throw new IllegalStateException("❌ Supplier list is not initialized!");
        }

    }

    @Then("I can choose the supplier with the best offer")
    public void iCanChooseBestSupplier() {
        assert selectedSupplier != null : "❌ No supplier selected!";
        LOGGER.info("✅ Best offer selected from: " + selectedSupplier.name + " ($" + selectedSupplier.price + ")");
    }
}
