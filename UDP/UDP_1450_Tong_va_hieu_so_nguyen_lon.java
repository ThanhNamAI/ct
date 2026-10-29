package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1450] UDP - Data Type
 * Loại: UDP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN011;A1F3D5B7
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;num1;num2
 * , với:
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất.
 * num1, num2 là hai số nguyên không âm rất lớn (num1 có 25–50 chữ số, num2 có 20–40 chữ số, có thể bắt đầu bằng chữ số 0).
 * 
 * 
 * c. Tính tổng num1 + num2 và hiệu num1 - num2, gửi về server theo định dạng 
 * requestId;sum,difference
 *  (tổng và hiệu phân tách bởi dấu phẩy, viết như số nguyên thông thường không có chữ số 0 thừa ở đầu; hiệu có thể âm).
 * 
 * 
 * Ví dụ:
 *  nhận 
 * a1b2c3d4;123456789012345678901234567;98765432109876543210987654321
 *  thì gửi 
 * a1b2c3d4;98888888898888888889888888888,-98641975320864197532086419754
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_1450_Tong_va_hieu_so_nguyen_lon {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1450";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // a. Gửi thông điệp
            String req = ";" + sCode + ";" + qCode;
            byte[] reqData = req.getBytes();
            socket.send(new DatagramPacket(reqData, reqData.length, ip, port));

            // b. Nhận phản hồi
            byte[] buf = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String res = new String(packet.getData(), 0, packet.getLength()).trim();
            String[] parts = res.split(";", 2);
            String requestId = parts[0];
            String data = parts.length > 1 ? parts[1] : "";

            // c. Tính tổng và hiệu hai số nguyên lớn
            String[] nums = data.split(";");
            java.math.BigInteger n1 = new java.math.BigInteger(nums[0].trim());
            java.math.BigInteger n2 = new java.math.BigInteger(nums[1].trim());
            String result = n1.add(n2) + "," + n1.subtract(n2);

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
