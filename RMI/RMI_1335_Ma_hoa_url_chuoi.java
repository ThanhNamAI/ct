package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1335] RMI - Character
 * Loại: RMI_CHARACTER
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để xử lý chuỗi.
 * 
 * 
 * Giao diện từ xa:
 * 
 * 
 * public interface CharacterService extends Remote {
 *     public String requestCharacter(String studentCode, String qCode) throws RemoteException;
 *     public void submitCharacter(String studentCode, String qCode, String strSubmit) throws RemoteException;
 * }
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface CharacterService được viết trong package RMI.
 * Đối tượng cài đặt giao diện từ xa CharacterService được đăng ký với RegistryServer với tên là: RMICharacterService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với chuỗi được nhận từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server. Chuỗi này gồm chữ, số, dấu cách và các ký tự đặc biệt như 
 * ~=+%^* /()[]{}!@#$?|
 * .
 * 
 * 
 * b. Thực hiện thao tác mã hóa URL (URL Encoding) cho chuỗi đầu vào nhận được từ server. Mã hóa theo quy tắc phần đường dẫn của URI (tương đương 
 * new java.net.URI(null, null, s, null).toASCIIString()
 * ): dấu cách, ký tự không hợp lệ trong URI (như 
 * % " < > [ ] { } | \ ^
 * ), 
 * ?
 * , 
 * #
 *  và ký tự ngoài ASCII được thay bằng 
 * %XX
 *  (UTF-8); chữ, số và 
 * - _ . ~ ! * ' ( ) ; : @ & = + $ , /
 *  giữ nguyên.
 * 
 * 
 * Ví dụ:
 *  Chuỗi ban đầu "Hello World!" -> Chuỗi mã hóa URL là: "Hello%20World!"
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi chuỗi đã được mã hóa trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1335_Ma_hoa_url_chuoi {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1335";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            String res = new java.net.URI(null, null, str, null).toASCIIString();
            service.submitCharacter(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
