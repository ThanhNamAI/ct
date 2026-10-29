package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1612] [UDP Data] Tính weighted checksum
 * Loại: UDP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207 theo cơ chế hai pha.
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
 * NjMPqAdf;264,340,630,733,395,954,431,631,650,412
 * .
 * 
 * 
 * c. Tính checksum theo công thức 
 * sum((i + 1) * value[i]) mod 100000
 * , với i bắt đầu từ 0.
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;answer
 * . Ví dụ: 
 * NjMPqAdf;31500
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1612_Tinh_Weighted_Checksum {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1612";

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

            // c. Tính weighted checksum: sum((i + 1) * value[i]) mod 100000
            String[] numStrs = data.split(",");
            long checksum = 0;
            for (int i = 0; i < numStrs.length; i++) {
                checksum += (long) (i + 1) * Long.parseLong(numStrs[i].trim());
            }
            String result = String.valueOf(checksum % 100000);

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
