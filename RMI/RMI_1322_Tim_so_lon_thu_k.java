package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1322] RMI - Data
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
 * a. Triệu gọi phương thức requestData để nhận một chuỗi chứa mảng số nguyên và một số nguyên K từ server với định dạng: “mảng; số nguyên K”.
 * 
 * 
 * b. Sử dụng thuật toán phù hợp để tìm phần tử lớn thứ K trong mảng số nguyên đã cho.
 * 
 * 
 * Ví dụ:
 *  Với Chuỗi 
 * 3, 1, 5, 12, 2, 11; 3
 *  nghĩa là mảng [3, 1, 5, 12, 2, 11] và K = 3 -> cần tìm phần tử lớn thứ 3 là 5.
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi kết quả (kiểu int) phần tử lớn thứ K đã tìm được trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1322_Tim_so_lon_thu_k {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1322";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            String[] parts = String.valueOf(data).split(";");
            List<Integer> arr = nums(parts[0]);
            int k = nums(parts[1]).get(0);
            List<Integer> sorted = new ArrayList<>(arr);
            sorted.sort(Collections.reverseOrder());
            int res = sorted.get(k - 1);
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
