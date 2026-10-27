package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1313] RMI - Byte
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
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với dữ liệu byte nhận được từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestData để nhận một mảng dữ liệu byte từ server, đại diện cho một chuỗi dữ liệu byte tổng quát.
 * 
 * 
 * b. Thực hiện nén dữ liệu sử dụng thuật toán Run-Length Encoding (RLE) bằng cách ghi nhận mỗi byte và số lần lặp liên tiếp của nó. Kết quả nén là một mảng mới biểu diễn các cặp (byte, số lần lặp).
 * 
 * 
 * Ví dụ:
 *  Nếu dữ liệu byte nhận được là [10, 10, 10, 20, 20, 30, 30, 30, 30], chương trình sẽ thực hiện nén RLE như sau:
 * 
 * 
 * Kết quả nén RLE là mảng [10, 3, 20, 2, 30, 4].
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi mảng dữ liệu đã được nén RLE trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1313_Nen_rle_byte {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1313";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            for (int i = 0; i < data.length; ) {
                int j = i;
                while (j < data.length && data[j] == data[i]) j++;
                out.write(data[i]);
                out.write(j - i);
                i = j;
            }
            byte[] res = out.toByteArray();
            service.submitData(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
