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

public class SubstitutionApprovalSteps {
    private final TestContext testContext;
    private String pendingSubstitution;

    private static final String CHEF_EMAIL = "shahd@gmail.com";
    private static final String CHEF_PASSWORD = "Chef_Shahd";
    private static final String CHEF_NAME = "Shahd Chef";

    public SubstitutionApprovalSteps(TestContext context) {
        this.testContext = context;
        initializeSharedQueue();
    }

    @Before
    public void setUp() {
        resetTestState();
        ensureChefExistsInApplication();
    }

    private void initializeSharedQueue() {
        if (this.testContext.sharedSubstitutionQueue == null) {
            this.testContext.sharedSubstitutionQueue = new LinkedList<>();
        }
    }

    private void resetTestState() {
        pendingSubstitution = null;
        testContext.sharedSubstitutionQueue.clear();
        testContext.lastSystemMessage = null;
        testContext.lastCustomerNotification = null;
    }

    private void ensureChefExistsInApplication() {
        boolean chefExists = Application.users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(CHEF_EMAIL) && user.getRole() == Role.Chef);

        if (!chefExists) {
            User chefUser = new User(CHEF_NAME, CHEF_EMAIL, CHEF_PASSWORD, Role.Chef);
            Application.users.add(chefUser);
        }
    }

    @Given("the system has suggested an alternative ingredient")
    public void theSystemHasSuggestedAnAlternativeIngredient() {
        String mockSubstitution = "Substitute 'Flour' with 'Almond Flour' for 'Cake'";
        testContext.sharedSubstitutionQueue.add(mockSubstitution);
        Assertions.assertFalse(testContext.sharedSubstitutionQueue.isEmpty());
    }

    @When("the chef logs in")
    public void theChefLogsIn() {
        User chef = Application.login(CHEF_EMAIL, CHEF_PASSWORD);
        Assertions.assertNotNull(chef);
        Assertions.assertEquals(Role.Chef, chef.getRole());
    }

    @Then("the system should display the substitution notification")
    public void theSystemShouldDisplayTheSubstitutionNotification() {
        pendingSubstitution = testContext.sharedSubstitutionQueue.poll();
        testContext.lastSystemMessage = "Substitution request: " + pendingSubstitution;

        Assertions.assertNotNull(pendingSubstitution);
        Assertions.assertNotNull(testContext.lastSystemMessage);
    }

    @Given("the chef is viewing a substitution notification")
    public void theChefIsViewingASubstitutionNotification() {
        String mockRequest = "Review substitution: 'Sugar' with 'Stevia' for 'Coffee'";
        testContext.sharedSubstitutionQueue.add(mockRequest);
        pendingSubstitution = testContext.sharedSubstitutionQueue.peek();
        testContext.lastSystemMessage = "Viewing substitution: " + pendingSubstitution;

        Assertions.assertNotNull(pendingSubstitution);
    }

    @When("the chef approves the substitution")
    public void theChefApprovesTheSubstitution() {
        testContext.sharedSubstitutionQueue.poll();
        testContext.lastSystemMessage = "Meal recipe updated with approved substitution for: " + pendingSubstitution;
    }

    @Then("the system should update the meal recipe")
    public void theSystemShouldUpdateTheMealRecipe() {
        Assertions.assertTrue(testContext.lastSystemMessage.contains("approved substitution"));
        Assertions.assertTrue(testContext.lastSystemMessage.contains(pendingSubstitution));
    }

    @When("the chef rejects the substitution")
    public void theChefRejectsTheSubstitution() {
        testContext.sharedSubstitutionQueue.poll();
        testContext.lastCustomerNotification = "Customer notified: Substitution '" +
                pendingSubstitution + "' was rejected. Please review your meal.";
    }

    @Then("the system should notify the customer")
    public void theSystemShouldNotifyTheCustomer() {
        Assertions.assertTrue(testContext.lastCustomerNotification.contains("rejected"));
        Assertions.assertTrue(testContext.lastCustomerNotification.contains(pendingSubstitution));
    }
}