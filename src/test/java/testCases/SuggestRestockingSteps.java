package testCases;

import io.cucumber.java.en.*;

public class SuggestRestockingSteps {

    @Given("an ingredient is below the threshold")
    public void an_ingredient_is_below_the_threshold() {
        System.out.println("Ingredient is below restocking threshold");
    }

    @When("I view the inventory")
    public void i_view_the_inventory() {
        System.out.println("Viewed inventory");
    }

    @Then("the system should suggest restocking")
    public void the_system_should_suggest_restocking() {
        System.out.println("Suggested restocking");
    }

    @Given("multiple items are low")
    public void multiple_items_are_low() {
        System.out.println("Multiple items are below threshold");
    }

    @When("I open the restock suggestions")
    public void i_open_the_restock_suggestions() {
        System.out.println("Opened restock suggestions");
    }

    @Then("I should see suggested quantities based on usage rate")
    public void i_should_see_suggested_quantities_based_on_usage_rate() {
        System.out.println("Displayed suggested restocking quantities");
    }

    @Given("I see restock suggestions")
    public void i_see_restock_suggestions() {
        System.out.println("Viewing restock suggestions");
    }

    @When("I review them")
    public void i_review_them() {
        System.out.println("Reviewed restocking plan");
    }

    @Then("I can approve or reject the restocking plan")
    public void i_can_approve_or_reject_the_restocking_plan() {
        System.out.println("Approved or rejected restocking plan");
    }
}

