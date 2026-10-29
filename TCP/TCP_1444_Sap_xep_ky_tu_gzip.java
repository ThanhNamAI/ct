package TCP;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [Mã câu hỏi: 1444] TCP - GZIP Stream
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
 * B16DCCN999;GZCRC_LEN03
 * 
 * 
 * b. Nhận dữ liệu từ server, sau khi giải nén là một chuỗi văn bản.
 * 
 * 
 * c. Sắp xếp các ký tự trong chuỗi nhận được theo thứ tự từ điển (tăng dần theo mã ASCII). Sau đó gửi chuỗi kết quả đã sắp xếp lên server.
 * 
 * 
 * Ví dụ:
 *  Nhận về 
 * dbca1
 *  thì gửi lên server 
 * 1abcd
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1444_Sap_xep_ky_tu_gzip {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2210;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1444";

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
                char[] chars = res.toCharArray();
                Arrays.sort(chars);
                String ans = new String(chars) + "\n";

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
