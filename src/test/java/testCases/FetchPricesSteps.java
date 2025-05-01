package testCases;

import io.cucumber.java.en.*;

public class FetchPricesSteps {

    @Given("I am logged in as a manager")
    public void i_am_logged_in_as_a_manager() {
        System.out.println("Manager logged in");
    }

    @When("I open the supplier section")
    public void i_open_the_supplier_section() {
        System.out.println("Opened supplier section");
    }

    @Then("I should see updated prices for ingredients")
    public void i_should_see_updated_prices_for_ingredients() {
        System.out.println("Displayed updated prices");
    }

    @Given("I have multiple supplier options")
    public void i_have_multiple_supplier_options() {
        System.out.println("Multiple suppliers available");
    }

    @When("I view the prices")
    public void i_view_the_prices() {
        System.out.println("Viewed supplier prices");
    }

    @Then("I should be able to compare them side-by-side")
    public void i_should_be_able_to_compare_them_side_by_side() {
        System.out.println("Compared supplier prices");
    }

    @Given("I need to restock an item")
    public void i_need_to_restock_an_item() {
        System.out.println("Restocking item");
    }

    @When("I compare prices")
    public void i_compare_prices() {
        System.out.println("Comparing prices");
    }

    @Then("I can choose the supplier with the best offer")
    public void i_can_choose_the_supplier_with_the_best_offer() {
        System.out.println("Selected best-priced supplier");
    }
}
