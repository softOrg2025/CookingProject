package testCases;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import cook.InventoryItem;
import cook.InventoryService;
import cook.NotificationService;
import cook.kitchen_manager;
import cook.PurchaseOrder;

import java.util.List;
import java.util.stream.Collectors;

public class LowStockAlertsSteps {

    private InventoryService inventoryService;
    private NotificationService notificationService;
    private kitchen_manager kitchenManager;
    private final String managerId = "kitchen_manager_main";

    public LowStockAlertsSteps() {
        this.inventoryService = new InventoryService();
        this.notificationService = new NotificationService();
        this.kitchenManager = new kitchen_manager("Manager Name", "manager@example.com", "password", inventoryService);
    }

    private void clearManagerNotifications() {
        this.notificationService.clearNotifications(this.managerId);
    }

    @Given("an ingredient drops below its restock level")
    public void an_ingredient_drops_below_its_restock_level() {
        InventoryItem item = new InventoryItem("Tomatoes", 4, 5, 0.50);
        inventoryService.addInventoryItem(item);
    }

    @When("the system detects it")
    public void the_system_detects_it() {
        clearManagerNotifications();
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();

        for (InventoryItem item : lowStockItems) {
            String message = "⚠ Low stock: " + item.getIngredientName() + " (" + item.getQuantity() + " left)";
            notificationService.sendNotification(managerId, message);

            // Create purchase order for critical items
            if (item.getQuantity() <= 2) {
                PurchaseOrder po = inventoryService.createPurchaseOrderForCriticalStock(
                        item.getIngredientName(),
                        item.getThreshold() * 2,
                        "Supplier",
                        item.getUnitPrice()
                );
                inventoryService.sendPurchaseOrderToSupplier(po.getOrderId());
            }
        }
    }

    @Then("the kitchen manager should be notified")
    public void the_kitchen_manager_should_be_notified() {
        List<String> notifications = notificationService.getNotifications(managerId);
        assertFalse(notifications.isEmpty(), "Notification should have been sent to the manager.");

        // Update system message with notification details
        cook.Application.setSystemMessage("Manager received low stock notification");
    }

    @Given("an alert is triggered")
    public void an_alert_is_triggered() {
        InventoryItem item = new InventoryItem("Olive Oil", 2, 5, 2.50);
        inventoryService.addInventoryItem(item);
        the_system_detects_it();
    }

    @When("the manager opens the notification")
    public void the_manager_opens_the_notification() {
        List<String> notifications = notificationService.getNotifications(managerId);
        assertFalse(notifications.isEmpty(), "Manager should have notifications to open.");

        // Update system message with notification content
        cook.Application.setSystemMessage("Manager viewed notification: " + notifications.get(0));
    }

    @Then("it should list the item name and quantity left")
    public void it_should_list_the_item_name_and_quantity_left() {
        List<String> notifications = notificationService.getNotifications(managerId);
        assertTrue(!notifications.isEmpty(), "No notifications found for the manager.");

        String oliveOilNotification = notifications.stream()
                .filter(n -> n.contains("Olive Oil"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Notification for Olive Oil not found."));

        assertTrue(oliveOilNotification.contains("Olive Oil"), "Alert message missing item name 'Olive Oil'.");
        assertTrue(oliveOilNotification.contains("(2 left)"), "Alert message missing correct quantity '2 left' for Olive Oil.");
    }

    @Given("several items are low")
    public void several_items_are_low() {
        inventoryService.addInventoryItem(new InventoryItem("Milk", 1, 3, 1.00));
        inventoryService.addInventoryItem(new InventoryItem("Eggs", 5, 10, 0.20));
        inventoryService.addInventoryItem(new InventoryItem("Cheese", 2, 5, 3.00));
    }

    @When("the alert is generated")
    public void the_alert_is_generated() {
        clearManagerNotifications();
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();

        if (!lowStockItems.isEmpty()) {
            String combinedMessageContent = lowStockItems.stream()
                    .map(item -> item.getIngredientName() + " (" + item.getQuantity() + ")")
                    .collect(Collectors.joining(", "));
            String fullMessage = "🚨 Low-stock items: " + combinedMessageContent;
            notificationService.sendNotification(managerId, fullMessage);

            // Update system message with combined alert
            cook.Application.setSystemMessage("Generated combined alert for " + lowStockItems.size() + " items");
        }
    }

    @Then("it should combine them in one message")
    public void it_should_combine_them_in_one_message() {
        List<String> notifications = notificationService.getNotifications(managerId);
        assertFalse(notifications.isEmpty(), "A combined notification message should have been sent.");
        assertEquals(1, notifications.size(), "Expected exactly one combined notification message.");

        String combinedMessage = notifications.get(0);
        assertTrue(combinedMessage.startsWith("🚨 Low-stock items: "), "Combined message prefix is incorrect.");
        assertTrue(combinedMessage.contains("Milk (1)"), "Combined message missing Milk (1) details.");
        assertTrue(combinedMessage.contains("Eggs (5)"), "Combined message missing Eggs (5) details.");
        assertTrue(combinedMessage.contains("Cheese (2)"), "Combined message missing Cheese (2) details.");
    }
}