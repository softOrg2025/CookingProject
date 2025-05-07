package cook;


import java.util.*;

public class Meal {
    private List<String> ingredients ;
    private Map<String, Integer> ingredientQuantities;
    private char size;
    private double price;
    private String name;

    private static final Set<List<String>> incompatibleCombinations = new HashSet<>();

    static {
        incompatibleCombinations.add(Arrays.asList("Milk", "Lemon"));
        incompatibleCombinations.add(Arrays.asList("Fish", "Cheese"));
    }




    public Meal(List<String> ingredients, char size, double price) {
        this.ingredients = ingredients;
        this.size = size;
        this.price = price;
        this.ingredientQuantities = new HashMap<>();
        // Default quantity of 1 for each ingredient
        ingredients.forEach(ing -> ingredientQuantities.put(ing, 1));
    }
    public Map<String, Integer> getIngredientQuantities() {
        return Collections.unmodifiableMap(ingredientQuantities);
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public char getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }



    public static List<String> suggestAlternative(String ingredient) {
        Map<String, List<String>> alternatives = Map.of(
                "Milk", Arrays.asList("Almond Milk", "Oat Milk"),
                "Lemon", Arrays.asList("Lime", "Orange"),
                "Fish", Arrays.asList("Tofu", "Mushrooms")
        );
        return alternatives.getOrDefault(ingredient, Collections.emptyList());
    }




    public boolean hasIncompatibleIngredients() {
        for (List<String> pair : incompatibleCombinations) {
            if (ingredients.containsAll(pair)) {
                return true;
            }
        }
        return false;
    }



    public boolean substituteIngredient(String oldIngredient, String newIngredient) {
        if (ingredients.contains(oldIngredient)) {
            ingredients.remove(oldIngredient);
            ingredients.add(newIngredient);
            System.out.println("Updated meal: " + name + " → Replaced " + oldIngredient + " with " + newIngredient);
            return true;
        } else {
            System.out.println("Error: " + oldIngredient + " not found in " + name);
            return false;
        }
    }


    public String getName() {
        return name;
    }
}
