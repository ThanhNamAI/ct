package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1338] RMI - Character
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
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server với định dạng: "Chuỗi văn bản đầu vào".
 * 
 * 
 * b. Thực hiện thao tác đếm tần số xuất hiện của từng ký tự trong chuỗi đầu vào. Kết quả trả về là danh sách các ký tự kèm theo số lần xuất hiện của mỗi ký tự.
 * 
 * 
 * Ví dụ:
 *  Chuỗi ban đầu "Hello world" -> Kết quả đếm tần số ký tự: {
 * H
 * : 1, 
 * e
 * : 1, 
 * l
 * : 3, 
 * o
 * : 2, " ": 1, 
 * w
 * : 1, 
 * r
 * : 1, 
 * d
 * : 1}.
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi kết quả đếm tần số ký tự trở lại server dưới dạng chuỗi kết quả đã được định dạng.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1338_Dem_tan_so_ky_tu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1338";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Đếm tần số xuất hiện của từng ký tự (giữ thứ tự xuất hiện)
            Map<Character, Integer> freq = new LinkedHashMap<>();
            for (char ch : str.toCharArray()) freq.put(ch, freq.getOrDefault(ch, 0) + 1);
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<Character, Integer> e : freq.entrySet()) {
                if (!first) sb.append(", ");
                first = false;
                sb.append(e.getKey() == ' ' ? "\" \"" : String.valueOf(e.getKey())).append(": ").append(e.getValue());
            }
            sb.append("}");

            // c. Gửi kết quả
            service.submitCharacter(sCode, qCode, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
