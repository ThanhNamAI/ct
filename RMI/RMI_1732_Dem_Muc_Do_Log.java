package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1732] [RMI Character] Đếm mức độ log
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
 * b. Triệu gọi requestCharacter(studentCode, qCode) để nhận chuỗi log gồm nhiều dòng được nối bằng ký tự ||.
 * 
 * 
 * c. Đếm số dòng bắt đầu bằng INFO, WARN và ERROR.
 * 
 * 
 * d. Triệu gọi submitCharacter(studentCode, qCode, strSubmit) để gửi kết quả theo định dạng ERROR=n;INFO=n;WARN=n.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * ERROR service=api0 code=E017||WARN service=api1 code=NONE||INFO service=api2 code=NONE
 *  thì dữ liệu nộp lại là 
 * ERROR=1;INFO=1;WARN=1
 * .
 */
public class RMI_1732_Dem_Muc_Do_Log {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1732";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // a. Nhận chuỗi
            String str = service.requestCharacter(sCode, qCode);

            // b. Đếm số dòng bắt đầu bằng INFO, WARN, ERROR
            String[] lines = str.split("\\|\\|");
            int info = 0, warn = 0, error = 0;
            for (String l : lines) {
                l = l.trim();
                if (l.startsWith("INFO")) info++;
                else if (l.startsWith("WARN")) warn++;
                else if (l.startsWith("ERROR")) error++;
            }

            // c. Gửi kết quả
            service.submitCharacter(sCode, qCode, "ERROR=" + error + ";INFO=" + info + ";WARN=" + warn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
