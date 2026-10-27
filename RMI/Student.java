package RMI;

import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = 20241130L;
    private String id;
    private String name;
    private int enrollmentYear;
    private String code;

    public Student() {}
    public Student(String id, String name, int enrollmentYear) {
        this.id = id;
        this.name = name;
        this.enrollmentYear = enrollmentYear;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getEnrollmentYear() { return enrollmentYear; }
    public void setEnrollmentYear(int enrollmentYear) { this.enrollmentYear = enrollmentYear; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
