package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1622] [UDP String] Che dữ liệu nhạy cảm
 * Loại: UDP_STRING
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2208 theo cơ chế hai pha.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi datagram đầu tiên chứa chuỗi theo định dạng 
 * ;studentCode;qCode
 * . Ví dụ: 
 * ;B15DCCN001;EE29C059
 * .
 * 
 * 
 * b. Nhận phản hồi từ server theo định dạng 
 * requestId;data
 * . Ví dụ: 
 * rBhDz2JJ;INFO user=minh email=minh0@example.com phone=0309453907 token=bDzj8Olxc9 action=refund
 * .
 * 
 * 
 * c. Che email, số điện thoại và token theo quy tắc 
 * [EMAIL]
 * , 
 * [PHONE]
 * , 
 * token=[TOKEN]
 * , giữ nguyên thứ tự các dòng log.
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;answer
 * . Ví dụ: 
 * rBhDz2JJ;INFO user=minh email=[EMAIL] phone=[PHONE] token=[TOKEN] action=refund
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1622_Che_Du_Lieu_Nhay_Cam {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1622";

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

            // c. Che email, số điện thoại và token
            String result = data.replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[EMAIL]")
                    .replaceAll("token=[^\\s|]+", "token=[TOKEN]")
                    .replaceAll("(?<!\\d)(\\+84|0)\\d{9}(?!\\d)", "[PHONE]");

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
