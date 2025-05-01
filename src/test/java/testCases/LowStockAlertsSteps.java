package testCases;

import io.cucumber.java.en.*;

public class LowStockAlertsSteps {

    @Given("an ingredient drops below its restock level")
    public void an_ingredient_drops_below_its_restock_level() {
        System.out.println("Ingredient dropped below restock level");
    }

    @When("the system detects it")
    public void the_system_detects_it() {
        System.out.println("System detected low stock");
    }

    @Then("the kitchen manager should be notified")
    public void the_kitchen_manager_should_be_notified() {
        System.out.println("Kitchen manager notified");
    }

    @Given("an alert is triggered")
    public void an_alert_is_triggered() {
        System.out.println("Stock alert triggered");
    }

    @When("the manager opens the notification")
    public void the_manager_opens_the_notification() {
        System.out.println("Manager opened notification");
    }

    @Then("it should list the item name and quantity left")
    public void it_should_list_the_item_name_and_quantity_left() {
        System.out.println("Alert includes item and quantity");
    }

    @Given("several items are low")
    public void several_items_are_low() {
        System.out.println("Multiple items below threshold");
    }

    @When("the alert is generated")
    public void the_alert_is_generated() {
        System.out.println("Alert generated");
    }

    @Then("it should combine them in one message")
    public void it_should_combine_them_in_one_message() {
        System.out.println("Grouped low-stock alerts in one message");
    }
}
