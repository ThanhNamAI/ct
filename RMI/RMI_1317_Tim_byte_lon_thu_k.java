package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1317] RMI - Byte
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
 * a. Triệu gọi phương thức requestData để nhận một mảng dữ liệu nhị phân (byte[]) từ server, cùng với một số nguyên K, đại diện cho thứ tự phần tử cần tìm.
 * 
 * 
 * b. Tìm phần tử lớn thứ K trong mảng byte[] với K là phần tử cuối cùng của mảng.
 * 
 * 
 * Ví dụ:
 *  Nếu mảng dữ liệu nhận được là [3, 1, 5, 12, 2, 11, 3], giá trị K = 3, chương trình sẽ tìm phần tử lớn thứ ba -> Kết quả là 5.
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi mảng byte gồm phần tử lớn thứ K và vị trí của K trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1317_Tim_byte_lon_thu_k {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1317";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            // K là phần tử cuối cùng của mảng
            int n = data.length - 1;
            int k = data[n];
            Integer[] idx = new Integer[n];
            for (int i = 0; i < n; i++) idx[i] = i;
            // Sắp xếp chỉ số theo giá trị giảm dần (ổn định)
            Arrays.sort(idx, (x, y) -> Byte.compare(data[y], data[x]));
            int pos = idx[k - 1];
            byte[] res = new byte[]{data[pos], (byte) pos};
            service.submitData(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
