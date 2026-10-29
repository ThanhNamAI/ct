package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 934] UDP - Data Type
 * Loại: UDP_DATA
 *
 * Đề bài:
 * Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s.
 * 
 * 
 * Ví dụ:
 *  với s = 3 thì ký tự 
 * A
 *  sẽ được thay thế bằng ký tự 
 * D
 * 
 * 
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu xây dựng chương trình client trao đổi thông tin với server theo kịch bản mô tả dưới đây:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;825EE3A7
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;strEncode;s
 * .
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất
 * strEncode là chuỗi thông điệp bị mã hóa
 * s là số nguyên chứa giá trị độ dịch của mã
 * 
 * 
 * c. Giải mã tìm thông điệp ban đầu và gửi lên server theo định dạng 
 * requestId;strDecode
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_934_Giai_ma_caesar {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-934";

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

            // c. Giải mã Caesar = dịch ngược s đoạn so với lúc mã hóa (dịch lùi -s). Nếu máy chấm đòi dịch tiến thì đổi "- shift" thành "+ shift"
            String[] cp = data.split(";");
            String enc = cp[0];
            int shift = Integer.parseInt(cp[1].trim());
            StringBuilder sb = new StringBuilder();
            for (char ch : enc.toCharArray()) {
                if (ch >= 'A' && ch <= 'Z') {
                    sb.append((char) ('A' + ((ch - 'A' - shift) % 26 + 26) % 26));
                } else if (ch >= 'a' && ch <= 'z') {
                    sb.append((char) ('a' + ((ch - 'a' - shift) % 26 + 26) % 26));
                } else {
                    sb.append(ch);
                }
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
