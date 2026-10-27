package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1340] RMI - Character
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
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server với định dạng: "Chuỗi đầu vào".
 * 
 * 
 * b. Thực hiện đếm tần số xuất hiện của mỗi ký tự trong chuỗi đầu vào và tạo ra chuỗi kết quả theo định dạng <Ký tự><Số lần xuất hiện>, sắp xếp theo thứ tự xuất hiện của các ký tự trong chuỗi.
 * 
 * 
 * Ví dụ:
 *  Chuỗi đầu vào 
 * AAABBC
 *  -> Kết quả: 
 * A3B2C1
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi chuỗi kết quả trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1340_Nen_chuoi_dem_ky_tu_lap {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1340";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Đếm tần số mỗi ký tự, tạo chuỗi <Ký tự><Số lần> theo thứ tự xuất hiện
            Map<Character, Integer> freq = new LinkedHashMap<>();
            for (char ch : str.toCharArray()) freq.put(ch, freq.getOrDefault(ch, 0) + 1);
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Character, Integer> e : freq.entrySet()) sb.append(e.getKey()).append(e.getValue());

            // c. Gửi kết quả
            service.submitCharacter(sCode, qCode, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
