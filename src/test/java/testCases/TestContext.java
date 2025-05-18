package testCases; // Or whatever package your TestContext is in

import java.util.Queue;
import java.util.LinkedList;
import cook.Ingredient; // If used elsewhere in TestContext
import cook.Meal;      // If used elsewhere in TestContext
import java.util.List; // If used elsewhere in TestContext
import java.util.ArrayList; // If used elsewhere in TestContext


public class TestContext {
    // Fields from MealCustomizationSteps
    public List<Ingredient> sharedSelectedIngredients = new ArrayList<>();
    public Meal sharedCurrentMeal = null;
    public boolean sharedErrorDisplayed = false;

    // Fields for SubstitutionApprovalSteps (and possibly others)
    public String lastSystemMessage = null;
    public Queue<String> sharedSubstitutionQueue = new LinkedList<>();
    public String lastCustomerNotification = null; // <<<<<<<< THIS FIELD IS ESSENTIAL

    // Optional: A reset method
    public void reset() {
        sharedSelectedIngredients.clear();
        sharedCurrentMeal = null;
        sharedErrorDisplayed = false;
        lastSystemMessage = null;
        if (sharedSubstitutionQueue != null) {
            sharedSubstitutionQueue.clear();
        }
        lastCustomerNotification = null;
    }
}