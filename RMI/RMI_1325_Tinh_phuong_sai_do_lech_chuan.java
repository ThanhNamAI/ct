package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1325] RMI - Data
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
 * a. Triệu gọi phương thức requestData để nhận một chuỗi chá tập hợp các số thực từ server.
 * 
 * 
 * b. Tính toán phương sai (variance) và độ lệch chuẩn (standard deviation) của tập hợp dữ liệu này (làm tròn tới 02 chữ số thập phân)
 * 
 * 
 * Ví dụ:
 *  Với tập dữ liệu nhận được 
 * 2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0
 * , phương sai là 4.00 và độ lệch chuẩn là 2.00
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi chuỗi (kiểu String) chứa kết quả phương sai và độ lệch chuẩn trở lại server dưới dạng một cặp giá trị 
 * variance : standard deviation
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1325_Tinh_phuong_sai_do_lech_chuan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1325";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            List<Double> v = new ArrayList<>();
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("-?\\d+(\\.\\d+)?").matcher(String.valueOf(data));
            while (m.find()) v.add(Double.parseDouble(m.group()));
            double mean = 0;
            for (double x : v) mean += x;
            mean /= v.size();
            double var = 0;
            for (double x : v) var += (x - mean) * (x - mean);
            var /= v.size();
            String res = String.format(Locale.US, "%.2f : %.2f", var, Math.sqrt(var));
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
