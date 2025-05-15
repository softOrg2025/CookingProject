package cook;

public class PurchaseOrder {
    private String ingredientName;
    private int quantity;
    private String supplierName; // أو كائن Supplier
    private double pricePerUnit;
    private double totalPrice;
    private String orderId; // معرّف فريد لأمر الشراء

    public PurchaseOrder(String ingredientName, int quantity, String supplierName, double pricePerUnit) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.pricePerUnit = pricePerUnit;

        this.totalPrice = this.quantity * this.pricePerUnit;
        this.orderId = "PO-" + System.currentTimeMillis(); // مثال بسيط لمعرف فريد
    }

    // Getters
    public String getIngredientName() { return ingredientName; }
    public int getQuantity() { return quantity; }
    public String getSupplierName() { return supplierName; }
    public double getPricePerUnit() { return pricePerUnit; }
    public double getTotalPrice() { return totalPrice; }
    public String getOrderId() { return orderId; }







}
