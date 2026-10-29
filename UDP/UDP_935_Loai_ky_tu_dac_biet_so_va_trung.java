package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 935] UDP - String
 * Loại: UDP_STRING
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208 . Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản dưới đây:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;06D6800D
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;strInput
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất
 * strInput là chuỗi thông điệp cần xử lý
 * 
 * 
 * c. Thực hiện loại bỏ ký tự đặc biệt, số, ký tự trùng và giữ nguyên thứ tự xuất hiện của chúng. Gửi thông điệp lên server theo định dạng 
 * requestId;strOutput
 * , trong đó strOutput là chuỗi đã được xử lý ở trên
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_935_Loai_ky_tu_dac_biet_so_va_trung {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-935";

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

            // c. Xử lý
            String result = "";
            // Loại bỏ ký tự đặc biệt, số, ký tự trùng giữ nguyên thứ tự
            StringBuilder sb = new StringBuilder();
            Set<Character> seen = new HashSet<>();
            for (char c : data.toCharArray()) {
                if (Character.isLetter(c) && !seen.contains(c)) {
                    seen.add(c);
                    sb.append(c);
                }
            }
            result = sb.toString();

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
