package TCP;

import java.io.Serializable;

public class Address implements Serializable {
    private static final long serialVersionUID = 20180801L;
    public int id;
    public String code, addressLine, city, postalCode;

    public Address() {}
    public Address(int id, String code, String addressLine, String city, String postalCode) {
        this.id = id; this.code = code; this.addressLine = addressLine; this.city = city; this.postalCode = postalCode;
    }
}
