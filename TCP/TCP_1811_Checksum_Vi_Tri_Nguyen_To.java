package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * [Mã câu hỏi: 1811] [TCP Byte Raw] Checksum vị trí nguyên tố
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
 * b. Nhận từ server một chuỗi số byte không dấu phân tách bằng dấu phẩy. Ví dụ: 
 * 12,45,255,8,19,20
 * .
 * 
 * 
 * c. Tính 
 * primeSum
 *  là tổng các giá trị ở vị trí nguyên tố theo chỉ số bắt đầu từ 1, lấy modulo 65536; đồng thời tính 
 * xor
 *  của toàn bộ giá trị.
 * 
 * 
 * d. Gửi kết quả theo định dạng 
 * primeSum=<n>;xor=<m>
 * . Ví dụ: 
 * primeSum=319;xor=209
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1811_Checksum_Vi_Tri_Nguyen_To {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "Q1811";

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
                String[] parts = res.split(",");
                int n = parts.length;
                boolean[] isPrime = new boolean[n + 2];
                Arrays.fill(isPrime, true);
                isPrime[0] = false;
                isPrime[1] = false;
                for (int p = 2; p * p <= n; p++) {
                    if (isPrime[p]) {
                        for (int i = p * p; i <= n; i += p) isPrime[i] = false;
                    }
                }
                int primeSum = 0;
                int xor = 0;
                for (int i = 0; i < n; i++) {
                    int v = Integer.parseInt(parts[i].trim());
                    xor ^= v;
                    if (isPrime[i + 1]) primeSum = (primeSum + v) % 65536;
                }
                String ans = "primeSum=" + primeSum + ";xor=" + xor;

                // d. Gửi kết quả
                out.write(ans.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
