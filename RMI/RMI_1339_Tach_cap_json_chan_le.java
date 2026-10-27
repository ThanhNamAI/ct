package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1339] RMI - Character
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
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi JSON ngẫu nhiên từ server với định dạng: "Chuỗi JSON đầu vào".
 * 
 * 
 * b. Phân tích cú pháp chuỗi JSON nhận được và trích xuất các cặp key: value dựa trên vị trí của chúng:
 * 
 * 
 * Các cặp key: value ở vị trí chẵn sẽ được đưa vào chuỗi đầu tiên.
 * Các cặp key: value ở vị trí lẻ sẽ được đưa vào chuỗi thứ hai.
 * Hai chuỗi kết quả sẽ được nối với nhau và phân tách bởi dấu ;
 * 
 * 
 * Ví dụ:
 *  Chuỗi JSON ban đầu {
 * name
 * : 
 * Alice
 * , 
 * age
 * : 25, 
 * city
 * : 
 * Wonderland
 * , 
 * country
 * : 
 * Fictionland
 * } -> Kết quả trích xuất: 
 * name: Alice, city: Wonderland; age: 25, country: Fictionland
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi chuỗi kết quả trích xuất đã được định dạng trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1339_Tach_cap_json_chan_le {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1339";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Phân tích JSON, tách các cặp key: value ở vị trí chẵn và lẻ
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("\"([^\"]*)\"\\s*:\\s*(?:\"([^\"]*)\"|([^,}\\s]+))").matcher(str);
            List<String> evenPairs = new ArrayList<>();
            List<String> oddPairs = new ArrayList<>();
            int idx = 0;
            while (m.find()) {
                String val = m.group(2) != null ? m.group(2) : m.group(3);
                String pair = m.group(1) + ": " + val;
                if (idx % 2 == 0) evenPairs.add(pair);
                else oddPairs.add(pair);
                idx++;
            }
            String res = String.join(", ", evenPairs) + "; " + String.join(", ", oddPairs);

            // c. Gửi kết quả
            service.submitCharacter(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
