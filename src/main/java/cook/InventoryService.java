package cook;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryService {
    private Map<String, InventoryItem> inventory = new HashMap<>();

    private List<Meal> mealsUsingIngredients = new ArrayList<>();

    public void registerMeal(Meal meal) {
        mealsUsingIngredients.add(meal);
    }
    public void updateStockFromMealPreparation(Meal meal) {
        meal.getIngredientQuantities().forEach((ingredient, quantity) -> {
            InventoryItem item = inventory.get(ingredient);
            if (item != null) {
                item.use(quantity);

                // Notify if low stock after usage
                if (item.isLowStock()) {
                    System.out.println("Low stock alert for: " + ingredient);
                }
            }
        });
    }

    public void addInventoryItem(InventoryItem item) {
        inventory.put(item.getIngredientName(), item);
    }

    public List<InventoryItem> getLowStockItems() {
        return inventory.values().stream()
                .filter(InventoryItem::isLowStock)
                .collect(Collectors.toList());
    }

    public void updateStock(String ingredientName, int usedAmount) {
        InventoryItem item = inventory.get(ingredientName);
        if (item != null) {
            item.use(usedAmount);
        }
    }

    public Map<String, Integer> getRestockSuggestions() {
        Map<String, Integer> suggestions = new HashMap<>();
        for (InventoryItem item : getLowStockItems()) {
            int suggestedQty = (item.getThreshold() * 2) - item.getQuantity();
            suggestions.put(item.getIngredientName(), suggestedQty);
        }
        return suggestions;
    }

    // Additional useful methods
    public int getCurrentStock(String ingredientName) {
        InventoryItem item = inventory.get(ingredientName);
        return item != null ? item.getQuantity() : 0;
    }


    private Map<String, PurchaseOrder> generatedPurchaseOrders = new HashMap<>(); // لتخزين أوامر الشراء التي تم إنشاؤها

    // دالة جديدة لإنشاء وحفظ أمر شراء
    public PurchaseOrder createPurchaseOrderForCriticalStock(String ingredientName, int quantityToOrder, String supplierName, double price) {
        // في الواقع، قد يكون هناك منطق أكثر تعقيدًا لتحديد المورد والسعر
        PurchaseOrder po = new PurchaseOrder(ingredientName, quantityToOrder, supplierName, price);
        generatedPurchaseOrders.put(po.getOrderId(), po);
        System.out.println("Purchase order created: " + po.getOrderId() + " for " + ingredientName);
        return po;
    }

    public PurchaseOrder getPurchaseOrderDetails(String orderId) {
        return generatedPurchaseOrders.get(orderId);
    }

    public boolean sendPurchaseOrderToSupplier(String orderId) {
        PurchaseOrder po = generatedPurchaseOrders.get(orderId);
        if (po != null) {
            // منطق إرسال أمر الشراء إلى المورد
            // مثلاً: EmailService.sendEmail(po.getSupplierEmail(), "New PO: " + po.getOrderId(), po.toString());
            System.out.println("Purchase order " + po.getOrderId() + " sent to supplier " + po.getSupplierName());
            return true;
        }
        return false;

    }
    public PurchaseOrder getPurchaseOrderByIngredientName(String ingredientName) {
        if (ingredientName == null) {
            System.err.println("Attempting to find PO with null ingredient name.");
            return null;
        }

        for (PurchaseOrder po : generatedPurchaseOrders.values()) {
            if (ingredientName.equals(po.getIngredientName())) {
                return po;
            }
        }
        System.err.println("No PurchaseOrder found for ingredient name: " + ingredientName);
        return null;}








}
