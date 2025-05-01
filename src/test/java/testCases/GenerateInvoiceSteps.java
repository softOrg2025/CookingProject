package testCases;

import io.cucumber.java.en.*;

public class GenerateInvoiceSteps {

    @Given("I place a custom meal order")
    public void i_place_a_custom_meal_order() {
        System.out.println("Placed a custom meal order");
    }

    @When("the order is confirmed")
    public void the_order_is_confirmed() {
        System.out.println("Order confirmed");
    }

    @Then("I should receive an invoice via email")
    public void i_should_receive_an_invoice_via_email() {
        System.out.println("Invoice received via email");
    }

    @Given("I am a customer")
    public void i_am_a_customer() {
        System.out.println("Logged in as customer");
    }

    @When("I go to my billing section")
    public void i_go_to_my_billing_section() {
        System.out.println("Accessed billing section");
    }

    @Then("I should see a downloadable copy of my invoice")
    public void i_should_see_a_downloadable_copy_of_my_invoice() {
        System.out.println("Invoice available for download");
    }

    @Given("I made changes to my meal")
    public void i_made_changes_to_my_meal() {
        System.out.println("Meal customization applied");
    }

    @When("the invoice is generated")
    public void the_invoice_is_generated() {
      //  System.out.println("Accessed billing section");

    }

    @Then("it should list the changes and updated price")
    public void it_should_list_the_changes_and_updated_price() {
        System.out.println("Invoice includes custom changes and price");
    }
}
