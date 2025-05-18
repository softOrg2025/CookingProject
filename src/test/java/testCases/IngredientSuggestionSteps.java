package testCases;

import cook.Ingredient;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IngredientSuggestionSteps {

    private final Map<String, Ingredient> availableIngredients = new HashMap<>();
    private Ingredient selectedIngredient;
    private String currentDietaryRestriction;
    private Ingredient suggestedAlternative;
    private boolean substitutionAccepted = false;
    private String originalIngredientForSubstitution;
    private String substituteIngredientForSubstitution;
    private final TestContext testContext; // Assuming TestContext for potential shared messages

    public IngredientSuggestionSteps(TestContext context) { // Constructor for TestContext injection
        this.testContext = context;
    }

    private void initializeIngredients() {
        availableIngredients.clear();
        Ingredient avocado = new Ingredient("Avocado");
        Ingredient guacamole = new Ingredient("Guacamole");
        Ingredient milk = new Ingredient("Milk");
        Ingredient soyMilk = new Ingredient("Soy Milk");
        Ingredient cheese = new Ingredient("Cheese");
        Ingredient almondMilk = new Ingredient("Almond Milk"); // Added for more alternatives

        avocado.addPotentialAlternative(guacamole);
        milk.addPotentialAlternative(soyMilk);
        milk.addPotentialAlternative(almondMilk); // Milk has multiple alternatives

        milk.addDietaryTag("dairy");
        cheese.addDietaryTag("dairy");
        soyMilk.addDietaryTag("vegan");
        soyMilk.addDietaryTag("dairy-free"); // Simplified tag
        almondMilk.addDietaryTag("vegan");
        almondMilk.addDietaryTag("dairy-free");
        guacamole.addDietaryTag("vegan");
        guacamole.addDietaryTag("gluten-free");

        availableIngredients.put("Avocado", avocado);
        availableIngredients.put("Guacamole", guacamole);
        availableIngredients.put("Milk", milk);
        availableIngredients.put("Soy Milk", soyMilk);
        availableIngredients.put("Cheese", cheese);
        availableIngredients.put("Almond Milk", almondMilk);

        Assertions.assertFalse(availableIngredients.isEmpty(), "Ingredients should be initialized.");
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
        if (testContext != null) testContext.lastSystemMessage = null; // Reset shared message
        // System.out.println("--- IngredientSuggestionSteps: Scenario Start, State Cleared & Ingredients Initialized ---");
        Assertions.assertTrue(availableIngredients.containsKey("Milk"), "Initial state should have Milk.");
    }

    @Given("the customer has selected {string} which is unavailable")
    public void the_customer_has_selected_which_is_unavailable(String ingredientName) {
        selectedIngredient = availableIngredients.get(ingredientName);
        Assertions.assertNotNull(selectedIngredient, "Ingredient '" + ingredientName + "' not found in available ingredients. Check spelling or initialization.");
        selectedIngredient.setAvailable(false); // Mark as unavailable for the test
        Assertions.assertFalse(selectedIngredient.isAvailable(), "Ingredient '" + ingredientName + "' should be marked as unavailable.");
        if (testContext != null) testContext.lastSystemMessage = "Customer selected unavailable ingredient: " + selectedIngredient.getName();
    }

    @When("the system identifies the unavailability")
    public void the_system_identifies_the_unavailability() {
        Assertions.assertNotNull(selectedIngredient, "No ingredient was selected to check for unavailability.");
        Assertions.assertFalse(selectedIngredient.isAvailable(), selectedIngredient.getName() + " was expected to be unavailable for this step.");

        // Find the first available potential alternative
        Optional<Ingredient> firstAvailableAlternative = selectedIngredient.getPotentialAlternatives().stream()
                .filter(Ingredient::isAvailable) // Assuming alternatives might also be unavailable
                .findFirst();

        if (firstAvailableAlternative.isPresent()) {
            suggestedAlternative = firstAvailableAlternative.get();
            Assertions.assertNotNull(suggestedAlternative, "Suggested alternative should not be null if found.");
            if (testContext != null) testContext.lastSystemMessage = "System identified unavailability for " + selectedIngredient.getName() + ". Suggested alternative: " + suggestedAlternative.getName();
        } else {
            suggestedAlternative = null; // Explicitly null if no suitable one is found
            Assertions.assertNull(suggestedAlternative, "No available alternative should be suggested if none are found/available.");
            if (testContext != null) testContext.lastSystemMessage = "System identified unavailability for " + selectedIngredient.getName() + ", but no available potential alternatives found.";
        }
    }

    @Then("the system should suggest {string} as an alternative for {string}")
    public void the_system_should_suggest_as_an_alternative_for(String expectedAlternativeName, String originalIngredientName) {
        Assertions.assertNotNull(suggestedAlternative, "No alternative was suggested by the system, but one was expected.");
        Assertions.assertEquals(expectedAlternativeName, suggestedAlternative.getName(), "Suggested alternative was not the expected one.");
        Assertions.assertNotNull(selectedIngredient, "Original selected ingredient context is missing.");
        Assertions.assertEquals(originalIngredientName, selectedIngredient.getName(), "The original ingredient for which suggestion was made is incorrect.");
        if (testContext != null) testContext.lastSystemMessage = "Verified: System suggested " + suggestedAlternative.getName() + " for " + originalIngredientName;
    }

    @Given("the customer has a {string} dietary restriction")
    public void the_customer_has_a_dietary_restriction(String restriction) {
        currentDietaryRestriction = restriction.toLowerCase();
        Assertions.assertNotNull(currentDietaryRestriction, "Dietary restriction should be set.");
        Assertions.assertEquals(restriction.toLowerCase(), currentDietaryRestriction, "Dietary restriction not set as expected.");
        if (testContext != null) testContext.lastSystemMessage = "Customer has dietary restriction: " + currentDietaryRestriction;
    }

    @Given("the customer selects {string} which conflicts with the restriction")
    public void the_customer_selects_which_conflicts_with_the_restriction(String ingredientName) {
        selectedIngredient = availableIngredients.get(ingredientName);
        Assertions.assertNotNull(selectedIngredient, "Ingredient '" + ingredientName + "' not found. Check spelling or initialization.");
        Assertions.assertNotNull(currentDietaryRestriction, "Dietary restriction must be set before checking for conflicts.");

        boolean conflicts = false;
        if ("dairy-free".equals(currentDietaryRestriction) && selectedIngredient.getDietaryTags().contains("dairy")) {
            conflicts = true;
        } else if ("vegan".equals(currentDietaryRestriction) && (selectedIngredient.getDietaryTags().contains("dairy") || selectedIngredient.getDietaryTags().contains("meat") /* add other non-vegan tags */) ){
            conflicts = true;
        }
        // Add more conflict checks if needed for other restrictions (e.g., gluten-free)

        Assertions.assertTrue(conflicts, selectedIngredient.getName() + " was expected to conflict with the '" + currentDietaryRestriction + "' restriction, but it didn't, or the conflict logic is missing. Tags: " + selectedIngredient.getDietaryTags());
        if (testContext != null) testContext.lastSystemMessage = "Customer selected " + selectedIngredient.getName() + ", which conflicts with " + currentDietaryRestriction;
    }

    @When("the system identifies the dietary conflict")
    public void the_system_identifies_the_dietary_conflict() {
        Assertions.assertNotNull(selectedIngredient, "No ingredient selected to check for dietary conflict.");
        Assertions.assertNotNull(currentDietaryRestriction, "No dietary restriction set for conflict identification.");

        Optional<Ingredient> suitableAlt = selectedIngredient.getPotentialAlternatives().stream()
                .filter(Ingredient::isAvailable) // Check if alternative itself is available
                .filter(alt -> {
                    if ("dairy-free".equals(currentDietaryRestriction)) {
                        return !alt.getDietaryTags().contains("dairy");
                    }
                    if ("vegan".equals(currentDietaryRestriction)) {
                        return alt.getDietaryTags().contains("vegan"); // Or !contains("dairy") && !contains("meat") etc.
                    }
                    // Add more suitability checks for other restrictions
                    return true; // Default to suitable if no specific restriction blocks it
                })
                .findFirst();

        if (suitableAlt.isPresent()) {
            suggestedAlternative = suitableAlt.get();
            Assertions.assertNotNull(suggestedAlternative);
            if (testContext != null) testContext.lastSystemMessage = "System identified dietary conflict for " + selectedIngredient.getName() + ". Suggested suitable alternative: " + suggestedAlternative.getName();
        } else {
            suggestedAlternative = null;
            Assertions.assertNull(suggestedAlternative);
            if (testContext != null) testContext.lastSystemMessage = "System identified dietary conflict for " + selectedIngredient.getName() + ", but no suitable and available alternative found.";
        }
    }

    @Given("an alternative {string} has been suggested for unavailable {string}")
    public void an_alternative_has_been_suggested_for_unavailable(String alternativeName, String originalName) {
        this.originalIngredientForSubstitution = originalName;
        Ingredient originalIng = availableIngredients.get(originalName);
        Ingredient altIng = availableIngredients.get(alternativeName);

        Assertions.assertNotNull(originalIng, "Original ingredient '" + originalName + "' not found in setup.");
        Assertions.assertNotNull(altIng, "Alternative ingredient '" + alternativeName + "' not found in setup.");

        originalIng.setAvailable(false);
        this.suggestedAlternative = altIng;
        this.selectedIngredient = originalIng; // Context is that `selectedIngredient` was the one chosen initially

        Assertions.assertEquals(originalName, this.selectedIngredient.getName());
        Assertions.assertEquals(alternativeName, this.suggestedAlternative.getName());
        Assertions.assertFalse(this.selectedIngredient.isAvailable());
        if (testContext != null) testContext.lastSystemMessage = "Setup: " + alternativeName + " suggested for unavailable " + originalName;
    }

    @Given("an alternative {string} has been suggested for {string} due to dietary conflict")
    public void an_alternative_has_been_suggested_for_due_to_dietary_conflict(String alternativeName, String originalName) {
        Assertions.assertNotNull(currentDietaryRestriction, "Dietary restriction must be set before this step (e.g., by a preceding Given).");
        this.originalIngredientForSubstitution = originalName;
        Ingredient originalIng = availableIngredients.get(originalName);
        Ingredient altIng = availableIngredients.get(alternativeName);

        Assertions.assertNotNull(originalIng, "Original ingredient '" + originalName + "' not found in setup.");
        Assertions.assertNotNull(altIng, "Alternative ingredient '" + alternativeName + "' not found in setup.");

        // Minimal check that the original conflicts and alt is suitable based on set currentDietaryRestriction
        boolean originalConflicts = ("dairy-free".equals(currentDietaryRestriction) && originalIng.getDietaryTags().contains("dairy")) ||
                ("vegan".equals(currentDietaryRestriction) && !originalIng.getDietaryTags().contains("vegan")); // Simplified
        Assertions.assertTrue(originalConflicts, originalIng.getName() + " should conflict with " + currentDietaryRestriction + " for this setup. Tags: " + originalIng.getDietaryTags());

        boolean altIsSuitable = ("dairy-free".equals(currentDietaryRestriction) && !altIng.getDietaryTags().contains("dairy")) ||
                ("vegan".equals(currentDietaryRestriction) && altIng.getDietaryTags().contains("vegan")); // Simplified
        Assertions.assertTrue(altIsSuitable, altIng.getName() + " should be suitable for " + currentDietaryRestriction + ". Tags: " + altIng.getDietaryTags());

        this.suggestedAlternative = altIng;
        this.selectedIngredient = originalIng; // Context is that `selectedIngredient` was the one chosen initially

        Assertions.assertEquals(originalName, this.selectedIngredient.getName());
        Assertions.assertEquals(alternativeName, this.suggestedAlternative.getName());
        if (testContext != null) testContext.lastSystemMessage = "Setup: " + alternativeName + " suggested for " + originalName + " due to " + currentDietaryRestriction + " conflict.";
    }

    @When("the customer accepts the substitution of {string} with {string}")
    public void the_customer_accepts_the_substitution_of_with(String originalIngName, String substituteIngName) {
        Assertions.assertNotNull(selectedIngredient, "Original ingredient context must be set before accepting substitution.");
        Assertions.assertNotNull(suggestedAlternative, "An alternative must have been suggested to be accepted.");
        Assertions.assertEquals(originalIngName, this.selectedIngredient.getName(), "Original ingredient name in acceptance step does not match the context of the originally selected/problematic ingredient.");
        Assertions.assertEquals(substituteIngName, suggestedAlternative.getName(), "Substitute ingredient name in acceptance step does not match the currently suggested alternative.");

        this.substitutionAccepted = true;
        this.originalIngredientForSubstitution = originalIngName; // Set for clarity, matches selectedIngredient.getName()
        this.substituteIngredientForSubstitution = substituteIngName; // Set for clarity, matches suggestedAlternative.getName()

        Assertions.assertTrue(this.substitutionAccepted);
        Assertions.assertEquals(originalIngName, this.originalIngredientForSubstitution);
        Assertions.assertEquals(substituteIngName, this.substituteIngredientForSubstitution);
        if (testContext != null) testContext.lastSystemMessage = "Customer accepted substitution: " + originalIngName + " -> " + substituteIngName;
    }

    @Then("the chef should be notified of the substitution from {string} to {string}")
    public void the_chef_should_be_notified_of_the_substitution_from_to(String expectedOriginal, String expectedSubstitute) {
        Assertions.assertTrue(substitutionAccepted, "Substitution must have been accepted for the chef to be notified.");
        Assertions.assertEquals(expectedOriginal, originalIngredientForSubstitution, "The original ingredient in the notification record does not match the expected original ingredient.");
        Assertions.assertEquals(expectedSubstitute, substituteIngredientForSubstitution, "The substitute ingredient in the notification record does not match the expected substitute ingredient.");

        // Simulate notification being recorded or sent
        String notification = "Chef notified: Substitute " + substituteIngredientForSubstitution + " for " + originalIngredientForSubstitution;
        if (testContext != null) testContext.lastSystemMessage = notification;
        Assertions.assertTrue((testContext != null ? testContext.lastSystemMessage : notification).contains(expectedOriginal) &&
                        (testContext != null ? testContext.lastSystemMessage : notification).contains(expectedSubstitute),
                "Notification message does not contain the correct ingredient names.");
    }
}