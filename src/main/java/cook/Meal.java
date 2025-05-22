package cook;
import java.util.*;
public class Meal {
    private final List<String> ingredients;
    private final Map<String, Integer> ingredientQuantities;
    private final char size;
    private final double price;
    private String name;
    private static final Set<List<String>> incompatibleCombinations = new HashSet<>();
    private final Map<String, Ingredient> ingredientObjects = new HashMap<>();

    static {
        incompatibleCombinations.add(Arrays.asList("Milk", "Lemon"));
        incompatibleCombinations.add(Arrays.asList("Fish", "Cheese"));
    }

    public Meal(List<String> ingredients, char size, double price) {
        this.ingredients = new ArrayList<>(ingredients != null ? ingredients : Collections.emptyList()); // Handle null input
        this.size = size;
        this.price = price;
        this.ingredientQuantities = new HashMap<>();
        this.ingredients.forEach(ing -> ingredientQuantities.put(ing, 1));
        this.name = "Custom Meal";
    }

    public Meal(String name, List<String> ingredients, char size, double price) {
        this(ingredients, size, price);
        this.name = name;
    }


    public Map<String, Integer> getIngredientQuantities() {
        return Collections.unmodifiableMap(ingredientQuantities);
    }

    public List<String> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public double getPrice() {
        return price;
    }

    public static List<String> suggestAlternative(String ingredient) {

        String normalizedIngredient = ingredient.toLowerCase();
        Map<String, List<String>> alternatives = Map.of(
                "milk", Arrays.asList("Almond Milk", "Oat Milk"),
                "lemon", Arrays.asList("Lime", "Orange"),
                "fish", Arrays.asList("Tofu", "Mushrooms"),
                "shellfish", Arrays.asList("King Oyster Mushrooms", "Artichoke Hearts", "Tofu Puffs"), // Added Shellfish
                "peanuts", Arrays.asList("Sunflower Seeds", "Almonds (if no nut allergy)", "Pumpkin Seeds") // Added Peanuts for completeness
        );
        return alternatives.getOrDefault(normalizedIngredient, Collections.emptyList());
    }

    public boolean hasIncompatibleIngredients() {
        if (this.ingredients == null || this.ingredients.isEmpty()) {
            return false;
        }
        for (List<String> pair : incompatibleCombinations) {
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
                Objects.equals(new HashSet<>(ingredients), new HashSet<>(meal.ingredients));
    }

    @Override
    public int hashCode() {
        return Objects.hash(new HashSet<>(ingredients), size, price, name);
    }
}