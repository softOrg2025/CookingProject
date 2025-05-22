package testCases;

import cook.InventoryItem;
import cook.InventoryService;
import cook.PurchaseOrder;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class PurchaseOrdersSteps {

    private final InventoryService inventoryService = new InventoryService();
    private InventoryItem criticalStockItem;
    private PurchaseOrder currentPurchaseOrder;
    private boolean poSentSuccessfully;

    private static final String DEFAULT_SUPPLIER_NAME = "Default Supplier Inc.";
    private static final double DEFAULT_PRICE_PER_UNIT = 1.50;
    private static final int DEFAULT_REORDER_QUANTITY = 50;


    @Given("an ingredient reaches critical stock")
    public void an_ingredient_reaches_critical_stock() {
        criticalStockItem = new InventoryItem("CriticalPepper", 0, 1, DEFAULT_PRICE_PER_UNIT);
        inventoryService.addInventoryItem(criticalStockItem);
    }

    @When("the system detects a critical stock level")
    public void the_system_detects_a_critical_stock_level() {
        if (criticalStockItem != null && criticalStockItem.isLowStock()) {
            this.currentPurchaseOrder = inventoryService.createPurchaseOrderForCriticalStock(
                    criticalStockItem.getIngredientName(),
                    DEFAULT_REORDER_QUANTITY,
                    DEFAULT_SUPPLIER_NAME,
                    criticalStockItem.getUnitPrice()
            );
        }
    }

    @Then("a purchase order should be generated automatically")
    public void a_purchase_order_should_be_generated_automatically() {
        assertNotNull(this.currentPurchaseOrder, "Purchase order should have been generated for critical stock.");
        assertEquals(criticalStockItem.getIngredientName(), this.currentPurchaseOrder.getIngredientName(), "PO ingredient name mismatch.");
    }


    @Given("a purchase order for {string} has been created with quantity {int}, supplier {string}, and unit price {double}")
    public void a_purchase_order_for_ingredient_has_been_created(String ingredientName, int quantity, String supplierName, double unitPrice) {

        inventoryService.addInventoryItem(new InventoryItem(ingredientName, quantity / 2, quantity * 2, unitPrice));

        PurchaseOrder createdPO = inventoryService.createPurchaseOrderForCriticalStock(
                ingredientName,
                quantity,
                supplierName,
                unitPrice
        );
        assertNotNull(createdPO, "Failed to create PO in Given step for ingredient: " + ingredientName);
        System.out.println("DEBUG @Given (Scenario 2): Created PO for " + ingredientName + " with ID: " + createdPO.getOrderId());

    }

    @When("I view the details for the {string} purchase order")
    public void i_view_the_details_for_the_purchase_order(String ingredientNameForLookup) {

        this.currentPurchaseOrder = inventoryService.getPurchaseOrderByIngredientName(ingredientNameForLookup); //  ستحتاج لإضافة هذه الدالة إلى InventoryService
        assertNotNull(this.currentPurchaseOrder, "Could not find Purchase Order for ingredient: " + ingredientNameForLookup);
        System.out.println("DEBUG @When (Scenario 2): Retrieved PO for " + ingredientNameForLookup + " with ID: " + this.currentPurchaseOrder.getOrderId());
    }

    @Then("the purchase order details should show:")
    public void the_purchase_order_details_should_show(DataTable expectedDetailsTable) {
        assertNotNull(this.currentPurchaseOrder, "Cannot verify details, currentPurchaseOrder is null.");

        Map<String, String> expectedDetailsMap = expectedDetailsTable.asMap(String.class, String.class);
        double delta = 0.001;

        String expectedIngredient = expectedDetailsMap.get("IngredientName");
        int expectedQuantity = Integer.parseInt(expectedDetailsMap.get("Quantity"));
        String expectedSupplier = expectedDetailsMap.get("SupplierName");
        double expectedUnitPrice = Double.parseDouble(expectedDetailsMap.get("UnitPrice"));
        double expectedTotalPriceCalculated = (double) expectedQuantity * expectedUnitPrice;

        System.out.println("DEBUG @Then (Scenario 2): Verifying details for PO ID: " + this.currentPurchaseOrder.getOrderId());

        assertEquals(expectedIngredient, this.currentPurchaseOrder.getIngredientName(), "PO IngredientName mismatch.");
        System.out.println("  Verified IngredientName: " + expectedIngredient + " == " + this.currentPurchaseOrder.getIngredientName());

        assertEquals(expectedQuantity, this.currentPurchaseOrder.getQuantity(), "PO Quantity mismatch.");
        System.out.println("  Verified Quantity: " + expectedQuantity + " == " + this.currentPurchaseOrder.getQuantity());

        assertEquals(expectedSupplier, this.currentPurchaseOrder.getSupplierName(), "PO SupplierName mismatch.");
        System.out.println("  Verified SupplierName: " + expectedSupplier + " == " + this.currentPurchaseOrder.getSupplierName());

        assertEquals(expectedUnitPrice, this.currentPurchaseOrder.getPricePerUnit(), delta, "PO UnitPrice mismatch.");
        System.out.println("  Verified UnitPrice: " + expectedUnitPrice + " == " + this.currentPurchaseOrder.getPricePerUnit());

        assertEquals(expectedTotalPriceCalculated, this.currentPurchaseOrder.getTotalPrice(), delta, "PO TotalPrice mismatch based on calculation.");
        System.out.println("  Verified Calculated TotalPrice: " + expectedTotalPriceCalculated + " == " + this.currentPurchaseOrder.getTotalPrice());
    }



    @Given("the PO is ready")
    public void the_po_is_ready() {

        if (this.currentPurchaseOrder == null) {
            System.out.println("DEBUG @Given (Scenario 3): currentPurchaseOrder is null, creating a new one.");
            String ingredientForPO = "ReadySugar";
            int quantityForPO = 200;
            String supplierForPO = "Sweet Supplies Co.";
            double priceForPO = 1.20;
            inventoryService.addInventoryItem(new InventoryItem(ingredientForPO, 10, 30, priceForPO));
            this.currentPurchaseOrder = inventoryService.createPurchaseOrderForCriticalStock(
                    ingredientForPO, quantityForPO, supplierForPO, priceForPO
            );
        }
        assertNotNull(this.currentPurchaseOrder, "A PO should be ready for approval.");
        System.out.println("DEBUG @Given (Scenario 3): PO is ready with ID: " + this.currentPurchaseOrder.getOrderId());
    }

    @When("I approve it")
    public void i_approve_it() {
        assertNotNull(this.currentPurchaseOrder, "Cannot approve a null PO.");
        this.poSentSuccessfully = inventoryService.sendPurchaseOrderToSupplier(this.currentPurchaseOrder.getOrderId());
    }

    @Then("the system should send it to the respective supplier")
    public void the_system_should_send_it_to_the_respective_supplier() {
        assertTrue(this.poSentSuccessfully, "The system failed to send the approved PO to the supplier.");
    }
}