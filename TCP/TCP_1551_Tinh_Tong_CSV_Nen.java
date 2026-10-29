package TCP;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [Mã câu hỏi: 1551] [TCP GZIP] Tính tổng CSV nén
 * Loại: TCP_GZIP
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2210, sử dụng GZIPInputStream và GZIPOutputStream để trao đổi dữ liệu đã nén.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng "studentCode;qCode" qua GZIPOutputStream, kết thúc bằng ký tự xuống dòng. Ví dụ: "B15DCCN999;A1B2C3D4".
 * 
 * 
 * b. Nhận từ server một dòng CSV các số nguyên sau khi giải nén.
 * 
 * 
 * c. Tính tổng tất cả các số nguyên trong CSV.
 * 
 * 
 * d. Gửi kết quả tổng lên server dưới dạng một dòng dữ liệu được nén GZIP.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 858,141,217,223,436,927,227,745,827,111,994,902,627,561
 *  thì dữ liệu nộp lại là 
 * 7796
 * .
 */
public class TCP_1551_Tinh_Tong_CSV_Nen {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2210;
        String sCode = "B23DCCN861";
        String qCode = "Q1551";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            GZIPOutputStream out = new GZIPOutputStream(socket.getOutputStream(), true);

            // a. Gửi mã sinh viên và mã câu hỏi (kết thúc bằng xuống dòng)
            String req = sCode + ";" + qCode + "\n";
            out.write(req.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // b. Nhận dữ liệu nén GZIP
            GZIPInputStream gin = new GZIPInputStream(socket.getInputStream());
            // Đọc từng byte đến '\n' (BufferedReader trên GZIPInputStream dễ bị treo vì available() luôn > 0)
            ByteArrayOutputStream lineBuf = new ByteArrayOutputStream();
            int ch;
            while ((ch = gin.read()) != -1 && ch != '\n') lineBuf.write(ch);
            String res = lineBuf.toString(StandardCharsets.UTF_8).trim();
            if (!res.isEmpty()) {
                // c. Xử lý dữ liệu
                long sum = 0;
                for (String p : res.split(",")) {
                    if (p.trim().isEmpty()) continue;
                    sum += Long.parseLong(p.trim());
                }
                String ans = String.valueOf(sum);

                // d. Gửi kết quả qua luồng GZIP
                out.write((ans + "\n").getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
