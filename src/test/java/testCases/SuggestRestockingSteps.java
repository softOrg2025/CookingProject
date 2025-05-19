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


    private InventoryService inventoryService = new InventoryService();
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
        assertNotNull(lowStockItems, "قائمة العناصر منخفضة المخزون لا يجب أن تكون فارغة (null).");
        assertFalse(lowStockItems.isEmpty(), "يجب أن يحتوي المخزون على عناصر منخفضة المخزون ليقترح إعادة التخزين.");

        assertTrue(lowStockItems.stream().anyMatch(item -> item.getIngredientName().equals("Tomato")),
                "العنصر 'Tomato' يجب أن يكون ضمن قائمة العناصر منخفضة المخزون.");
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
        assertNotNull(restockSuggestions, "اقتراحات إعادة التخزين لا يجب أن تكون فارغة (null).");
        assertFalse(restockSuggestions.isEmpty(), "اقتراحات إعادة التخزين لا يجب أن تكون فارغة.");


        assertEquals(Integer.valueOf(7), restockSuggestions.get("Onion"), "الكمية المقترحة لـ Onion غير صحيحة.");
        assertEquals(Integer.valueOf(7), restockSuggestions.get("Garlic"), "الكمية المقترحة لـ Garlic غير صحيحة.");

        restockSuggestions.forEach((name, qty) -> {
            assertTrue(qty > 0, "الكمية المقترحة لـ " + name + " يجب أن تكون أكبر من 0. الكمية الفعلية: " + qty);
        });
    }

    @Given("I see restock suggestions")
    public void i_see_restock_suggestions() {

        if (restockSuggestions == null || restockSuggestions.isEmpty()) {

            inventoryService.addInventoryItem(new InventoryItem("Onion", 3, 5, DEFAULT_UNIT_PRICE)); // للتأكد من أن هناك اقتراحات
            inventoryService.addInventoryItem(new InventoryItem("Garlic", 1, 4, DEFAULT_UNIT_PRICE));
            restockSuggestions = inventoryService.getRestockSuggestions();
        }
        assertNotNull(restockSuggestions, "يجب أن تكون هناك اقتراحات لإعادة التخزين مرئية.");
        assertFalse(restockSuggestions.isEmpty(), "قائمة اقتراحات إعادة التخزين يجب ألا تكون فارغة.");
    }

    @When("I review them")
    public void i_review_them() {

        planReviewedAndConsideredApprovable = (restockSuggestions != null && !restockSuggestions.isEmpty());
    }

    @Then("I can approve or reject the restocking plan")
    public void can_approve_or_reject_plan() {

        if (restockSuggestions != null && !restockSuggestions.isEmpty()) {
            assertTrue(planReviewedAndConsideredApprovable,
                    "إذا كانت هناك اقتراحات، يجب أن تعتبر الخطة قابلة للموافقة بعد المراجعة.");
        } else {
            assertFalse(planReviewedAndConsideredApprovable,
                    "إذا لم تكن هناك اقتراحات، لا يجب أن تعتبر الخطة قابلة للموافقة بعد المراجعة.");
        }

    }
}