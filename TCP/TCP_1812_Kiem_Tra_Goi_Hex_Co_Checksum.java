package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * [Mã câu hỏi: 1812] [TCP Byte Raw] Kiểm tra gói hex có checksum
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2206, sử dụng luồng byte dữ liệu (InputStream/OutputStream) để trao đổi thông tin.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 *  qua 
 * OutputStream
 * . Ví dụ: 
 * B21DCCN001;A1B2C3D4
 * .
 * 
 * 
 * b. Nhận từ server nhiều gói hex, các gói phân tách bởi ký tự 
 * |
 * , mỗi gói gồm các byte hex phân tách bởi dấu 
 * -
 * ; byte cuối là checksum. Ví dụ: 
 * 0A-1F-29|10-20-31
 * .
 * 
 * 
 * c. Một gói hợp lệ khi tổng các byte payload trước byte cuối, lấy modulo 256, bằng checksum của gói.
 * 
 * 
 * d. Gửi số gói hợp lệ và tổng số byte payload của các gói hợp lệ theo định dạng 
 * valid=<n>;payloadBytes=<m>
 * . Ví dụ: 
 * valid=1;payloadBytes=2
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1812_Kiem_Tra_Goi_Hex_Co_Checksum {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "Q1812";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.write(req.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // b. Nhận dữ liệu
            byte[] buffer = new byte[4096];
            int bytesRead = in.read(buffer);
            if (bytesRead > 0) {
                String res = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();

                // c. Xử lý dữ liệu
                int valid = 0;
                int payloadBytes = 0;
                for (String pkt : res.split("\\|")) {
                    pkt = pkt.trim();
                    if (pkt.isEmpty()) continue;
                    String[] bytes = pkt.split("-");
                    if (bytes.length < 2) continue;
                    int sum = 0;
                    for (int i = 0; i < bytes.length - 1; i++) {
                        sum += Integer.parseInt(bytes[i].trim(), 16);
                    }
                    int checksum = Integer.parseInt(bytes[bytes.length - 1].trim(), 16);
                    if (sum % 256 == checksum) {
                        valid++;
                        payloadBytes += bytes.length - 1;
                    }
                }
                String ans = "valid=" + valid + ";payloadBytes=" + payloadBytes;

                // d. Gửi kết quả
                out.write(ans.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
