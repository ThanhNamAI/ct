package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 704] TCP - Byte Stream
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server hỗ trợ kết nối qua giao thức TCP tại cổng 2206 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu xây dựng chương trình client thực hiện kết nối tới server sử dụng luồng byte dữ liệu (InputStream/OutputStream) để trao đổi thông tin theo thứ tự:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B16DCCN999;76B68B3B
 * 
 * 
 * b. Nhận dữ liệu từ server là một chuỗi các giá trị số nguyên được phân tách bởi ký tự 
 * ,
 * .
 * 
 * 
 * Ví dụ:
 *  
 * 5,10,20,25,50,40,30,35
 * 
 * 
 * c. Tìm dãy con tăng ngặt dài nhất (các phần tử không cần liền kề nhưng giữ nguyên thứ tự) và gửi lên server theo định dạng 
 * dãy con;độ dài
 * . Nếu có nhiều dãy cùng độ dài, chọn dãy có phần tử cuối xuất hiện sớm nhất; với mỗi phần tử, phần tử đứng trước nó là phần tử xuất hiện sớm nhất cho được độ dài lớn nhất.
 * 
 * 
 * Ví dụ:
 *  
 * 5,10,20,25,30,35;6
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_704_Day_con_tang_dai_nhat {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "JNP-704";

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
            int n = parts.length;
            int[] a = new int[n];
            int[] len = new int[n];
            int[] prev = new int[n];
            for (int i = 0; i < n; i++) a[i] = Integer.parseInt(parts[i].trim());
            int best = 0;
            for (int i = 0; i < n; i++) {
                len[i] = 1;
                prev[i] = -1;
                for (int j = 0; j < i; j++) {
                    // chỉ cập nhật khi dài hơn hẳn -> giữ phần tử trước xuất hiện sớm nhất
                    if (a[j] < a[i] && len[j] + 1 > len[i]) {
                        len[i] = len[j] + 1;
                        prev[i] = j;
                    }
                }
                // chỉ lấy khi dài hơn hẳn -> phần tử cuối xuất hiện sớm nhất
                if (len[i] > len[best]) best = i;
            }
            LinkedList<Integer> seq = new LinkedList<>();
            for (int i = best; i != -1 && n > 0; i = prev[i]) seq.addFirst(a[i]);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < seq.size(); i++) {
                sb.append(seq.get(i));
                if (i < seq.size() - 1) sb.append(",");
            }
            sb.append(";").append(seq.size());
            out.write(sb.toString().getBytes());
            out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
