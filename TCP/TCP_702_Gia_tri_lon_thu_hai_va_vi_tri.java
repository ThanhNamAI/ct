package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 702] TCP - Byte Stream
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2206 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng các luồng byte (InputStream/OutputStream) để trao đổi thông tin theo thứ tự:
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
 * b. Nhận dữ liệu từ server là một chuỗi các giá trị số nguyên được phân tách nhau bởi ký tự 
 * ,
 * .
 * 
 * 
 * Ví dụ:
 *  
 * 1,3,9,19,33,20
 * 
 * 
 * c. Tìm và gửi lên server giá trị lớn thứ hai cùng vị trí xuất hiện của nó trong chuỗi.Ví dụ: 20,5
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_702_Gia_tri_lon_thu_hai_va_vi_tri {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "JNP-702";

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
            String[] parts = res.split(",");
            int[] arr = new int[parts.length];
            int max1 = Integer.MIN_VALUE, max2 = Integer.MIN_VALUE;
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Integer.parseInt(parts[i].trim());
                if (arr[i] > max1) {
                    max2 = max1;
                    max1 = arr[i];
                } else if (arr[i] > max2 && arr[i] < max1) {
                    max2 = arr[i];
                }
            }
            int pos2 = -1;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == max2) {
                    pos2 = i;
                    break;
                }
            }
            String ans = max2 + "," + pos2;
            out.write(ans.getBytes());
            out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
