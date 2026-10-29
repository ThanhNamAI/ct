package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1882] [UDP String] Che log và thống kê mức độ
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
 * , trong đó data gồm nhiều dòng log nối bằng 
 * ||
 * . Ví dụ: 
 * Wx12Yz34;ERROR user=a email=a@example.com phone=0123456789 token=abc
 * .
 * 
 * 
 * c. Che email, số điện thoại và token; đồng thời đếm số dòng bắt đầu bằng 
 * ERROR
 * , 
 * INFO
 * , 
 * WARN
 * .
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;maskedLog##ERROR=n;INFO=n;WARN=n
 * . Ví dụ: 
 * Wx12Yz34;ERROR user=a email=[EMAIL] phone=[PHONE] token=[TOKEN]##ERROR=1;INFO=0;WARN=0
 * .
 * 
 * 
 * e. Đóng socket hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1882_Che_Log_Va_Thong_Ke_Muc_Do {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1882";

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

            // c. Che log và đếm số dòng theo mức độ
            String[] lines = data.split("\\|\\|");
            int errorCount = 0, infoCount = 0, warnCount = 0;
            List<String> maskedLines = new ArrayList<>();
            for (String line : lines) {
                String t = line.trim();
                if (t.startsWith("ERROR")) errorCount++;
                else if (t.startsWith("INFO")) infoCount++;
                else if (t.startsWith("WARN")) warnCount++;
                maskedLines.add(line.replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[EMAIL]")
                        .replaceAll("token=[^\\s|]+", "token=[TOKEN]")
                        .replaceAll("(?<!\\d)(\\+84|0)\\d{9}(?!\\d)", "[PHONE]"));
            }
            String result = String.join("||", maskedLines) + "##ERROR=" + errorCount + ";INFO=" + infoCount + ";WARN=" + warnCount;

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
