package testCases;

import cook.*;
import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class TrackStockSteps {
    private InventoryService inventoryService = new InventoryService();
    private Meal currentMeal;
    private kitchen_manager activeKitchenManager;

    private static final String MANAGER_EMAIL = "manager@catering.com";
    private static final String MANAGER_PASSWORD = "kitchenMasterPass";
    private static final String MANAGER_NAME = "Head Chef Manager";
    private static final int DEFAULT_THRESHOLD = 5;

    @Given("I am a kitchen manager")
    public void i_am_a_kitchen_manager() {
        this.activeKitchenManager = new kitchen_manager(MANAGER_NAME, MANAGER_EMAIL, MANAGER_PASSWORD, this.inventoryService);

        // تنظيف وإعداد بيانات الاختبار
        Application.users.removeIf(user -> user.getEmail().equals(MANAGER_EMAIL));
        Application.users.add(this.activeKitchenManager);

        // تنفيذ عملية تسجيل الدخول
        User loggedInUser = Application.login(MANAGER_EMAIL, MANAGER_PASSWORD);

        // التحقق من النتائج
        assertEquals(this.activeKitchenManager, loggedInUser);
        assertEquals(this.activeKitchenManager, Application.currentUser);
    }

    @When("I open the inventory dashboard")
    public void i_open_the_inventory_dashboard() {
        // هذه الخطوة لا تحتاج إلى تنفيذ فعلي في الاختبار
        assertNotNull(inventoryService);
    }

    @Then("I should see updated quantities of all ingredients")
    public void i_should_see_updated_quantities_of_all_ingredients() {
        inventoryService.addInventoryItem(new InventoryItem("Tomato", 50, DEFAULT_THRESHOLD, 0.50));
        assertEquals(50, inventoryService.getCurrentStock("Tomato"));

        inventoryService.addInventoryItem(new InventoryItem("Cheese", 30, 8, 2.50));
        assertEquals(30, inventoryService.getCurrentStock("Cheese"));
    }

    @Given("an ingredient is used in a meal")
    public void an_ingredient_is_used_in_a_meal() {
        InventoryItem tomatoItem = new InventoryItem("Tomato", 10, 5, 0.50);
        inventoryService.addInventoryItem(tomatoItem);

        currentMeal = new Meal(Collections.singletonList("Tomato"), 'M', 12.00);
        currentMeal.setName("Special Tomato Dish");
        currentMeal.updateIngredientQuantity("Tomato", 2);
    }

    @When("the meal is confirmed")
    public void the_meal_is_confirmed() {
        inventoryService.updateStockFromMealPreparation(currentMeal);
    }

    @Then("the ingredient stock should decrease accordingly")
    public void the_ingredient_stock_should_decrease_accordingly() {
        assertEquals(8, inventoryService.getCurrentStock("Tomato"));
    }

    @Given("stock levels are updated")
    public void stock_levels_are_updated() {
        inventoryService.addInventoryItem(new InventoryItem("Garlic", 3, 2, 0.20));
    }

    @When("any item goes below threshold")
    public void any_item_goes_below_threshold() {
        inventoryService.updateStock("Garlic", 2);
    }

    @Then("the system should highlight it in red")
    public void the_system_should_highlight_it_in_red() {
        List<InventoryItem> lowStockItems = inventoryService.getLowStockItems();
        assertTrue(lowStockItems.stream()
                .anyMatch(item -> item.getIngredientName().equals("Garlic")));
    }
}