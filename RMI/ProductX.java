package RMI;

import java.io.Serializable;

public class ProductX implements Serializable {
    private static final long serialVersionUID = 20171107;
    public String id, code, discountCode;
    public int discount;
    public ProductX() {}
    public ProductX(String id, String code, String discountCode, int discount) {
        this.id = id; this.code = code; this.discountCode = discountCode; this.discount = discount;
    }
}
