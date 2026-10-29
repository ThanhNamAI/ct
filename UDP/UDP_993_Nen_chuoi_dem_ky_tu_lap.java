package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 993] UDP - String
 * Loại: UDP_STRING
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2208. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:
 * 
 * 
 * a. Gửi một thông điệp chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;9F8C2D3A
 * 
 * 
 * b. Nhận một thông điệp từ server theo định dạng 
 * requestId;data
 * , với:
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất.
 * data là một chuỗi ký tự liên tiếp cần xử lý.
 * 
 * 
 * Ví dụ:
 *  
 * requestId;aaabbbccdaa
 * 
 * 
 * c. Xử lý chuỗi bằng cách duyệt lần lượt và đếm số lần xuất hiện của từng ký tự. Gửi kết quả về server theo định dạng: 
 * requestId;processedData
 * 
 * 
 * Ví dụ:
 *  Với chuỗi 
 * aaazbbbccdaaz
 * , kết quả sẽ là: 
 * requestId;5a2z3b2c1d
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_993_Nen_chuoi_dem_ky_tu_lap {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-993";

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

            // c. Đếm số lần xuất hiện của từng ký tự (theo thứ tự xuất hiện đầu tiên)
            Map<Character, Integer> counts = new LinkedHashMap<>();
            for (char ch : data.toCharArray()) counts.put(ch, counts.getOrDefault(ch, 0) + 1);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Character, Integer> e : counts.entrySet()) {
                sb.append(e.getValue()).append(e.getKey());
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
