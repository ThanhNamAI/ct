package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1328] RMI - Data
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
 * b. Xác định tất cả các bộ ba số nguyên (a, b, c) sao cho a² + b² = c² và a < b < c ≤ N. Kết quả trả về là danh sách mọi bộ ba thỏa mãn, kể cả các bộ là bội của bộ nguyên thủy (như 6, 8, 10), sắp theo a rồi b tăng dần.
 * 
 * 
 * Ví dụ:
 *  Với N = 20, kết quả là [[3, 4, 5], [5, 12, 13], [6, 8, 10], [8, 15, 17], [9, 12, 15], [12, 16, 20]].
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi đối tượng List<List<Integer>> danh sách các bộ ba số Pythagore đã tìm được trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1328_Tim_bo_ba_pytago {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1328";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            int n = nums(data).get(0);
            List<List<Integer>> res = new ArrayList<>();
            for (int a = 1; a <= n; a++) {
                for (int b = a + 1; b <= n; b++) {
                    int c2 = a * a + b * b;
                    int c = (int) Math.round(Math.sqrt(c2));
                    if (c <= n && c > b && c * c == c2) res.add(Arrays.asList(a, b, c));
                }
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
