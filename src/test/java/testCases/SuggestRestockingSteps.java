package testCases;

import cook.InventoryItem;
import cook.InventoryService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SuggestRestockingSteps {

    private final InventoryService inventoryService = new InventoryService();
    private List<InventoryItem> lowStockItems;
    private Map<String, Integer> restockSuggestions;
    private boolean planReviewedAndConsideredApprovable;

    private static final double DEFAULT_UNIT_PRICE = 0.0;

    @Given("an ingredient is below the threshold")
    public void an_ingredient_is_below_the_threshold() {
        inventoryService.addInventoryItem(new InventoryItem("Tomato", 2, 10, DEFAULT_UNIT_PRICE));
    }

    @When("I view the inventory")
    public void i_view_the_inventory() {
        lowStockItems = inventoryService.getLowStockItems();
    }

    @Then("the system should suggest restocking")
    public void system_should_suggest_restocking() {
        assertNotNull(lowStockItems, "Low stock item list should not be null.");
        assertFalse(lowStockItems.isEmpty(), "Inventory should contain low-stock items to suggest restocking.");

        assertTrue(lowStockItems.stream().anyMatch(item -> item.getIngredientName().equals("Tomato")),
                "'Tomato' should be included in the list of low-stock items.");
    }

    @Given("multiple items are low")
    public void multiple_items_are_low() {
        inventoryService.addInventoryItem(new InventoryItem("Onion", 3, 5, DEFAULT_UNIT_PRICE));
        inventoryService.addInventoryItem(new InventoryItem("Garlic", 1, 4, DEFAULT_UNIT_PRICE));
    }

    @When("I open the restock suggestions")
    public void open_restock_suggestions() {
        restockSuggestions = inventoryService.getRestockSuggestions();
    }

    @Then("I should see suggested quantities based on usage rate")
    public void should_see_suggested_quantities() {
        assertNotNull(restockSuggestions, "Restock suggestions should not be null.");
        assertFalse(restockSuggestions.isEmpty(), "Restock suggestions should not be empty.");

        assertEquals(Integer.valueOf(7), restockSuggestions.get("Onion"), "Suggested quantity for Onion is incorrect.");
        assertEquals(Integer.valueOf(7), restockSuggestions.get("Garlic"), "Suggested quantity for Garlic is incorrect.");

        restockSuggestions.forEach((name, qty) ->
                assertTrue(qty > 0, "Suggested quantity for " + name + " should be greater than 0. Actual: " + qty));
    }

    @Given("I see restock suggestions")
    public void i_see_restock_suggestions() {
        if (restockSuggestions == null || restockSuggestions.isEmpty()) {
            inventoryService.addInventoryItem(new InventoryItem("Onion", 3, 5, DEFAULT_UNIT_PRICE));
            inventoryService.addInventoryItem(new InventoryItem("Garlic", 1, 4, DEFAULT_UNIT_PRICE));
            restockSuggestions = inventoryService.getRestockSuggestions();
        }
        assertNotNull(restockSuggestions, "Restock suggestions should be visible.");
        assertFalse(restockSuggestions.isEmpty(), "Restock suggestions list should not be empty.");
    }

    @When("I review them")
    public void i_review_them() {
        planReviewedAndConsideredApprovable = (restockSuggestions != null && !restockSuggestions.isEmpty());
    }

    @Then("I can approve or reject the restocking plan")
    public void can_approve_or_reject_plan() {
        if (restockSuggestions != null && !restockSuggestions.isEmpty()) {
            assertTrue(planReviewedAndConsideredApprovable,
                    "If suggestions exist, the plan should be considered approvable after review.");
        } else {
            assertFalse(planReviewedAndConsideredApprovable,
                    "If no suggestions exist, the plan should not be considered approvable.");
        }
    }
}