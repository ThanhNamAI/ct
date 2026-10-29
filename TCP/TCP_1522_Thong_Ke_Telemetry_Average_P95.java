package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1522] [TCP Data Stream] Thống kê telemetry average p95
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207, sử dụng DataInputStream và DataOutputStream để trao đổi dữ liệu số thực.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi một chuỗi chứa mã sinh viên và mã câu hỏi bằng phương thức writeUTF theo định dạng "studentCode;qCode". Ví dụ: "B15DCCN999;1D25ED92".
 * 
 * 
 * b. Nhận từ server một số nguyên n, sau đó nhận tiếp n giá trị double.
 * 
 * 
 * c. Tính giá trị trung bình, giá trị p95 tại vị trí ceil(n * 0.95) - 1 sau khi sắp xếp tăng dần, và số phần tử lớn hơn giá trị trung bình.
 * 
 * 
 * d. Gửi kết quả bằng writeUTF theo định dạng average;p95;aboveAvg, trong đó các giá trị số thực được làm tròn 02 chữ số thập phân. Ví dụ: 18.45;30.12;4.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 90.0,47.66,81.95,93.56,9.72,77.04,13.72,88.62,5.34,85.55
 *  thì dữ liệu nộp lại là 
 * 59.32;93.56;6
 * .
 */
public class TCP_1522_Thong_Ke_Telemetry_Average_P95 {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1522";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận n và n giá trị double
            int n = in.readInt();
            double[] arr = new double[n];
            double sum = 0;
            for (int i = 0; i < n; i++) {
                arr[i] = in.readDouble();
                sum += arr[i];
            }

            // c. Xử lý dữ liệu
            double avg = sum / n;
            double[] sorted = arr.clone();
            Arrays.sort(sorted);
            int p95Index = (int) Math.ceil(n * 0.95) - 1;
            if (p95Index < 0) p95Index = 0;
            double p95 = sorted[p95Index];
            int above = 0;
            for (double v : arr) if (v > avg) above++;

            // d. Gửi kết quả average;p95;aboveAvg
            out.writeUTF(String.format(Locale.US, "%.2f;%.2f;%d", avg, p95, above));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
