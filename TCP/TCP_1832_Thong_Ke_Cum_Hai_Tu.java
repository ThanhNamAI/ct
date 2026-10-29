package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1832] [TCP Character Stream] Thống kê cụm hai từ
 * Loại: TCP_CHARACTER
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208, sử dụng BufferedReader và BufferedWriter để trao đổi chuỗi ký tự.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi một dòng chứa mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 *  bằng 
 * BufferedWriter
 * , sau đó kết thúc dòng. Ví dụ: 
 * B21DCCN001;BAA62945
 * .
 * 
 * 
 * b. Nhận từ server một câu tiếng Anh. Ví dụ: 
 * payment ticket payment ticket refund.
 * .
 * 
 * 
 * c. Chuẩn hóa câu về chữ thường, loại bỏ ký tự không thuộc 
 * [a-z0-9 ]
 * , sau đó đếm tần suất các cụm hai từ liên tiếp.
 * 
 * 
 * d. Gửi tối đa 03 cụm có tần suất cao nhất theo định dạng 
 * word_word=count|word_word=count
 * ; nếu bằng tần suất thì sắp xếp tăng dần theo từ điển. Ví dụ: 
 * payment_ticket=2|ticket_payment=1|ticket_refund=1
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1832_Thong_Ke_Cum_Hai_Tu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1832";

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
                Map<String, Integer> bigrams = new HashMap<>();
                for (int i = 0; i < words.length - 1; i++) {
                    String bg = words[i] + "_" + words[i + 1];
                    bigrams.put(bg, bigrams.getOrDefault(bg, 0) + 1);
                }
                List<Map.Entry<String, Integer>> entries = new ArrayList<>(bigrams.entrySet());
                entries.sort((a, b) -> {
                    if (!a.getValue().equals(b.getValue())) return b.getValue() - a.getValue();
                    return a.getKey().compareTo(b.getKey());
                });
                List<String> list = new ArrayList<>();
                for (int i = 0; i < Math.min(3, entries.size()); i++) {
                    list.add(entries.get(i).getKey() + "=" + entries.get(i).getValue());
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
