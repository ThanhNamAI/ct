package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1721] [RMI Data] Liệt kê số nguyên tố trong khoảng
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
 * b. Triệu gọi requestData(studentCode, qCode) để nhận chuỗi khoảng giá trị có dạng start;end.
 * 
 * 
 * c. Liệt kê tất cả số nguyên tố trong đoạn [start, end], theo thứ tự tăng dần.
 * 
 * 
 * d. Triệu gọi submitData(studentCode, qCode, data) để gửi List<Integer> kết quả về server.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 10;20
 *  thì dữ liệu nộp lại là 
 * [11, 13, 17, 19]
 * .
 */
public class RMI_1721_Liet_Ke_So_Nguyen_To_Trong_Khoang {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1721";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // a. Nhận dữ liệu
            Object data = service.requestData(sCode, qCode);

            // b. Xử lý và c. Gửi lại kết quả
            String rangeStr = String.valueOf(data);
            String[] parts = rangeStr.split(";");
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());
            List<Integer> primes = new ArrayList<>();
            for (int num = Math.max(2, start); num <= end; num++) {
                boolean isPrime = true;
                for (int i = 2; i * i <= num; i++) {
                    if (num % i == 0) { isPrime = false; break; }
                }
                if (isPrime) primes.add(num);
            }
            service.submitData(sCode, qCode, primes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
