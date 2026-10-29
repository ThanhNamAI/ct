package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 991] UDP - Data Type
 * Loại: UDP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN011;A1F3D5B7
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;num
 * , với:
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất.
 * num là một số nguyên lớn.
 * 
 * 
 * c. Tính tổng các chữ số trong num và gửi lại tổng này về server theo định dạng 
 * requestId;sumDigits
 * .
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_991_Tong_cac_chu_so_so_lon {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-991";

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
            // Tổng các chữ số trong num
            int sum = 0;
            for (char c : data.trim().toCharArray()) {
                if (Character.isDigit(c)) sum += (c - '0');
            }
            result = String.valueOf(sum);

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
