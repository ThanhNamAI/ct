package RMI;

import java.io.Serializable;

public class Product953 implements Serializable {
    private static final long serialVersionUID = 953;
    public String id, code, discountCode;
    public int discount;
    public Product953() {}
    public Product953(String id, String code, String discountCode, int discount) {
        this.id = id; this.code = code; this.discountCode = discountCode; this.discount = discount;
    }
}
