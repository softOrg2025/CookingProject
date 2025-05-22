package cook;

import java.util.HashMap;
import java.util.Map;

public class IngredientSupplier {
    private final Map<Ingredient, Supplier> ingredientToSupplier = new HashMap<>();

    public void addSupplierForIngredient(Ingredient ingredient, Supplier supplier) {
        ingredientToSupplier.put(ingredient, supplier);
    }

    public Supplier getSupplierForIngredient(Ingredient ingredient) {
        return ingredientToSupplier.get(ingredient);
    }
}
