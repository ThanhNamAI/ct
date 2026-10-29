package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 914] TCP - Data Stream
 * Loại: TCP_DATA
 *
 * Đề bài:
 * Mật mã caesar, còn gọi là mật mã dịch chuyển, để giải mã thì mỗi ký tự nhận được sẽ được thay thế bằng một ký tự cách nó một đoạn s.
 * 
 * 
 * Ví dụ:
 *  với s = 3 thì ký tự 
 * A
 *  sẽ được thay thế bằng ký tự 
 * D
 * 
 * 
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2207 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng chương trình client tương tác với server trên, sử dụng các luồng byte (DataInputStream/DataOutputStream) để trao đổi thông tin theo thứ tự:
 * 
 * 
 * a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;D68C93F7
 * 
 * 
 * b. Nhận lần lượt chuỗi đã bị mã hóa caesar và giá trị dịch chuyển s nguyên
 * 
 * 
 * c. Thực hiện giải mã ra thông điệp ban đầu và gửi lên Server
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_914_Giai_ma_Caesar {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-914";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.writeUTF(req);
            out.flush();

            // b. Nhận và c. Xử lý (giải mã = dịch ngược s đoạn so với lúc mã hóa)
            String cipher = in.readUTF();
            int shift = in.readInt();
            shift = ((shift % 26) + 26) % 26;
            StringBuilder plain = new StringBuilder();
            for (char c : cipher.toCharArray()) {
                if (c >= 'A' && c <= 'Z') {
                    plain.append((char) ('A' + (c - 'A' - shift + 26) % 26));
                } else if (c >= 'a' && c <= 'z') {
                    plain.append((char) ('a' + (c - 'a' - shift + 26) % 26));
                } else {
                    plain.append(c);
                }
            }
            out.writeUTF(plain.toString());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
