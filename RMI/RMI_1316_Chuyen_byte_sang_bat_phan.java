package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1316] RMI - Byte
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
 * b. Chuyển đổi mỗi byte trong mảng dữ liệu nhận được thành chuỗi biểu diễn số bát phân (octal). Mỗi byte sẽ được chuyển thành một chuỗi số bát phân gồm ba chữ số (nếu cần, thêm các chữ số 0 ở đầu để đảm bảo đúng định dạng).
 * 
 * 
 * Ví dụ:
 *  Nếu mảng dữ liệu nhận được là [72, 101, 108], chương trình sẽ thực hiện chuyển đổi từng byte thành dạng bát phân:
 * 
 * 
 * 72 -> 
 * 110
 * 101 -> 
 * 145
 * 108 -> 
 * 154
 * 
 * 
 * Kết quả là chuỗi octal 
 * 110145154
 * . Chuyển chuỗi bát phân này trở lại thành mảng byte[] (mỗi ký tự trong chuỗi octal được mã hóa dưới dạng ASCII byte) để gửi về server.
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi mảng byte[] chứa chuỗi bát phân trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1316_Chuyen_byte_sang_bat_phan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1316";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            StringBuilder sb = new StringBuilder();
            for (byte b : data) sb.append(String.format("%03o", b & 0xFF));
            byte[] res = sb.toString().getBytes();
            service.submitData(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
