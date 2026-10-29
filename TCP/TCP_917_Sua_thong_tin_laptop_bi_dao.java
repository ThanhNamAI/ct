package TCP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * [Mã câu hỏi: 917] TCP - Object Stream
 * Loại: TCP_OBJECT
 *
 * Đề bài:
 * Thông tin sản phẩm vì một lý do nào đó đã bị sửa đổi thành không đúng, cụ thể:
 * 
 * 
 * a) Tên sản phẩm bị đổi ngược từ đầu tiên và từ cuối cùng, ví dụ: “lenovo thinkpad T520” bị chuyển thành “T520 thinkpad lenovo”
 * 
 * 
 * b) Số lượng sản phẩm cũng bị đảo ngược giá trị, ví dụ từ 9981 thành 1899
 * 
 * 
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng đối tượng (ObjectInputStream / ObjectOutputStream) để gửi/nhận và sửa các thông tin bị sai của sản phẩm. Chi tiết dưới đây:
 * 
 * 
 * a) Đối tượng trao đổi là thể hiện của lớp Laptop được mô tả như sau
 * 
 * 
 * Tên đầy đủ của lớp:
 *  TCP.Laptop
 * Các thuộc tính:
 *  id int, code String, name String, quantity int
 * Hàm khởi tạo đầy đủ các thuộc tính được liệt kê ở trên
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20150711L;
 * 
 * 
 * b) Tương tác với server theo kịch bản
 * 
 * 
 * 1) Gửi đối tượng là chuỗi chứa mã sinh viên và mã câu hỏi với định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;5AD2B818
 * 
 * 
 * 2) Nhận một đối tượng là thể hiện của lớp Laptop từ server
 * 
 * 
 * 3) Sửa các thông tin sai của sản phẩm về tên và số lượng.
 * 
 * 
 * Gửi đối tượng vừa được sửa sai lên server
 * 
 * 
 * 4) Đóng socket và kết thúc chương trình.
 */
public class TCP_917_Sua_thong_tin_laptop_bi_dao {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-917";

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
            Laptop laptop = (Laptop) in.readObject();
            String[] words = laptop.name.trim().split("\\s+");
            if (words.length > 1) {
                String temp = words[0];
                words[0] = words[words.length - 1];
                words[words.length - 1] = temp;
                laptop.name = String.join(" ", words);
            }
            laptop.quantity = Integer.parseInt(new StringBuilder(String.valueOf(laptop.quantity)).reverse().toString());
            out.writeObject(laptop);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
