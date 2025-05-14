package testCases;

import cook.*;
import io.cucumber.java.en.*;

import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrackStockSteps {

    private InventoryService inventoryService = new InventoryService();
    private Meal currentMeal;



    private kitchen_manager activeKitchenManager; // لتخزين مدير المطبخ النشط في هذا السيناريو


    // بيانات اعتماد ثابتة لمدير المطبخ لأغراض الاختبار
    private static final String MANAGER_EMAIL = "manager@catering.com";
    private static final String MANAGER_PASSWORD = "kitchenMasterPass";
    private static final String MANAGER_NAME = "Head Chef Manager";



    private static final int DEFAULT_THRESHOLD = 5;


    @Given("I am a kitchen manager")
    public void i_am_a_kitchen_manager() {
        this.activeKitchenManager = new kitchen_manager(MANAGER_NAME, MANAGER_EMAIL, MANAGER_PASSWORD, this.inventoryService);




        Application.users.removeIf(user -> user.getEmail().equals(MANAGER_EMAIL));
        Application.users.add(this.activeKitchenManager);


        User loggedInUser = Application.login(MANAGER_EMAIL, MANAGER_PASSWORD);


        assertNotNull(loggedInUser, "Login attempt should return a user object.");
        assertEquals(MANAGER_EMAIL, loggedInUser.getEmail(), "Logged in user email should match.");
        assertEquals(Role.manager, loggedInUser.getRole(), "Logged in user role should be manager.");
        assertTrue(loggedInUser instanceof kitchen_manager, "Logged in user should be an instance of kitchen_manager.");



        assertEquals(this.activeKitchenManager.getEmail(), Application.currentUser.getEmail(), "Application.currentUser should be the logged-in kitchen manager.");
    }

    @When("I open the inventory dashboard")
    public void i_open_the_inventory_dashboard() {

    }

    @Then("I should see updated quantities of all ingredients")
    public void i_should_see_updated_quantities_of_all_ingredients() {

        inventoryService.addInventoryItem(new InventoryItem("Tomato", 50, DEFAULT_THRESHOLD, 0.50));
        int stockLevelTomato = inventoryService.getCurrentStock("Tomato");
        assertEquals(50, stockLevelTomato, "The stock level for Tomato should be 50");


        inventoryService.addInventoryItem(new InventoryItem("Cheese", 30, 8, 2.50));
        int stockLevelCheese = inventoryService.getCurrentStock("Cheese");
        assertEquals(30, stockLevelCheese, "The stock level for Cheese should be 30");
    }

    @Given("an ingredient is used in a meal")
    public void an_ingredient_is_used_in_a_meal() {
        String ingredientName = "Tomato";
        int initialStock = 10;
        int specificThresholdForTomato = 5;
        double priceForTomato = 0.50;


        inventoryService.addInventoryItem(new InventoryItem(ingredientName, initialStock, specificThresholdForTomato, priceForTomato));


        List<String> mealIngredients = new ArrayList<>();
        mealIngredients.add(ingredientName);
        currentMeal = new Meal(mealIngredients, 'M', 12.00);
        currentMeal.setName("Special Tomato Dish");
        currentMeal.updateIngredientQuantity(ingredientName, 2);
    }

    @When("the meal is confirmed")
    public void the_meal_is_confirmed() {
        if (currentMeal == null) {
            throw new IllegalStateException("No meal has been set up to be confirmed. Ensure 'an ingredient is used in a meal' step runs first.");
        }

        inventoryService.updateStockFromMealPreparation(currentMeal);
    }

    @Then("the ingredient stock should decrease accordingly")
    public void the_ingredient_stock_should_decrease_accordingly() {

        int expectedStock = 8;
        String ingredientName = "Tomato";

        int actualStockLevel = inventoryService.getCurrentStock(ingredientName);
        assertEquals(expectedStock, actualStockLevel, "The stock level for " + ingredientName + " should be " + expectedStock + " after meal confirmation.");
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


        boolean garlicIsLow = lowStockItems.stream()
                .anyMatch(item -> item.getIngredientName().equals("Garlic") && item.isLowStock());

        assertTrue(garlicIsLow, "Low stock items should include Garlic as its quantity is below its threshold.");
    }
}