package testCases;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;


public class IngredientSuggestionSteps {

    private List<String> selectedIngredients = new ArrayList<>();
    private Set<String> unavailableIngredients = new HashSet<>(Arrays.asList("Avocado", "Truffle Oil"));
    private Map<String, List<String>> alternativeIngredients = new HashMap<>();
    private boolean alternativeSuggested = false;
    private boolean chefNotified = false;
    private boolean dietaryRestrictionApplied = false;


    @Given("the customer has selected an unavailable ingredient")
    public void the_customer_has_selected_an_unavailable_ingredient() {
        selectedIngredients.clear();
        selectedIngredients.add("Avocado");

    }

    @Given("the customer has a dietary restriction")
    public void the_customer_has_a_dietary_restriction() {
        dietaryRestrictionApplied = true;

    }

    @When("the customer selects an incompatible ingredient")
    public void the_customer_selects_an_incompatible_ingredient() {
        selectedIngredients.clear();
        selectedIngredients.add("Milk");

    }

    @Given("the system has suggested an alternative ingredient")
    public void the_system_has_suggested_an_alternative_ingredient() {
        alternativeSuggested = true;

    }

    @When("the substitution is applied")
    public void the_substitution_is_applied() {
        if (alternativeSuggested && !selectedIngredients.isEmpty()) {
            String ingredient = selectedIngredients.get(0);
            if (alternativeIngredients.containsKey(ingredient)) {
               selectedIngredients.clear();
                selectedIngredients.add(alternativeIngredients.get(ingredient).get(0)); // Apply first alternative
               chefNotified = true;
            }
        }

    }

    @Then("the system should notify the chef")
    public void the_system_should_notify_the_chef() {
        if (chefNotified) {
            System.out.println("Chef notified of substitution: " + selectedIngredients.get(0));
        }

    }






}
