package UDP;

import java.io.Serializable;

public class PricedProduct implements Serializable {
    private static final long serialVersionUID = 20260517L;
    private String id;
    private String name;
    private double basePrice;
    private double taxRate;
    private double discountRate;
    private double finalPrice;

    public PricedProduct() { }
    public PricedProduct(String id, String name, double basePrice, double taxRate, double discountRate) { this.id = id; this.name = name; this.basePrice = basePrice; this.taxRate = taxRate; this.discountRate = discountRate; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
    public double getDiscountRate() { return discountRate; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
}
