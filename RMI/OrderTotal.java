package RMI;

import java.io.Serializable;

public class OrderTotal implements Serializable {
    private static final long serialVersionUID = 20260517L;
    private String orderId;
    private double itemsSubtotal;
    private double shippingFee;
    private double discountRate;
    private double total;
    private String status;

    public OrderTotal() {}
    public OrderTotal(String orderId, double itemsSubtotal, double shippingFee, double discountRate) {
        this.orderId = orderId;
        this.itemsSubtotal = itemsSubtotal;
        this.shippingFee = shippingFee;
        this.discountRate = discountRate;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getItemsSubtotal() { return itemsSubtotal; }
    public void setItemsSubtotal(double itemsSubtotal) { this.itemsSubtotal = itemsSubtotal; }

    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }

    public double getDiscountRate() { return discountRate; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
