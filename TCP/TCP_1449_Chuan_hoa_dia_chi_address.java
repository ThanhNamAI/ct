package TCP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * [Mã câu hỏi: 1449] TCP - Object Stream
 * Loại: TCP_OBJECT
 *
 * Đề bài:
 * Thông tin địa chỉ nhận từ server đang sai định dạng, cần được chuẩn hóa lại, cụ thể:
 * 
 * 
 * a. addressLine: loại bỏ mọi ký tự không phải chữ cái, chữ số hoặc khoảng trắng; gộp các khoảng trắng liên tiếp thành một và bỏ khoảng trắng ở hai đầu; mỗi từ viết hoa chữ cái đầu, các chữ còn lại viết thường.
 * 
 * 
 * Ví dụ:
 *  
 * 125 NGUYEN van cu#48 tran   THI MINH -> 125 Nguyen Van Cu48 Tran Thi Minh
 * 
 * 
 * b. postalCode: chỉ giữ lại chữ số và dấu gạch ngang.
 * 
 * 
 * Ví dụ:
 *  
 * 0931?-5 -> 0931-5
 * 
 * 
 * c. Các thuộc tính id, code, city giữ nguyên.
 * 
 * 
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng đối tượng (ObjectInputStream / ObjectOutputStream) thực hiện gửi/nhận đối tượng địa chỉ và chuẩn hóa. Cụ thể:
 * 
 * 
 * a. Đối tượng trao đổi là thể hiện của lớp Address được mô tả như sau
 * 
 * 
 * Tên đầy đủ của lớp:
 *  TCP.Address
 * Các thuộc tính:
 *  id int, code String, addressLine String, city String, postalCode String
 * Hàm khởi tạo đầy đủ các thuộc tính được liệt kê ở trên
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20180801L
 * 
 * 
 * b. Tương tác với server theo kịch bản dưới đây:
 * 
 * 
 * 1) Gửi đối tượng là một chuỗi gồm mã sinh viên và mã câu hỏi ở định dạng 
 * studentCode;qCode
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;F2DA54F3
 * 
 * 
 * 2) Nhận một đối tượng là thể hiện của lớp Address từ server
 * 
 * 
 * 3) Chuẩn hóa addressLine và postalCode theo các yêu cầu ở trên, gán vào các thuộc tính tương ứng rồi gửi đối tượng đã sửa lên server
 * 
 * 
 * 4) Đóng socket và kết thúc chương trình.
 */
public class TCP_1449_Chuan_hoa_dia_chi_address {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1449";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeObject(req);
            out.flush();

            // b. Nhận đối tượng từ server
            Address o = (Address) in.readObject();

            // c. Xử lý dữ liệu
            StringBuilder sb = new StringBuilder();
            String line = o.addressLine == null ? "" : o.addressLine.replaceAll("[^\\p{L}\\p{N}\\s]", "").replaceAll("\\s+", " ").trim();
            for (String w : line.split(" ")) {
                if (w.isEmpty()) continue;
                if (sb.length() > 0) sb.append(" ");
                sb.append(w.substring(0, 1).toUpperCase()).append(w.substring(1).toLowerCase());
            }
            o.addressLine = sb.toString();
            o.postalCode = o.postalCode == null ? "" : o.postalCode.replaceAll("[^0-9-]", "");

            // d. Gửi lại đối tượng đã cập nhật
            out.writeObject(o);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
