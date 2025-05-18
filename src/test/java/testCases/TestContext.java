package testCases;


import cook.Ingredient;
import cook.Meal;
import java.util.ArrayList;
import java.util.LinkedList; //  لاستخدام Queue
import java.util.List;
import java.util.Queue; //  لاستخدام Queue

public class TestContext {
    public List<Ingredient> sharedSelectedIngredients = new ArrayList<>();
    public Meal sharedCurrentMeal;
    public boolean sharedErrorDisplayed = false;
    public Queue<String> sharedSubstitutionQueue = new LinkedList<>(); //  طابور مشترك لطلبات التبديل

    public void reset() {
        sharedSelectedIngredients.clear();
        sharedCurrentMeal = null;
        sharedErrorDisplayed = false;
        sharedSubstitutionQueue.clear(); //  تنظيف الطابور
        System.out.println("--- TestContext Cleared (including substitution queue) ---");
    }
}
