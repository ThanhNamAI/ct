package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1922] [RMI Data] Thống kê số thực nâng cao
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
 *  để nhận chuỗi CSV các số thực. Ví dụ: 
 * 10.00,20.00,30.00,40.00
 * .
 * 
 * 
 * c. Tính giá trị trung bình, độ lệch chuẩn trên toàn bộ dãy và phân vị 95 tại vị trí 
 * ceil(n * 0.95) - 1
 *  sau khi sắp xếp tăng dần.
 * 
 * 
 * d. Triệu gọi 
 * submitData(studentCode, qCode, data)
 *  để gửi chuỗi 
 * average=<avg>;stddev=<stddev>;p95=<p95>
 * , các số thực làm tròn 02 chữ số thập phân. Ví dụ: 
 * average=25.00;stddev=11.18;p95=40.00
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1922_Thong_Ke_So_Thuc_Nang_Cao {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1922";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            String csv = String.valueOf(data);
            String[] parts = csv.split(",");
            double[] arr = new double[parts.length];
            double sum = 0;
            for (int i = 0; i < parts.length; i++) {
                arr[i] = Double.parseDouble(parts[i].trim());
                sum += arr[i];
            }
            double avg = sum / arr.length;
            double var = 0;
            for (double v : arr) var += (v - avg) * (v - avg);
            double std = Math.sqrt(var / arr.length);
            Arrays.sort(arr);
            int p95Idx = (int) Math.ceil(0.95 * arr.length) - 1;
            double p95 = arr[Math.max(0, p95Idx)];
            service.submitData(sCode, qCode, String.format(Locale.US, "average=%.2f;stddev=%.2f;p95=%.2f", avg, std, p95));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
