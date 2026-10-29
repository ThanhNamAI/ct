package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 998] UDP - String
 * Loại: UDP_STRING
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2208. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN000;XbYdNZ3
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;b1,b2
 * , trong đó:
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất.
 * b1 là số nhị phân thứ nhất
 * b2 là số nhị phân thứ hai.
 * 
 * 
 * Ví dụ:
 *  
 * requestId;0100011111001101,1101000111110101
 * 
 * 
 * c. Thực hiện tính tổng hai số nhị phân nhận được, chuyển về dạng thập phân và gửi lên server theo định dạng 
 * requestId;sum
 * 
 * 
 * Kết quả: requestId;72130
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_998_Tong_hai_so_nhi_phan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-998";

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

            // c. Cộng hai số nhị phân và đổi sang thập phân
            String[] bin = data.split(",");
            java.math.BigInteger sumBin = new java.math.BigInteger(bin[0].trim(), 2).add(new java.math.BigInteger(bin[1].trim(), 2));
            String result = sumBin.toString();

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
