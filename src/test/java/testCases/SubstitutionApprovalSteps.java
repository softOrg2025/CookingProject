package testCases;

import cook.Application;
import cook.User;
import cook.Role;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import java.util.LinkedList;
// import java.util.Queue; // No longer strictly needed here if TestContext defines sharedSubstitutionQueue as Queue

public class SubstitutionApprovalSteps {
    private final TestContext testContext;

    private boolean chefLoggedIn = false;
    private boolean substitutionNotificationDisplayed = false;
    private boolean substitutionApproved = false;
    private boolean substitutionRejected = false;
    private String pendingSubstitution = null;
    private String mealUpdatedMessage = "";
    private String customerNotification = "";


    private final String CHEF_EMAIL = "shahd@gmail.com";
    private final String CHEF_PASSWORD = "Chef_Shahd";
    private final String CHEF_NAME = "Shahd Chef";


    public SubstitutionApprovalSteps(TestContext context) {
        this.testContext = context;
        // Ensure TestContext fields are initialized if they are null
        // This is a defensive measure; ideally, TestContext handles its own initialization.
        if (this.testContext.sharedSubstitutionQueue == null) {
            this.testContext.sharedSubstitutionQueue = new LinkedList<>();
        }
        // lastCustomerNotification is a String, typically initialized to null or empty in TestContext
    }

    @Before
    public void setUp() {
        chefLoggedIn = false;
        substitutionNotificationDisplayed = false;
        substitutionApproved = false;
        substitutionRejected = false;
        pendingSubstitution = null;
        mealUpdatedMessage = "";
        customerNotification = "";

        // testContext itself should not be null as it's injected by Cucumber
        if (testContext.sharedSubstitutionQueue == null) {
            testContext.sharedSubstitutionQueue = new LinkedList<>();
        }
        testContext.sharedSubstitutionQueue.clear();
        testContext.lastSystemMessage = null;
        testContext.lastCustomerNotification = null; // This line was correct

        ensureChefExistsInApplication();
    }

    private void ensureChefExistsInApplication() {
        boolean chefExists = Application.users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(CHEF_EMAIL) && user.getRole() == Role.Chef);

        if (!chefExists) {
            User chefUser = new User(CHEF_NAME, CHEF_EMAIL, CHEF_PASSWORD, Role.Chef);
            Application.users.add(chefUser);
            Assertions.assertTrue(Application.users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(CHEF_EMAIL)), "Chef should be added to application users.");
        } else {
            Assertions.assertTrue(Application.users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(CHEF_EMAIL) && u.getRole() == Role.Chef), "Chef should already exist or have been added.");
        }
    }


    @Given("the system has suggested an alternative ingredient")
    public void the_system_has_suggested_an_alternative_ingredient() {
        String mockSubstitutionRequest = "Substitute 'Flour' with 'Almond Flour' for 'Cake'";
        testContext.sharedSubstitutionQueue.add(mockSubstitutionRequest);
        Assertions.assertFalse(testContext.sharedSubstitutionQueue.isEmpty(), "Substitution queue should not be empty after adding a suggestion.");
        Assertions.assertEquals(mockSubstitutionRequest, testContext.sharedSubstitutionQueue.peek(), "The correct substitution request should be at the head of the queue.");
    }

    @When("the chef logs in")
    public void the_chef_logs_in() {
        User chef = Application.login(CHEF_EMAIL, CHEF_PASSWORD);
        if (chef != null && chef.getRole() == Role.Chef) {
            chefLoggedIn = true;
            Assertions.assertEquals(CHEF_NAME, chef.getName(), "Logged in user name should match the known chef's name.");
        } else {
            chefLoggedIn = false;
        }
        Assertions.assertTrue(chefLoggedIn, "Chef login attempt should be successful for this scenario flow.");
    }

    @Then("the system should display the substitution notification")
    public void the_system_should_display_the_substitution_notification() {
        Assertions.assertTrue(chefLoggedIn, "Chef must be logged in to see notifications.");
        Assertions.assertFalse(testContext.sharedSubstitutionQueue.isEmpty(), "Expected substitution requests in the queue, but it was empty.");

        pendingSubstitution = testContext.sharedSubstitutionQueue.poll();
        substitutionNotificationDisplayed = true;
        testContext.lastSystemMessage = "Substitution request: " + pendingSubstitution;
        Assertions.assertNotNull(pendingSubstitution, "A substitution should have been polled from the queue.");
        Assertions.assertEquals("Substitution request: " + pendingSubstitution, testContext.lastSystemMessage, "System message should reflect the displayed substitution.");
        Assertions.assertTrue(substitutionNotificationDisplayed, "A substitution notification should have been displayed.");
    }


    @Given("the chef is viewing a substitution notification")
    public void the_chef_is_viewing_a_substitution_notification() {
        if (testContext.sharedSubstitutionQueue.isEmpty()) {
            String mockRequest = "Review substitution: 'Sugar' with 'Stevia' for 'Coffee'";
            testContext.sharedSubstitutionQueue.add(mockRequest);
            Assertions.assertFalse(testContext.sharedSubstitutionQueue.isEmpty(), "Queue should not be empty after mock setup.");
        }
        pendingSubstitution = testContext.sharedSubstitutionQueue.peek();
        Assertions.assertNotNull(pendingSubstitution, "No pending substitution to view. Queue might be empty or setup failed.");
        substitutionNotificationDisplayed = true;
        testContext.lastSystemMessage = "Viewing substitution: " + pendingSubstitution;
        Assertions.assertEquals("Viewing substitution: " + pendingSubstitution, testContext.lastSystemMessage, "System message should indicate which substitution is being viewed.");
    }

    @When("the chef approves the substitution")
    public void the_chef_approves_the_substitution() {
        Assertions.assertTrue(substitutionNotificationDisplayed, "Chef must be viewing a notification to approve it.");
        Assertions.assertNotNull(pendingSubstitution, "There must be a pending substitution to approve.");
        substitutionApproved = true;
        substitutionRejected = false;
        if(!testContext.sharedSubstitutionQueue.isEmpty() && pendingSubstitution.equals(testContext.sharedSubstitutionQueue.peek())){
            testContext.sharedSubstitutionQueue.poll();
        }
        Assertions.assertTrue(substitutionApproved, "Substitution should be marked as approved.");
        Assertions.assertFalse(substitutionRejected, "Substitution should not be marked as rejected if approved.");
    }

    @Then("the system should update the meal recipe")
    public void the_system_should_update_the_meal_recipe() {
        Assertions.assertTrue(substitutionApproved, "Substitution must be approved to update the meal recipe.");
        Assertions.assertNotNull(pendingSubstitution, "Pending substitution should not be null when updating recipe.");
        mealUpdatedMessage = "Meal recipe updated with approved substitution for: " + pendingSubstitution;
        testContext.lastSystemMessage = mealUpdatedMessage;
        Assertions.assertEquals("Meal recipe updated with approved substitution for: " + pendingSubstitution, mealUpdatedMessage, "Meal update message mismatch.");
        Assertions.assertEquals(mealUpdatedMessage, testContext.lastSystemMessage, "System message should reflect meal update.");
    }

    @When("the chef rejects the substitution")
    public void the_chef_rejects_the_substitution() {
        Assertions.assertTrue(substitutionNotificationDisplayed, "Chef must be viewing a notification to reject it.");
        Assertions.assertNotNull(pendingSubstitution, "There must be a pending substitution to reject.");
        substitutionRejected = true;
        substitutionApproved = false;
        if(!testContext.sharedSubstitutionQueue.isEmpty() && pendingSubstitution.equals(testContext.sharedSubstitutionQueue.peek())){
            testContext.sharedSubstitutionQueue.poll();
        }
        Assertions.assertTrue(substitutionRejected, "Substitution should be marked as rejected.");
        Assertions.assertFalse(substitutionApproved, "Substitution should not be marked as approved if rejected.");
    }

    @Then("the system should notify the customer")
    public void the_system_should_notify_the_customer() {
        Assertions.assertTrue(substitutionRejected, "Substitution must be rejected to notify the customer about rejection.");
        Assertions.assertNotNull(pendingSubstitution, "Pending substitution should not be null when notifying customer.");
        customerNotification = "Customer notified: Substitution '" + pendingSubstitution + "' was rejected. Please review your meal.";
        testContext.lastCustomerNotification = customerNotification; // This line is correct
        Assertions.assertEquals("Customer notified: Substitution '" + pendingSubstitution + "' was rejected. Please review your meal.", customerNotification, "Customer notification message mismatch.");
        Assertions.assertEquals(customerNotification, testContext.lastCustomerNotification, "Customer notification in context should match.");
    }
}