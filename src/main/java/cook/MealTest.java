package cook;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MealTest {

    @Test
    void constructor_withNullIngredients_shouldCreateEmptyList() {
        Meal meal = new Meal(null, 'M', 10.0);
        assertNotNull(meal.getIngredients(), "Ingredients list should not be null.");
        assertTrue(meal.getIngredients().isEmpty(), "Ingredients list should be empty.");
        assertEquals("Custom Meal", meal.getName(), "Default name should be 'Custom Meal'");
    }

    @Test
    void constructor_withNameAndIngredients_shouldSetPropertiesCorrectly() {
        List<String> ingredients = Arrays.asList("Chicken", "Rice");
        Meal meal = new Meal("Chicken Rice", ingredients, 'L', 15.0);
        assertEquals("Chicken Rice", meal.getName());
        assertEquals(ingredients, meal.getIngredients());
        // assertEquals('L', meal.getSize()); // getSize() is missing, assuming it would exist
        assertEquals(15.0, meal.getPrice());
        assertEquals(1, meal.getIngredientQuantities().get("Chicken"));
        assertEquals(1, meal.getIngredientQuantities().get("Rice"));
    }

    @Test
    void suggestAlternative_ingredientNotKnown_shouldReturnEmptyList() {
        List<String> alternatives = Meal.suggestAlternative("UnknownIngredient");
        assertTrue(alternatives.isEmpty());
    }

    @Test
    void suggestAlternative_ingredientKnown_shouldReturnAlternatives() {
        List<String> milkAlternatives = Meal.suggestAlternative("milk");
        assertEquals(Arrays.asList("Almond Milk", "Oat Milk"), milkAlternatives);

        List<String> fishAlternatives = Meal.suggestAlternative("FISH"); // Test case-insensitivity
        assertEquals(Arrays.asList("Tofu", "Mushrooms"), fishAlternatives);
    }

    @Test
    void hasIncompatibleIngredients_emptyIngredients_shouldReturnFalse() {
        Meal meal = new Meal(new ArrayList<>(), 'S', 5.0);
        assertFalse(meal.hasIncompatibleIngredients());
    }

    @Test
    void hasIncompatibleIngredients_noIncompatible_shouldReturnFalse() {
        Meal meal = new Meal(Arrays.asList("Chicken", "Rice"), 'M', 10.0);
        assertFalse(meal.hasIncompatibleIngredients());
    }

    @Test
    void hasIncompatibleIngredients_withMilkAndLemon_shouldReturnTrue() {
        Meal meal = new Meal(Arrays.asList("Milk", "Sugar", "Lemon"), 'M', 10.0);
        assertTrue(meal.hasIncompatibleIngredients());
    }

    @Test
    void hasIncompatibleIngredients_withFishAndCheese_shouldReturnTrue() {
        Meal meal = new Meal(Arrays.asList("Fish", "Potatoes", "Cheese"), 'L', 12.0);
        assertTrue(meal.hasIncompatibleIngredients());
    }

    @Test
    void updateIngredientQuantity_ingredientNotPresent_shouldNotUpdateAndPrintMessage() {
        // To test the System.out.println, you might need to redirect System.out
        // or just verify that the quantities map doesn't change for the non-existent ingredient.
        Meal meal = new Meal(Arrays.asList("Chicken"), 'M', 10.0);
        meal.updateIngredientQuantity("Beef", 2);
        assertNull(meal.getIngredientQuantities().get("Beef"));
        assertEquals(1, meal.getIngredientQuantities().get("Chicken")); // Original quantity remains
    }

    @Test
    void updateIngredientQuantity_ingredientPresent_shouldUpdateQuantity() {
        Meal meal = new Meal(Arrays.asList("Chicken", "Rice"), 'M', 10.0);
        meal.updateIngredientQuantity("Chicken", 3);
        assertEquals(3, meal.getIngredientQuantities().get("Chicken"));
        assertEquals(1, meal.getIngredientQuantities().get("Rice")); // Other ingredients unaffected
    }

    @Test
    void equals_sameObject_shouldReturnTrue() {
        Meal meal1 = new Meal(Arrays.asList("Pasta"), 'S', 7.0);
        assertTrue(meal1.equals(meal1));
    }

    @Test
    void equals_nullObject_shouldReturnFalse() {
        Meal meal1 = new Meal(Arrays.asList("Pasta"), 'S', 7.0);
        assertFalse(meal1.equals(null));
    }

    @Test
    void equals_differentClass_shouldReturnFalse() {
        Meal meal1 = new Meal(Arrays.asList("Pasta"), 'S', 7.0);
        String notAMeal = "I am not a meal";
        assertFalse(meal1.equals(notAMeal));
    }

    @Test
    void equals_identicalMeals_shouldReturnTrue() {
        Meal meal1 = new Meal("Spaghetti", Arrays.asList("Pasta", "Sauce"), 'M', 12.0);
        Meal meal2 = new Meal("Spaghetti", Arrays.asList("Pasta", "Sauce"), 'M', 12.0);
        assertTrue(meal1.equals(meal2));
        assertEquals(meal1.hashCode(), meal2.hashCode());
    }

    @Test
    void equals_differentName_shouldReturnFalse() {
        Meal meal1 = new Meal("Spaghetti", Arrays.asList("Pasta", "Sauce"), 'M', 12.0);
        Meal meal2 = new Meal("Lasagna", Arrays.asList("Pasta", "Sauce"), 'M', 12.0);
        assertFalse(meal1.equals(meal2));
    }

    @Test
    void equals_differentIngredients_shouldReturnFalse() {
        Meal meal1 = new Meal("Spaghetti", Arrays.asList("Pasta", "Sauce"), 'M', 12.0);
        Meal meal2 = new Meal("Spaghetti", Arrays.asList("Pasta", "Meatballs"), 'M', 12.0);
        assertFalse(meal1.equals(meal2));
    }

    @Test
    void equals_differentPrice_shouldReturnFalse() {
        Meal meal1 = new Meal("Spaghetti", Arrays.asList("Pasta", "Sauce"), 'M', 12.0);
        Meal meal2 = new Meal("Spaghetti", Arrays.asList("Pasta", "Sauce"), 'M', 10.0);
        assertNotEquals(meal1, meal2);
    }


    @Test
    void hashCode_consistentForEqualObjects() {
        Meal meal1 = new Meal("Burger", Arrays.asList("Bun", "Patty"), 'M', 8.0);
        Meal meal2 = new Meal("Burger", Arrays.asList("Bun", "Patty"), 'M', 8.0);
        assertEquals(meal1.hashCode(), meal2.hashCode());
    }

    @Test
    void setName_shouldUpdateName() {
        Meal meal = new Meal(Collections.emptyList(), 'S', 5.0);
        meal.setName("New Fancy Meal");
        assertEquals("New Fancy Meal", meal.getName());
    }
}