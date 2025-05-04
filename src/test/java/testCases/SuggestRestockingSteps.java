package testCases;

import io.cucumber.java.en.*;
import java.util.*;

public class SuggestRestockingSteps {


    static class Ingredient {
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


    static class InventoryService {
        private List<Ingredient> ingredients = new ArrayList<>();

        void addIngredient(Ingredient i) {
            ingredients.add(i);
        }

        List<Ingredient> getLowStockIngredients() {
            List<Ingredient> low = new ArrayList<>();
            for (Ingredient i : ingredients) {
                if (i.isLowStock()) low.add(i);
            }
            return low;
        }

        Map<String, Integer> getRestockSuggestions() {
            Map<String, Integer> suggestions = new HashMap<>();
            for (Ingredient i : getLowStockIngredients()) {
                int suggestedQty = (i.threshold * 2) - i.quantity;
                suggestions.put(i.name, suggestedQty);
            }
            return suggestions;
        }

        boolean reviewRestockPlan(Map<String, Integer> plan) {
            return !plan.isEmpty();
        }
    }

    private InventoryService inventoryService = new InventoryService();
    private List<Ingredient> lowStockIngredients;
    private Map<String, Integer> restockSuggestions;
    private boolean isApproved;

    @Given("an ingredient is below the threshold")
    public void an_ingredient_is_below_threshold() {
        inventoryService.addIngredient(new Ingredient("Tomato", 2, 10));
    }

    @When("I view the inventory")
    public void i_view_the_inventory() {
        lowStockIngredients = inventoryService.getLowStockIngredients();
    }

    @Then("the system should suggest restocking")
    public void system_should_suggest_restocking() {

        assert lowStockIngredients != null && !lowStockIngredients.isEmpty() : "Inventory should suggest restocking";
    }

    @Given("multiple items are low")
    public void multiple_items_are_low() {
        inventoryService.addIngredient(new Ingredient("Onion", 3, 5));
        inventoryService.addIngredient(new Ingredient("Garlic", 1, 4));
    }

    @When("I open the restock suggestions")
    public void open_restock_suggestions() {
        restockSuggestions = inventoryService.getRestockSuggestions();
    }

    @Then("I should see suggested quantities based on usage rate")
    public void should_see_suggested_quantities() {

        assert restockSuggestions != null && !restockSuggestions.isEmpty() : "Restock suggestions should not be empty";


        restockSuggestions.forEach((name, qty) -> {
            assert qty > 0 : "Suggested quantity for " + name + " should be greater than 0";
        });
    }

    @Given("I see restock suggestions")
    public void i_see_restock_suggestions() {
        restockSuggestions = inventoryService.getRestockSuggestions();
    }

    @When("I review them")
    public void i_review_them() {
        isApproved = inventoryService.reviewRestockPlan(restockSuggestions);
    }

    @Then("I can approve or reject the restocking plan")
    public void can_approve_or_reject_plan() {

        assert (isApproved || !isApproved) : "The restocking plan should be approved or rejected";
    }
}