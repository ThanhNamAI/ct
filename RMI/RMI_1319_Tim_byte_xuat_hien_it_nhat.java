package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1319] RMI - Byte
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
 * b. Tìm phần tử có số lần xuất hiện ít nhất trong mảng, nếu có nhiều phần tử có cùng số lần xuất hiện ít nhất, chỉ cần trả về phần tử đầu tiên xuất hiện trong các phần tử đó
 * 
 * 
 * Ví dụ:
 *  Nếu mảng dữ liệu nhận được là [1, 2, 3, 2, 1], chương trình sẽ tìm ra phần tử xuất hiện ít nhất là 3 (xuất hiện 1 lần), nên mảng gửi lên server là [3, 1].
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi mảng byte kết quả chứa phẩn tử có số lần xuất hiện ít nhất và số lần xuất hiện trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1319_Tim_byte_xuat_hien_it_nhat {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1319";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            Map<Byte, Integer> cnt = new LinkedHashMap<>();
            for (byte b : data) cnt.merge(b, 1, Integer::sum);
            byte best = data[0];
            int bc = Integer.MAX_VALUE;
            for (Map.Entry<Byte, Integer> e : cnt.entrySet()) {
                if (e.getValue() < bc) { bc = e.getValue(); best = e.getKey(); }
            }
            byte[] res = new byte[]{best, (byte) bc};
            service.submitData(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
