package cook;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryService {
    private final Map<String, InventoryItem> inventory = new HashMap<>();
    private final Map<String, PurchaseOrder> generatedPurchaseOrders = new HashMap<>();
    private final NotificationService notificationService;
    private String Null_Ingredient = "Ingredient name cannot be null";

    public InventoryService() {
        this.notificationService = new NotificationService();
    }

    public InventoryService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void updateStockFromMealPreparation(Meal meal) {
        Objects.requireNonNull(meal, "Meal cannot be null");
        meal.getIngredientQuantities().forEach((ingredient, quantity) -> {
            InventoryItem item = inventory.get(ingredient);
            if (item != null) {
                try {
                    item.use(quantity);
                    if (item.isLowStock()) {
                        notificationService.sendNotification("inventory-manager",
                                "Low stock alert for: " + ingredient);
                    }
                } catch (IllegalArgumentException e) {
                    notificationService.sendNotification("inventory-manager",
                            "Failed to use " + quantity + " of " + ingredient + ": " + e.getMessage());
                }
            }
        });
    }

    public void addInventoryItem(InventoryItem item) {
        Objects.requireNonNull(item, "Inventory item cannot be null");
        inventory.put(item.getIngredientName(), item);
    }

    public List<InventoryItem> getLowStockItems() {
        return inventory.values().stream()
                .filter(InventoryItem::isLowStock)
                .collect(Collectors.toList());
    }

    public void updateStock(String ingredientName, int usedAmount) {
        Objects.requireNonNull(ingredientName, Null_Ingredient);
        InventoryItem item = inventory.get(ingredientName);
        if (item != null) {
            item.use(usedAmount);
        }
    }

    public Map<String, Integer> getRestockSuggestions() {
        return getLowStockItems().stream()
                .collect(Collectors.toMap(
                        InventoryItem::getIngredientName,
                        item -> (item.getThreshold() * 2) - item.getQuantity()
                ));
    }

    public int getCurrentStock(String ingredientName) {
        Objects.requireNonNull(ingredientName, Null_Ingredient);
        InventoryItem item = inventory.get(ingredientName);
        return item != null ? item.getQuantity() : 0;
    }

    public PurchaseOrder createPurchaseOrderForCriticalStock(String ingredientName,
                                                             int quantityToOrder,
                                                             String supplierName,
                                                             double price) {
        Objects.requireNonNull(ingredientName, Null_Ingredient);
        Objects.requireNonNull(supplierName, "Supplier name cannot be null");

        PurchaseOrder po = new PurchaseOrder(ingredientName, quantityToOrder, supplierName, price);
        generatedPurchaseOrders.put(po.getOrderId(), po);
        return po;
    }

    public boolean sendPurchaseOrderToSupplier(String orderId) {
        Objects.requireNonNull(orderId, "Order ID cannot be null");
        return generatedPurchaseOrders.containsKey(orderId);
    }

    public Map<String, InventoryItem> getInventoryItemsMap() {
        return Collections.unmodifiableMap(this.inventory);
    }

    public PurchaseOrder getPurchaseOrderByIngredientName(String ingredientName) {
        Objects.requireNonNull(ingredientName, Null_Ingredient);
        return generatedPurchaseOrders.values().stream()
                .filter(po -> ingredientName.equals(po.getIngredientName()))
                .findFirst()
                .orElse(null);
    }


}