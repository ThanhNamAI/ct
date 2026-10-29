package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1831] [TCP Character Stream] Lọc giao dịch rủi ro
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
 * b. Nhận từ server một chuỗi log giao dịch, các dòng nối bằng 
 * ||
 * . Mỗi dòng có dạng 
 * user action=... risk=... amount=...
 * . Ví dụ: 
 * U300 action=PAY risk=85 amount=7200.50||U302 action=PAY risk=72 amount=3300.25
 * .
 * 
 * 
 * c. Một dòng rủi ro cao nếu 
 * risk >= 70
 *  hoặc 
 * amount >= 5000
 * . Giữ thứ tự các user rủi ro cao theo dữ liệu ban đầu và tính tổng amount của các dòng này.
 * 
 * 
 * d. Gửi kết quả theo định dạng 
 * count=<n>;ids=<id1,id2>;amount=<tổng>
 * , tổng làm tròn 02 chữ số thập phân. Ví dụ: 
 * count=2;ids=U300,U302;amount=10500.75
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1831_Loc_Giao_Dich_Rui_Ro {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "Q1831";

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
                List<String> ids = new ArrayList<>();
                double total = 0;
                for (String line : res.split("\\|\\|")) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String user = line.split("\\s+")[0];
                    double risk = 0, amount = 0;
                    Matcher mr = Pattern.compile("risk=([0-9.]+)").matcher(line);
                    Matcher ma = Pattern.compile("amount=([0-9.]+)").matcher(line);
                    if (mr.find()) risk = Double.parseDouble(mr.group(1));
                    if (ma.find()) amount = Double.parseDouble(ma.group(1));
                    if (risk >= 70 || amount >= 5000) {
                        ids.add(user);
                        total += amount;
                    }
                }
                String ans = String.format(Locale.US, "count=%d;ids=%s;amount=%.2f", ids.size(), String.join(",", ids), total);

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
