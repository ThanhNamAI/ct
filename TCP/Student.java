package TCP;

import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = 20151107L;
    public int id;
    public String code;
    public float gpa;
    public String gpaLetter;

    public Student() {}
    public Student(int id, String code, float gpa, String gpaLetter) {
        this.id = id; this.code = code; this.gpa = gpa; this.gpaLetter = gpaLetter;
    }
}
