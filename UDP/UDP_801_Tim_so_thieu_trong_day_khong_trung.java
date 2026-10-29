package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 801] UDP - Data Type
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
 * ;B15DCCN001;371EA16D
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId; n; A1,A2,...An
 * , với
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất
 * n là một số ngẫu nhiên nhỏ hơn 100.
 * A1, A2 ... Am với m <= n là các giá trị nguyên liên tiếp, nhỏ hơn hoặc bằng n và không trùng nhau.
 * 
 * 
 * Ví dụ:
 *  
 * requestId;10;2,3,5,6,9
 * 
 * 
 * c. Tìm kiếm các giá trị còn thiếu và gửi lên server theo định dạng 
 * requestId;B1,B2,...,Bm
 * 
 * 
 * Ví dụ:
 *  
 * requestId;1,4,7,8,10
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_801_Tim_so_thieu_trong_day_khong_trung {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-801";

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
            // Tìm các giá trị còn thiếu trong dãy 1..n
            String[] subParts = data.split(";", 2);
            int n = Integer.parseInt(subParts[0].trim());
            String[] numStrs = subParts[1].split(",");
            Set<Integer> present = new HashSet<>();
            for (String s : numStrs) present.add(Integer.parseInt(s.trim()));
            List<String> missing = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                if (!present.contains(i)) missing.add(String.valueOf(i));
            }
            result = String.join(",", missing);

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
