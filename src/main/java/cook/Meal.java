package cook;

import java.util.ArrayList;
import java.util.List;

public class Meal {
    private List<String> ingredients = new ArrayList<String>();
    private char size;
    private double price;

    public List<String> getIngredients() {
        return ingredients;
    }
}
