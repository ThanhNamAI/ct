package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1921] [RMI Data] Tìm vị trí đỉnh cục bộ
 * Loại: RMI_DATA
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện DataService trong package RMI, được đăng ký với RegistryServer bằng tên RMIDataService.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="A1B2C3D4"
 * .
 * 
 * 
 * b. Triệu gọi 
 * requestData(studentCode, qCode)
 *  để nhận một mảng số nguyên từ server. Ví dụ: 
 * [3,8,5,10,7]
 * .
 * 
 * 
 * c. Một phần tử là đỉnh cục bộ nếu lớn hơn phần tử liền kề bên trái và bên phải; phần tử đầu/cuối chỉ cần lớn hơn phần tử kề duy nhất. Trả về vị trí theo chỉ số bắt đầu từ 1.
 * 
 * 
 * d. Triệu gọi 
 * submitData(studentCode, qCode, data)
 *  để gửi 
 * List<Integer>
 *  các vị trí đỉnh cục bộ. Ví dụ: 
 * [2,4]
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1921_Tim_Vi_Tri_Dinh_Cuc_Bo {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1921";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Chuyển dữ liệu nhận được thành danh sách số nguyên
            List<Integer> nums = new ArrayList<>();
            if (data instanceof int[]) {
                for (int v : (int[]) data) nums.add(v);
            } else if (data instanceof Object[]) {
                for (Object v : (Object[]) data) nums.add(Integer.parseInt(String.valueOf(v).trim()));
            } else if (data instanceof Collection) {
                for (Object v : (Collection<?>) data) nums.add(Integer.parseInt(String.valueOf(v).trim()));
            } else {
                String csv = String.valueOf(data).replaceAll("[\\[\\]\\s]", "");
                for (String p : csv.split(",")) if (!p.isEmpty()) nums.add(Integer.parseInt(p));
            }
            int n = nums.size();

            // c. Tìm vị trí đỉnh cục bộ (chỉ số từ 1)
            List<Integer> peaks = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                boolean left = (i == 0 || nums.get(i) > nums.get(i - 1));
                boolean right = (i == n - 1 || nums.get(i) > nums.get(i + 1));
                if (left && right) peaks.add(i + 1);
            }

            // d. Gửi kết quả
            service.submitData(sCode, qCode, peaks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
