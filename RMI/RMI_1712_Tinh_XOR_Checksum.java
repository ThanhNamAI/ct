package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1712] [RMI Byte] Tính XOR checksum
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
 * c. Tính XOR của tất cả các byte theo giá trị không dấu.
 * 
 * 
 * d. Triệu gọi submitData(studentCode, qCode, data) để gửi byte[] UTF-8 của checksum về server.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * [5, 7, 5]
 *  thì dữ liệu nộp lại là 
 * 7
 * .
 */
public class RMI_1712_Tinh_XOR_Checksum {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1712";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. XOR checksum của tất cả các byte (không dấu)
            int xorSum = 0;
            for (byte b : data) xorSum ^= (b & 0xFF);

            // c. Gửi kết quả (UTF-8 của chuỗi số)
            service.submitData(sCode, qCode, String.valueOf(xorSum).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
