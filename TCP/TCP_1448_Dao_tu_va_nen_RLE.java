package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 1448] TCP - Character Stream
 * Loại: TCP_CHARACTER
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5 giây). Yêu cầu là xây dựng một chương trình client thực hiện kết nối tới server và sử dụng luồng ký tự (BufferedWriter/BufferedReader) để trao đổi thông tin theo kịch bản sau:
 * 
 * 
 * a. Gửi một dòng gồm mã sinh viên và mã câu hỏi với định dạng 
 * studentCode;qCode
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;X1107ABC
 * 
 * 
 * b. Nhận từ server một dòng gồm nhiều từ, các từ phân tách bởi một khoảng trắng.
 * 
 * 
 * Ví dụ:
 *  
 * Balloon cOFFee Apple
 * 
 * 
 * c. Thực hiện các bước xử lý:
 * 
 * 
 * Bước 1: Đảo ngược các ký tự trong từng từ, giữ nguyên thứ tự các từ, nối lại bằng một khoảng trắng.
 * 
 * 
 * Ví dụ:
 *  
 * noollaB eeFFOc elppA
 * 
 * 
 * Bước 2: Nén chuỗi ở bước 1 bằng mã hóa RLE: mỗi dãy ký tự giống nhau liên tiếp (phân biệt chữ hoa thường) được thay bằng ký tự đó kèm số lần lặp; dãy chỉ có một ký tự thì giữ nguyên, không ghi số 1.
 * 
 * 
 * Ví dụ:
 *  
 * no2l2aB e2F2Oc elp2A
 * 
 * 
 * d. Gửi chuỗi đã nén lên server trên một dòng.
 * 
 * 
 * e. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1448_Dao_tu_va_nen_RLE {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1448";

        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            out.write(req);
            out.newLine();
            out.flush();

            // b. Nhận và c. Xử lý
            String res = in.readLine();
            if (res != null) {
                String[] words = res.trim().split("\\s+");
                // Bước 1: đảo ngược từng từ
                StringBuilder rev = new StringBuilder();
                for (int i = 0; i < words.length; i++) {
                    rev.append(new StringBuilder(words[i]).reverse());
                    if (i < words.length - 1) rev.append(" ");
                }
                // Bước 2: nén RLE
                String s = rev.toString();
                StringBuilder rle = new StringBuilder();
                for (int i = 0; i < s.length(); ) {
                    int j = i;
                    while (j < s.length() && s.charAt(j) == s.charAt(i)) j++;
                    rle.append(s.charAt(i));
                    if (j - i > 1) rle.append(j - i);
                    i = j;
                }
                out.write(rle.toString());
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
