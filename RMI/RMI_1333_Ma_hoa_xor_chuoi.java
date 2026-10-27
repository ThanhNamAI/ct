package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1333] RMI - Character
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
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server với định dạng: "Khóa XOR;Chuỗi đầu vào".
 * 
 * 
 * b. Thực hiện thao tác mã hóa XOR cho chuỗi đầu vào với khóa XOR nhận được từ server. Mã hóa XOR thực hiện bằng cách áp dụng phép XOR trên từng ký tự trong chuỗi đầu vào và ký tự tương ứng trong khóa (khóa được lặp lại để khớp độ dài chuỗi đầu vào).
 * 
 * 
 * Ví dụ:
 *  Chuỗi ban đầu 
 * A;HELLO
 *  -> Khóa 
 * A
 *  -> chuỗi mã hóa gồm các ký tự có mã [9, 4, 13, 13, 14] (ví dụ 'H' (72) XOR 'A' (65) = 9). Kết quả thường chứa ký tự không in được; gửi nguyên chuỗi này qua submitCharacter
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi chuỗi đã được mã hóa trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1333_Ma_hoa_xor_chuoi {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1333";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            int p = str.indexOf(';');
            String key = str.substring(0, p);
            String text = str.substring(p + 1);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                sb.append((char) (text.charAt(i) ^ key.charAt(i % key.length())));
            }
            String res = sb.toString();
            service.submitCharacter(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
