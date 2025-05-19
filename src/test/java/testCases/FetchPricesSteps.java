package testCases;

import io.cucumber.java.en.*;
import java.util.*;
import java.util.logging.Logger;
import cook.Supplier;
import cook.InventoryService;
import cook.InventoryItem;
import cook.PurchaseOrder;
import cook.kitchen_manager;

public class FetchPricesSteps {

    private static final Logger LOGGER = Logger.getLogger(FetchPricesSteps.class.getName());
    private List<Supplier> suppliers;
    private Supplier selectedSupplier;
    private InventoryService inventoryService;
    private kitchen_manager manager;
    private PurchaseOrder purchaseOrder;

    @Given("I am logged in as a manager")
    public void iAmLoggedInAsManager() {
        inventoryService = new InventoryService();
        manager = new kitchen_manager("Manager", "manager@example.com", "password", inventoryService);
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
        suppliers.forEach(supplier -> LOGGER.info(" - " + supplier.toString()));
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
        assert suppliers != null && !suppliers.isEmpty() : "❌ No suppliers to view!";
        LOGGER.info("👀 Viewing all supplier prices:");
        suppliers.forEach(s -> LOGGER.info(" - " + s.toString()));
    }

    @Then("I should be able to compare them side-by-side")
    public void iShouldBeAbleToCompareThem() {
        assert suppliers != null && suppliers.size() > 1 : "❌ Not enough suppliers to compare!";
        LOGGER.info("📈 Side-by-side comparison view:");
        suppliers.forEach(supplier -> LOGGER.info(supplier.toString()));
    }

    @Given("I need to restock an item")
    public void iNeedToRestockAnItem() {
        inventoryService = new InventoryService();
        inventoryService.addInventoryItem(new InventoryItem("Olive Oil", 5, 10, 12.5));
        LOGGER.info("📥 Restock request initiated: Olive Oil.");
        suppliers = Arrays.asList(
                new Supplier("Supplier A", 10.0),
                new Supplier("Supplier B", 9.5),
                new Supplier("Supplier C", 10.5)
        );
    }

    @When("I compare prices")
    public void iComparePrices() {
        if (suppliers == null || suppliers.isEmpty()) {
            throw new IllegalStateException("❌ Supplier list is not initialized or is empty!");
        }

        selectedSupplier = suppliers.stream()
                .min(Comparator.comparingDouble(Supplier::getPrice))
                .orElse(null);

        assert selectedSupplier != null : "❌ No supplier found after comparison (should not happen if list is not empty)!";

        // Create purchase order using InventoryService
        purchaseOrder = inventoryService.createPurchaseOrderForCriticalStock(
                "Olive Oil",
                20,
                selectedSupplier.getName(),
                selectedSupplier.getPrice()
        );

        LOGGER.info("🔍 Lowest price found at: " + selectedSupplier.getName());
    }

    @Then("I can choose the supplier with the best offer")
    public void iCanChooseBestSupplier() {
        assert selectedSupplier != null : "❌ No supplier selected!";
        assert purchaseOrder != null : "❌ No purchase order created!";

        // Send purchase order to supplier
        boolean orderSent = inventoryService.sendPurchaseOrderToSupplier(purchaseOrder.getOrderId());

        if (orderSent) {
            LOGGER.info("✅ Best offer selected from: " + selectedSupplier.getName() +
                    " ($" + selectedSupplier.getPrice() + ") - Order ID: " + purchaseOrder.getOrderId());
        } else {
            LOGGER.warning("❌ Failed to send purchase order to supplier");
        }
    }
}