package TCP;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [Mã câu hỏi: 1852] [TCP GZIP] Che và sắp xếp log bảo mật nén
 * Loại: TCP_GZIP
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2210, sử dụng GZIPInputStream và GZIPOutputStream để trao đổi dữ liệu đã nén.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 *  qua 
 * GZIPOutputStream
 * , kết thúc bằng ký tự xuống dòng. Ví dụ: 
 * B21DCCN001;C7D8E9F0
 * .
 * 
 * 
 * b. Nhận từ server chuỗi log đã nén GZIP, các dòng nối bằng 
 * ||
 * . Mỗi dòng có thể chứa email, số điện thoại và token.
 * 
 * 
 * c. Che email bằng 
 * [EMAIL]
 * , số điện thoại 10 chữ số bắt đầu bằng 0 bằng 
 * [PHONE]
 * , token bằng 
 * token=[TOKEN]
 * , sau đó sắp xếp dòng theo mức 
 * ERROR
 * , 
 * WARN
 * , 
 * INFO
 * .
 * 
 * 
 * d. Gửi chuỗi log đã xử lý qua luồng GZIP, các dòng vẫn nối bằng 
 * ||
 * . Ví dụ: 
 * ERROR user=a email=[EMAIL] phone=[PHONE] token=[TOKEN]
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1852_Che_Va_Sap_Xep_Log_Bao_Mat_Nen {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2210;
        String sCode = "B23DCCN861";
        String qCode = "Q1852";

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
                List<String> lines = new ArrayList<>();
                for (String line : res.split("\\|\\|")) {
                    if (line.trim().isEmpty()) continue;
                    lines.add(line.trim()
                            .replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[EMAIL]")
                            .replaceAll("(?<!\\d)0\\d{9}(?!\\d)", "[PHONE]")
                            .replaceAll("token=[^\\s|]+", "token=[TOKEN]"));
                }
                // Sắp xếp ổn định theo mức ERROR, WARN, INFO
                lines.sort(Comparator.comparingInt((String l) -> {
                    String level = l.split("\\s+")[0];
                    if (level.equals("ERROR")) return 0;
                    if (level.equals("WARN")) return 1;
                    if (level.equals("INFO")) return 2;
                    if (l.contains("ERROR")) return 0;
                    if (l.contains("WARN")) return 1;
                    if (l.contains("INFO")) return 2;
                    return 3;
                }));
                String ans = String.join("||", lines);

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
