package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 938] UDP - Object
 * Loại: UDP_OBJECT
 *
 * Đề bài:
 * Thông tin khách hàng được yêu cầu thay đổi định dạng lại cho phù hợp với khu vực, cụ thể:
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
 * b. Ngày sinh của khách hàng đang ở dạng mm-dd-yyyy, cần được chuyển thành định dạng dd/mm/yyyy.
 * 
 * 
 * Ví dụ:
 *  
 * 10-11-2012 -> 11/10/2012
 * 
 * 
 * c. Tài khoản khách hàng được tạo từ các chữ cái in thường được sinh tự động từ họ tên khách hàng.
 * 
 * 
 * Ví dụ:
 *  
 * nguyen van hai duong -> nvhduong
 * 
 * 
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. Yêu cầu là xây dựng một chương trình client giao tiếp với server theo mô tả sau:
 * 
 * 
 * a. Đối tượng trao đổi là thể hiện của lớp UDP.Customer được mô tả như sau
 * 
 * 
 * Tên đầy đủ của lớp:
 *  UDP.Customer
 * Các thuộc tính:
 *  id String, code String, name String, , dayOfBirth String, userName String
 * Một Hàm khởi tạo với đầy đủ các thuộc tính được liệt kê ở trên
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20151107;
 * 
 * 
 * b. Client giao tiếp với server theo các bước
 * 
 * 
 * Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;EE29C059
 * 
 * 
 * Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Customer từ server. Trong đó, các thuộc tính id, code, name,dayOfBirth đã được thiết lập sẵn.
 * Yêu cầu thay đổi thông tin các thuộc tính như yêu cầu ở trên và gửi lại đối tượng khách hàng đã được sửa đổi lên server với cấu trúc: 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Customer đã được sửa đổi.
 * Đóng socket và kết thúc chương trình.
 */
public class UDP_938_Chuan_hoa_khach_hang_ten_ngay_sinh_tai_khoan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-938";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // a. Gửi thông điệp
            String req = ";" + sCode + ";" + qCode;
            byte[] reqData = req.getBytes();
            socket.send(new DatagramPacket(reqData, reqData.length, ip, port));

            // b. Nhận 8 byte requestId + object
            byte[] buf = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            byte[] reqIdBytes = Arrays.copyOfRange(packet.getData(), 0, 8);
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 8, packet.getLength() - 8);
            ObjectInputStream ois = new ObjectInputStream(bais);

            // c. Xử lý object
            Customer c = (Customer) ois.readObject();
            String[] cw = c.getName().trim().toLowerCase().split("\\s+");
            // a. Chuẩn hóa tên: DUONG, Nguyen Van Hai
            StringBuilder rest = new StringBuilder();
            StringBuilder user = new StringBuilder();
            for (int i = 0; i < cw.length - 1; i++) {
                if (rest.length() > 0) rest.append(' ');
                rest.append(Character.toUpperCase(cw[i].charAt(0))).append(cw[i].substring(1));
                user.append(cw[i].charAt(0));
            }
            c.setName(cw[cw.length - 1].toUpperCase() + (rest.length() > 0 ? ", " + rest : ""));
            // c. Tài khoản: nguyen van hai duong -> nvhduong
            user.append(cw[cw.length - 1]);
            c.setUserName(user.toString());
            // b. Ngày sinh mm-dd-yyyy -> dd/mm/yyyy
            String[] dp = c.getDayOfBirth().trim().split("-");
            if (dp.length == 3) c.setDayOfBirth(dp[1] + "/" + dp[0] + "/" + dp[2]);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(reqIdBytes);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(c);
            oos.flush();

            // d. Gửi lại kết quả
            byte[] ansData = baos.toByteArray();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
