package TCP;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [Mã câu hỏi: 1552] [TCP GZIP] Che dữ liệu nhạy cảm trong log nén
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
 * b. Nhận từ server một chuỗi log đã nén, các dòng log được nối bằng ký tự ||.
 * 
 * 
 * c. Che email, số điện thoại và token theo cùng quy tắc của bài TCP Character Stream về che dữ liệu nhạy cảm.
 * 
 * 
 * d. Gửi chuỗi log đã xử lý lên server qua luồng GZIP.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * INFO user=anna email=anna0@example.com phone=0459803028 token=aIm7ZM1AEW action=shipping
 *  thì dữ liệu nộp lại là 
 * INFO user=anna email=[EMAIL] phone=[PHONE] token=[TOKEN] action=shipping
 * .
 */
public class TCP_1552_Che_Du_Lieu_Nhay_Cam_Trong_Log_Nen {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2210;
        String sCode = "B23DCCN861";
        String qCode = "Q1552";

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
                String ans = res
                                        .replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[EMAIL]")
                                        .replaceAll("(?<!\\d)0\\d{9}(?!\\d)", "[PHONE]")
                                        .replaceAll("token=[^\\s|]+", "token=[TOKEN]");

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
