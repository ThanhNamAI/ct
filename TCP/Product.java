package TCP;

import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 20231107L;
    public int id;
    public String name;
    public double price;
    public int discount;

    public Product() {}
    public Product(int id, String name, double price, int discount) {
        this.id = id; this.name = name; this.price = price; this.discount = discount;
    }
}
