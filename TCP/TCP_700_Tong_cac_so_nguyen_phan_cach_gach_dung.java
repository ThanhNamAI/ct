package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 700] TCP - Byte Stream
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server hỗ trợ kết nối qua giao thức TCP tại cổng 2206 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s).
 * 
 * 
 * Yêu cầu xây dựng chương trình client thực hiện kết nối tới server trên sử dụng luồng byte dữ liệu (InputStream/OutputStream) để trao đổi thông tin theo thứ tự:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B16DCCN999;C64967DD
 * 
 * 
 * b. Nhận dữ liệu từ server là một chuỗi gồm các giá trị nguyên được phân tách với nhau bằng 
 * |
 * 
 * 
 * Ví dụ:
 *  
 * 2|5|9|11
 * 
 * 
 * c. Thực hiện tìm giá trị tổng của các số nguyên trong chuỗi và gửi lên server
 * 
 * 
 * Ví dụ:
 *  
 * 27
 * 
 * 
 * d. Đóng kết nối và kết thúc
 */
public class TCP_700_Tong_cac_so_nguyen_phan_cach_gach_dung {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "JNP-700";

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
            String[] parts = res.split("\\|");
            int sum = 0;
            for (String p : parts) sum += Integer.parseInt(p.trim());
            String ans = String.valueOf(sum);
            out.write(ans.getBytes());
            out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
