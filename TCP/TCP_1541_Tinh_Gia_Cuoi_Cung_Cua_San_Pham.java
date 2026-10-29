package TCP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * [Mã câu hỏi: 1541] [TCP Object Stream] Tính giá cuối cùng của sản phẩm
 * Loại: TCP_OBJECT
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209, sử dụng ObjectInputStream và ObjectOutputStream để trao đổi đối tượng Java.
 * Lớp dữ liệu trao đổi
 * Tạo lớp dưới đây trong project client, đúng package, tên lớp, serialVersionUID, tên và kiểu của từng thuộc tính. Nếu khác, server không đọc được đối tượng gửi lên.
 * package TCP;
 * 
 * public class Product implements java.io.Serializable {
 *     private static final long serialVersionUID = 20231107L;
 * 
 *     private int id;
 *     private String name;
 *     private double price;
 *     private int discount;
 * 
 *     public Product() { }
 *     public Product(int id, String name, double price, int discount) { /* gán 4 thuộc tính * / }
 *     // getter/setter cho tất cả thuộc tính
 * }
 * 
 * Yêu cầu
 * a. Gửi một đối tượng String chứa mã sinh viên và mã câu hỏi theo định dạng studentCode;qCode bằng ObjectOutputStream. Ví dụ: B15DCCN999;5AD2B818.
 * b. Nhận từ server một đối tượng TCP.Product đã có sẵn id, name, price và discount.
 * c. Tính lại price theo công thức round2(price * (100 - discount) / 100). Giữ nguyên id, name và discount.
 * d. Gửi lại đối tượng TCP.Product đã cập nhật price lên server.
 * e. Đóng kết nối và kết thúc chương trình.
 * Ví dụ:
 *  nhận id=9817; name=product-74SD; price=786.64; discount=32 thì gửi lại id=9817; name=product-74SD; price=534.92; discount=32.
 */
public class TCP_1541_Tinh_Gia_Cuoi_Cung_Cua_San_Pham {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "Q1541";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeObject(req);
            out.flush();

            // b. Nhận đối tượng từ server
            Product o = (Product) in.readObject();

            // c. Xử lý dữ liệu
            o.price = Math.round(o.price * (100 - o.discount) / 100 * 100) / 100.0;

            // d. Gửi lại đối tượng đã cập nhật
            out.writeObject(o);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
