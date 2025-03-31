package cook;

public class chef extends User{
    private String name;
    private String email;
    private String password;

    public chef(String name , String email , String password ){
        super(name , email , password , Role.Chef);

    }


    public void approveSubstitution(Meal meal, String oldIngredient, String newIngredient) {
        meal.substituteIngredient(oldIngredient, newIngredient);
        System.out.println("Chef approved substitution for " + oldIngredient + " to " + newIngredient);
    }


    public void rejectSubstitution() {
        System.out.println("Chef rejected substitution.");
    }



}
