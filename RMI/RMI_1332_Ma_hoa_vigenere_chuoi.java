package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1332] RMI - Character
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
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server với định dạng: "Từ khóa;Chuỗi đầu vào"
 * 
 * 
 * b. Thực hiện thao tác mã hóa Vigenère cho chuỗi nhận được. Biết rằng, mã hóa Vigenère thực hiện mã hóa mỗi ký tự trong chuỗi đầu vào được dịch đi một khoảng bằng với vị trí tương ứng của ký tự trong từ khóa. (Từ khóa được lặp lại để khớp với độ dài của chuỗi)
 * 
 * 
 * Ví dụ:
 *  chuỗi ban đầu 
 * PTIT;HELLO
 *  -> từ khóa 
 * PTIT
 *  và chuỗi mã hóa là: 
 * WXTED
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi chuỗi đã được mã hóa trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1332_Ma_hoa_vigenere_chuoi {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1332";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            int p = str.indexOf(';');
            String key = str.substring(0, p).toUpperCase();
            String text = str.substring(p + 1);
            StringBuilder sb = new StringBuilder();
            int ki = 0;
            for (char ch : text.toCharArray()) {
                int s = key.charAt(ki % key.length()) - 'A';
                if (Character.isUpperCase(ch)) { sb.append((char) ('A' + (ch - 'A' + s) % 26)); ki++; }
                else if (Character.isLowerCase(ch)) { sb.append((char) ('a' + (ch - 'a' + s) % 26)); ki++; }
                else sb.append(ch);
            }
            String res = sb.toString();
            service.submitCharacter(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
