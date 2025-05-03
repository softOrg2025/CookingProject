package testCases;

import io.cucumber.java.en.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class LowStockAlertsSteps {

    private static class Ingredient {
        String name;
        int quantity;
        int threshold;

        Ingredient(String name, int quantity, int threshold) {
            this.name = name;
            this.quantity = quantity;
            this.threshold = threshold;
        }

        boolean isLowStock() {
            return quantity < threshold;
        }
    }

    private List<Ingredient> inventory = new ArrayList<>();
    private List<String> notifications = new ArrayList<>();

    @Given("an ingredient drops below its restock level")
    public void an_ingredient_drops_below_its_restock_level() {
        inventory.add(new Ingredient("Tomatoes", 4, 5));
    }

    @When("the system detects it")
    public void the_system_detects_it() {
        for (Ingredient ing : inventory) {
            if (ing.isLowStock()) {
                notifications.add("⚠ Low stock: " + ing.name + " (" + ing.quantity + " left)");
            }
        }
    }

    @Then("the kitchen manager should be notified")
    public void the_kitchen_manager_should_be_notified() {
        assertFalse(notifications.isEmpty(), "Notification should be sent");
        System.out.println("📢 Notification: " + notifications.get(0));
    }

    @Given("an alert is triggered")
    public void an_alert_is_triggered() {
        inventory.add(new Ingredient("Olive Oil", 2, 5));
        the_system_detects_it(); // reuse logic
    }

    @When("the manager opens the notification")
    public void the_manager_opens_the_notification() {
        assertFalse(notifications.isEmpty());
    }

    @Then("it should list the item name and quantity left")
    public void it_should_list_the_item_name_and_quantity_left() {
        String message = notifications.get(0);
        assertTrue(message.contains("Olive Oil") && message.contains("2"), "Alert missing info");
        System.out.println("📋 Notification details: " + message);
    }

    @Given("several items are low")
    public void several_items_are_low() {
        inventory.addAll(List.of(
                new Ingredient("Milk", 1, 3),
                new Ingredient("Eggs", 5, 10),
                new Ingredient("Cheese", 2, 5)
        ));
    }

    @When("the alert is generated")
    public void the_alert_is_generated() {
        notifications.clear();
        for (Ingredient ing : inventory) {
            if (ing.isLowStock()) {
                notifications.add(ing.name + " (" + ing.quantity + ")");
            }
        }
    }

    @Then("it should combine them in one message")
    public void it_should_combine_them_in_one_message() {
        assertFalse(notifications.isEmpty());
        String combinedMessage = "🚨 Low-stock items: " + String.join(", ", notifications);
        System.out.println(combinedMessage);
    }
}

