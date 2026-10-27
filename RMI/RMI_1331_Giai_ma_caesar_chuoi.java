package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1331] RMI - Character
 * Loại: RMI_CHARACTER
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để xử lý chuỗi. Cụ thể:
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
 * a. Triệu gọi phương thức requestCharacter để nhận chuỗi ngẫu nhiên từ server.
 * 
 * 
 * b. Thực hiện thao tác giải mã Caesar (mã hóa chuỗi bằng cách dịch từng ký tự đi một số lượng vị trí nhất định). Biết rằng giá trị dịch đúng bằng kích thước của chuỗi chia lấy dư cho 7.
 * 
 * 
 * c. Triệu gọi phương thức submitCharacter để gửi chuỗi đã được giải mã trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1331_Giai_ma_caesar_chuoi {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1331";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            // Giải mã Caesar: dịch lùi (độ dài chuỗi % 7) vị trí
            int shift = str.length() % 7;
            StringBuilder sb = new StringBuilder();
            for (char ch : str.toCharArray()) {
                if (Character.isUpperCase(ch)) ch = (char) ('A' + (ch - 'A' - shift + 26) % 26);
                else if (Character.isLowerCase(ch)) ch = (char) ('a' + (ch - 'a' - shift + 26) % 26);
                sb.append(ch);
            }
            String res = sb.toString();
            service.submitCharacter(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
