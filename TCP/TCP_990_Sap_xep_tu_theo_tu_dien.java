package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 990] TCP - Character Stream
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
 * B15DCCN999;X1107ABC
 * 
 * 
 * b. Nhận từ server một chuỗi ngẫu nhiên chứa nhiều từ, các từ phân tách bởi khoảng trắng.
 * 
 * 
 * c. Thực hiện các bước xử lý:
 * 
 * 
 * Bước 1: Tách chuỗi thành các từ dựa trên khoảng trắng.
 * 
 * 
 * Bước 2: Sắp xếp các từ theo thứ tự từ điển (có phân biệt chữ cái hoa thường).
 * 
 * 
 * d. Gửi lại chuỗi đã sắp xếp theo thứ tự từ điển lên server.
 * 
 * 
 * e. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_990_Sap_xep_tu_theo_tu_dien {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-990";

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
                Arrays.sort(words);
                out.write(String.join(" ", words));
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
