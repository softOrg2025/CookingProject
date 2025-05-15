package cook;

public class Ingredients {
    private String name;
    private int quantity;
    private int threshold;
    private double price;

    public Ingredients(String name, int quantity, int threshold, double price) {
        this.name = name;
        this.quantity = quantity;
        this.threshold = threshold;
        this.price = price;
    }


    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public int getThreshold() { return threshold; }
    public double getPrice() { return price; }

    public void use(int amount) {
        this.quantity -= amount;
    }

    public boolean isLowStock() {
        return quantity < threshold;
    }
}
