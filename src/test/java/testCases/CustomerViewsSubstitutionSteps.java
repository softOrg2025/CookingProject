package testCases;

import cook.Meal;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;

public class CustomerViewsSubstitutionSteps {
    private String selectedIncompatibleIngredient;
    private List<String> alternativeSuggestions = new ArrayList<>();
    private boolean suggestionsDisplayed = false;
    private boolean substitutionAccepted = false;
    private boolean substitutionRejected = false;
    private String customerNotification = "";
    private Meal currentMeal;

    private static final Map<String, List<String>> substitutionOptions = new HashMap<>();

    static {
        substitutionOptions.put("Milk", Arrays.asList("Almond Milk", "Soy Milk", "Oat Milk"));
        substitutionOptions.put("Peanuts", Arrays.asList("Cashews", "Almonds", "Sunflower Seeds"));
        substitutionOptions.put("Wheat", Arrays.asList("Rice Flour", "Corn Flour", "Quinoa Flour"));
    }



    @Given("the customer has selected an incompatible ingredient")
    public void the_customer_has_selected_an_incompatible_ingredient() {
        if (currentMeal != null) {
            for (String ingredient : currentMeal.getIngredients()) {
                if (substitutionOptions.containsKey(ingredient)) {
                    selectedIncompatibleIngredient = ingredient;
                    alternativeSuggestions = substitutionOptions.get(ingredient);
                    return;
                }
            }
        }
        System.out.println("No incompatible ingredient found in the meal.");
    }

    @When("the system suggests alternatives")
    public void the_system_suggests_alternatives() {
        if (!alternativeSuggestions.isEmpty()) {
            suggestionsDisplayed = true;
            System.out.println("Suggested alternatives for " + selectedIncompatibleIngredient + ": " + alternativeSuggestions);
        } else {
            System.out.println("No substitutions available for " + selectedIncompatibleIngredient);
        }
    }

    @Then("the system should display the suggestions to the customer")
    public void the_system_should_display_the_suggestions_to_the_customer() {
        if (suggestionsDisplayed) {
            System.out.println("Displayed to customer: " + alternativeSuggestions);
        } else {
            System.out.println("No suggestions to display.");
        }
    }

    @Given("the customer is viewing substitution suggestions")
    public void the_customer_is_viewing_substitution_suggestions() {
        if (suggestionsDisplayed) {
            System.out.println("Customer is reviewing the suggestions.");
        } else {
            System.out.println("No suggestions available to view.");
        }
    }

    @When("the customer accepts a suggestion")
    public void the_customer_accepts_a_suggestion() {
        if (!alternativeSuggestions.isEmpty() && currentMeal != null) {
            String chosenSubstitution = alternativeSuggestions.get(0); // Automatically select the first alternative
            substitutionAccepted = true;
            currentMeal.substituteIngredient(selectedIncompatibleIngredient, chosenSubstitution);
            System.out.println("Customer accepted substitution: " + selectedIncompatibleIngredient + " → " + chosenSubstitution);
        } else {
            System.out.println("No valid substitutions available.");
        }
    }

    @When("the customer rejects a suggestion")
    public void the_customer_rejects_a_suggestion() {
        if (suggestionsDisplayed) {
            substitutionRejected = true;
            customerNotification = "Customer rejected substitution. Chef has been notified.";
            System.out.println(customerNotification);
        }
    }



}
