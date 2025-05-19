package testCases;

import java.util.Queue;
import java.util.LinkedList;
import cook.Ingredient;
import cook.Meal;
import java.util.List;
import java.util.ArrayList;


public class TestContext {

    public List<Ingredient> sharedSelectedIngredients = new ArrayList<>();
    public Meal sharedCurrentMeal = null;
    public boolean sharedErrorDisplayed = false;


    public String lastSystemMessage = null;
    public Queue<String> sharedSubstitutionQueue = new LinkedList<>();
    public String lastCustomerNotification = null; // <<<<<<<< THIS FIELD IS ESSENTIAL


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