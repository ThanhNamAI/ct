package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1822] [TCP Data Stream] Thống kê độ trễ loại ngoại lệ
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
 *  giá trị 
 * double
 *  biểu diễn độ trễ. Ví dụ: 
 * 12.50,30.00,45.25,80.00
 * .
 * 
 * 
 * c. Tính trung bình sau khi loại một giá trị nhỏ nhất và một giá trị lớn nhất, tính độ lệch chuẩn trên toàn bộ dãy, và đếm số giá trị lớn hơn 
 * average + stddev
 * .
 * 
 * 
 * d. Gửi kết quả bằng 
 * writeUTF
 *  theo định dạng 
 * trimmedAvg=<avg>;stddev=<stddev>;outliers=<n>
 * , các số thực làm tròn 02 chữ số thập phân. Ví dụ: 
 * trimmedAvg=37.63;stddev=24.84;outliers=1
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1822_Thong_Ke_Do_Tre_Loai_Ngoai_Le {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "Q1822";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận n giá trị double
            int n = in.readInt();
            double[] arr = new double[n];
            double total = 0;
            for (int i = 0; i < n; i++) {
                arr[i] = in.readDouble();
                total += arr[i];
            }

            // c. Trung bình sau khi loại 1 min và 1 max, độ lệch chuẩn toàn dãy, đếm outlier
            double[] sorted = arr.clone();
            Arrays.sort(sorted);
            double trimmedAvg;
            if (n > 2) {
                double s = 0;
                for (int i = 1; i < n - 1; i++) s += sorted[i];
                trimmedAvg = s / (n - 2);
            } else {
                trimmedAvg = total / n;
            }
            double mean = total / n;
            double sq = 0;
            for (double v : arr) sq += (v - mean) * (v - mean);
            double stddev = Math.sqrt(sq / n);
            int outliers = 0;
            for (double v : arr) if (v > trimmedAvg + stddev) outliers++;

            // d. Gửi kết quả
            out.writeUTF(String.format(Locale.US, "trimmedAvg=%.2f;stddev=%.2f;outliers=%d", trimmedAvg, stddev, outliers));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
