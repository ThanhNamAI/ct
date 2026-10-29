package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1447] TCP - Character Stream
 * Loại: TCP_CHARACTER
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu là xây dựng một chương trình client thực hiện kết nối tới server và sử dụng luồng ký tự (BufferedWriter/BufferedReader) để trao đổi thông tin theo kịch bản sau:
 * 
 * 
 * a. Gửi một dòng gồm mã sinh viên và mã câu hỏi với định dạng 
 * studentCode;qCode
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;X1107ABC
 * 
 * 
 * b. Nhận từ server một dòng gồm nhiều từ, các từ phân tách bởi một khoảng trắng.
 * 
 * 
 * Ví dụ:
 *  
 * Orange Kiwis bANANa Apple CherRies Lemon
 * 
 * 
 * c. Sắp xếp các từ theo độ dài tăng dần; các từ có cùng độ dài giữ nguyên thứ tự xuất hiện ban đầu. Gửi lên server một dòng gồm các từ đã sắp xếp, phân tách bởi dấu phẩy và một khoảng trắng.
 * 
 * 
 * Ví dụ:
 *  
 * Kiwis, Apple, Lemon, Orange, bANANa, CherRies
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1447_Sap_xep_tu_theo_do_dai {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1447";

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
                // Arrays.sort với object là stable -> giữ thứ tự ban đầu khi cùng độ dài
                Arrays.sort(words, Comparator.comparingInt(String::length));
                out.write(String.join(", ", words));
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
