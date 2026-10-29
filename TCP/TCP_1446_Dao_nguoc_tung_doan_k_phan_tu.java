package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1446] TCP - Data Stream
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu sinh viên xây dựng chương trình client để tương tác với server, sử dụng các luồng data (DataInputStream và DataOutputStream) để trao đổi thông tin theo thứ tự sau:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi: chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;D68C93F7
 * 
 * 
 * b. Nhận lần lượt từ server: một số nguyên k (readInt) là độ dài mỗi đoạn, sau đó một chuỗi (readUTF) gồm các số nguyên phân tách bởi dấu phẩy.
 * 
 * 
 * Ví dụ:
 *  
 * 3
 *  và 
 * 12,5,7,30,41,8,9,66
 * 
 * 
 * c. Chia dãy số thành các đoạn liên tiếp, mỗi đoạn k phần tử (đoạn cuối có thể ít hơn k phần tử). Đảo ngược thứ tự các phần tử trong từng đoạn, giữ nguyên thứ tự các đoạn, rồi gửi chuỗi kết quả lên server bằng writeUTF (các số phân tách bởi dấu phẩy, không có khoảng trắng).
 * 
 * 
 * Ví dụ:
 *  các đoạn 
 * 12,5,7 | 30,41,8 | 9,66
 *  sẽ thành 
 * 7,5,12,8,41,30,66,9
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1446_Dao_nguoc_tung_doan_k_phan_tu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1446";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận và c. Xử lý
            int k = in.readInt();
            String numbersStr = in.readUTF();
            String[] parts = numbersStr.split(",");
            List<Integer> list = new ArrayList<>();
            for (String p : parts) list.add(Integer.parseInt(p.trim()));
            List<Integer> res = new ArrayList<>();
            for (int i = 0; i < list.size(); i += k) {
                int end = Math.min(i + k, list.size());
                List<Integer> sub = new ArrayList<>(list.subList(i, end));
                Collections.reverse(sub);
                res.addAll(sub);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < res.size(); i++) {
                sb.append(res.get(i));
                if (i < res.size() - 1) sb.append(",");
            }
            out.writeUTF(sb.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
