package testCases;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;



public class SystemValidationSteps {

    private List<String> selectedIngredients = new ArrayList<>();
    private Set<String> incompatibleIngredients = new HashSet<>(Arrays.asList("Milk", "Lemon"));
    private Map<String, List<String>> alternativeIngredients = new HashMap<>();
    private boolean errorDisplayed = false;

    private Set<String> invalidIngredients = new HashSet<>();
    private boolean isMealSubmitted = false;




    @When("the system checks the combination")
    public void the_system_checks_the_combination() {
        for (String ingredient : selectedIngredients) {
            if (incompatibleIngredients.contains(ingredient)) {
                errorDisplayed = true;
                break;
            }
        }
    }

    @Then("the system should flag any incompatible ingredients")
    public void the_system_should_flag_any_incompatible_ingredients() {
        if (errorDisplayed) {
            System.out.println("Error: Incompatible ingredients selected.");
        }

    }

    @Given("the customer has selected incompatible ingredients")
    public void the_customer_has_selected_incompatible_ingredients() {
        selectedIngredients.clear();
        selectedIngredients.add("Milk");
        selectedIngredients.add("Lemon");

    }

    @When("the system identifies the issue")
    public void the_system_identifies_the_issue() {
        for (String ingredient : selectedIngredients) {
            if (incompatibleIngredients.contains(ingredient)) {
                errorDisplayed = true;
                break;
            }
        }

    }

    @Then("the system should suggest alternative ingredients")
    public void the_system_should_suggest_alternative_ingredients() {
        if (errorDisplayed) {
            for (String ingredient : selectedIngredients) {
                if (incompatibleIngredients.contains(ingredient)) {
                    List<String> alternatives = alternativeIngredients.get(ingredient);
                    System.out.println("Suggested alternatives for " + ingredient + ": " + alternatives);
                }
            }
        }

    }

    @Given("the customer has selected invalid ingredients")
    public void the_customer_has_selected_invalid_ingredients() {
        selectedIngredients.clear();
        selectedIngredients.add("Milk");
        selectedIngredients.add("Lemon");

    }

    @When("the customer tries to submit the meal")
    public void the_customer_tries_to_submit_the_meal() {
        for (String ingredient : selectedIngredients) {
            if (incompatibleIngredients.contains(ingredient)) {
                errorDisplayed = true;
                break;
            }
        }

    }

    @Then("the system should prevent submission and display an error")
    public void the_system_should_prevent_submission_and_display_an_error() {
        if (errorDisplayed) {
            System.out.println("Error: Cannot submit meal with incompatible ingredients.");
        }

    }












}
