package TCP;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 20170711L;
    public int id;
    public String code, name, dayOfBirth, userName;

    public Customer() {}
    public Customer(int id, String code, String name, String dayOfBirth) {
        this.id = id; this.code = code; this.name = name; this.dayOfBirth = dayOfBirth;
    }
}
