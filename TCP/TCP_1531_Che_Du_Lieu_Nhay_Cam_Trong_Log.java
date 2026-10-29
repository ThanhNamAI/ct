package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1531] [TCP Character Stream] Che dữ liệu nhạy cảm trong log
 * Loại: TCP_CHARACTER
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208, sử dụng BufferedReader và BufferedWriter để trao đổi chuỗi ký tự.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi một dòng chứa mã sinh viên và mã câu hỏi theo định dạng "studentCode;qCode" bằng BufferedWriter, sau đó kết thúc dòng. Ví dụ: "B15DCCN999;BAA62945".
 * 
 * 
 * b. Nhận từ server một chuỗi log gồm nhiều dòng được nối bằng ký tự ||. Mỗi dòng có thể chứa email, số điện thoại Việt Nam và token.
 * 
 * 
 * c. Thay mọi email bằng [EMAIL], mọi số điện thoại 10 chữ số bắt đầu bằng 0 bằng [PHONE], và mọi token dạng token=<giá_trị> bằng token=[TOKEN].
 * 
 * 
 * d. Gửi chuỗi log đã được che dữ liệu nhạy cảm, giữ nguyên thứ tự các dòng và ký tự phân tách ||.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * INFO user=cuong email=cuong0@example.com phone=0684775637 token=ftrTU9XmBX action=shipping
 *  thì dữ liệu nộp lại là 
 * INFO user=cuong email=[EMAIL] phone=[PHONE] token=[TOKEN] action=shipping
 * .
 */
public class TCP_1531_Che_Du_Lieu_Nhay_Cam_Trong_Log {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1531";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.write(req);
            out.newLine();
            out.flush();

            // b. Nhận dữ liệu
            String res = in.readLine();
            if (res != null) {
                // c. Xử lý dữ liệu
                String ans = res
                                        .replaceAll("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", "[EMAIL]")
                                        .replaceAll("(?<!\\d)0\\d{9}(?!\\d)", "[PHONE]")
                                        .replaceAll("token=[^\\s|]+", "token=[TOKEN]");

                // d. Gửi kết quả
                out.write(ans);
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
