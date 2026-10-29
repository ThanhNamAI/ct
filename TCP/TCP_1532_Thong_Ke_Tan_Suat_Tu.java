package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1532] [TCP Character Stream] Thống kê tần suất từ
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
 * b. Nhận từ server một câu tiếng Anh ngẫu nhiên.
 * 
 * 
 * c. Chuẩn hóa chuỗi bằng cách chuyển về chữ thường, loại bỏ ký tự không thuộc [a-z0-9 ], gom nhiều khoảng trắng thành một khoảng trắng, sau đó đếm tần suất xuất hiện của từng từ.
 * 
 * 
 * d. Gửi kết quả theo thứ tự từ tăng dần theo từ điển, định dạng word=count|word=count. Ví dụ: account=2|payment=1.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * Payment Account Account ticket REFUND REFUND customer Payment ticket customer ticket.
 *  thì dữ liệu nộp lại là 
 * account=2|customer=2|payment=2|refund=2|ticket=3
 * .
 */
public class TCP_1532_Thong_Ke_Tan_Suat_Tu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1532";

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
                String clean = res.toLowerCase().replaceAll("[^a-z0-9 ]", "").replaceAll(" +", " ").trim();
                String[] words = clean.isEmpty() ? new String[0] : clean.split(" ");
                Map<String, Integer> freq = new TreeMap<>();
                for (String w : words) {
                    if (!w.isEmpty()) freq.put(w, freq.getOrDefault(w, 0) + 1);
                }
                List<String> list = new ArrayList<>();
                for (Map.Entry<String, Integer> e : freq.entrySet()) {
                    list.add(e.getKey() + "=" + e.getValue());
                }
                String ans = String.join("|", list);

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
