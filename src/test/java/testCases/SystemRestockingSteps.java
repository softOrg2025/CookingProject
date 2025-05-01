package testCases;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class SystemRestockingSteps {

//    @Given("an ingredient is running low")
//    public void anIngredientIsRunningLow() {
//        System.out.println("Ingredient is low in stock.");
//    }

    @When("the system checks stock levels")
    public void theSystemChecksStockLevels() {
        System.out.println("System is checking stock levels.");
    }

    @Then("the system should flag the ingredient for restocking")
    public void theSystemShouldFlagTheIngredientForRestocking() {
        System.out.println("System flagged the ingredient for restocking.");
    }

    @Given("an ingredient is flagged for restocking")
    public void anIngredientIsFlaggedForRestocking() {
        System.out.println("Ingredient is flagged for restocking.");
    }

    @When("the system suggests a restocking quantity")
    public void theSystemSuggestsARestockingQuantity() {
        System.out.println("System suggests restocking quantity.");
    }

    @Then("the kitchen manager should receive the suggestion")
    public void theKitchenManagerShouldReceiveTheSuggestion() {
        System.out.println("Kitchen manager receives the suggestion.");
    }

    @Given("the kitchen manager has restocked an ingredient")
    public void theKitchenManagerHasRestockedAnIngredient() {
        System.out.println("Kitchen manager restocked the ingredient.");
    }

    @When("the system updates the stock levels")
    public void theSystemUpdatesTheStockLevels() {
        System.out.println("System updates the stock levels.");
    }

    @Then("the system should reflect the new stock levels")
    public void theSystemShouldReflectTheNewStockLevels() {
        System.out.println("System shows updated stock levels.");
    }
}
