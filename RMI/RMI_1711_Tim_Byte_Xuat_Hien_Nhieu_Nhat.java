package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1711] [RMI Byte] Tìm byte xuất hiện nhiều nhất
 * Loại: RMI_BYTE
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện ByteService trong package RMI, được đăng ký với RegistryServer bằng tên RMIByteService.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: studentCode="B15DCCN999", qCode="1D25ED92".
 * 
 * 
 * b. Triệu gọi requestData(studentCode, qCode) để nhận một mảng byte[] từ server.
 * 
 * 
 * c. Tìm giá trị byte không dấu xuất hiện nhiều nhất. Nếu có nhiều giá trị cùng tần suất cao nhất, chọn giá trị đạt tần suất đó trước trong quá trình duyệt dữ liệu.
 * 
 * 
 * d. Triệu gọi submitData(studentCode, qCode, data) để gửi byte[] UTF-8 của chuỗi value:count về server.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * [1, 2, 3, 2, 1, 2]
 *  thì dữ liệu nộp lại là 
 * 2:3
 * .
 */
public class RMI_1711_Tim_Byte_Xuat_Hien_Nhieu_Nhat {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1711";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Tìm byte không dấu xuất hiện nhiều nhất (ưu tiên giá trị đạt tần suất đó trước)
            int[] cnt = new int[256];
            int bestVal = -1, maxF = 0;
            for (byte b : data) {
                int val = b & 0xFF;
                cnt[val]++;
                if (cnt[val] > maxF) {
                    maxF = cnt[val];
                    bestVal = val;
                }
            }

            // c. Gửi kết quả dạng value:count (UTF-8)
            service.submitData(sCode, qCode, (bestVal + ":" + maxF).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
