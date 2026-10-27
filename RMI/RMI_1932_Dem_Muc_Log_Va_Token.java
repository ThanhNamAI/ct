package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1932] [RMI Character] Đếm mức log và token
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
 *  để nhận chuỗi log gồm nhiều dòng nối bằng 
 * ||
 * . Ví dụ: 
 * ERROR user=a token=abc||INFO user=b token=def
 * .
 * 
 * 
 * c. Đếm số dòng bắt đầu bằng 
 * ERROR
 * , 
 * INFO
 * , 
 * WARN
 * , đồng thời đếm số dòng có chứa 
 * token=
 * .
 * 
 * 
 * d. Triệu gọi 
 * submitCharacter(studentCode, qCode, strSubmit)
 *  để gửi chuỗi 
 * ERROR=n;INFO=n;WARN=n;TOKEN=n
 * . Ví dụ: 
 * ERROR=1;INFO=1;WARN=0;TOKEN=2
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1932_Dem_Muc_Log_Va_Token {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1932";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Đếm số dòng theo mức log và số dòng chứa token=
            String[] lines = str.split("\\|\\|");
            int error = 0, info = 0, warn = 0, token = 0;
            for (String l : lines) {
                l = l.trim();
                if (l.startsWith("ERROR")) error++;
                else if (l.startsWith("INFO")) info++;
                else if (l.startsWith("WARN")) warn++;
                if (l.contains("token=")) token++;
            }

            // c. Gửi kết quả
            service.submitCharacter(sCode, qCode,
                    "ERROR=" + error + ";INFO=" + info + ";WARN=" + warn + ";TOKEN=" + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
