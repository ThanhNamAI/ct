package TCP;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * [Mã câu hỏi: 1445] TCP - GZIP Stream
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
 * B16DCCN999;GZFRAME01
 * 
 * 
 * b. Nhận dữ liệu từ server, sau khi giải nén là một chuỗi văn bản.
 * 
 * 
 * c. Tạo một chuỗi mới theo định dạng "<length>#<crc32>#<data>", trong đó <length> là số byte UTF-8 của chuỗi ban đầu, <crc32> là giá trị CRC32 của chuỗi (UTF-8), <data> là chuỗi gốc. Sau đó gửi chuỗi kết quả lên server.
 * 
 * 
 * Ví dụ:
 *  
 * 24#2234503588#Hello GZIP Checksum Test
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1445_Dong_goi_do_dai_crc32_gzip {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2210;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1445";

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
                byte[] data = res.getBytes(StandardCharsets.UTF_8);
                java.util.zip.CRC32 crc = new java.util.zip.CRC32();
                crc.update(data);
                String ans = data.length + "#" + crc.getValue() + "#" + res + "\n";

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
