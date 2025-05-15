package cook;

public class InventoryItem {
    private String ingredientName;
    private int quantity;
    private int threshold;
    private double unitPrice;


    public InventoryItem(String ingredientName, int quantity, int threshold, double unitPrice) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.threshold = threshold;
        this.unitPrice = unitPrice;
    }


    public String getIngredientName() {
        return ingredientName;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getThreshold() {
        return threshold;
    }


    public double getUnitPrice() {
        return this.unitPrice;
    }


    public boolean isLowStock() {

        return quantity <= threshold;
    }

    public void use(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Not enough stock for " + ingredientName + ". Requested: " + amount + ", Available: " + quantity);
        }
        quantity -= amount;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}