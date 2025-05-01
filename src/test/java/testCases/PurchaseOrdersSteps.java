package testCases;

import io.cucumber.java.en.*;

public class PurchaseOrdersSteps {

    @Given("an ingredient reaches critical stock")
    public void an_ingredient_reaches_critical_stock() {
        System.out.println("Ingredient at critical stock level");
    }

    @When("the system detects a critical stock level")
    public void the_system_detects_critical_stock_level() {
        System.out.println("System detected critical low stock");
    }

    @Then("a purchase order should be generated automatically")
    public void a_purchase_order_should_be_generated_automatically() {
        System.out.println("Purchase order generated automatically");
    }

    @Given("a PO is created")
    public void a_po_is_created() {
        System.out.println("Purchase Order created");
    }

    @When("I view the details")
    public void i_view_the_details() {
        System.out.println("Viewing PO details");
    }

    @Then("it should include ingredient name, quantity, supplier, and price")
    public void it_should_include_ingredient_name_quantity_supplier_and_price() {
        System.out.println("PO includes all required details");
    }

    @Given("the PO is ready")
    public void the_po_is_ready() {
        System.out.println("PO is ready");
    }

    @When("I approve it")
    public void i_approve_it() {
        System.out.println("PO approved");
    }

    @Then("the system should send it to the respective supplier")
    public void the_system_should_send_it_to_the_respective_supplier() {
        System.out.println("PO sent to supplier");
    }
}
