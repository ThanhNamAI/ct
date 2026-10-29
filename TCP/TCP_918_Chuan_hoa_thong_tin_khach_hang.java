package TCP;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * [Mã câu hỏi: 918] TCP - Object Stream
 * Loại: TCP_OBJECT
 *
 * Đề bài:
 * Thông tin khách hàng cần thay đổi định dạng lại cho phù hợp với khu vực, cụ thể:
 * 
 * 
 * a. Tên khách hàng cần được chuẩn hóa theo định dạng mới.
 * 
 * 
 * Ví dụ:
 *  
 * nguyen van hai duong -> DUONG, Nguyen Van Hai
 * 
 * 
 * b. Ngày sinh của khách hàng hiện đang ở dạng mm-dd-yyyy, cần được chuyển thành định dạng dd/mm/yyyy.
 * 
 * 
 * Ví dụ:
 *  
 * 10-11-2012 -> 11/10/2012
 * 
 * 
 * c. Tài khoản khách hàng là các chữ cái in thường được sinh tự động từ họ tên khách hàng.
 * 
 * 
 * Ví dụ:
 *  
 * nguyen van hai duong -> nvhduong
 * 
 * 
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2209 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng đối tượng (ObjectInputStream / ObjectOutputStream) thực hiện gửi/nhận đối tượng khách hàng và chuẩn hóa. Cụ thể:
 * 
 * 
 * a. Đối tượng trao đổi là thể hiện của lớp Customer được mô tả như sau
 * 
 * 
 * Tên đầy đủ của lớp:
 *  TCP.Customer
 * Các thuộc tính:
 *  id int, code String, name String, dayOfBirth String, userName String
 * Hàm khởi tạo đầy đủ các thuộc tính được liệt kê ở trên
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20170711L;
 * 
 * 
 * b. Tương tác với server theo kịch bản dưới đây:
 * 
 * 
 * 1) Gửi đối tượng là một chuỗi gồm mã sinh viên và mã câu hỏi ở định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;F2DA54F3
 * 
 * 
 * 2) Nhận một đối tượng là thể hiện của lớp Customer từ server với các thông tin đã được thiết lập
 * 
 * 
 * 3) Thay đổi định dạng theo các yêu cầu ở trên và gán vào các thuộc tính tương ứng.
 * 
 * 
 * Gửi đối tượng đã được sửa đổi lên server
 * 
 * 
 * 4) Đóng socket và kết thúc chương trình.
 */
public class TCP_918_Chuan_hoa_thong_tin_khach_hang {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-918";

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
            Customer c = (Customer) in.readObject();
            String[] parts = c.name.trim().toLowerCase().split("\\s+");
            StringBuilder formattedName = new StringBuilder();
            StringBuilder user = new StringBuilder();
            String last = parts[parts.length - 1];
            // Tên: HỌ_CUỐI_IN_HOA, Các Từ Còn Lại Viết Hoa Chữ Đầu
            StringBuilder rest = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (parts[i].isEmpty()) continue;
                rest.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1)).append(" ");
                user.append(parts[i].charAt(0));
            }
            formattedName.append(last.toUpperCase());
            if (rest.length() > 0) formattedName.append(", ").append(rest.toString().trim());
            user.append(last);
            c.name = formattedName.toString();
            c.userName = user.toString();
            // Ngày sinh mm-dd-yyyy -> dd/mm/yyyy
            String[] d = c.dayOfBirth.trim().split("-");
            c.dayOfBirth = d[1] + "/" + d[0] + "/" + d[2];
            out.writeObject(c);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
