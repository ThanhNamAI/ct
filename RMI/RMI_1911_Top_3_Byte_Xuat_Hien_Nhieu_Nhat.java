package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

/**
 * [Mã câu hỏi: 1911] [RMI Byte] Top 3 byte xuất hiện nhiều nhất
 * Loại: RMI_BYTE
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện ByteService trong package RMI, được đăng ký với RegistryServer bằng tên RMIByteService.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="1D25ED92"
 * .
 * 
 * 
 * b. Triệu gọi 
 * requestData(studentCode, qCode)
 *  để nhận một mảng 
 * byte[]
 *  từ server. Ví dụ dữ liệu không dấu: 
 * [1,2,2,7,7,7,9]
 * .
 * 
 * 
 * c. Đếm tần suất từng giá trị byte không dấu, lấy tối đa 03 giá trị có tần suất cao nhất; nếu bằng tần suất thì chọn giá trị nhỏ hơn trước.
 * 
 * 
 * d. Triệu gọi 
 * submitData(studentCode, qCode, data)
 *  để gửi 
 * byte[]
 *  UTF-8 của chuỗi 
 * value=count|value=count
 * . Ví dụ: 
 * 7=3|2=2|1=1
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1911_Top_3_Byte_Xuat_Hien_Nhieu_Nhat {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1911";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ByteService service = (ByteService) registry.lookup("RMIByteService");

            // a. Nhận dữ liệu
            byte[] data = service.requestData(sCode, qCode);

            // b. Đếm tần suất, lấy tối đa 3 giá trị (tần suất giảm dần, bằng nhau thì giá trị nhỏ trước)
            int[] cnt = new int[256];
            for (byte b : data) cnt[b & 0xFF]++;
            List<Integer> vals = new ArrayList<>();
            for (int i = 0; i < 256; i++) if (cnt[i] > 0) vals.add(i);
            vals.sort((x, y) -> cnt[y] != cnt[x] ? Integer.compare(cnt[y], cnt[x]) : Integer.compare(x, y));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, vals.size()); i++) {
                if (i > 0) sb.append("|");
                sb.append(vals.get(i)).append("=").append(cnt[vals.get(i)]);
            }

            // c. Gửi kết quả (UTF-8)
            service.submitData(sCode, qCode, sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
