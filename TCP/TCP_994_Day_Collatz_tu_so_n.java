package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 994] TCP - Byte Stream
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2206 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s).
 * 
 * 
 * Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng các luồng byte (InputStream/OutputStream) để trao đổi thông tin theo thứ tự:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B16DCCN999;2B3A6510
 * 
 * 
 * b. Nhận dữ liệu từ server là một số nguyên n nhỏ hơn 400.
 * 
 * 
 * Ví dụ:
 *  
 * 7
 * 
 * 
 * c. Thực hiện các bước sau đây để sinh ra chuỗi từ số nguyên n ban đầu và gửi lên server.
 * 
 * 
 * Bắt đầu với số nguyên nn:
 * 
 * 
 * Nếu n là số chẵn, chia nn cho 2 để tạo ra số tiếp theo trong dãy.
 * 
 * 
 * Nếu n là số lẻ và khác 1, thực hiện phép toán n=3*n+1 để tạo ra số tiếp theo.
 * 
 * 
 * Lặp lại quá trình trên cho đến khi n=1, tại đó dừng thuật toán.
 * 
 * 
 * Kết quả là một dãy số liên tiếp, bắt đầu từ n ban đầu, kết thúc tại 1 và độ dài của chuỗi theo format "chuỗi kết quả; độ dài"
 * 
 * 
 * Ví dụ:
 *  kết quả với n = 7 thì dãy: 7 22 11 34 17 52 26 13 40 20 10 5 16 8 4 2 1; 17
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_994_Day_Collatz_tu_so_n {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "JNP-994";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.write(req.getBytes());
            out.flush();

            // b. Nhận dữ liệu
            byte[] buffer = new byte[2048];
            int bytesRead = in.read(buffer);
            if (bytesRead > 0) {
                String res = new String(buffer, 0, bytesRead).trim();
                // c. Xử lý và gửi lại kết quả
            int n = Integer.parseInt(res.trim());
            StringBuilder sb = new StringBuilder();
            int count = 1;
            sb.append(n);
            while (n != 1) {
                if (n % 2 == 0) n /= 2;
                else n = 3 * n + 1;
                sb.append(" ").append(n);
                count++;
            }
            sb.append("; ").append(count);
            out.write(sb.toString().getBytes());
            out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
