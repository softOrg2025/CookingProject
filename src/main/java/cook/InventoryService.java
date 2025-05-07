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

    public boolean isInStock(String ingredientName, int requiredAmount) {
        InventoryItem item = inventory.get(ingredientName);
        return item != null && item.getQuantity() >= requiredAmount;
    }
}
