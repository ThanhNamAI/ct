package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1336] RMI - Character
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
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server với định dạng: "Chuỗi số La Mã đầu vào".
 * 
 * 
 * b. Thực hiện chuyển đổi chuỗi số La Mã nhận được thành số thập phân (Decimal).
 * 
 * 
 * Quy tắc chuyển đổi: Các ký tự La Mã chính bao gồm: I=1, V=5, X=10, L=50, C=100, D=500, M=1000.
 * 
 * 
 * Ví dụ:
 *  
 * "MCMXCIV" -> 1994
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi số thập phân đã chuyển đổi trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1336_Doi_so_la_ma_sang_thap_phan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1336";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            Map<Character, Integer> v = new HashMap<>();
            v.put('I', 1); v.put('V', 5); v.put('X', 10); v.put('L', 50);
            v.put('C', 100); v.put('D', 500); v.put('M', 1000);
            String s = str.trim().toUpperCase();
            int total = 0;
            for (int i = 0; i < s.length(); i++) {
                int cur = v.get(s.charAt(i));
                if (i + 1 < s.length() && cur < v.get(s.charAt(i + 1))) total -= cur;
                else total += cur;
            }
            String res = String.valueOf(total);
            service.submitCharacter(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
