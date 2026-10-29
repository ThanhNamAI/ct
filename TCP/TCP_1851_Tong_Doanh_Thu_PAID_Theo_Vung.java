package TCP;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [Mã câu hỏi: 1851] [TCP GZIP] Tổng doanh thu PAID theo vùng
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
 * b. Nhận từ server chuỗi đã nén GZIP gồm nhiều bản ghi phân tách bằng 
 * |
 * , mỗi bản ghi có dạng 
 * region,status,amount
 * . Ví dụ: 
 * north,PAID,120.50|south,FAILED,33.00
 * .
 * 
 * 
 * c. Chỉ xét bản ghi có 
 * status=PAID
 * , tính tổng amount theo từng region.
 * 
 * 
 * d. Gửi kết quả qua luồng GZIP theo thứ tự region tăng dần, định dạng 
 * region=total|region=total
 * , total làm tròn 02 chữ số thập phân. Ví dụ: 
 * north=120.50
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1851_Tong_Doanh_Thu_PAID_Theo_Vung {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2210;
        String sCode = "B23DCCN861";
        String qCode = "Q1851";

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
                Map<String, Double> byRegion = new TreeMap<>();
                for (String rec : res.split("\\|")) {
                    String[] f = rec.trim().split(",");
                    if (f.length < 3) continue;
                    if (f[1].trim().equals("PAID")) {
                        byRegion.put(f[0].trim(), byRegion.getOrDefault(f[0].trim(), 0.0) + Double.parseDouble(f[2].trim()));
                    }
                }
                List<String> list = new ArrayList<>();
                for (Map.Entry<String, Double> e : byRegion.entrySet()) {
                    list.add(e.getKey() + "=" + String.format(Locale.US, "%.2f", e.getValue()));
                }
                String ans = String.join("|", list);

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
