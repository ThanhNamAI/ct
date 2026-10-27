package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1731] [RMI Character] Chuẩn hóa câu
 * Loại: RMI_CHARACTER
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện CharacterService trong package RMI, được đăng ký với RegistryServer bằng tên RMICharacterService.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: studentCode="B15DCCN999", qCode="BAA62945".
 * 
 * 
 * b. Triệu gọi requestCharacter(studentCode, qCode) để nhận một chuỗi từ server.
 * 
 * 
 * c. Chuyển chuỗi về chữ thường, loại bỏ dấu câu và gom nhiều khoảng trắng thành một khoảng trắng.
 * 
 * 
 * d. Triệu gọi submitCharacter(studentCode, qCode, strSubmit) để gửi chuỗi đã chuẩn hóa về server.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * ticket Payment customer ticket shipping shipping Payment ticket REFUND shipping ticket.
 *  thì dữ liệu nộp lại là 
 * ticket payment customer ticket shipping shipping payment ticket refund shipping ticket
 * .
 */
public class RMI_1731_Chuan_Hoa_Cau {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1731";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Chuẩn hóa câu: chữ thường, bỏ dấu câu, gom khoảng trắng
            String clean = str.toLowerCase().replaceAll("\\p{Punct}", "").replaceAll("\\s+", " ").trim();

            // c. Gửi kết quả
            service.submitCharacter(sCode, qCode, clean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
