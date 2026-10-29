package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 989] TCP - Character Stream
 * Loại: TCP_CHARACTER
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu là xây dựng một chương trình client thực hiện kết nối tới server và sử dụng luồng ký tự (BufferedWriter/BufferedReader) để trao đổi thông tin theo kịch bản sau:
 * 
 * 
 * a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi với định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;A1B2C3D4
 * 
 * 
 * b. Nhận từ server một chuỗi ngẫu nhiên.
 * 
 * 
 * c. Xử lý chuỗi đã nhận theo các bước sau:
 * 
 * 
 * Bước 1: Tìm từ dài nhất trong trong chuỗi ngẫu nhiên (từ là chuỗi con phân tách bởi khoảng trắng).
 * 
 * 
 * Bước 2: Xác định vị trí bắt đầu của từ dài nhất đó trong chuỗi ban đầu.
 * 
 * 
 * d. Gửi lần lượt hai giá trị lên server:
 * 
 * 
 * Từ dài nhất xuất hiện trong chuỗi.
 * Vị trí bắt đầu của từ trong chuỗi ban đầu.
 * 
 * 
 * e. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_989_Tu_dai_nhat_va_vi_tri {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-989";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.write(req);
            out.newLine();
            out.flush();

            // b. Nhận và c. Xử lý
            String res = in.readLine();
            if (res != null) {
                String[] words = res.trim().split("\\s+");
                String longest = "";
                for (String w : words) {
                    if (w.length() > longest.length()) longest = w;
                }
                int pos = res.indexOf(longest);
                out.write(longest);
                out.newLine();
                out.flush();
                out.write(String.valueOf(pos));
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
