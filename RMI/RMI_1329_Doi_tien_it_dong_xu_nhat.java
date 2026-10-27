package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1329] RMI - Data
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
 * a. Triệu gọi phương thức requestData để nhận một số nguyên dương amount từ server, đại diện cho số tiền cần đạt được.
 * 
 * 
 * b. Sử dụng thuật toán xếp đồng xu với các mệnh giá cố định [1, 2, 5, 10] để xác định số lượng đồng xu tối thiểu cần thiết để đạt được số tiền amount. Nếu không thể đạt được số tiền đó với các mệnh giá hiện có, trả về -1.
 * 
 * 
 * Ví dụ:
 *  Với amount = 18 và mệnh giá đồng xu cố định [1, 2, 5, 10], kết quả là 4 (18 = 10 + 5 + 2 + 1). Chuỗi cần gửi lên server là: 4; 10,5,2,1
 * 
 * 
 * c. Triệu gọi phương thức submitData để gửi chuỗi (kiểu String) chứa kết quả số lượng đồng xu tối thiểu và giá trị các đồng xu tương ứng trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class RMI_1329_Doi_tien_it_dong_xu_nhat {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1329";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            int amount = nums(data).get(0);
            int[] coins = {10, 5, 2, 1};
            List<Integer> used = new ArrayList<>();
            int rest = amount;
            for (int c : coins) {
                while (rest >= c) { used.add(c); rest -= c; }
            }
            String res;
            if (rest != 0) {
                res = "-1";
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < used.size(); i++) sb.append(i > 0 ? "," : "").append(used.get(i));
                res = used.size() + "; " + sb;
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
