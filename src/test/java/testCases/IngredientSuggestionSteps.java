package testCases;

import cook.Ingredient;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IngredientSuggestionSteps {

    private final Map<String, Ingredient> availableIngredients = new HashMap<>();
    private Ingredient selectedIngredient;
    private String currentDietaryRestriction;
    private Ingredient suggestedAlternative;
    private boolean substitutionAccepted = false;
    private String originalIngredientForSubstitution;
    private String substituteIngredientForSubstitution;

    private void initializeIngredients() {
        availableIngredients.clear();
        Ingredient avocado = new Ingredient("Avocado");
        Ingredient guacamole = new Ingredient("Guacamole");
        Ingredient milk = new Ingredient("Milk");
        Ingredient soyMilk = new Ingredient("Soy Milk");
        Ingredient cheese = new Ingredient("Cheese");

        avocado.addPotentialAlternative(guacamole);
        milk.addPotentialAlternative(soyMilk);

        milk.addDietaryTag("dairy");
        cheese.addDietaryTag("dairy");
        soyMilk.addDietaryTag("vegan");
        soyMilk.addDietaryTag("dairy-free-alternative"); // Tag for being a dairy-free alternative
        guacamole.addDietaryTag("vegan");
        guacamole.addDietaryTag("gluten-free");

        availableIngredients.put("Avocado", avocado);
        availableIngredients.put("Guacamole", guacamole);
        availableIngredients.put("Milk", milk);
        availableIngredients.put("Soy Milk", soyMilk);
        availableIngredients.put("Cheese", cheese);
    }

    @Before
    public void setUp() {
        initializeIngredients();
        selectedIngredient = null;
        currentDietaryRestriction = null;
        suggestedAlternative = null;
        substitutionAccepted = false;
        originalIngredientForSubstitution = null;
        substituteIngredientForSubstitution = null;
        System.out.println("--- IngredientSuggestionSteps: Scenario Start, State Cleared & Ingredients Initialized ---");
    }

    @Given("the customer has selected {string} which is unavailable")
    public void the_customer_has_selected_which_is_unavailable(String ingredientName) {
        selectedIngredient = availableIngredients.get(ingredientName);
        Assertions.assertNotNull(selectedIngredient, "Ingredient " + ingredientName + " not found in available ingredients.");
        selectedIngredient.setAvailable(false);
        System.out.println("Customer selected unavailable ingredient: " + selectedIngredient.getName());
    }

    @When("the system identifies the unavailability")
    public void the_system_identifies_the_unavailability() {
        Assertions.assertNotNull(selectedIngredient, "No ingredient was selected to check for unavailability.");
        Assertions.assertFalse(selectedIngredient.isAvailable(), selectedIngredient.getName() + " was expected to be unavailable.");

        if (!selectedIngredient.getPotentialAlternatives().isEmpty()) {
            suggestedAlternative = selectedIngredient.getPotentialAlternatives().get(0);
            System.out.println("System identified unavailability for " + selectedIngredient.getName() + ". Suggested alternative: " + suggestedAlternative.getName());
        } else {
            System.out.println("System identified unavailability for " + selectedIngredient.getName() + ", but no potential alternatives found.");
            suggestedAlternative = null;
        }
    }

    @Then("the system should suggest {string} as an alternative for {string}")
    public void the_system_should_suggest_as_an_alternative_for(String expectedAlternativeName, String originalIngredientName) {
        Assertions.assertNotNull(suggestedAlternative, "No alternative was suggested by the system.");
        Assertions.assertEquals(expectedAlternativeName, suggestedAlternative.getName(), "Suggested alternative was not the expected one.");
        Assertions.assertEquals(originalIngredientName, selectedIngredient.getName(), "The original ingredient for which suggestion was made is incorrect.");
        System.out.println("Verified: System suggested " + suggestedAlternative.getName() + " for " + originalIngredientName);
    }

    @Given("the customer has a {string} dietary restriction")
    public void the_customer_has_a_dietary_restriction(String restriction) {
        currentDietaryRestriction = restriction.toLowerCase();
        System.out.println("Customer has dietary restriction: " + currentDietaryRestriction);
    }

    @Given("the customer selects {string} which conflicts with the restriction")
    public void the_customer_selects_which_conflicts_with_the_restriction(String ingredientName) {
        selectedIngredient = availableIngredients.get(ingredientName);
        Assertions.assertNotNull(selectedIngredient, "Ingredient " + ingredientName + " not found.");
        Assertions.assertNotNull(currentDietaryRestriction, "Dietary restriction not set for this scenario.");

        boolean conflicts = false;
        if ("dairy-free".equals(currentDietaryRestriction) && selectedIngredient.getDietaryTags().contains("dairy")) {
            conflicts = true;
        }
        // Add more conflict checks if needed for other restrictions

        Assertions.assertTrue(conflicts, selectedIngredient.getName() + " was expected to conflict with " + currentDietaryRestriction + " restriction.");
        System.out.println("Customer selected " + selectedIngredient.getName() + ", which conflicts with " + currentDietaryRestriction);
    }

    @When("the system identifies the dietary conflict")
    public void the_system_identifies_the_dietary_conflict() {
        Assertions.assertNotNull(selectedIngredient, "No ingredient selected to check for dietary conflict.");
        Assertions.assertNotNull(currentDietaryRestriction, "No dietary restriction set.");

        for (Ingredient alt : selectedIngredient.getPotentialAlternatives()) {
            boolean suitable = true;
            if ("dairy-free".equals(currentDietaryRestriction) && alt.getDietaryTags().contains("dairy")) {
                suitable = false;
            }
            // Add more suitability checks for other restrictions

            if (suitable) {
                suggestedAlternative = alt;
                System.out.println("System identified dietary conflict for " + selectedIngredient.getName() + ". Suggested alternative: " + suggestedAlternative.getName());
                return;
            }
        }
        System.out.println("System identified dietary conflict for " + selectedIngredient.getName() + ", but no suitable alternative found.");
        suggestedAlternative = null;
    }

    @Given("an alternative {string} has been suggested for unavailable {string}")
    public void an_alternative_has_been_suggested_for_unavailable(String alternativeName, String originalName) {
        this.originalIngredientForSubstitution = originalName;
        Ingredient originalIng = availableIngredients.get(originalName);
        Ingredient altIng = availableIngredients.get(alternativeName);

        Assertions.assertNotNull(originalIng, "Original ingredient " + originalName + " not found.");
        Assertions.assertNotNull(altIng, "Alternative ingredient " + alternativeName + " not found.");

        originalIng.setAvailable(false); // Ensure it's marked as unavailable
        this.suggestedAlternative = altIng; // Set the suggested alternative
        this.selectedIngredient = originalIng; // The context is about this original ingredient

        System.out.println("Setup: " + alternativeName + " suggested for unavailable " + originalName);
    }

    @Given("an alternative {string} has been suggested for {string} due to dietary conflict")
    public void an_alternative_has_been_suggested_for_due_to_dietary_conflict(String alternativeName, String originalName) {
        Assertions.assertNotNull(currentDietaryRestriction, "Dietary restriction must be set before this step.");
        this.originalIngredientForSubstitution = originalName;
        Ingredient originalIng = availableIngredients.get(originalName);
        Ingredient altIng = availableIngredients.get(alternativeName);

        Assertions.assertNotNull(originalIng, "Original ingredient " + originalName + " not found.");
        Assertions.assertNotNull(altIng, "Alternative ingredient " + alternativeName + " not found.");

        // Verify the conflict and suitability of alternative (optional, but good for robust Given)
        boolean conflicts = false;
        if ("dairy-free".equals(currentDietaryRestriction) && originalIng.getDietaryTags().contains("dairy")) {
            conflicts = true;
        }
        Assertions.assertTrue(conflicts, originalIng.getName() + " should conflict with " + currentDietaryRestriction);

        boolean suitableAlternative = true;
        if ("dairy-free".equals(currentDietaryRestriction) && altIng.getDietaryTags().contains("dairy")) {
            suitableAlternative = false;
        }
        Assertions.assertTrue(suitableAlternative, altIng.getName() + " should be a suitable alternative for " + currentDietaryRestriction);


        this.suggestedAlternative = altIng;
        this.selectedIngredient = originalIng;

        System.out.println("Setup: " + alternativeName + " suggested for " + originalName + " due to " + currentDietaryRestriction + " conflict.");
    }

    @When("the customer accepts the substitution of {string} with {string}")
    public void the_customer_accepts_the_substitution_of_with(String originalIngName, String substituteIngName) {
        Assertions.assertNotNull(suggestedAlternative, "No alternative was suggested to accept.");
        Assertions.assertEquals(originalIngName, this.selectedIngredient.getName(), "Original ingredient in acceptance step does not match context.");
        Assertions.assertEquals(substituteIngName, suggestedAlternative.getName(), "Substitute in acceptance step does not match suggested alternative.");

        this.substitutionAccepted = true;
        this.originalIngredientForSubstitution = originalIngName; // Already set in Given, but good to confirm
        this.substituteIngredientForSubstitution = substituteIngName;
        System.out.println("Customer accepted substitution: " + originalIngName + " -> " + substituteIngName);
    }

    @Then("the chef should be notified of the substitution from {string} to {string}")
    public void the_chef_should_be_notified_of_the_substitution_from_to(String expectedOriginal, String expectedSubstitute) {
        Assertions.assertTrue(substitutionAccepted, "Substitution was not accepted, so chef should not be notified.");
        Assertions.assertEquals(expectedOriginal, originalIngredientForSubstitution, "Original ingredient in notification mismatch.");
        Assertions.assertEquals(expectedSubstitute, substituteIngredientForSubstitution, "Substitute ingredient in notification mismatch.");
        System.out.println("Verified: Chef notified of substitution from " + expectedOriginal + " to " + expectedSubstitute);
        // In a real system, this would trigger an event or call a notification service.
    }
}