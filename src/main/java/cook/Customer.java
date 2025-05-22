package cook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class Customer extends User {
    private List<String> preferences = new ArrayList<String>();
    private List<String> allergies = new ArrayList<String>();
    private List<String> selectedIngredients = new ArrayList<>();
    private static final Set<String> INCOMPATIBLE_INGREDIENTS = new HashSet<>(Arrays.asList("Milk", "Lemon"));
    private Map<String, Meal> savedMeals = new HashMap<>();


    public Customer(String name, String email, String password) {
        super(name, email, password, Role.Customer);
    }

    public boolean savePreferences(String option) {

        if (preferences.stream().anyMatch(p -> p.equalsIgnoreCase(option))) {

        }
        preferences.add(option);
        return true;
    }

    public boolean saveAllergy(String allergy) {
        if (allergies.stream().anyMatch(a -> a.equalsIgnoreCase(allergy))) {

        }
        allergies.add(allergy);
        return true;
    }

    public boolean allergyExist(String string) {
        return allergies.stream().anyMatch(a -> a.equalsIgnoreCase(string));
    }

    public List<String> getAllergies() {
        return Collections.unmodifiableList(allergies); // Return unmodifiable list
    }

    public List<String> getPreferences() {
        return Collections.unmodifiableList(preferences);
    }

    public void saveMeal(String mealName, Meal meal) {
        this.savedMeals.put(mealName, meal);
    }

    public Meal getSavedMeal(String mealName) {
        return this.savedMeals.get(mealName);
    }


    public Map<String, Meal> getSavedMeals() {
        return Collections.unmodifiableMap(savedMeals);
    }




}
