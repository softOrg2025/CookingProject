package testCases;
import java.util.*;
import static org.junit.Assert.*;



import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class MealCustomizationSteps {

    private List<String> selectedIngredients = new ArrayList<>();
    private Set<String> incompatibleIngredients = new HashSet<>(Arrays.asList("Milk", "Lemon"));
    private boolean errorDisplayed = false;
    private Map<String, List<String>> savedMeals = new HashMap<>();
    private String currentUser = "testUser";


    @When("the customer selects {string}")
    public void the_customer_selects(String ingredient) {
        selectedIngredients.add(ingredient);

    }

    @When("the customer chooses ingredients")
    public void the_customer_chooses_ingredients() {
        // Write code here that turns the phrase above into concrete actions

    }

    @Then("the system should save the selected ingredients")
    public void the_system_should_save_the_selected_ingredients() {
        savedMeals.put(currentUser, new ArrayList<>(selectedIngredients));

    }

    @Given("the customer has selected ingredients")
    public void the_customer_has_selected_ingredients() {
        selectedIngredients.add("Tomato");
        selectedIngredients.add("Cheese");

    }

    @When("the customer tries to combine incompatible ingredients")
    public void the_customer_tries_to_combine_incompatible_ingredients() {
        for (String ingredient : selectedIngredients) {
            if (incompatibleIngredients.contains(ingredient)) {
                errorDisplayed = true;
                break;
            }
        }

    }

    @Then("the system should display an error message")
    public void the_system_should_display_an_error_message() {
        if (errorDisplayed) {
            System.out.println("Error: Incompatible ingredients selected.");
        }

    }

    @When("the customer saves the custom meal")
    public void the_customer_saves_the_custom_meal() {
        if (!savedMeals.containsKey(currentUser)) {
            savedMeals.put(currentUser, new ArrayList<>(selectedIngredients));
        }


    }

    @Then("the system should store the meal for future orders")
    public void the_system_should_store_the_meal_for_future_orders() {
        List<String> savedMeal = savedMeals.get(currentUser);
        if (savedMeal != null && savedMeal.equals(selectedIngredients)) {
            System.out.println("Custom meal saved successfully.");
        } else {
            System.out.println("Failed to save the custom meal.");
        }


    }







}
