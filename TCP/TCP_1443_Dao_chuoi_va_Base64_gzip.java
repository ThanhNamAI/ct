package TCP;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [Mã câu hỏi: 1443] TCP - GZIP Stream
 * Loại: TCP_GZIP
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2210 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng GZIPInputStream/GZIPOutputStream để trao đổi thông tin (mỗi thông điệp là một dòng text UTF-8 kết thúc bằng ‘\n’ và toàn bộ dữ liệu truyền/nhận đều được nén GZIP), theo thứ tự sau:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B16DCCN999;GZLEN01
 * 
 * 
 * b. Nhận dữ liệu từ server, sau khi giải nén là một chuỗi văn bản.
 * 
 * 
 * c. Thực hiện đảo ngược chuỗi nhận được, sau đó mã hóa chuỗi đã đảo ngược sang định dạng Base64. Gửi kết quả lên server theo khuôn dạng: <reversed_string>|<base64_string> Ví dụ: Nhận 
 * 123
 *  → Đảo ngược thành 
 * 321
 *  → Base64 của 
 * 321
 *  là 
 * MzIx
 *  → Gửi lên: 
 * 321|MzIx
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1443_Dao_chuoi_va_Base64_gzip {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2210;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1443";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            GZIPOutputStream out = new GZIPOutputStream(socket.getOutputStream(), true);

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode + "\n";
            out.write(req.getBytes(StandardCharsets.UTF_8));
            out.flush();

            // b. Nhận luồng nén GZIP
            GZIPInputStream in = new GZIPInputStream(socket.getInputStream());
            byte[] buf = new byte[4096];
            int len = in.read(buf);
            if (len > 0) {
                String res = new String(buf, 0, len, StandardCharsets.UTF_8).trim();
                
                // c. Xử lý dữ liệu
                String rev = new StringBuilder(res).reverse().toString();
                String encoded = Base64.getEncoder().encodeToString(rev.getBytes(StandardCharsets.UTF_8));
                String ans = rev + "|" + encoded + "\n";

                // d. Gửi lại kết quả
                out.write(ans.getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
