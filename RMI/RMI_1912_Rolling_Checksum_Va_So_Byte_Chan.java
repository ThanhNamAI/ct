package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1912] [RMI Byte] Rolling checksum và số byte chẵn
 * Loại: RMI_BYTE
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện ByteService trong package RMI, được đăng ký với RegistryServer bằng tên RMIByteService.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="1D25ED92"
 * .
 * 
 * 
 * b. Triệu gọi 
 * requestData(studentCode, qCode)
 *  để nhận một mảng 
 * byte[]
 *  từ server. Ví dụ dữ liệu không dấu: 
 * [5,7,10]
 * .
 * 
 * 
 * c. Tính checksum theo công thức lặp 
 * checksum = (checksum * 31 + value) mod 100000
 * , đồng thời đếm số byte có giá trị chẵn.
 * 
 * 
 * d. Triệu gọi 
 * submitData(studentCode, qCode, data)
 *  để gửi 
 * byte[]
 *  UTF-8 của chuỗi 
 * checksum=<n>;evenBytes=<m>
 * . Ví dụ: 
 * checksum=5032;evenBytes=1
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1912_Rolling_Checksum_Va_So_Byte_Chan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1912";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Rolling checksum và đếm số byte chẵn
            int evenCount = 0;
            long checksum = 0;
            for (byte b : data) {
                int v = b & 0xFF;
                if (v % 2 == 0) evenCount++;
                checksum = (checksum * 31 + v) % 100000;
            }

            // c. Gửi kết quả (UTF-8)
            String res = "checksum=" + checksum + ";evenBytes=" + evenCount;
            service.submitData(sCode, qCode, res.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
