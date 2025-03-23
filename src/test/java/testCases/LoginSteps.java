package testCases;

import cook.Application;
import cook.Role;
import cook.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static cook.Application.users;
import static org.junit.Assert.*;

public class LoginSteps {
    Application app = new Application();
    String page;
    User user = new User("testUser","testUser","password123", Role.manager);

    public LoginSteps(){

    }
    @Given("the user is on the login page")
    public void the_user_is_on_the_login_page() {
        page="loginPage";
    }

    @When("the user enters valid username {string} and password {string}")
    public void the_user_enters_valid_username_and_password(String string, String string2) {
        users.add(user);
        user=app.login(string,string2);
        assertNotNull(user);
        if(user != null)
            assertTrue(user.getEmail().equals(string));
    }

    @Then("the user should be redirected to the dashboard")
    public void the_user_should_be_redirected_to_the_dashboard() {
        page = "dashboard";
    }

    @Then("the system should display a welcome message")
    public void the_system_should_display_a_welcome_message() {
        System.out.println("Welcome to dashboard page");
    }

    @When("the user enters invalid username {string} and password {string}")
    public void the_user_enters_invalid_username_and_password(String string, String string2) {
        user=app.login(string,string2);
        assertNull(user);
        if(user != null)
        assertFalse(user.getEmail().equals(string));
    }

    @Then("the system should display an error message {string}")
    public void the_system_should_display_an_error_message(String string) {
        System.out.println(string);
    }

    @When("the user enters valid username {string} and invalid password {string}")
    public void the_user_enters_valid_username_and_invalid_password(String string, String string2) {
        user = app.login(string,string2);
        assertNull(user);
        if(user != null)
            assertFalse(user.getEmail().equals(string));
    }


    @When("the user enters empty username and password")
    public void the_user_enters_empty_username_and_password() {
        user = app.login("","");
        assertNull(user);

    }



}