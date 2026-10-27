package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1324] RMI - Data
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
 * a. Triệu gọi phương thức requestData để nhận một chuỗi gồm tập hợp số nguyên và một số nguyên K từ server với định dạng: “mảng; số nguyên K”.
 * 
 * 
 * b. Sử dụng thuật toán sinh tổ hợp để tạo ra tất cả các tổ hợp kích thước K của tập hợp đã cho. Kết quả trả về là danh sách các tổ hợp con có K phần tử.
 * 
 * 
 * Ví dụ:
 *  Với tập hợp [1, 2, 3, 4] và K = 2, kết quả là danh sách các tổ hợp [[1, 2], [1, 3], [1, 4], [2, 3], [2, 4], [3, 4]].
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi List<List<Integer>> chứa danh sách các tổ hợp đã sinh được trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1324_Sinh_to_hop_chap_k {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1324";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            String[] parts = String.valueOf(data).split(";");
            List<Integer> arr = nums(parts[0]);
            int k = nums(parts[1]).get(0);
            List<List<Integer>> res = new ArrayList<>();
            combine(arr, k, 0, new ArrayList<>(), res);
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

    // Sinh tổ hợp chập k của danh sách (theo thứ tự xuất hiện)
    static void combine(List<Integer> a, int k, int start, List<Integer> cur, List<List<Integer>> out) {
        if (cur.size() == k) { out.add(new ArrayList<>(cur)); return; }
        for (int i = start; i < a.size(); i++) {
            cur.add(a.get(i));
            combine(a, k, i + 1, cur, out);
            cur.remove(cur.size() - 1);
        }
    }
}
