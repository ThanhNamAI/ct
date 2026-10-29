package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * [Mã câu hỏi: 1511] [TCP Byte Raw] Checksum byte payload
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2206, sử dụng luồng byte dữ liệu (InputStream/OutputStream) để trao đổi thông tin.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng "studentCode;qCode" qua OutputStream. Ví dụ: "B15DCCN999;1D25ED92".
 * 
 * 
 * b. Nhận từ server một chuỗi các giá trị byte không dấu, mỗi giá trị nằm trong khoảng 0..255 và được phân tách bởi dấu phẩy. Ví dụ: 12,45,255,8.
 * 
 * 
 * c. Tính tổng tất cả các giá trị byte nhận được theo modulo 256.
 * 
 * 
 * d. Gửi kết quả checksum lên server dưới dạng một chuỗi số nguyên. Ví dụ: 64.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 75,68,12,158,115,63,175,113,189,91,158,56,29,215,122
 *  thì dữ liệu nộp lại là 
 * 103
 * .
 */
public class TCP_1511_Checksum_Byte_Payload {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "Q1511";

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
                int sum = 0;
                for (String p : res.split(",")) {
                    if (p.trim().isEmpty()) continue;
                    sum = (sum + Integer.parseInt(p.trim())) % 256;
                }
                String ans = String.valueOf(sum);

                // d. Gửi kết quả
                out.write(ans.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
