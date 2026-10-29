package TCP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * [Mã câu hỏi: 1542] [TCP Object Stream] Chuẩn hóa mã sinh viên và xếp loại GPA
 * Loại: TCP_OBJECT
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209, sử dụng 
 * ObjectInputStream
 *  và 
 * ObjectOutputStream
 *  để trao đổi đối tượng Java.
 * 
 * 
 * Lớp dữ liệu trao đổi
 * 
 * 
 * Tạo lớp dưới đây trong project client, đúng package, tên lớp, 
 * serialVersionUID
 * , tên và kiểu của từng thuộc tính. Nếu khác, server không đọc được đối tượng gửi lên.
 * 
 * 
 * package TCP;
 * 
 * public class Student implements java.io.Serializable {
 *     private static final long serialVersionUID = 20151107L;
 * 
 *     private int id;
 *     private String code;
 *     private float gpa;
 *     private String gpaLetter;
 * 
 *     public Student() { }
 *     public Student(int id, String code, float gpa, String gpaLetter) { /* gán 4 thuộc tính * / }
 *     // getter/setter cho tất cả thuộc tính
 * }
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi một đối tượng 
 * String
 *  chứa mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 *  bằng 
 * ObjectOutputStream
 * . Ví dụ: 
 * B15DCCN999;5AD2B818
 * .
 * 
 * 
 * b. Nhận từ server một đối tượng 
 * TCP.Student
 *  đã có sẵn 
 * id
 * , 
 * code
 * , 
 * gpa
 * ; 
 * gpaLetter
 *  đang rỗng.
 * 
 * 
 * c. Chuyển 
 * code
 *  sang chữ hoa và thiết lập 
 * gpaLetter
 *  theo quy tắc:
 * 
 * 
 * 
 * 
 * A
 *  nếu 
 * gpa >= 3.6
 * 
 * 
 * B
 *  nếu 
 * gpa >= 3.2
 * 
 * 
 * C
 *  nếu 
 * gpa >= 2.5
 * 
 * 
 * D
 *  nếu 
 * gpa >= 2.0
 * 
 * 
 * F
 *  trong các trường hợp còn lại
 * 
 * 
 * 
 * 
 * d. Gửi lại đối tượng 
 * TCP.Student
 *  đã cập nhật lên server (giữ nguyên 
 * id
 *  và 
 * gpa
 * ).
 * 
 * 
 * e. Đóng kết nối và kết thúc chương trình.
 * 
 * 
 * Ví dụ:
 *  nhận 
 * id=2897; code=sv0uone; gpa=1.19; gpaLetter=""
 *  thì gửi lại 
 * id=2897; code=SV0UONE; gpa=1.19; gpaLetter=F
 * .
 */
public class TCP_1542_Chuan_Hoa_Ma_Sinh_Vien_Va_Xep_Loai_GPA {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "Q1542";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeObject(req);
            out.flush();

            // b. Nhận đối tượng từ server
            Student o = (Student) in.readObject();

            // c. Xử lý dữ liệu
            o.code = o.code.toUpperCase();
            float g = o.gpa;
            if (g >= 3.6f) o.gpaLetter = "A";
            else if (g >= 3.2f) o.gpaLetter = "B";
            else if (g >= 2.5f) o.gpaLetter = "C";
            else if (g >= 2.0f) o.gpaLetter = "D";
            else o.gpaLetter = "F";

            // d. Gửi lại đối tượng đã cập nhật
            out.writeObject(o);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
