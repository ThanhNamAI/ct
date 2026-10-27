package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1722] [RMI Data] Thống kê số thực
 * Loại: RMI_DATA
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện DataService trong package RMI, được đăng ký với RegistryServer bằng tên RMIDataService.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: studentCode="B15DCCN999", qCode="A1B2C3D4".
 * 
 * 
 * b. Triệu gọi requestData(studentCode, qCode) để nhận chuỗi CSV các số thực.
 * 
 * 
 * c. Tính average, p95 và số phần tử lớn hơn average theo cùng quy tắc của bài TCP Data Stream telemetry.
 * 
 * 
 * d. Triệu gọi submitData(studentCode, qCode, data) để gửi chuỗi average;p95;aboveAvg về server.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 10.00,20.00,30.00,40.00
 *  thì dữ liệu nộp lại là 
 * 25.00;40.00;2
 * .
 */
public class RMI_1722_Thong_Ke_So_Thuc {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1722";

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
            Arrays.sort(arr);
            double avg = sum / arr.length;
            int p95Idx = (int) Math.ceil(0.95 * arr.length) - 1;
            if (p95Idx < 0) p95Idx = 0;
            double p95 = arr[p95Idx];
            int above = 0;
            for (double v : arr) if (v > avg) above++;
            service.submitData(sCode, qCode, String.format(Locale.US, "%.2f;%.2f;%d", avg, p95, above));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
