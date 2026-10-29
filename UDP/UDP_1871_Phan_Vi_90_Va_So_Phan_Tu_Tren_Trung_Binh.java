package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1871] [UDP Data] Phân vị 90 và số phần tử trên trung bình
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
 * Ab12Cd34;10,20,30,40,100
 * .
 * 
 * 
 * c. Sắp xếp dãy tăng dần và lấy phân vị 90 tại vị trí 
 * ceil(n * 0.9) - 1
 * ; đồng thời đếm số phần tử lớn hơn giá trị trung bình của dãy ban đầu.
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;p90=<giá_trị>;aboveAvg=<n>
 * . Ví dụ: 
 * Ab12Cd34;p90=100;aboveAvg=1
 * .
 * 
 * 
 * e. Đóng socket hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1871_Phan_Vi_90_Va_So_Phan_Tu_Tren_Trung_Binh {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1871";

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

            // c. Phân vị 90 và số phần tử lớn hơn trung bình
            String[] numStrs = data.split(",");
            long[] arr = new long[numStrs.length];
            long sum = 0;
            for (int i = 0; i < numStrs.length; i++) {
                arr[i] = Long.parseLong(numStrs[i].trim());
                sum += arr[i];
            }
            int aboveAvg = 0;
            for (long v : arr) if (v * arr.length > sum) aboveAvg++;
            Arrays.sort(arr);
            long p90 = arr[(int) Math.ceil(arr.length * 0.9) - 1];
            String result = "p90=" + p90 + ";aboveAvg=" + aboveAvg;

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
