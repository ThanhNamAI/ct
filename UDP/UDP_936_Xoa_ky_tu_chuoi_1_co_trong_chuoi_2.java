package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 936] UDP - String
 * Loại: UDP_STRING
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức UDP tại cổng 2208 . Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản dưới đây:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * ."
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;B34D51E0
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;str1;str2
 * .
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất
 * str1,str2 lần lượt là chuỗi thứ nhất và chuỗi thứ hai
 * 
 * 
 * c. Loại bỏ các ký tự trong chuỗi thứ nhất mà xuất hiện trong chuỗi thứ hai, giữ nguyên thứ tự xuất hiện. Gửi thông điệp là một chuỗi lên server theo định dạng 
 * requestId;strOutput
 * , trong đó chuỗi strOutput là chuỗi đã được xử lý ở trên.
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_936_Xoa_ky_tu_chuoi_1_co_trong_chuoi_2 {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-936";

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

            // c. Loại bỏ các ký tự của chuỗi thứ nhất xuất hiện trong chuỗi thứ hai
            String[] strs = data.split(";", 2);
            Set<Character> set2 = new HashSet<>();
            if (strs.length > 1) for (char ch : strs[1].toCharArray()) set2.add(ch);
            StringBuilder sb = new StringBuilder();
            for (char ch : strs[0].toCharArray()) {
                if (!set2.contains(ch)) sb.append(ch);
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
