package testCases;

import cook.Ingredient;
import cook.Meal;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import testCases.TestContext; //  استيراد السياق

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
// لا حاجة لـ DataTable هنا إذا كانت الخطوة المعرفة هنا لا تستخدمها مباشرة

public class SystemValidationSteps {
    private final TestContext testContext; //  للسياق المشترك
    private boolean mealSubmissionPrevented = false; //  هذا خاص بمنع الإرسال

    private static final char DEFAULT_MEAL_SIZE = 'M'; // يمكن إبقاؤها إذا استخدمت محليًا
    private static final double DEFAULT_MEAL_PRICE = 0.0; // يمكن إبقاؤها

    public SystemValidationSteps(TestContext context) { //  Constructor لحقن السياق
        this.testContext = context;
    }

    @Before // Cucumber Hook
    public void setUp() {
        testContext.reset(); //  مهم جدًا: إعادة تعيين السياق المشترك قبل كل سيناريو
        mealSubmissionPrevented = false;
        System.out.println("--- SystemValidationSteps: Scenario Start, TestContext Reset ---");
    }

    // دالة مساعدة لإنشاء وجبة في السياق إذا لم تكن موجودة (خاصة بخطوات Given هنا)
    private void ensureSharedMealFromSharedIngredients(String mealName) {
        List<String> ingredientNames = testContext.sharedSelectedIngredients.stream()
                .map(Ingredient::getName)
                .collect(Collectors.toList());
        testContext.sharedCurrentMeal = new Meal(mealName, ingredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
        System.out.println("SystemValidationSteps: Ensured/Created sharedCurrentMeal as '" + mealName + "' with ingredients: " + ingredientNames);
    }


    // هذه الخطوة الآن ستُعرف هنا، وتعمل على السياق المشترك
    @Given("the customer has selected incompatible ingredients")
    public void the_customer_has_selected_incompatible_ingredients() {
        testContext.sharedSelectedIngredients.clear();
        testContext.sharedSelectedIngredients.add(new Ingredient("Milk"));
        testContext.sharedSelectedIngredients.add(new Ingredient("Lemon"));
        ensureSharedMealFromSharedIngredients("IncompatibleTestMeal");
        System.out.println("SystemValidationSteps: Set (in context) incompatible ingredients: Milk, Lemon");
    }

    // وهذه الخطوة أيضًا تُعرف هنا
    @Given("the customer has selected invalid ingredients")
    public void the_customer_has_selected_invalid_ingredients() {
        testContext.sharedSelectedIngredients.clear();
        testContext.sharedSelectedIngredients.add(new Ingredient("Milk"));
        testContext.sharedSelectedIngredients.add(new Ingredient("Lemon"));
        ensureSharedMealFromSharedIngredients("InvalidTestMeal");
        System.out.println("SystemValidationSteps: Set (in context) invalid ingredients: Milk, Lemon");
    }

    @When("the system checks the combination")
    public void the_system_checks_the_combination() {
        // تأكد من أن الوجبة في السياق تم إنشاؤها (عادةً بواسطة خطوة Given)
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared current meal in context should not be null when checking combination.");

        if (testContext.sharedCurrentMeal.hasIncompatibleIngredients()) {
            testContext.sharedErrorDisplayed = true; // تحديث الحالة في السياق
            System.out.println("SystemValidationSteps Check (on shared meal): Incompatible combination detected.");
        } else {
            testContext.sharedErrorDisplayed = false; // تحديث الحالة في السياق
            System.out.println("SystemValidationSteps Check (on shared meal): No incompatible combination detected.");
        }
    }

    @When("the system identifies the issue")
    public void the_system_identifies_the_issue() {
        System.out.println("SystemValidationSteps: Identifying issue (on shared meal)...");
        the_system_checks_the_combination(); // ستعمل على testContext.sharedCurrentMeal و testContext.sharedErrorDisplayed
        if (testContext.sharedErrorDisplayed) {
            System.out.println("SystemValidationSteps Identified Issue (on shared meal): Incompatibility found.");
        } else {
            System.out.println("SystemValidationSteps Identified Issue (on shared meal): No incompatibility found.");
        }
    }

    @Then("the system should flag any incompatible ingredients")
    public void the_system_should_flag_any_incompatible_ingredients() {
        Assertions.assertTrue(testContext.sharedErrorDisplayed, "System should have flagged incompatible ingredients (shared state), but it didn't.");
        if (testContext.sharedErrorDisplayed) {
            System.out.println("Assertion Passed: System correctly flagged incompatible ingredients (shared state).");
        }
    }

    @Then("the system should suggest alternative ingredients")
    public void the_system_should_suggest_alternative_ingredients() {
        Assertions.assertTrue(testContext.sharedErrorDisplayed, "Alternatives should only be suggested if an incompatibility was found (shared state).");
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared current meal in context should not be null for suggestions.");

        if (testContext.sharedErrorDisplayed && testContext.sharedCurrentMeal != null) {
            System.out.println("Suggesting alternatives for shared meal due to incompatibility:");
            boolean suggestionsFound = false;
            for (String ingredientNameInMeal : testContext.sharedCurrentMeal.getIngredients()) {
                List<String> alternatives = Meal.suggestAlternative(ingredientNameInMeal);
                if (!alternatives.isEmpty()) {
                    System.out.println("  Alternatives for " + ingredientNameInMeal + ": " + alternatives);
                    suggestionsFound = true;
                }
            }
            if (!suggestionsFound) {
                System.out.println("  No specific alternatives found for the current incompatible ingredients in the shared meal.");
            } else {
                System.out.println("Assertion Passed: System suggested alternatives for shared meal.");
            }
        }
    }

    @When("the customer tries to submit the meal")
    public void the_customer_tries_to_submit_the_meal() {
        // تأكد من أن الوجبة في السياق تم إنشاؤها
        if (testContext.sharedCurrentMeal == null && !testContext.sharedSelectedIngredients.isEmpty()) {
            // إذا لم تقم خطوة Given بإنشاء الوجبة، أنشئها الآن
            ensureSharedMealFromSharedIngredients("SubmissionTestMeal");
        }
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared current meal must exist before attempting submission.");

        if (testContext.sharedCurrentMeal.hasIncompatibleIngredients()) {
            testContext.sharedErrorDisplayed = true;
            mealSubmissionPrevented = true;
            System.out.println("Submission Attempt (shared meal): Meal contains incompatible ingredients. Submission will be prevented.");
        } else {
            // testContext.sharedErrorDisplayed يجب أن تكون false إذا لم تكن هناك مشكلة
            mealSubmissionPrevented = false;
            System.out.println("Submission Attempt (shared meal): Meal is valid. Submission would proceed.");
        }
    }

    @Then("the system should prevent submission and display an error")
    public void the_system_should_prevent_submission_and_display_an_error() {
        Assertions.assertTrue(mealSubmissionPrevented, "Meal submission (based on shared meal) should have been prevented.");
        Assertions.assertTrue(testContext.sharedErrorDisplayed, "An error (in shared state) should be displayed for incompatible ingredients during submission.");

        if (mealSubmissionPrevented && testContext.sharedErrorDisplayed) {
            System.out.println("Assertion Passed: System correctly prevented submission and an error condition was present (shared state).");
            System.out.println("Error Displayed (simulated): Cannot submit meal with incompatible ingredients. Please revise your selection.");
        }
    }
}