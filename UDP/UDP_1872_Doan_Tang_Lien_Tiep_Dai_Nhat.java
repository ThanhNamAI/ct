package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1872] [UDP Data] Đoạn tăng liên tiếp dài nhất
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
 * ;B21DCCN001;EE29C059
 * .
 * 
 * 
 * b. Nhận phản hồi từ server theo định dạng 
 * requestId;data
 * , trong đó data là dãy số nguyên phân tách bằng dấu phẩy. Ví dụ: 
 * Ef56Gh78;4,9,12,3,8
 * .
 * 
 * 
 * c. Tìm đoạn con liên tiếp tăng nghiêm ngặt dài nhất. Nếu có nhiều đoạn cùng độ dài, chọn đoạn xuất hiện trước.
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;segment=<csv>;length=<n>;sum=<tổng>
 * . Ví dụ: 
 * Ef56Gh78;segment=4,9,12;length=3;sum=25
 * .
 * 
 * 
 * e. Đóng socket hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1872_Doan_Tang_Lien_Tiep_Dai_Nhat {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1872";

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

            // c. Tìm đoạn con tăng nghiêm ngặt dài nhất (đoạn xuất hiện trước nếu bằng nhau)
            String[] numStrs = data.split(",");
            int[] arr = new int[numStrs.length];
            for (int i = 0; i < numStrs.length; i++) arr[i] = Integer.parseInt(numStrs[i].trim());
            int bestLen = 1, curLen = 1, bestStart = 0, curStart = 0;
            for (int i = 1; i < arr.length; i++) {
                if (arr[i] > arr[i - 1]) {
                    curLen++;
                    if (curLen > bestLen) {
                        bestLen = curLen;
                        bestStart = curStart;
                    }
                } else {
                    curLen = 1;
                    curStart = i;
                }
            }
            List<String> resList = new ArrayList<>();
            long segSum = 0;
            for (int i = bestStart; i < bestStart + bestLen; i++) {
                resList.add(String.valueOf(arr[i]));
                segSum += arr[i];
            }
            String result = "segment=" + String.join(",", resList) + ";length=" + bestLen + ";sum=" + segSum;

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
