package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 932] UDP - String
 * Loại: UDP_STRING
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản dưới đây:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;5B35BCC1
 * 
 * 
 * b. Nhận thông điệp từ server theo định dạng 
 * requestId;data
 * 
 * 
 * requestId là một chuỗi ngẫu nhiên duy nhất
 * data là chuỗi dữ liệu cần xử lý
 * 
 * 
 * c. Xử lý chuẩn hóa chuỗi đã nhận thành theo nguyên tắc
 * 
 * 
 * Ký tự đầu tiên của từng từ trong chuỗi là in hoa
 * Các ký tự còn lại của chuỗi là in thường Gửi thông điệp chứa chuỗi đã được chuẩn hóa lên server theo định dạng 
 * requestId;data
 * 
 * 
 * d. Đóng socket và kết thúc chương trình
 */
public class UDP_932_Chuan_hoa_chu_hoa_dau_tu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-932";

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
            // Chuẩn hóa chuỗi (viết hoa chữ cái đầu mỗi từ)
            String[] words = data.trim().toLowerCase().split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (!words[i].isEmpty()) {
                    sb.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1));
                    if (i < words.length - 1) sb.append(" ");
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
