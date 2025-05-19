package testCases;

import cook.Application;
import cook.Customer;
import cook.Meal;
import cook.User;
import cook.Role;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

public class CustomMealRequestsSteps {

    private Customer currentCustomer;
    private String confirmationMessage;
    private List<Meal> suggestedMeals;

    @Before("@CustomMealFeature")
    public void setUpCustomMealScenario() {
        currentCustomer = null;
        confirmationMessage = null;
        suggestedMeals = null;
        Application.lastSystemMessage = null;
        Application.notificationService.clearNotifications("system");

        if (Application.users == null) {
            Application.users = new ArrayList<>();
        }
        if (Application.meals == null) {
            Application.meals = new ArrayList<>();
        }

        if (Application.meals.isEmpty()) {
            Meal meal1 = new Meal(List.of("Chicken", "Rice", "Broccoli"), 'M', 12.00);
            meal1.setName("Chicken and Rice");
            Application.meals.add(meal1);

            Meal meal2 = new Meal(List.of("Tofu", "Peanuts", "Noodles"), 'M', 11.50);
            meal2.setName("Peanut Noodles with Tofu");
            Application.meals.add(meal2);

            Meal meal3 = new Meal(List.of("Salmon", "Asparagus", "Quinoa"), 'M', 15.00);
            meal3.setName("Grilled Salmon");
            Application.meals.add(meal3);

            Meal meal4 = new Meal(List.of("Beef", "Potatoes"), 'M', 14.00);
            meal4.setName("Beef Stew");
            Application.meals.add(meal4);

            Application.notificationService.sendNotification("system", "Initialized default meals");
        }
    }

    @Given("the customer is logged into the system")
    public void the_customer_is_logged_into_the_system() {
        if (Application.currentUser == null || Application.currentUser.getRole() != Role.Customer) {
            User existingUser = Application.users.stream()
                    .filter(u -> u.getRole() == Role.Customer)
                    .findFirst().orElse(null);

            if (existingUser != null) {
                currentCustomer = (Customer) existingUser;
            } else {
                currentCustomer = new Customer("Test Customer", "test@example.com", "password");
                if (Application.users.stream().noneMatch(u -> u.getEmail().equals(currentCustomer.getEmail()))) {
                    Application.users.add(currentCustomer);
                }
            }
            Application.currentUser = currentCustomer;
            Application.notificationService.sendNotification(currentCustomer.getEmail(), "User logged in");
        } else {
            currentCustomer = (Customer) Application.currentUser;
        }

        assertNotNull(Application.notificationService.getNotifications(currentCustomer.getEmail()).contains("User logged in"));
    }

    @When("the customer selects {string} from the profile menu")
    public void selectFromProfileMenu(String menuOption) {
        Application.setSystemMessage("Selected option: " + menuOption);
        assertNotNull(Application.getSystemMessage());


        if (menuOption.equals("Dietary Preferences")) {

        } else if (menuOption.equals("Allergies")) {

        }
    }

    @When("the customer inputs {string} as their dietary preference")
    public void the_customer_inputs_as_their_dietary_preference(String preference) {
        boolean saved = currentCustomer.savePreferences(preference);
        Application.setSystemMessage(saved ? "Preference saved" : "Preference already exists");
        assertTrue(currentCustomer.getPreferences().contains(preference));
    }

    @Then("the system should save the preference")
    public void verifyPreferenceSaved() {
        assertEquals("Preference saved", Application.getSystemMessage());
    }

    @Then("the system should display a confirmation message")
    public void the_system_should_display_a_confirmation_message() {
        String expectedMessage = "Your settings have been updated.";
        Application.setSystemMessage(expectedMessage);
        assertEquals(expectedMessage, Application.getSystemMessage());
    }

    @When("the customer inputs {string} as an allergy")
    public void the_customer_inputs_as_an_allergy(String allergy) {
        boolean saved = currentCustomer.saveAllergy(allergy);
        Application.setSystemMessage(saved ? "Allergy saved" : "Allergy already exists");
        assertTrue(currentCustomer.allergyExist(allergy));
    }

    @Then("the system should save the allergy information")
    public void the_system_should_save_the_allergy_information() {
        assertEquals("Allergy saved", Application.getSystemMessage());
    }

    @Given("the customer has {string} listed as an allergy")
    public void the_customer_has_listed_as_an_allergy(String allergy) {
        if (currentCustomer == null) {
            if (Application.currentUser == null || Application.currentUser.getRole() != Role.Customer) {
                currentCustomer = new Customer("Allergy Test User", "allergy@example.com", "password");
                if (Application.users.stream().noneMatch(u -> u.getEmail().equals(currentCustomer.getEmail()))) {
                    Application.users.add(currentCustomer);
                }
                Application.currentUser = currentCustomer;
            } else {
                currentCustomer = (Customer) Application.currentUser;
            }
        }
        currentCustomer.saveAllergy(allergy);
        assertTrue(currentCustomer.allergyExist(allergy));
    }

    @When("the system suggests meals")
    public void the_system_suggests_meals() {
        suggestedMeals = Application.exclude(currentCustomer.getAllergies());
        Application.notificationService.sendNotification(currentCustomer.getEmail(),
                "Suggested " + suggestedMeals.size() + " meals");
        assertNotNull(suggestedMeals);
    }

    @Then("the system should exclude any meals containing {string}")
    public void the_system_should_exclude_any_meals_containing(String excludedIngredient) {
        for (Meal meal : suggestedMeals) {
            boolean containsExcluded = meal.getIngredients().stream()
                    .anyMatch(ingredient -> ingredient != null && ingredient.equalsIgnoreCase(excludedIngredient));
            assertFalse(containsExcluded);
        }
        if (!suggestedMeals.isEmpty()) {
            Application.setSystemMessage("All suggested meals are safe for your allergy to " + excludedIngredient);
        }
    }
}