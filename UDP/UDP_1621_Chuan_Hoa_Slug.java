package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 1621] [UDP String] Chuẩn hóa slug
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
 * ;B15DCCN001;EE29C059
 * .
 * 
 * 
 * b. Nhận phản hồi từ server theo định dạng 
 * requestId;data
 * . Ví dụ: 
 * 7Brz6QQA;REFUND ticket Payment Payment customer Payment customer ticket REFUND customer shipping shipping Payment.
 * .
 * 
 * 
 * c. Chuyển chuỗi về chữ thường, loại bỏ dấu câu, gom nhiều khoảng trắng thành một khoảng trắng và thay khoảng trắng bằng dấu gạch ngang.
 * 
 * 
 * d. Gửi datagram nộp kết quả theo định dạng 
 * requestId;answer
 * . Ví dụ: 
 * 7Brz6QQA;refund-ticket-payment-payment-customer-payment-customer-ticket-refund-customer-shipping-shipping-payment
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class UDP_1621_Chuan_Hoa_Slug {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1621";

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

            // c. Chuẩn hóa slug
            String result = data.toLowerCase().replaceAll("[^\\p{L}\\p{N}\\s]", "").trim().replaceAll("\\s+", "-");

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
