package testCases;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import cook.InventoryItem;
import cook.InventoryService;
import cook.NotificationService;

import java.util.List;
import java.util.stream.Collectors;



public class LowStockAlertsSteps {

    private InventoryService inventoryService;
    private NotificationService notificationService;
    private final String managerId = "kitchen_manager_main"; // An ID for the manager

    // Constructor will be called by Cucumber for each scenario, ensuring fresh services
    public LowStockAlertsSteps() {
        this.inventoryService = new InventoryService();
        this.notificationService = new NotificationService();
    }

    // Helper to clear previous notifications for the manager before a new alert/detection
    private void clearManagerNotifications() {
        this.notificationService.clearNotifications(this.managerId);
    }

    @Given("an ingredient drops below its restock level")
    public void an_ingredient_drops_below_its_restock_level() {
        // Add an item to inventory that is below its threshold
        // InventoryItem(String ingredientName, int quantity, int threshold, double unitPrice)
        inventoryService.addInventoryItem(new InventoryItem("Tomatoes", 4, 5, 0.50));
        System.out.println("SETUP: Tomatoes (4) added, threshold (5).");
    }

    @When("the system detects it")
    public void the_system_detects_it() {
        clearManagerNotifications(); // Ensure we're checking fresh notifications
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();

        for (InventoryItem item : lowStockItems) {
            // For this step, each low stock item triggers its own notification.
            String message = "⚠ Low stock: " + item.getIngredientName() + " (" + item.getQuantity() + " left)";
            notificationService.sendNotification(managerId, message);
            System.out.println("SYSTEM: Detected low stock for " + item.getIngredientName() + ". Sending notification: " + message);
        }
    }

    @Then("the kitchen manager should be notified")
    public void the_kitchen_manager_should_be_notified() {
        List<String> notifications = notificationService.getNotifications(managerId);
        assertFalse(notifications.isEmpty(), "Notification should have been sent to the manager.");
        // Log the notification for verification during test runs
        notifications.forEach(notification -> System.out.println("📢 MANAGER NOTIFIED: " + notification));
    }

    @Given("an alert is triggered")
    public void an_alert_is_triggered() {
        // This implies an item is low and the system has processed it.
        inventoryService.addInventoryItem(new InventoryItem("Olive Oil", 2, 5, 2.50));
        System.out.println("SETUP: Olive Oil (2) added, threshold (5) for alert trigger.");
        // Reuse the system detection logic which sends notifications
        the_system_detects_it();
    }

    @When("the manager opens the notification")
    public void the_manager_opens_the_notification() {
        List<String> notifications = notificationService.getNotifications(managerId);
        // This step mainly asserts that there's a notification to "open".
        // The actual content check happens in the "Then" step.
        assertFalse(notifications.isEmpty(), "Manager should have notifications to open.");
        System.out.println("MANAGER ACTION: Opens notifications. Found " + notifications.size() + " notification(s).");
    }

    @Then("it should list the item name and quantity left")
    public void it_should_list_the_item_name_and_quantity_left() {
        List<String> notifications = notificationService.getNotifications(managerId);
        assertTrue(!notifications.isEmpty(), "No notifications found for the manager.");

        // Find the specific notification for "Olive Oil"
        String oliveOilNotification = notifications.stream()
                .filter(n -> n.contains("Olive Oil"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Notification for Olive Oil not found."));

        assertTrue(oliveOilNotification.contains("Olive Oil"), "Alert message missing item name 'Olive Oil'.");
        assertTrue(oliveOilNotification.contains("(2 left)"), "Alert message missing correct quantity '2 left' for Olive Oil.");
        System.out.println("📋 VERIFIED NOTIFICATION: " + oliveOilNotification);
    }

    @Given("several items are low")
    public void several_items_are_low() {
        inventoryService.addInventoryItem(new InventoryItem("Milk", 1, 3, 1.00));
        inventoryService.addInventoryItem(new InventoryItem("Eggs", 5, 10, 0.20));
        inventoryService.addInventoryItem(new InventoryItem("Cheese", 2, 5, 3.00));
        System.out.println("SETUP: Multiple low stock items added: Milk (1/3), Eggs (5/10), Cheese (2/5).");
    }

    @When("the alert is generated") // This step name matches the Gherkin for the combined alert scenario
    public void the_alert_is_generated() {
        clearManagerNotifications(); // Ensure fresh notifications for this combined alert
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();

        if (!lowStockItems.isEmpty()) {
            // Create a single combined message for all low stock items
            String combinedMessageContent = lowStockItems.stream()
                    .map(item -> item.getIngredientName() + " (" + item.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            String fullMessage = "🚨 Low-stock items: " + combinedMessageContent;
            notificationService.sendNotification(managerId, fullMessage);
            System.out.println("SYSTEM: Generated combined alert: " + fullMessage);
        } else {
            System.out.println("SYSTEM: No low stock items found to generate a combined alert.");
            // Depending on strictness, one might fail here if "Given several items are low"
            // should guarantee items for the alert. For now, it handles the empty case gracefully.
        }
    }

    @Then("it should combine them in one message")
    public void it_should_combine_them_in_one_message() {
        List<String> notifications = notificationService.getNotifications(managerId);
        assertFalse(notifications.isEmpty(), "A combined notification message should have been sent.");
        assertEquals(1, notifications.size(), "Expected exactly one combined notification message.");

        String combinedMessage = notifications.get(0);
        System.out.println("📢 VERIFIED COMBINED NOTIFICATION: " + combinedMessage);

        assertTrue(combinedMessage.startsWith("🚨 Low-stock items: "), "Combined message prefix is incorrect.");
        assertTrue(combinedMessage.contains("Milk (1)"), "Combined message missing Milk (1) details.");
        assertTrue(combinedMessage.contains("Eggs (5)"), "Combined message missing Eggs (5) details.");
        assertTrue(combinedMessage.contains("Cheese (2)"), "Combined message missing Cheese (2) details.");
    }
}

