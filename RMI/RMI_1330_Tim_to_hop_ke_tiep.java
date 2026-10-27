package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1330] RMI - Data
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
 * a. Triệu gọi phương thức requestData để nhận một chuỗi các số nguyên.
 * 
 * 
 * b. Sử dụng thuật toán sinh tổ hợp kế tiếp để tìm tổ hợp kế tiếp của chuỗi số này theo thứ tự từ điển. Nếu chuỗi đã là tổ hợp lớn nhất, trả về tổ hợp nhỏ nhất (sắp xếp lại từ đầu theo thứ tự từ điển).
 * 
 * 
 * Ví dụ:
 *  Với chuỗi 1, 2, 3 tổ hợp kế tiếp là 1, 3, 2. Nếu chuỗi là 3, 2, 1 (tổ hợp lớn nhất), kết quả sẽ là 1, 2, 3 (tổ hợp nhỏ nhất).
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi chuỗi (String) chứa tổ hợp kế tiếp đã tìm được trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1330_Tim_to_hop_ke_tiep {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1330";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            List<Integer> a = nums(data);
            int i = a.size() - 2;
            while (i >= 0 && a.get(i) >= a.get(i + 1)) i--;
            if (i >= 0) {
                int j = a.size() - 1;
                while (a.get(j) <= a.get(i)) j--;
                Collections.swap(a, i, j);
                Collections.reverse(a.subList(i + 1, a.size()));
            } else {
                Collections.sort(a);
            }
            StringBuilder sb = new StringBuilder();
            for (int t = 0; t < a.size(); t++) sb.append(t > 0 ? ", " : "").append(a.get(t));
            String res = sb.toString();
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
