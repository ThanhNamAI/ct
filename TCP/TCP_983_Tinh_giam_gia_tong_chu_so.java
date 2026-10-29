package TCP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * [Mã câu hỏi: 983] TCP - Object Stream
 * Loại: TCP_OBJECT
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng đối tượng (ObjectOutputStream/ObjectInputStream) theo kịch bản dưới đây:
 * 
 * 
 * Biết lớp TCP.Product gồm các thuộc tính (id int, name String, price double, int discount) và private static final long serialVersionUID = 20231107;
 * 
 * 
 * a. Gửi đối tượng là một chuỗi gồm mã sinh viên và mã câu hỏi với định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;1E08CA31
 * 
 * 
 * b. Nhận một đối tượng là thể hiện của lớp TCP.Product từ server.
 * 
 * 
 * c. Tính toán giá trị giảm giá theo price theo nguyên tắc: Giá trị giảm giá (discount) bằng tổng các chữ số trong phần nguyên của giá sản phẩm (price). Thực hiện gán giá trị cho thuộc tính discount và gửi lên đối tượng nhận được lên server.
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_983_Tinh_giam_gia_tong_chu_so {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-983";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // đẩy header stream đi trước để tránh deadlock khi server tạo ObjectInputStream trước
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeObject(req);
            out.flush();

            // b. Nhận và c. Xử lý
            Product p = (Product) in.readObject();
            int sumDigits = 0;
            for (char ch : String.valueOf((long) p.price).toCharArray()) {
                if (Character.isDigit(ch)) sumDigits += ch - '0';
            }
            p.discount = sumDigits;
            out.writeObject(p);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
