package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 800] TCP - Data Stream
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Một chương trình máy chủ cho phép kết nối qua TCP tại cổng 2207 (hỗ trợ thời gian liên lạc tối đa cho mỗi yêu cầu là 5s), yêu cầu xây dựng chương trình (tạm gọi là client) thực hiện kết nối tới server tại cổng 2207, sử dụng luồng byte dữ liệu (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:
 * 
 * 
 * a. Gửi chuỗi là mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;1D25ED92
 * 
 * 
 * b. Nhận lần lượt hai số nguyên a và b từ server
 * 
 * 
 * c. Thực hiện tính toán tổng, tích và gửi lần lượt từng giá trị theo đúng thứ tự trên lên server
 * 
 * 
 * d. Đóng kết nối và kết thúc
 */
public class TCP_800_Tong_va_tich_hai_so_nguyen {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-800";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận và c. Xử lý
            int a = in.readInt();
            int b = in.readInt();
            int sum = a + b;
            int product = a * b;
            out.writeInt(sum);
            out.writeInt(product);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
