package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 721] TCP - Character Stream
 * Loại: TCP_CHARACTER
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng byte (BufferedWriter/BufferedReader) theo kịch bản sau:
 * 
 * 
 * a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi với định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;BAA62945
 * 
 * 
 * b. Nhận một chuỗi ngẫu nhiên từ server
 * 
 * 
 * Ví dụ:
 *  
 * dgUOo ch2k22ldsOo
 * 
 * 
 * c. Liệt kê các ký tự (là chữ hoặc số) xuất hiện nhiều hơn một lần trong chuỗi và số lần xuất hiện của chúng và gửi lên server
 * 
 * 
 * Ví dụ:
 *  
 * d:2,O:2,o:2,2:3,
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_721_Ky_tu_lap_lai_va_so_lan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-721";

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
                Map<Character, Integer> counts = new LinkedHashMap<>();
                for (char c : res.toCharArray()) {
                    if (Character.isLetterOrDigit(c)) counts.put(c, counts.getOrDefault(c, 0) + 1);
                }
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Character, Integer> e : counts.entrySet()) {
                    if (e.getValue() > 1) sb.append(e.getKey()).append(":").append(e.getValue()).append(",");
                }
                out.write(sb.toString());
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
