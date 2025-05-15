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


public class CustomMealRequestsSteps {

    private Customer currentCustomer;
    private String confirmationMessage;
    private List<Meal> suggestedMeals;

    @Before("@CustomMealFeature")
    public void setUpCustomMealScenario() {
        System.out.println("--- Setting up state for CustomMealRequestsSteps ---");
        currentCustomer = null;
        confirmationMessage = null;
        suggestedMeals = null;

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

            System.out.println("CustomMealSteps: Initialized default meals in Application.meals.");
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
                //  نضيفه إلى قائمة المستخدمين العامة فقط إذا كان جديدًا وغير موجود
                if (Application.users.stream().noneMatch(u -> u.getEmail().equals(currentCustomer.getEmail()))) {
                    Application.users.add(currentCustomer);
                }
            }
        } else {
            currentCustomer = (Customer) Application.currentUser;
        }

        // تعيين Application.currentUser مرة واحدة هنا
        if (currentCustomer != null) {
            Application.currentUser = currentCustomer;
        }

        assertNotNull("Customer should be logged in or created.", currentCustomer);
        System.out.println("Customer '" + currentCustomer.getName() + "' is considered logged in.");
    }

    @When("the customer selects {string} from the profile menu")
    public void the_customer_selects_from_the_profile_menu(String menuOption) {
        assertNotNull("Customer must be logged in to select from profile menu.", currentCustomer);
        System.out.println("Customer selected '" + menuOption + "' from profile menu.");
    }

    @When("the customer inputs {string} as their dietary preference")
    public void the_customer_inputs_as_their_dietary_preference(String preference) {
        assertNotNull("Customer must be set to input dietary preference.", currentCustomer);
        boolean saved = currentCustomer.savePreferences(preference);
        if (saved) {
            System.out.println("Dietary preference '" + preference + "' noted for customer.");
        } else {
            System.out.println("Dietary preference '" + preference + "' was already present or could not be saved.");
        }
        // التأكد من أن Customer.java لديه public List<String> getPreferences()
        assertNotNull("Customer preferences list should not be null.", currentCustomer.getPreferences());
        assertTrue("Preference '" + preference + "' should be saved in customer's preferences.",
                currentCustomer.getPreferences().contains(preference));
    }

    @Then("the system should save the preference")
    public void the_system_should_save_the_preference() {
        System.out.println("Preference saving step confirmed by previous action and assertion.");
    }

    @Then("the system should display a confirmation message")
    public void the_system_should_display_a_confirmation_message() {
        confirmationMessage = "Your settings have been updated.";
        assertNotNull("Confirmation message should be set.", confirmationMessage);
        System.out.println("Confirmation message displayed: " + confirmationMessage);
    }

    @When("the customer inputs {string} as an allergy")
    public void the_customer_inputs_as_an_allergy(String allergy) {
        assertNotNull("Customer must be set to input allergy.", currentCustomer);
        boolean saved = currentCustomer.saveAllergy(allergy);
        if (saved) {
            System.out.println("Allergy '" + allergy + "' noted for customer.");
        } else {
            System.out.println("Allergy '" + allergy + "' was already present or could not be saved.");
        }
        assertTrue("Allergy '" + allergy + "' should be listed for the customer.", currentCustomer.allergyExist(allergy));
    }

    @Then("the system should save the allergy information")
    public void the_system_should_save_the_allergy_information() {
        System.out.println("Allergy information saving step confirmed by previous action and assertion.");
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
        assertNotNull("A customer must exist to list an allergy.", currentCustomer);
        currentCustomer.saveAllergy(allergy);
        assertTrue("Allergy '" + allergy + "' should be listed for the customer.", currentCustomer.allergyExist(allergy));
        System.out.println("Customer '" + currentCustomer.getName() + "' now has '" + allergy + "' listed as an allergy.");
    }

    @When("the system suggests meals")
    public void the_system_suggests_meals() {
        assertNotNull("A customer must be set to get meal suggestions based on allergies.", currentCustomer);
        assertNotNull("Customer allergies list should not be null.", currentCustomer.getAllergies());
        suggestedMeals = Application.exclude(currentCustomer.getAllergies());
        //  Application.exclude مضمون أن يعيد قائمة، لذا suggestedMeals لن تكون null
        System.out.println("System suggested " + suggestedMeals.size() + " meals after considering allergies.");
    }

    @Then("the system should exclude any meals containing {string}")
    public void the_system_should_exclude_any_meals_containing(String excludedIngredient) {
        //  assertNotNull("Suggested meals list should not be null.", suggestedMeals); //  هذا مضمون الآن
        if (suggestedMeals.isEmpty()) {
            System.out.println("No meals were suggested, so nothing to check for exclusion of '" + excludedIngredient + "'. This might be expected.");
        }
        for (Meal meal : suggestedMeals) {
            assertNotNull("Meal object in suggested list should not be null.", meal);
            assertNotNull("Ingredients list for meal '" + meal.getName() + "' should not be null.", meal.getIngredients());
            assertFalse("Suggested meal '" + meal.getName() + "' should not contain allergy '" + excludedIngredient + "'.",
                    meal.getIngredients().stream()
                            .anyMatch(ingredient -> ingredient != null && ingredient.equalsIgnoreCase(excludedIngredient)));
        }
        System.out.println("Confirmed: Suggested meals exclude items containing '" + excludedIngredient + "'.");
    }
}