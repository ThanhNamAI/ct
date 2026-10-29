package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1821] [TCP Data Stream] Tổng doanh thu theo nhóm
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207, sử dụng DataInputStream và DataOutputStream để trao đổi dữ liệu.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi chuỗi chứa mã sinh viên và mã câu hỏi bằng phương thức 
 * writeUTF
 *  theo định dạng 
 * studentCode;qCode
 * . Ví dụ: 
 * B21DCCN001;A1B2C3D4
 * .
 * 
 * 
 * b. Nhận từ server một số nguyên 
 * n
 * , sau đó nhận 
 * n
 *  bản ghi; mỗi bản ghi gồm 
 * category
 *  bằng 
 * readUTF
 * , 
 * amount
 *  bằng 
 * readDouble
 * , và 
 * quantity
 *  bằng 
 * readInt
 * . Ví dụ bản ghi: 
 * BOOK,120.50,3
 * .
 * 
 * 
 * c. Tính doanh thu từng bản ghi là 
 * amount * quantity
 * , cộng tổng toàn bộ doanh thu và xác định category có tổng doanh thu lớn nhất; nếu bằng nhau chọn category theo thứ tự từ điển nhỏ hơn.
 * 
 * 
 * d. Gửi kết quả bằng 
 * writeUTF
 *  theo định dạng 
 * category=<category>;total=<tổng>;records=<n>
 * , làm tròn tổng 02 chữ số thập phân. Ví dụ: 
 * category=BOOK;total=361.50;records=1
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1821_Tong_Doanh_Thu_Theo_Nhom {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1821";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận n bản ghi (category, amount, quantity)
            int n = in.readInt();
            Map<String, Double> byCat = new TreeMap<>();
            double total = 0;
            for (int i = 0; i < n; i++) {
                String category = in.readUTF();
                double amount = in.readDouble();
                int quantity = in.readInt();
                double revenue = amount * quantity;
                total += revenue;
                byCat.put(category, byCat.getOrDefault(category, 0.0) + revenue);
            }

            // c. Tìm category có doanh thu lớn nhất (bằng nhau lấy theo từ điển nhỏ hơn)
            String best = "";
            double bestVal = Double.NEGATIVE_INFINITY;
            for (Map.Entry<String, Double> e : byCat.entrySet()) {
                if (e.getValue() > bestVal) {
                    bestVal = e.getValue();
                    best = e.getKey();
                }
            }

            // d. Gửi kết quả
            out.writeUTF(String.format(Locale.US, "category=%s;total=%.2f;records=%d", best, total, n));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
