package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.nio.charset.StandardCharsets;

/**
 * [Mã câu hỏi: 1512] [TCP Byte Raw] Đếm bit trong các đoạn nhị phân
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
 * b. Nhận từ server một chuỗi gồm nhiều đoạn nhị phân, các đoạn được phân tách bởi ký tự |. Ví dụ: 1010|111|0001.
 * 
 * 
 * c. Đếm tổng số bit 1 và tổng số bit 0 trong toàn bộ chuỗi dữ liệu nhận được.
 * 
 * 
 * d. Gửi kết quả theo định dạng ones=<số_lượng_bit_1>;zeros=<số_lượng_bit_0>. Ví dụ: ones=6;zeros=5.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 000111000|101101|0110010|101101100|011110101|0110010
 *  thì dữ liệu nộp lại là 
 * ones=24;zeros=23
 * .
 */
public class TCP_1512_Dem_Bit_Trong_Cac_Doan_Nhi_Phan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "Q1512";

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
                int ones = 0, zeros = 0;
                for (char ch : res.toCharArray()) {
                    if (ch == '1') ones++;
                    else if (ch == '0') zeros++;
                }
                String ans = "ones=" + ones + ";zeros=" + zeros;

                // d. Gửi kết quả
                out.write(ans.getBytes(StandardCharsets.UTF_8));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
