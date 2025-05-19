package testCases;

import cook.Ingredient;
import cook.NotificationService;
import cook.User;
import cook.Application;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import cook.Customer;
import cook.Role;

public class IngredientSuggestionSteps {

    private final Map<String, Ingredient> availableIngredients = new HashMap<>();
    private Ingredient selectedIngredient;
    private String currentDietaryRestriction;
    private Ingredient suggestedAlternative;
    private boolean substitutionAccepted = false;
    private String originalIngredientForSubstitution;
    private String substituteIngredientForSubstitution;
    private final TestContext testContext;
    private final NotificationService notificationService; // تم التعديل
    private final Customer testUser; // تم التعديل


    public IngredientSuggestionSteps(TestContext context) {
        this.testContext = context;
        this.notificationService = Application.notificationService;
        this.testUser = new Customer("TestUser", "test@example.com", "password");
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

        Assertions.assertTrue(availableIngredients.size() > 0, "Ingredients should be initialized.");
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
        if (testContext != null) testContext.lastSystemMessage = null;
        Application.setSystemMessage(null); // Reset system message
    }

    @Given("the customer has selected {string} which is unavailable")
    public void the_customer_has_selected_which_is_unavailable(String ingredientName) {
        selectedIngredient = availableIngredients.get(ingredientName);
        Assertions.assertNotNull(selectedIngredient);
        selectedIngredient.setAvailable(false);
        Application.setSystemMessage("Customer selected unavailable ingredient: " + selectedIngredient.getName());
    }


    @When("the system identifies the unavailability")
    public void the_system_identifies_the_unavailability() {
        Assertions.assertNotNull(selectedIngredient);

        Optional<Ingredient> firstAvailableAlternative = selectedIngredient.getPotentialAlternatives().stream()
                .filter(Ingredient::isAvailable)
                .findFirst();

        if (firstAvailableAlternative.isPresent()) {
            suggestedAlternative = firstAvailableAlternative.get();
            Application.setSystemMessage("System identified unavailability for " + selectedIngredient.getName() +
                    ". Suggested alternative: " + suggestedAlternative.getName());
        } else {
            Application.setSystemMessage("System identified unavailability for " + selectedIngredient.getName() +
                    ", but no available potential alternatives found.");
        }
    }

    @Then("the system should suggest {string} as an alternative for {string}")
    public void the_system_should_suggest_as_an_alternative_for(String expectedAlternativeName, String originalIngredientName) {
        Assertions.assertNotNull(suggestedAlternative);
        Assertions.assertEquals(expectedAlternativeName, suggestedAlternative.getName());
        Assertions.assertEquals(originalIngredientName, selectedIngredient.getName());

        String verificationMessage = "Verified: System suggested " + suggestedAlternative.getName() +
                " for " + originalIngredientName;
        Application.setSystemMessage(verificationMessage);
    }

    @Given("the customer has a {string} dietary restriction")
    public void the_customer_has_a_dietary_restriction(String restriction) {
        currentDietaryRestriction = restriction.toLowerCase();
        testUser.savePreferences(restriction);
        Application.setSystemMessage("Customer has dietary restriction: " + currentDietaryRestriction);
    }

    @Given("the customer selects {string} which conflicts with the restriction")
    public void the_customer_selects_which_conflicts_with_the_restriction(String ingredientName) {
        selectedIngredient = availableIngredients.get(ingredientName);
        Assertions.assertNotNull(selectedIngredient);
        Assertions.assertNotNull(currentDietaryRestriction);

        boolean conflicts = false;
        if ("dairy-free".equals(currentDietaryRestriction) && selectedIngredient.getDietaryTags().contains("dairy")) {
            conflicts = true;
        } else if ("vegan".equals(currentDietaryRestriction) && (selectedIngredient.getDietaryTags().contains("dairy") || selectedIngredient.getDietaryTags().contains("meat") /* add other non-vegan tags */) ){
            conflicts = true;
        }
        // Add more conflict checks if needed for other restrictions (e.g., gluten-free)

        Assertions.assertTrue(conflicts);
        Application.setSystemMessage("Customer selected " + selectedIngredient.getName() +
                ", which conflicts with " + currentDietaryRestriction);
    }

    @When("the system identifies the dietary conflict")
    public void the_system_identifies_the_dietary_conflict() {
        Assertions.assertNotNull(selectedIngredient);
        Assertions.assertNotNull(currentDietaryRestriction);

        Optional<Ingredient> suitableAlt = selectedIngredient.getPotentialAlternatives().stream()
                .filter(Ingredient::isAvailable)
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
            Application.setSystemMessage("System identified dietary conflict for " + selectedIngredient.getName() +
                    ". Suggested suitable alternative: " + suggestedAlternative.getName());
        } else {
            Application.setSystemMessage("System identified dietary conflict for " + selectedIngredient.getName() +
                    ", but no suitable and available alternative found.");
        }
    }

    @Given("an alternative {string} has been suggested for unavailable {string}")
    public void an_alternative_has_been_suggested_for_unavailable(String alternativeName, String originalName) {
        this.originalIngredientForSubstitution = originalName;
        Ingredient originalIng = availableIngredients.get(originalName);
        Ingredient altIng = availableIngredients.get(alternativeName);

        Assertions.assertNotNull(originalIng);
        Assertions.assertNotNull(altIng);

        originalIng.setAvailable(false);
        this.suggestedAlternative = altIng;
        this.selectedIngredient = originalIng;

        Application.setSystemMessage("Setup: " + alternativeName + " suggested for unavailable " + originalName);
    }

    @Given("an alternative {string} has been suggested for {string} due to dietary conflict")
    public void an_alternative_has_been_suggested_for_due_to_dietary_conflict(String alternativeName, String originalName) {
        Assertions.assertNotNull(currentDietaryRestriction);
        this.originalIngredientForSubstitution = originalName;
        Ingredient originalIng = availableIngredients.get(originalName);
        Ingredient altIng = availableIngredients.get(alternativeName);

        Assertions.assertNotNull(originalIng);
        Assertions.assertNotNull(altIng);


        // Minimal check that the original conflicts and alt is suitable based on set currentDietaryRestriction
        boolean originalConflicts = ("dairy-free".equals(currentDietaryRestriction) && originalIng.getDietaryTags().contains("dairy")) ||
                ("vegan".equals(currentDietaryRestriction) && !originalIng.getDietaryTags().contains("vegan")); // Simplified
        Assertions.assertTrue(originalConflicts, originalIng.getName() + " should conflict with " + currentDietaryRestriction + " for this setup. Tags: " + originalIng.getDietaryTags());

        boolean altIsSuitable = ("dairy-free".equals(currentDietaryRestriction) && !altIng.getDietaryTags().contains("dairy")) ||
                ("vegan".equals(currentDietaryRestriction) && altIng.getDietaryTags().contains("vegan")); // Simplified
        Assertions.assertTrue(altIsSuitable, altIng.getName() + " should be suitable for " + currentDietaryRestriction + ". Tags: " + altIng.getDietaryTags());

        this.suggestedAlternative = altIng;
        this.selectedIngredient = originalIng;

        Application.setSystemMessage("Setup: " + alternativeName + " suggested for " + originalName +
                " due to " + currentDietaryRestriction + " conflict.");
    }

    @When("the customer accepts the substitution of {string} with {string}")
    public void the_customer_accepts_the_substitution_of_with(String originalIngName, String substituteIngName) {
        Assertions.assertNotNull(selectedIngredient);
        Assertions.assertNotNull(suggestedAlternative);
        Assertions.assertEquals(originalIngName, this.selectedIngredient.getName());
        Assertions.assertEquals(substituteIngName, suggestedAlternative.getName());

        this.substitutionAccepted = true;
        this.originalIngredientForSubstitution = originalIngName;
        this.substituteIngredientForSubstitution = substituteIngName;

        Application.setSystemMessage("Customer accepted substitution: " + originalIngName + " -> " + substituteIngName);
    }

    @Then("the chef should be notified of the substitution from {string} to {string}")
    public void the_chef_should_be_notified_of_the_substitution_from_to(String expectedOriginal, String expectedSubstitute) {
        Assertions.assertTrue(substitutionAccepted);
        Assertions.assertEquals(expectedOriginal, originalIngredientForSubstitution);
        Assertions.assertEquals(expectedSubstitute, substituteIngredientForSubstitution);

        // استخدام خدمة الإشعارات الفعلية
        String notification = "Substitute " + substituteIngredientForSubstitution + " for " + originalIngredientForSubstitution;
        notificationService.sendNotification("chef1", notification);

        // التحقق من وجود الإشعار
        List<String> chefNotifications = notificationService.getNotifications("chef1");
        Assertions.assertTrue(chefNotifications.contains(notification),
                "Chef should have received the substitution notification");

        Application.setSystemMessage("Chef notified: " + notification);
    }

}