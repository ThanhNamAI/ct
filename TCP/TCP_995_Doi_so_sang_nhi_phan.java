package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 995] TCP - Data Stream
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu sinh viên xây dựng chương trình client để tương tác với server, sử dụng các luồng data (DataInputStream và DataOutputStream) để trao đổi thông tin theo thứ tự sau:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi: Chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;D68C93F7
 * 
 * 
 * b. Nhận một số nguyên hệ thập phân từ server.
 * 
 * 
 * Ví dụ:
 *  
 * 45
 * 
 * 
 * c. Chuyển đổi số nguyên nhận được sang hệ nhị phân và gửi chuỗi kết quả này lại cho server.
 * 
 * 
 * Ví dụ:
 *  45 sẽ thành chuỗi 
 * 101101
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_995_Doi_so_sang_nhi_phan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-995";

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
            String bin = Integer.toBinaryString(n);
            out.writeUTF(bin);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
