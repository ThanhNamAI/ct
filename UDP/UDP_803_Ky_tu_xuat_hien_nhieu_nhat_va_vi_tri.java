package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 803] UDP - String
 * Loại: UDP_STRING
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208. Yêu cầu là xây dựng một chương trình client tương tác với server kịch bản dưới đây:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;EE29C059
 * 
 * 
 * b. Nhận thông điệp từ server theo định dạng 
 * requestId; data
 * 
 * 
 * requestId là một chuỗi ngẫu nhiên duy nhất
 * data là chuỗi dữ liệu đầu vào cần xử lý
 * 
 * 
 * Ví dụ:
 *  
 * requestId;Qnc8d5x78aldSGWWmaAAjyg3
 * 
 * 
 * c. Tìm kiếm ký tự xuất hiện nhiều nhất trong chuỗi và gửi lên server theo định dạng “requestId;ký tự xuất hiện nhiều nhất: các vị trí xuất hiện ký tự đó”
 * 
 * 
 * Ví dụ:
 *  
 * requestId;8:4,9,
 * 
 * 
 * d. Đóng socket và kết thúc chương trình
 */
public class UDP_803_Ky_tu_xuat_hien_nhieu_nhat_va_vi_tri {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-803";

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

            // c. Tìm ký tự xuất hiện nhiều nhất và các vị trí (đánh số từ 1) của nó
            Map<Character, Integer> counts = new LinkedHashMap<>();
            for (char ch : data.toCharArray()) counts.put(ch, counts.getOrDefault(ch, 0) + 1);
            char best = 0;
            int bestCount = 0;
            for (Map.Entry<Character, Integer> e : counts.entrySet()) {
                if (e.getValue() > bestCount) {
                    bestCount = e.getValue();
                    best = e.getKey();
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append(best).append(":");
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == best) sb.append(i + 1).append(",");
            }
            String result = sb.toString();

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
