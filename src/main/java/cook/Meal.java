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
        this.ingredients = new ArrayList<>(ingredients);
        this.size = size;
        this.price = price;
        this.ingredientQuantities = new HashMap<>();

        if (ingredients != null) {
            ingredients.forEach(ing -> ingredientQuantities.put(ing, 1));
        }
        this.name = "Custom Meal"; // Default name
    }

    public Meal(String name, List<String> ingredients, char size, double price) {
        this(ingredients, size, price);
        this.name = name;
    }


    public Map<String, Integer> getIngredientQuantities() {
        return Collections.unmodifiableMap(ingredientQuantities);
    }

    public List<String> getIngredients() {
        // Return a copy to prevent external modification if desired,
        // or Collections.unmodifiableList for stricter immutability.
        return new ArrayList<>(ingredients);
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
        if (this.ingredients == null || this.ingredients.isEmpty()) {
            return false;
        }
        for (List<String> pair : incompatibleCombinations) {
            // Check if the meal's ingredients contain ALL ingredients in an incompatible pair
            if (new HashSet<>(this.ingredients).containsAll(pair)) {
                return true;
            }
        }
        return false;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void updateIngredientQuantity(String ingredient, int quantity) {
        if (ingredients.contains(ingredient)) {
            this.ingredientQuantities.put(ingredient, quantity);
        } else {
            System.out.println("Cannot update quantity for " + ingredient + "; it is not in the meal.");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return size == meal.size &&
                Double.compare(meal.price, price) == 0 &&
                Objects.equals(name, meal.name) &&
                // For ingredients, consider order doesn't matter for equality
                Objects.equals(new HashSet<>(ingredients), new HashSet<>(meal.ingredients));
    }

    @Override
    public int hashCode() {
        return Objects.hash(new HashSet<>(ingredients), size, price, name);
    }
}