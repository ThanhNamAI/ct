package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1318] RMI - Byte
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
 * 
 * 
 * Đối tượng cài đặt giao diện từ xa ByteService được đăng ký với RegistryServer với tên là: RMIByteService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với dữ liệu nhị phân nhận được từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestData để nhận một mảng dữ liệu nhị phân (byte[]) từ server.
 * 
 * 
 * b. Thực hiện phân chia mảng byte[] nhận được thành hai phần: phần đầu chứa các byte có giá trị chẵn và phần sau chứa các byte có giá trị lẻ, duy trì thứ tự xuất hiện của các phần tử trong từng nhóm.
 * 
 * 
 * Ví dụ:
 *  Nếu mảng dữ liệu nhận được là [1, 2, 3, 4, 5], sau khi phân chia chẵn-lẻ, mảng kết quả sẽ là [2, 4, 1, 3, 5] (tất cả phần tử chẵn được đặt trước, theo sau là tất cả phần tử lẻ).
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi mảng byte[] đã được phân chia chẵn-lẻ trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1318_Tach_byte_chan_le {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1318";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            byte[] res = new byte[data.length];
            int p = 0;
            for (byte b : data) if (b % 2 == 0) res[p++] = b;
            for (byte b : data) if (b % 2 != 0) res[p++] = b;
            service.submitData(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
