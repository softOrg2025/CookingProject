package testCases;

import cook.Application;
import cook.User;
import cook.Role; // افترض أن هذا الـ enum موجود
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import testCases.TestContext;
import java.util.*;
import org.junit.jupiter.api.Assertions;

public class SubstitutionApprovalSteps {
    private final TestContext testContext;

    private boolean chefLoggedIn = false;
    private boolean substitutionNotificationDisplayed = false;
    private boolean substitutionApproved = false;
    private boolean substitutionRejected = false;
    private String pendingSubstitution = null;
    private String mealUpdatedMessage = "";
    private String customerNotification = "";

    // بيانات الشيف لهذا الاختبار
    private final String CHEF_EMAIL = "shahd@gmail.com";
    private final String CHEF_PASSWORD = "Chef_Shahd";
    private final String CHEF_NAME = "Shahd Chef";


    public SubstitutionApprovalSteps(TestContext context) {
        this.testContext = context;
    }

    @Before
    public void setUp() {
        // إعادة تعيين الحالة المحلية
        chefLoggedIn = false;
        substitutionNotificationDisplayed = false;
        substitutionApproved = false;
        substitutionRejected = false;
        pendingSubstitution = null;
        mealUpdatedMessage = "";
        customerNotification = "";

        // لا ننسى إعادة تعيين سياق TestContext إذا كان هذا هو المكان المناسب
        // testContext.reset(); // إذا لم يكن هناك @Before آخر يقوم بذلك أولاً

        // التأكد من وجود الشيف في Application.users لهذه الاختبارات
        // هذا يجعل هذه الفيتشر أكثر استقلالية
        ensureChefExistsInApplication();

        System.out.println("--- SubstitutionApprovalSteps: Scenario Start (local state cleared, chef ensured) ---");
    }

    private void ensureChefExistsInApplication() {
        // تحقق مما إذا كان الشيف موجودًا بالفعل لتجنب الإضافة المكررة إذا كانت Application.users لا تُنظف بالكامل
        boolean chefExists = Application.users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(CHEF_EMAIL) && user.getRole() == Role.Chef);

        if (!chefExists) {
            User chefUser = new User(CHEF_NAME, CHEF_EMAIL, CHEF_PASSWORD, Role.Chef);
            Application.users.add(chefUser);
            System.out.println("Added " + CHEF_NAME + " to Application.users for testing.");
        } else {
            System.out.println(CHEF_NAME + " already exists in Application.users.");
        }
    }


    @Given("the system has suggested an alternative ingredient")
    public void the_system_has_suggested_an_alternative_ingredient() {
        String mockSubstitutionRequest = "Substitute 'Flour' with 'Almond Flour' for 'Cake'";
        testContext.sharedSubstitutionQueue.add(mockSubstitutionRequest);
        System.out.println("Mock Setup: System suggested an alternative. Request added to shared queue: " + mockSubstitutionRequest);
    }

    @When("the chef logs in")
    public void the_chef_logs_in() {
        User chef = Application.login(CHEF_EMAIL, CHEF_PASSWORD);
        if (chef != null && chef.getRole() == Role.Chef) { //  مقارنة مباشرة إذا كان Role هو enum
            chefLoggedIn = true;
            System.out.println("Chef '" + chef.getName() + "' logged in successfully.");
        } else {
            chefLoggedIn = false;
            System.out.println("Chef login failed for " + CHEF_EMAIL + " or user is not a Chef.");
            // اطبع حالة Application.users هنا للمساعدة في التشخيص
            System.out.println("Current Application.users size: " + Application.users.size());
            Application.users.forEach(u -> System.out.println("User: " + u.getName() + ", Email: " + u.getEmail() + ", Role: " + u.getRole()));
        }
    }

    @Then("the system should display the substitution notification")
    public void the_system_should_display_the_substitution_notification() {
        Assertions.assertTrue(chefLoggedIn, "Chef must be logged in to see notifications.");
        if (!testContext.sharedSubstitutionQueue.isEmpty()) {
            pendingSubstitution = testContext.sharedSubstitutionQueue.poll();
            substitutionNotificationDisplayed = true;
            System.out.println("System displays substitution request: " + pendingSubstitution);
        } else {
            substitutionNotificationDisplayed = false;
            System.out.println("System: No substitution requests available in the shared queue.");
            // قد يكون من المناسب إضافة تأكيد هنا إذا كنت تتوقع دائمًا وجود إشعار
            Assertions.assertFalse(testContext.sharedSubstitutionQueue.isEmpty(), "Expected substitution requests in the queue, but it was empty.");
        }
        Assertions.assertTrue(substitutionNotificationDisplayed, "A substitution notification should have been displayed.");
    }

    // ... باقي دوال الخطوات كما هي ...
    @Given("the chef is viewing a substitution notification")
    public void the_chef_is_viewing_a_substitution_notification() {
        if (testContext.sharedSubstitutionQueue.isEmpty()) {
            String mockRequest = "Review substitution: 'Sugar' with 'Stevia' for 'Coffee'";
            testContext.sharedSubstitutionQueue.add(mockRequest);
            System.out.println("Mock setup for viewing: Added '" + mockRequest + "' to queue.");
        }
        pendingSubstitution = testContext.sharedSubstitutionQueue.peek();
        Assertions.assertNotNull(pendingSubstitution, "No pending substitution to view. Queue might be empty.");
        substitutionNotificationDisplayed = true;
        System.out.println("Chef is now viewing substitution notification: " + pendingSubstitution);
    }

    @When("the chef approves the substitution")
    public void the_chef_approves_the_substitution() {
        Assertions.assertTrue(substitutionNotificationDisplayed, "Chef must be viewing a notification to approve it.");
        Assertions.assertNotNull(pendingSubstitution, "There must be a pending substitution to approve.");
        substitutionApproved = true;
        substitutionRejected = false;
        System.out.println("Chef approved substitution: " + pendingSubstitution);
    }

    @Then("the system should update the meal recipe")
    public void the_system_should_update_the_meal_recipe() {
        Assertions.assertTrue(substitutionApproved, "Substitution must be approved to update the meal recipe.");
        mealUpdatedMessage = "Meal recipe updated with approved substitution for: " + pendingSubstitution;
        System.out.println(mealUpdatedMessage);
        pendingSubstitution = null;
    }

    @When("the chef rejects the substitution")
    public void the_chef_rejects_the_substitution() {
        Assertions.assertTrue(substitutionNotificationDisplayed, "Chef must be viewing a notification to reject it.");
        Assertions.assertNotNull(pendingSubstitution, "There must be a pending substitution to reject.");
        substitutionRejected = true;
        substitutionApproved = false;
        System.out.println("Chef rejected substitution: " + pendingSubstitution);
    }

    @Then("the system should notify the customer")
    public void the_system_should_notify_the_customer() {
        Assertions.assertTrue(substitutionRejected, "Substitution must be rejected to notify the customer about rejection.");
        customerNotification = "Customer notified: Substitution '" + pendingSubstitution + "' was rejected. Please review your meal.";
        System.out.println(customerNotification);
        pendingSubstitution = null;
    }
}