package testCases;

import static org.junit.Assert.*;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.util.HashMap;
import java.util.Map;

public class PurchaseOrdersSteps {

    private Map<String, Integer> stock;
    private PurchaseOrder purchaseOrder;
    private boolean poSent = false;

    private class PurchaseOrder {
        String ingredientName;
        int quantity;
        String supplier;
        double price;

        PurchaseOrder(String ingredientName, int quantity, String supplier, double price) {
            this.ingredientName = ingredientName;
            this.quantity = quantity;
            this.supplier = supplier;
            this.price = price;
        }
    }

    @Before
    public void setup() {
        stock = new HashMap<>();
        purchaseOrder = null;
        poSent = false;
    }


    @Given("an ingredient reaches critical stock")
    public void an_ingredient_reaches_critical_stock() {
        stock.put("Tomato", 1);  // below threshold
    }

    @When("the system detects a critical stock level")
    public void the_system_detects_critical_stock_level() {
        int qty = stock.getOrDefault("Tomato", 0);
        if (qty < 3) {
            purchaseOrder = new PurchaseOrder("Tomato", 50, "Supplier A", 15.5);
        }
    }

    @Then("a purchase order should be generated automatically")
    public void a_purchase_order_should_be_generated_automatically() {
        assertNotNull("Purchase Order should be generated", purchaseOrder);
        assertEquals("Tomato", purchaseOrder.ingredientName);
    }


    @Given("a PO is created")
    public void a_po_is_created() {
        purchaseOrder = new PurchaseOrder("Cheese", 100, "Supplier B", 30.0);
    }

    String viewedIngredient;
    int viewedQuantity;
    String viewedSupplier;
    double viewedPrice;


    @When("I view the details")
    public void i_view_the_details() {
        if (purchaseOrder != null) {
            viewedIngredient = purchaseOrder.ingredientName;
            viewedQuantity = purchaseOrder.quantity;
            viewedSupplier = purchaseOrder.supplier;
            viewedPrice = purchaseOrder.price;
        }
    }

    @Then("it should include ingredient name, quantity, supplier, and price")
    public void it_should_include_ingredient_name_quantity_supplier_and_price() {
        assertNotNull(purchaseOrder);
        assertEquals("Cheese", viewedIngredient);
        assertTrue(viewedQuantity > 0);
        assertNotNull(viewedSupplier);
        assertTrue(viewedPrice > 0);
    }


    @Given("the PO is ready")
    public void the_po_is_ready() {
        purchaseOrder = new PurchaseOrder("Flour", 80, "Supplier C", 20.0);
    }

    @When("I approve it")
    public void i_approve_it() {
        if (purchaseOrder != null && purchaseOrder.supplier != null) {
            poSent = true;
        }
    }

    @Then("the system should send it to the respective supplier")
    public void the_system_should_send_it_to_the_respective_supplier() {
        assertTrue("PO should be sent to supplier", poSent);
    }
}
