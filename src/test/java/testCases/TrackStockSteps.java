package testCases;
import io.cucumber.java.en.*;
public class TrackStockSteps {

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
        System.out.println("📦 Displayed updated stock levels");
    }

    @Given("an ingredient is used in a meal")
    public void an_ingredient_is_used_in_a_meal() {
        System.out.println("🥄 Ingredient used in meal");
    }

    @When("the meal is confirmed")
    public void the_meal_is_confirmed() {
        System.out.println("✅ Meal confirmed");
    }

    @Then("the ingredient stock should decrease accordingly")
    public void the_ingredient_stock_should_decrease_accordingly() {
        System.out.println("📉 Stock quantity updated");
    }

    @Given("stock levels are updated")
    public void stock_levels_are_updated() {
        System.out.println("🔄 Stock levels refreshed");
    }

    @When("any item goes below threshold")
    public void any_item_goes_below_threshold() {
        System.out.println("⚠️ Item reached low threshold");
    }

    @Then("the system should highlight it in red")
    public void the_system_should_highlight_it_in_red() {
        System.out.println("🚨 Low-stock item highlighted in red");
    }
}
