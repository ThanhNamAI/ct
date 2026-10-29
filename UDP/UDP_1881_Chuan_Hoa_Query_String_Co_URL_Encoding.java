package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1881] [UDP String] Chuẩn hóa query string có URL encoding
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
 * ;B21DCCN001;XbYdNZ3A
 * .
 * 
 * 
 * b. Nhận phản hồi từ server theo định dạng 
 * requestId;data
 * , trong đó data là query string. Ví dụ: 
 * Qr12St34;role=risk%20team&user=alice
 * .
 * 
 * 
 * c. Giải mã URL encoding cho key và value, sau đó sắp xếp key tăng dần theo thứ tự từ điển.
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;key=value;key=value
 * . Ví dụ: 
 * Qr12St34;role=risk team;user=alice
 * .
 * 
 * 
 * e. Đóng socket hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1881_Chuan_Hoa_Query_String_Co_URL_Encoding {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1881";

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

            // c. Giải mã URL encoding và sắp xếp key tăng dần
            String[] pairs = data.split("&");
            List<String[]> kvs = new ArrayList<>();
            for (String pair : pairs) {
                if (pair.isEmpty()) continue;
                String[] kv = pair.split("=", 2);
                String k = java.net.URLDecoder.decode(kv[0], "UTF-8");
                String v = kv.length > 1 ? java.net.URLDecoder.decode(kv[1], "UTF-8") : "";
                kvs.add(new String[]{k, v});
            }
            kvs.sort((x, y) -> x[0].compareTo(y[0]));
            List<String> list = new ArrayList<>();
            for (String[] kv : kvs) list.add(kv[0] + "=" + kv[1]);
            String result = String.join(";", list);

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
