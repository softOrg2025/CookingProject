package cook;

import java.util.*;
import java.util.stream.Collectors;

public class ChefOrderHistoryService {
    private final Map<String, List<Meal>> customerOrderHistory;
    private final Map<String, List<String>> customerPreferences;

    public ChefOrderHistoryService() {
        this.customerOrderHistory = new HashMap<>();
        this.customerPreferences = new HashMap<>();
    }

    public void addOrder(String customerId, Meal meal) {
        customerOrderHistory.computeIfAbsent(customerId, k -> new ArrayList<>()).add(meal);
    }

    public List<Meal> getCustomerOrderHistory(String customerId) {
        return customerOrderHistory.getOrDefault(customerId, Collections.emptyList());
    }

    public List<String> identifyFrequentMeals(String customerId) {
        List<Meal> orders = getCustomerOrderHistory(customerId);
        Map<String, Integer> mealCount = new HashMap<>();

        for (Meal meal : orders) {
            String mealName = meal.getName();
            mealCount.put(mealName, mealCount.getOrDefault(mealName, 0) + 1);
        }

        List<String> frequentMeals = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mealCount.entrySet()) {
            if (entry.getValue() >= 2) { // إذا طلب الوجبة مرتين أو أكثر
                frequentMeals.add(entry.getKey());
            }
        }

        return frequentMeals;
    }

    public String suggestMealPlan(String customerId) {
        List<String> frequentMeals = identifyFrequentMeals(customerId);

        List<String> prefs;
        if (customerPreferences.containsKey(customerId)) {
            prefs = customerPreferences.get(customerId);
        } else {
            prefs = Collections.emptyList();
        }


        List<String> filteredMeals = frequentMeals.stream()
                .filter(meal -> {
                    if (prefs.isEmpty()) return true;
                    return prefs.stream()
                            .anyMatch(pref -> meal.toLowerCase().contains(pref.toLowerCase()));
                })
                .collect(Collectors.toList());

        return "Suggested meal plan based on your preferences: " + String.join(", ", filteredMeals);
    }

    public void analyzePreferences(String customerId) {
        List<Meal> orders = getCustomerOrderHistory(customerId);
        for (Meal meal : orders) {
            for (String ingredient : meal.getIngredients()) {
                customerPreferences.computeIfAbsent(customerId, k -> new ArrayList<>()).add(ingredient);
            }
        }
    }

    public List<String> getCustomerPreferences(String customerId) {
        return customerPreferences.getOrDefault(customerId, Collections.emptyList());
    }
}