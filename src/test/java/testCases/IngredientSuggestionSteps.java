package testCases;



import cook.Ingredient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class IngredientSuggestionSteps {

    private List<Ingredient> selectedIngredients = new ArrayList<>();

    private Map<Ingredient, List<Ingredient>> alternativeIngredientsMap = new HashMap<>();

    private boolean alternativeSuggested = false;
    private boolean chefNotified = false;
    private boolean dietaryRestrictionApplied = false;


    private Ingredient avocado = new Ingredient("Avocado");
    private Ingredient guacamole = new Ingredient("Guacamole");
    private Ingredient milk = new Ingredient("Milk");
    private Ingredient soyMilk = new Ingredient("Soy Milk");
    private Ingredient cheese = new Ingredient("Cheese"); // Example for incompatibility

    public IngredientSuggestionSteps() {

        avocado.addPotentialAlternative(guacamole);
        milk.addPotentialAlternative(soyMilk);

        // Setup dietary tags
        milk.addDietaryTag("dairy");
        cheese.addDietaryTag("dairy");
        soyMilk.addDietaryTag("vegan");
        soyMilk.addDietaryTag("dairy-free");
        guacamole.addDietaryTag("vegan");
        guacamole.addDietaryTag("gluten-free");
    }


    @Given("the customer has selected an unavailable ingredient")
    public void the_customer_has_selected_an_unavailable_ingredient() {
        selectedIngredients.clear();
        alternativeIngredientsMap.clear();


        avocado.setAvailable(false);
        selectedIngredients.add(avocado);


        if (!avocado.isAvailable() && !avocado.getPotentialAlternatives().isEmpty()) {
            alternativeIngredientsMap.put(avocado, new ArrayList<>(avocado.getPotentialAlternatives()));
        }
        System.out.println("Scenario: Customer selected unavailable " + avocado.getName());
    }

    @Given("the customer has a dietary restriction")
    public void the_customer_has_a_dietary_restriction() {
        dietaryRestrictionApplied = true;

        System.out.println("Scenario: Customer has a dietary restriction (e.g., vegan).");
    }

    @When("the customer selects an incompatible ingredient")
    public void the_customer_selects_an_incompatible_ingredient() {
        selectedIngredients.clear();

        selectedIngredients.add(milk);
        System.out.println("Scenario: Customer selects " + milk.getName() + " (potentially incompatible).");
    }

    @Given("the system has suggested an alternative ingredient")
    public void the_system_has_suggested_an_alternative_ingredient() {

        if (!selectedIngredients.isEmpty()) {
            Ingredient currentSelection = selectedIngredients.get(0);
            if (!currentSelection.isAvailable() && alternativeIngredientsMap.containsKey(currentSelection) && !alternativeIngredientsMap.get(currentSelection).isEmpty()) {
                alternativeSuggested = true;
                System.out.println("Scenario: System has suggested alternatives for " + currentSelection.getName());
            } else {
                System.out.println("Scenario: System would suggest, but no unavailable item or no alternatives mapped.");

            }
        } else {
            System.out.println("Scenario: System would suggest, but no ingredient selected to base suggestion on.");
        }
    }

    @When("the substitution is applied")
    public void the_substitution_is_applied() {
        if (alternativeSuggested && !selectedIngredients.isEmpty()) {
            Ingredient unavailableIngredient = selectedIngredients.get(0);


            if (alternativeIngredientsMap.containsKey(unavailableIngredient) && !alternativeIngredientsMap.get(unavailableIngredient).isEmpty()) {
                Ingredient substitute = alternativeIngredientsMap.get(unavailableIngredient).get(0);


                boolean substituteIsCompatible = true;
                if (dietaryRestrictionApplied) {

                    if (substitute.getDietaryTags().contains("dairy")) {
                        System.out.println("Warning: Substitute " + substitute.getName() + " might be incompatible with dietary restriction.");
                        substituteIsCompatible = false;
                    }
                }

                if (substituteIsCompatible) {
                    selectedIngredients.clear();
                    selectedIngredients.add(substitute);
                    chefNotified = true;
                    System.out.println("Substitution applied: " + unavailableIngredient.getName() + " -> " + substitute.getName());
                } else {
                    System.out.println("Substitution NOT applied for " + unavailableIngredient.getName() + " due to dietary incompatibility of substitute " + substitute.getName());

                }
            } else {
                System.out.println("Substitution not applied: No alternatives mapped for " + unavailableIngredient.getName());
            }
        } else {
            System.out.println("Substitution not applied: Conditions not met (no alternative suggested or no ingredient selected).");
        }
    }

    @Then("the system should notify the chef")
    public void the_system_should_notify_the_chef() {

        if (chefNotified && !selectedIngredients.isEmpty()) {
            System.out.println("Chef notified of substitution: " + selectedIngredients.get(0).getName());

        } else if (chefNotified) {
            System.out.println("Chef notified, but selection list is empty (unexpected state).");
        }

    }
}


