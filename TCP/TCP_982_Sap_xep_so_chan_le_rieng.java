package TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 982] TCP - Byte Stream
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
 * B16DCCN999;A63D9404
 * 
 * 
 * b. Nhận dữ liệu từ server là một chuỗi các số nguyên được sắp xếp ngẫu nhiên, các số được phân tách nhau bởi ký tự 
 * ,
 * .
 * 
 * 
 * Ví dụ:
 *  
 * 2,15,4,3,6,8,10,7,1
 * 
 * 
 * c. Sắp xếp tăng dần các giá trị chẵn và sau đó tăng dần các giá trị lẻ trong dãy số.
 * 
 * 
 * Ví dụ:
 *  
 * [2, 4, 6, 8, 10];[1, 3, 7, 15]
 * . Gửi chuỗi được sắp xếp này lên server.
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_982_Sap_xep_so_chan_le_rieng {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2206;
        String sCode = "B23DCCN861";
        String qCode = "JNP-982";

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
            List<Integer> evens = new ArrayList<>();
            List<Integer> odds = new ArrayList<>();
            for (String p : parts) {
                int v = Integer.parseInt(p.trim());
                if (v % 2 == 0) evens.add(v);
                else odds.add(v);
            }
            Collections.sort(evens);
            Collections.sort(odds);
            String ans = evens + ";" + odds;
            out.write(ans.getBytes());
            out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
