package TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.*;

/**
 * [Mã câu hỏi: 600] TCP - Character Stream
 * Loại: TCP_CHARACTER
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2208 (hỗ trợ thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác với server sử dụng các luồng byte (BufferedWriter/BufferedReader) theo kịch bản sau:
 * 
 * 
 * a. Gửi một chuỗi gồm mã sinh viên và mã câu hỏi với định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B15DCCN999;EC4F899B
 * 
 * 
 * b. Nhận một chuỗi ngẫu nhiên là danh sách các một số tên miền từ server
 * 
 * 
 * Ví dụ:
 *  giHgWHwkLf0Rd0.io, I7jpjuRw13D.io, wXf6GP3KP.vn, MdpIzhxDVtTFTF.edu, TUHuMfn25chmw.vn, HHjE9.com, 4hJld2m2yiweto.vn, y2L4SQwH.vn, s2aUrZGdzS.com, 4hXfJe9giAA.edu
 * 
 * 
 * c. Tìm kiếm các tên miền .edu và gửi lên server
 * 
 * 
 * Ví dụ:
 *  
 * MdpIzhxDVtTFTF.edu, 4hXfJe9giAA.edu
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_600_Tim_ten_mien_edu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-600";

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
                String[] parts = res.split(",");
                List<String> eduList = new ArrayList<>();
                for (String part : parts) {
                    String domain = part.trim();
                    if (domain.endsWith(".edu")) eduList.add(domain);
                }
                String ans = String.join(", ", eduList);
                out.write(ans);
                out.newLine();
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
