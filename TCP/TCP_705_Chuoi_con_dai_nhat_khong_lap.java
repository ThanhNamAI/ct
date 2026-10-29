package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 705] TCP - Byte Stream
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2206 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client thực hiện kết nối tới server sử dụng các luồng byte (InputStream/OutputStream) để trao đổi thông tin theo thứ tự:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B16DCCN999;10048F28
 * 
 * 
 * b. Nhận chuỗi ký tự từ server.
 * 
 * 
 * Ví dụ:
 *  
 * abcabcbb
 * 
 * 
 * c. Tìm và gửi lên server chuỗi con dài nhất từ chuỗi nhận được mà không có ký tự lặp lại theo format 
 * longestsubstring;length
 * .
 * 
 * 
 * Ví dụ:
 *  
 * abc;3
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_705_Chuoi_con_dai_nhat_khong_lap {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "JNP-705";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.write(req.getBytes());
            out.flush();

            // b. Nhận dữ liệu
            byte[] buffer = new byte[2048];
            int bytesRead = in.read(buffer);
            if (bytesRead > 0) {
                String res = new String(buffer, 0, bytesRead).trim();
                // c. Xử lý và gửi lại kết quả
            Map<Character, Integer> lastIdx = new HashMap<>();
            int start = 0, bestStart = 0, bestLen = 0;
            for (int i = 0; i < res.length(); i++) {
                char c = res.charAt(i);
                if (lastIdx.containsKey(c) && lastIdx.get(c) >= start) {
                    start = lastIdx.get(c) + 1;
                }
                lastIdx.put(c, i);
                if (i - start + 1 > bestLen) {
                    bestLen = i - start + 1;
                    bestStart = start;
                }
            }
            String ans = res.substring(bestStart, bestStart + bestLen) + ";" + bestLen;
            out.write(ans.getBytes());
            out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
