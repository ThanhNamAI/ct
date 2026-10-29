package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1521] [TCP Data Stream] Thống kê min max sum
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207, sử dụng DataInputStream và DataOutputStream để trao đổi dữ liệu.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi một chuỗi chứa mã sinh viên và mã câu hỏi bằng phương thức writeUTF theo định dạng "studentCode;qCode". Ví dụ: "B15DCCN999;1D25ED92".
 * 
 * 
 * b. Nhận từ server một số nguyên n, sau đó nhận tiếp n số nguyên.
 * 
 * 
 * c. Tìm giá trị nhỏ nhất, giá trị lớn nhất và tổng của toàn bộ dãy số.
 * 
 * 
 * d. Gửi kết quả bằng writeUTF theo định dạng min;max;sum. Ví dụ: 3;20;87.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 496,342,197,73,-52,124,130,228,238,-147,-329,411
 *  thì dữ liệu nộp lại là 
 * -329;496;1711
 * .
 */
public class TCP_1521_Thong_Ke_Min_Max_Sum {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1521";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận n và n số nguyên
            int n = in.readInt();
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            long sum = 0;
            for (int i = 0; i < n; i++) {
                int val = in.readInt();
                if (val < min) min = val;
                if (val > max) max = val;
                sum += val;
            }

            // c. Gửi kết quả min;max;sum
            out.writeUTF(min + ";" + max + ";" + sum);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
