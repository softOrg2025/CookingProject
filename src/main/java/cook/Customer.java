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
    public Customer(String name , String email , String password ){
        super(name , email , password , Role.Customer);

    }

    public boolean savePreferences(String option) {
        if(preferences.contains(option)){
            return false;
        }
        preferences.add(option);
        return true;
    }

    public boolean saveAllergy(String allergy) {
        if(allergies.contains(allergy)){
            return false;
        }
        allergies.add(allergy);
        return true;
    }

    public boolean allergyExist(String string) {
        return allergies.contains(string) ;

    }

    public List<String> getAllergies() {
        return allergies;
    }








    public List<String> getPreferences() {
        return Collections.unmodifiableList(preferences); //  إعادة نسخة غير قابلة للتعديل للحماية
    }






}