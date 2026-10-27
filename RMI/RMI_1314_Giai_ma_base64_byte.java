package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1314] RMI - Byte
 * Loại: RMI_BYTE
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để xử lý dữ liệu nhị phân.
 * 
 * 
 * Giao diện từ xa:
 * 
 * 
 * public interface ByteService extends Remote {
 *     public byte[] requestData(String studentCode, String qCode) throws RemoteException;
 *     public void submitData(String studentCode, String qCode, byte[] data) throws RemoteException;
 * }
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface ByteService được viết trong package RMI.
 * Đối tượng cài đặt giao diện từ xa ByteService được đăng ký với RegistryServer với tên là: RMIByteService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với dữ liệu nhị phân đã mã hóa dưới dạng Base64 nhận được từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestData để nhận một mảng dữ liệu nhị phân (byte[]) từ server, trong đó mảng này chứa một chuỗi Base64 được mã hóa.
 * 
 * 
 * b. Thực hiện giải mã dữ liệu nhận được từ định dạng Base64 về dữ liệu gốc. Sử dụng giải mã Base64 để chuyển mảng byte Base64 thành mảng byte nguyên bản ban đầu.
 * 
 * 
 * Ví dụ:
 *  Nếu dữ liệu Base64 nhận được là chuỗi mã hóa SGVsbG8gV29ybGQ= (tương ứng với chuỗi "Hello World" trong ASCII), chương trình sẽ thực hiện giải mã để nhận được mảng dữ liệu nhị phân [72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100].
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi mảng dữ liệu gốc đã giải mã trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1314_Giai_ma_base64_byte {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1314";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            byte[] res = Base64.getDecoder().decode(data);
            service.submitData(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
