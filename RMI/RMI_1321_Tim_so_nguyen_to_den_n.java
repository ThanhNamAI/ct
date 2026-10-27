package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1321] RMI - Data
 * Loại: RMI_DATA
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để xử lý dữ liệu.
 * 
 * 
 * Giao diện từ xa:
 * 
 * 
 * public interface DataService extends Remote {
 *     public Object requestData(String studentCode, String qCode) throws RemoteException;
 *     public void submitData(String studentCode, String qCode, Object data) throws RemoteException;
 * }
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface DataService được viết trong package RMI.
 * Đối tượng cài đặt giao diện từ xa DataService được đăng ký với RegistryServer với tên là: RMIDataService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với dữ liệu nhận được từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestData để nhận một số nguyên dương N từ server, đại diện cho giới hạn trên của khoảng cần kiểm tra.
 * 
 * 
 * b. Thực hiện tìm tất cả các số nguyên tố trong khoảng từ 1 đến N.
 * 
 * 
 * Ví dụ:
 *  Với N = 10, kết quả là danh sách các số nguyên tố 
 * 2, 3, 5, 7
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi List< Integer> danh sách các số nguyên tố đã tìm được trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1321_Tim_so_nguyen_to_den_n {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1321";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            int n = nums(data).get(0);
            List<Integer> res = new ArrayList<>();
            for (int i = 2; i <= n; i++) {
                boolean prime = true;
                for (int j = 2; (long) j * j <= i; j++) if (i % j == 0) { prime = false; break; }
                if (prime) res.add(i);
            }
            service.submitData(sCode, qCode, res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả số nguyên trong dữ liệu nhận về (chuỗi hoặc mảng)
    static List<Integer> nums(Object o) {
        String s = o instanceof int[] ? Arrays.toString((int[]) o)
                : o instanceof Object[] ? Arrays.toString((Object[]) o) : String.valueOf(o);
        List<Integer> r = new ArrayList<>();
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("-?\\d+").matcher(s);
        while (m.find()) r.add(Integer.parseInt(m.group()));
        return r;
    }
}
