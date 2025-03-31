package testCases;
import cook.Application;
import cook.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.*;

public class SubstitutionApprovalSteps {

    private boolean chefLoggedIn = false;
    private boolean substitutionNotificationDisplayed = false;
    private boolean substitutionApproved = false;
    private boolean substitutionRejected = false;
    private String pendingSubstitution = null;
    private String mealUpdatedMessage = "";
    private String customerNotification = "";
    private Queue<String> substitutionQueue = new LinkedList<>();
    String email="shahd@gmail.com";
    String password="Chef_Shahd";





    @When("the chef logs in")
    public void the_chef_logs_in() {

        User chef = Application.login(email, password);
        if (chef != null && chef.getRole().equals("Chef")) {
            chefLoggedIn = true;
            System.out.println("Chef logged in successfully.");
        } else {
            System.out.println("Chef logged in failed");
        }

    }

    @Then("the system should display the substitution notification")
    public void the_system_should_display_the_substitution_notification() {
        if (chefLoggedIn && !substitutionQueue.isEmpty()) {
            pendingSubstitution = substitutionQueue.poll();
            substitutionNotificationDisplayed = true;
            System.out.println("Substitution request: " + pendingSubstitution);
        } else {
            System.out.println("No substitution requests available.");
        }


    }

    @Given("the chef is viewing a substitution notification")
    public void the_chef_is_viewing_a_substitution_notification() {
        if (substitutionNotificationDisplayed) {
            System.out.println("Chef is reviewing: " + pendingSubstitution);
        } else {
            System.out.println("No active substitution");
        }
    }

    @When("the chef approves the substitution")
    public void the_chef_approves_the_substitution() {
        if (pendingSubstitution != null) {
            substitutionApproved = true;
        }
    }

    @Then("the system should update the meal recipe")
    public void the_system_should_update_the_meal_recipe() {
        if (substitutionApproved) {
            mealUpdatedMessage = "Meal updated with: " + pendingSubstitution;
            System.out.println(mealUpdatedMessage);
        }
    }

    @When("the chef rejects the substitution")
    public void the_chef_rejects_the_substitution() {
        if (pendingSubstitution != null) {
            substitutionRejected = true;
        }
    }

    @Then("the system should notify the customer")
    public void the_system_should_notify_the_customer() {
        if (substitutionRejected) {
            customerNotification = "Substitution rejected. Please select another option.";
            System.out.println(customerNotification);
        }
    }





}
