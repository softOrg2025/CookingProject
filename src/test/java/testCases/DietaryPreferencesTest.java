package testCases;

import cook.Application;
import cook.Customer;
import cook.Meal; // استيراد الكلاس Meal
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.List;
import static cook.Application.users;
import static org.junit.Assert.*;

public class DietaryPreferencesTest {
    private String dietaryPreference; // إما استخدمه أو احذفه
    private String errorMessage;     // إما استخدمه أو احذفه
    private String option;
    private String confirmationMessage;
    String allergy = "Peanuts";
    Customer c = new Customer("aya", "aya@", "123");

    public DietaryPreferencesTest() {
        users.add(c);
        c.saveAllergy(allergy);
    }

    @Given("the customer is logged into the system")
    public void the_customer_is_logged_into_the_system() {
        Application.login(c.getEmail(), c.getPassword());
        System.out.println("Customer is logged in.");
    }

    @When("the customer selects {string} from the profile menu")
    public void the_customer_selects_from_the_profile_menu(String string) {
        // يتم استخدام المعلمة string هنا إذا لزم الأمر
        this.option = string; // مثال على استخدام المعلمة
    }

    @When("the customer inputs {string} as their dietary preference")
    public void the_customer_inputs_as_their_dietary_preference(String menuOption) {
        option = menuOption;
    }

    @Then("the system should save the preference")
    public void the_system_should_save_the_preference() {
        this.confirmationMessage = "Preference saved successfully";
        assertTrue(c.savePreferences(option));
    }

    @Then("the system should display a confirmation message")
    public void the_system_should_display_a_confirmation_message() {
        System.out.println(this.confirmationMessage); // عرض رسالة التأكيد
    }

    @When("the customer inputs {string} as an allergy")
    public void the_customer_inputs_as_an_allergy(String string) {
        this.allergy = string;
    }

    @Then("the system should save the allergy information")
    public void the_system_should_save_the_allergy_information() {
        assertTrue(c.allergyExist(allergy));
    }

    @Given("the customer has {string} listed as an allergy")
    public void the_customer_has_listed_as_an_allergy(String string) {
        assertTrue(c.allergyExist(string));
    }

    List<Meal> newMeals = new ArrayList<>();

    @When("the system suggests meals")
    public void the_system_suggests_meals() {
        newMeals = Application.exclude(c.getAllergies());
        assertTrue(newMeals.isEmpty());
    }

    @Then("the system should exclude any meals containing peanut")
    public void the_system_should_exclude_any_meals_containing_peanut() {
        System.out.println(newMeals);
    }
}