package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class PurchaseOrderSteps {

    private boolean isLowStock = false;
    private boolean orderGenerated = false;
    private boolean orderSent = false;
    private String orderStatus = "Pending";



    @Given("an ingredient is running low in purchase order")
    public void anIngredientIsRunningLowInPurchaseOrder() {
        isLowStock = true;
    }

    @When("the system detects the low stock in purchase order")
    public void theSystemDetectsTheLowStockInPurchaseOrder() {
        if (isLowStock) {
            orderGenerated = true;
        }
    }

    @Then("the system should generate a purchase order")
    public void theSystemShouldGenerateAPurchaseOrderInPurchaseOrder() {
        Assertions.assertTrue(orderGenerated, "Purchase order should be generated");
    }

    // SCENARIO 2: Send order to supplier
    @Given("a purchase order has been generated")
    public void aPurchaseOrderHasBeenGenerated() {
        orderGenerated = true;
    }

    @When("the system sends the order to the supplier")
    public void theSystemSendsTheOrderToTheSupplier() {
        if (orderGenerated) {
            orderSent = true;
        }
    }

    @Then("the supplier should receive the order")
    public void theSupplierShouldReceiveTheOrder() {
        Assertions.assertTrue(orderSent, "Supplier should receive the order");
    }



    @Given("a purchase order has been sent")
    public void aPurchaseOrderHasBeenSent() {
        orderSent = true;
    }

    @When("the supplier updates the order status")
    public void theSupplierUpdatesTheOrderStatus() {
        if (orderSent) {
            orderStatus = "Shipped";
        }
    }

    @Then("the system should reflect the updated status")
    public void theSystemShouldReflectTheUpdatedStatus() {
        Assertions.assertEquals("Shipped", orderStatus, "Order status should be updated to Shipped");
    }



    @Then("the system should not generate a purchase order")
    public void theSystemShouldNotGenerateAPurchaseOrder() {
        Assertions.assertFalse(orderGenerated, "Purchase order should NOT be generated");
    }

    @Then("the supplier should not receive the order")
    public void theSupplierShouldNotReceiveTheOrder() {
        Assertions.assertFalse(orderSent, "Supplier should NOT receive the order");
    }
}
