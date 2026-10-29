package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 701] TCP - Byte Stream
 * Loại: TCP_BYTE_RAW
 *
 * Đề bài:
 * Một chương trình server hỗ trợ kết nối qua giao thức TCP tại cổng 2206 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu xây dựng chương trình client thực hiện kết nối tới server trên sử dụng luồng byte dữ liệu (InputStream/OutputStream) để trao đổi thông tin theo thứ tự:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B16DCCN999;FF49DC02
 * 
 * 
 * b. Nhận dữ liệu từ server là một chuỗi các giá trị số nguyên được phân tách nhau bởi ký tự 
 * ,
 * 
 * 
 * Ví dụ:
 *  
 * 1,3,9,19,33,20
 * 
 * 
 * c. Thực hiện tìm giá trị khoảng cách nhỏ nhất của các phần tử nằm trong chuỗi và hai giá trị lớn nhất tạo nên khoảng cách đó. Gửi lên server chuỗi gồm "khoảng cách nhỏ nhất, số thứ nhất, số thứ hai".
 * 
 * 
 * Ví dụ:
 *  
 * 1,19,20
 * 
 * 
 * d. Đóng kết nối và kết thúc
 */
public class TCP_701_Khoang_cach_nho_nhat_giua_cac_so {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "JNP-701";

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
            List<Integer> list = new ArrayList<>();
            for (String p : parts) list.add(Integer.parseInt(p.trim()));
            Collections.sort(list);
            int minDiff = Integer.MAX_VALUE;
            int n1 = 0, n2 = 0;
            for (int i = 0; i < list.size() - 1; i++) {
                int diff = list.get(i + 1) - list.get(i);
                if (diff <= minDiff) {
                    minDiff = diff;
                    n1 = list.get(i);
                    n2 = list.get(i + 1);
                }
            }
            String ans = minDiff + "," + n1 + "," + n2;
            out.write(ans.getBytes());
            out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
