package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1931] [RMI Character] Chữ ký thống kê câu
 * Loại: RMI_CHARACTER
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện CharacterService trong package RMI, được đăng ký với RegistryServer bằng tên RMICharacterService.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="BAA62945"
 * .
 * 
 * 
 * b. Triệu gọi 
 * requestCharacter(studentCode, qCode)
 *  để nhận một câu tiếng Anh từ server. Ví dụ: 
 * payment ticket refund.
 * .
 * 
 * 
 * c. Chuẩn hóa về chữ thường, loại bỏ ký tự không thuộc 
 * [a-z0-9 ]
 * , đếm số từ, tìm từ dài nhất; nếu có nhiều từ dài nhất thì chọn từ nhỏ hơn theo thứ tự từ điển; tính hash bằng tổng mã ký tự của chuỗi đã chuẩn hóa modulo 10000.
 * 
 * 
 * d. Triệu gọi 
 * submitCharacter(studentCode, qCode, strSubmit)
 *  để gửi chuỗi 
 * words=<n>;longest=<word>;hash=<h>
 * . Ví dụ: 
 * words=3;longest=payment;hash=2118
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1931_Chu_Ky_Thong_Ke_Cau {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1931";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Chuẩn hóa: chữ thường, chỉ giữ [a-z0-9 ]
            String clean = str.toLowerCase().replaceAll("[^a-z0-9 ]", "").trim();
            String[] words = clean.isEmpty() ? new String[0] : clean.split(" +");
            String longest = "";
            for (String w : words) {
                if (w.length() > longest.length() || (w.length() == longest.length() && w.compareTo(longest) < 0)) {
                    longest = w;
                }
            }
            int hash = 0;
            for (char ch : clean.toCharArray()) hash += ch;
            hash %= 10000;

            // c. Gửi kết quả
            service.submitCharacter(sCode, qCode, "words=" + words.length + ";longest=" + longest + ";hash=" + hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
