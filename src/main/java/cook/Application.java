package cook;

import java.util.ArrayList;
import java.util.List;

public class Application {
    public static List<User> users = new ArrayList<User>();
    private static User currentUser = new User();
    public static List<Meal> meals = new ArrayList<Meal>();
    public Application (){

    }

    public static User login(String string, String pass) {
        for ( User user : users){
            if (user.getEmail().equals(string)&&user.getPassword().equals(pass)){
                currentUser = user;
                return currentUser;
            }

        }
        return null;
    }

    public static List<Meal> exclude(List<String> allergies) {
        List<Meal> suggestedMeals = new ArrayList<>();
        for(Meal meal :meals){
            for(String allergy :allergies){
                if(!meal.getIngredients().contains(allergy))
                    suggestedMeals.add(meal);
            }
        }
        return suggestedMeals;
    }
}
