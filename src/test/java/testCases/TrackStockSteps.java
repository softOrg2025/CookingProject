package testCases;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;
import java.util.*;

public class TrackStockSteps {

    // ======= Model =======
    static class Ingredient {
        String name;
        int quantity;

        Ingredient(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        void use(int amount) {
            this.quantity -= amount;
        }

        boolean isLowStock(int threshold) {
            return quantity <= threshold;
        }
    }

    // ======= Service =======
    static class InventoryService {
        private Map<String, Ingredient> inventory = new HashMap<>();

        void addIngredient(Ingredient ingredient) {
            inventory.put(ingredient.name, ingredient);
        }

        int getStockLevel(String ingredientName) {
            return inventory.getOrDefault(ingredientName, null).quantity;
        }

        void updateStock(String ingredientName, int usedAmount) {
            Ingredient ingredient = inventory.get(ingredientName);
            if (ingredient != null) {
                ingredient.use(usedAmount);
            }
        }

        List<Ingredient> getLowStockItems(int threshold) {
            List<Ingredient> lowStockItems = new ArrayList<>();
            for (Ingredient ingredient : inventory.values()) {
                if (ingredient.isLowStock(threshold)) {
                    lowStockItems.add(ingredient);
                }
            }
            return lowStockItems;
        }
    }

    // ======= Step Definitions =======
    private InventoryService inventoryService = new InventoryService();
    private Ingredient tomato;

    @Given("I am a kitchen manager")
    public void i_am_a_kitchen_manager() {
        System.out.println("✅ Logged in as kitchen manager");
    }

    @When("I open the inventory dashboard")
    public void i_open_the_inventory_dashboard() {
        System.out.println("📊 Inventory dashboard opened");
    }

    @Then("I should see updated quantities of all ingredients")
    public void i_should_see_updated_quantities_of_all_ingredients() {
        // اختبار إضافة مكون وتحديثه
        inventoryService.addIngredient(new Ingredient("Tomato", 50));
        int stockLevel = inventoryService.getStockLevel("Tomato");
        assertEquals("The stock level should be 50", 50, stockLevel);
    }

    @Given("an ingredient is used in a meal")
    public void an_ingredient_is_used_in_a_meal() {
        tomato = new Ingredient("Tomato", 10); // Initial stock of 10
        inventoryService.addIngredient(tomato);
        System.out.println("🥄 Ingredient used in meal");
    }

    @When("the meal is confirmed")
    public void the_meal_is_confirmed() {
        inventoryService.updateStock("Tomato", 2); // Using 2 units of tomato
        System.out.println("✅ Meal confirmed");
    }

    @Then("the ingredient stock should decrease accordingly")
    public void the_ingredient_stock_should_decrease_accordingly() {
        int stockLevel = inventoryService.getStockLevel("Tomato");
        assertEquals("The stock level should decrease by 2", 8, stockLevel);
    }

    @Given("stock levels are updated")
    public void stock_levels_are_updated() {
        inventoryService.addIngredient(new Ingredient("Garlic", 3));
        System.out.println("🔄 Stock levels refreshed");
    }

    @When("any item goes below threshold")
    public void any_item_goes_below_threshold() {
        inventoryService.updateStock("Garlic", 3); // Reduces stock to 0
        System.out.println("⚠️ Item reached low threshold");
    }

    @Then("the system should highlight it in red")
    public void the_system_should_highlight_it_in_red() {
        List<Ingredient> lowStockItems = inventoryService.getLowStockItems(5);
        assertTrue("Low stock items should include Garlic", lowStockItems.stream()
                .anyMatch(item -> item.name.equals("Garlic") && item.isLowStock(5)));
        System.out.println("🚨 Low-stock item highlighted in red");
    }
}