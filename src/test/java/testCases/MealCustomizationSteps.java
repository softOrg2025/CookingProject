package testCases;

import cook.Ingredient;
import cook.Meal;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import io.cucumber.datatable.DataTable;
import testCases.TestContext; //  استيراد السياق

public class MealCustomizationSteps {
    private final TestContext testContext; //  للسياق المشترك
    private Map<String, Meal> savedMeals = new HashMap<>(); //  هذا قد يبقى محليًا إذا كان الحفظ خاصًا بهذا الكلاس
    private String currentUser = "testUser"; //  هذا قد يبقى محليًا

    private static final char DEFAULT_MEAL_SIZE = 'M';
    private static final double DEFAULT_MEAL_PRICE = 10.0;
    private static final String DEFAULT_MEAL_NAME = "Custom Meal";

    public MealCustomizationSteps(TestContext context) { //  Constructor لحقن السياق
        this.testContext = context;
    }

    private List<String> getIngredientNames(List<Ingredient> ingredients) {
        if (ingredients == null) return new ArrayList<>();
        return ingredients.stream().map(Ingredient::getName).collect(Collectors.toList());
    }

    private void updateSharedCurrentMealWithName(String mealName) {
        List<String> ingredientNames = getIngredientNames(testContext.sharedSelectedIngredients);
        testContext.sharedCurrentMeal = new Meal(mealName, ingredientNames, DEFAULT_MEAL_SIZE, DEFAULT_MEAL_PRICE);
        System.out.println("Updated/Created sharedCurrentMeal as '" + mealName + "' with ingredients: " + ingredientNames);
    }

    private void updateSharedCurrentMealDefaultName() {
        updateSharedCurrentMealWithName(DEFAULT_MEAL_NAME);
    }


    @Before // Cucumber Hook
    public void setUp() {
        // testContext سيتم إعادة تعيينه بواسطة @Before في SystemValidationSteps أو في TestContext نفسه
        // هنا نعيد تعيين الحالة المحلية فقط لهذا الكلاس
        savedMeals.clear();
        currentUser = "testUser";
        System.out.println("--- MealCustomizationSteps: Scenario Start (local state cleared) ---");
    }

    @Given("the customer has selected ingredients:") // هذه الخطوة تعدّ السياق المشترك
    public void the_customer_has_selected_ingredients(DataTable dataTable) {
        testContext.sharedSelectedIngredients.clear(); // ابدأ بقائمة فارغة في السياق
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) {
                testContext.sharedSelectedIngredients.add(new Ingredient(ingredientName));
            }
        }
        updateSharedCurrentMealDefaultName(); // قم بتحديث الوجبة في السياق
        System.out.println("MealCustomizationSteps: Set sharedSelectedIngredients to: " + getIngredientNames(testContext.sharedSelectedIngredients));
    }

    @When("the customer chooses ingredients:")
    public void the_customer_chooses_ingredients(DataTable dataTable) {
        // إذا كانت هذه الخطوة يجب أن تبدأ من جديد، قم بـ clear أولاً
        // testContext.sharedSelectedIngredients.clear();
        List<String> ingredientsFromTable = dataTable.asList(String.class);
        for (String ingredientName : ingredientsFromTable) {
            if (!ingredientName.equalsIgnoreCase("ingredientName")) {
                testContext.sharedSelectedIngredients.add(new Ingredient(ingredientName));
                System.out.println("Customer chose (to context): " + ingredientName);
            }
        }
        updateSharedCurrentMealDefaultName();
    }

    @When("the customer selects {string}")
    public void the_customer_selects(String actionOrIngredientName) {
        if (actionOrIngredientName.equalsIgnoreCase("Create Custom Meal")) {
            System.out.println("Customer selected action: " + actionOrIngredientName);
            // قد ترغب في تهيئة testContext.sharedSelectedIngredients هنا إذا لزم الأمر
            testContext.sharedSelectedIngredients.clear();
            testContext.sharedCurrentMeal = null; // أو وجبة فارغة
        } else {
            // افترض أن هذا لاختيار مكون فردي، غير مستخدم في الفيتشر الحالي
            // testContext.sharedSelectedIngredients.add(new Ingredient(actionOrIngredientName));
            // updateSharedCurrentMealDefaultName();
            // System.out.println("Customer selected ingredient (to context): " + actionOrIngredientName);
        }
    }

    @When("the customer tries to combine incompatible ingredients")
    public void the_customer_tries_to_combine_incompatible_ingredients() {
        if (testContext.sharedCurrentMeal == null && !testContext.sharedSelectedIngredients.isEmpty()) {
            updateSharedCurrentMealDefaultName();
        }
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should exist before checking incompatibility.");

        if (testContext.sharedCurrentMeal.hasIncompatibleIngredients()) {
            testContext.sharedErrorDisplayed = true;
            System.out.println("MealCustomizationSteps: Incompatible ingredients detected in shared meal: " + testContext.sharedCurrentMeal.getIngredients());
        } else {
            testContext.sharedErrorDisplayed = false;
            System.out.println("MealCustomizationSteps: No incompatible ingredients detected in shared meal: " + testContext.sharedCurrentMeal.getIngredients());
        }
    }

    @Then("the system should display an error message")
    public void the_system_should_display_an_error_message() {
        Assertions.assertTrue(testContext.sharedErrorDisplayed, "An error message should have been displayed (from shared context).");
        if (testContext.sharedErrorDisplayed) {
            System.out.println("Error: Incompatible ingredients selected. Please revise your selection.");
        }
    }

    @Then("the system should save the selected ingredients as a meal named {string}")
    public void the_system_should_save_the_selected_ingredients_as_a_meal_named(String mealName) {
        updateSharedCurrentMealWithName(mealName); // تأكد من أن الوجبة في السياق تحمل الاسم الصحيح قبل الحفظ
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should not be null before saving.");

        savedMeals.put(currentUser, testContext.sharedCurrentMeal); // الحفظ في الخريطة المحلية
        System.out.println("Meal '" + mealName + "' (from context) saved for user " + currentUser + " with ingredients: " + testContext.sharedCurrentMeal.getIngredients());
        Assertions.assertTrue(savedMeals.containsKey(currentUser));
        Assertions.assertEquals(testContext.sharedCurrentMeal, savedMeals.get(currentUser));
    }

    @When("the customer saves the custom meal as {string}")
    public void the_customer_saves_the_custom_meal_as(String mealName) {
        updateSharedCurrentMealWithName(mealName); // تحديث/إنشاء الوجبة في السياق بالاسم والمكونات الحالية
        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should not be null to be saved.");

        savedMeals.put(currentUser, testContext.sharedCurrentMeal);
        System.out.println("Custom meal '" + mealName + "' (from context) saved for user " + currentUser + " with ingredients: " + testContext.sharedCurrentMeal.getIngredients());
    }

    @Then("the system should store the meal {string} for future orders")
    public void the_system_should_store_the_meal_for_future_orders(String mealName) {
        Meal mealFromSavedMap = savedMeals.get(currentUser);
        Assertions.assertNotNull(mealFromSavedMap, "No meal found saved for the current user.");
        Assertions.assertEquals(mealName, mealFromSavedMap.getName(), "The name of the saved meal does not match.");

        Assertions.assertNotNull(testContext.sharedCurrentMeal, "Shared meal in context should not be null for comparison.");
        // المقارنة بين ما تم حفظه في الخريطة وما هو موجود حاليًا في السياق (والذي يجب أن يكون هو ما تم حفظه)
        Assertions.assertEquals(
                new java.util.HashSet<>(testContext.sharedCurrentMeal.getIngredients()),
                new java.util.HashSet<>(mealFromSavedMap.getIngredients()),
                "Ingredients of the saved meal do not match the ingredients from the shared context at save time."
        );
        System.out.println("Custom meal '" + mealName + "' with ingredients " + mealFromSavedMap.getIngredients() + " is correctly stored.");
    }
}