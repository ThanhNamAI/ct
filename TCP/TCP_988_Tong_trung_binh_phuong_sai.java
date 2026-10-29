package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 988] TCP - Data Stream
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua TCP tại cổng 2207 (hỗ trợ thời gian liên lạc tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu là xây dựng chương trình client tương tác với server bằng các byte stream (DataInputStream/DataOutputStream) để trao đổi thông tin theo trình tự sau:
 * 
 * 
 * a. Gửi một chuỗi chứa mã sinh viên và mã câu hỏi ở định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B10DCCN000;A1B2C3D4
 * 
 * 
 * b. Nhận lần lượt từ server các số nguyên gồm:
 * 
 * 
 * số nguyên n kích thước của mảng
 * các số nguyên a1, a2, ... an là các giá trị trong mảng.
 * 
 * 
 * Ví dụ:
 *  n = 5, và các giá trị 5, 9, 3, 6, 8
 * 
 * 
 * c. Tính tổng, trung bình cộng, và phương sai của mảng. Gửi kết quả lần lượt lên server dưới dạng số nguyên và float. Ví dụ, gửi lên lần lượt: 31, 6.2, 4.5599995.
 * 
 * 
 * Biết:
 * 
 * 
 * Phương sai = [(x1 - Trung bình)^2 + (x2 - Trung bình)^2 + ... + (xn - Trung bình)^2] / n
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_988_Tong_trung_binh_phuong_sai {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-988";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận và c. Xử lý
            int n = in.readInt();
            int[] arr = new int[n];
            int sum = 0;
            for (int i = 0; i < n; i++) {
                arr[i] = in.readInt();
                sum += arr[i];
            }
            float mean = (float) sum / n;
            float variance = 0;
            for (int i = 0; i < n; i++) {
                variance += (arr[i] - mean) * (arr[i] - mean);
            }
            variance /= n;
            out.writeInt(sum);
            out.writeFloat(mean);
            out.writeFloat(variance);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
