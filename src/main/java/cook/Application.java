package cook;

import java.util.ArrayList;
import java.util.List;

public class Application {
    public static List<User> users = new ArrayList<User>();
    public static User currentUser = null;
    public static List<Meal> meals = new ArrayList<Meal>();
    public static NotificationService notificationService = new NotificationService(); // Added for potential broader use

    public static String lastSystemMessage = null;




    public Application (){

    }

    public static User login(String email, String pass) {
        for (User user : users){
            if (user.getEmail().equals(email) && user.getPassword().equals(pass)){
                currentUser = user;
                return currentUser;
            }
        }
        setSystemMessage("Login failed: Invalid email or password");
        return null;
    }

    public static List<Meal> exclude(List<String> allergies) {
        List<Meal> suggestedMeals = new ArrayList<>();
        if (allergies == null) { // Prevent NullPointerException if allergies list is null

        }

        for (Meal meal : meals) {
            boolean containsAllergy = false;
            if (meal.getIngredients() == null) continue; // Skip meal if ingredients are null

            for (String allergy : allergies) {
                if (allergy == null) continue; // Skip null allergy strings
                // Case-insensitive check for ingredients
                if (meal.getIngredients().stream().anyMatch(ingredient -> ingredient != null && ingredient.equalsIgnoreCase(allergy))) {
                    containsAllergy = true;
                    break;
                }
            }
            if (!containsAllergy) {
                suggestedMeals.add(meal);
            }
        }
        return suggestedMeals;
    }


    public static void setSystemMessage(String message) {
        lastSystemMessage = message;

    }


    public static String getSystemMessage() {
        return lastSystemMessage;
    }
}