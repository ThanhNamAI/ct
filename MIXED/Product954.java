package RMI;

import java.io.Serializable;

public class Product954 implements Serializable {
    private static final long serialVersionUID = 954;
    public String id, code, discountCode;
    public int discount;
    public Product954() {}
    public Product954(String id, String code, String discountCode, int discount) {
        this.id = id; this.code = code; this.discountCode = discountCode; this.discount = discount;
    }
}
