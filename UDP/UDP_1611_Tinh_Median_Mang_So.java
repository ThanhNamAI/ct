package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1611] [UDP Data] Tính median mảng số
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
 * SjkZFpBk;268,125,584,33,685,530,661,168,589,515,142,619,286,24,241
 * .
 * 
 * 
 * c. Sắp xếp dãy số tăng dần và tìm giá trị trung vị. Nếu số lượng phần tử chẵn, lấy trung bình hai phần tử giữa và làm tròn 02 chữ số thập phân.
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;answer
 * . Ví dụ: 
 * SjkZFpBk;286
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1611_Tinh_Median_Mang_So {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1611";

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

            // c. Tính median của dãy số
            String[] numStrs = data.split(",");
            List<Double> list = new ArrayList<>();
            for (String s : numStrs) list.add(Double.parseDouble(s.trim()));
            Collections.sort(list);
            int sz = list.size();
            String result;
            if (sz % 2 == 1) {
                result = String.valueOf(Math.round(list.get(sz / 2)));
            } else {
                result = String.format(Locale.US, "%.2f", (list.get(sz / 2 - 1) + list.get(sz / 2)) / 2.0);
            }

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
