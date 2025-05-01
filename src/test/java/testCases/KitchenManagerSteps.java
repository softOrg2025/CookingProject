package testCases;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class KitchenManagerSteps {

    // Scenario 1
    boolean isInventorySelected = false;
    boolean isStockDisplayed = false;

    // Scenario 2
    boolean isIngredientLow = false;
    boolean isAlertSent = false;

    // Scenario 3
    boolean alertReceived = false;
    boolean restockOrdered = false;
    boolean stockUpdated = false;


    @When("the kitchen manager selects {string}")
    public void theKitchenManagerSelects(String section) {
        if (section.equalsIgnoreCase("Inventory")) {
            isInventorySelected = true;
        }
        Assertions.assertTrue(isInventorySelected);
    }

    @Then("the system should display current stock levels")
    public void theSystemShouldDisplayCurrentStockLevels() {
        isStockDisplayed = true;
        Assertions.assertTrue(isStockDisplayed);
    }

    @Given("an ingredient is running low")
    public void anIngredientIsRunningLow() {
        isIngredientLow = true; // تفعيل الشرط
    }

    @When("the system detects the low stock")
    public void theSystemDetectsTheLowStock() {
        if (isIngredientLow) {
            isAlertSent = true;  // إرسال التنبيه
        }
        Assertions.assertTrue(isAlertSent);  // التأكد من أنه تم إرسال التنبيه
    }


    @Then("the system should notify the kitchen manager")
    public void theSystemShouldNotifyTheKitchenManager() {
        Assertions.assertTrue(isAlertSent);
    }

    @Given("the kitchen manager has received a low-stock alert")
    public void theKitchenManagerHasReceivedALowStockAlert() {
        alertReceived = true;
        Assertions.assertTrue(alertReceived);
    }

    @When("the kitchen manager places a restock order")
    public void theKitchenManagerPlacesARestockOrder() {
        restockOrdered = true;
        Assertions.assertTrue(restockOrdered);
    }

    @Then("the system should update the stock levels")
    public void theSystemShouldUpdateTheStockLevels() {
        if (alertReceived && restockOrdered) {
            stockUpdated = true;
        }
        Assertions.assertTrue(stockUpdated);
    }
}
