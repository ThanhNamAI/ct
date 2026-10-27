package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1344] RMI - Object
 * Loại: RMI_OBJECT
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để xử lý thông tin sinh viên trong hệ thống quản lý giáo dục. Chương trình sẽ ngẫu nhiên tạo ra đối tượng Student với các giá trị ban đầu và cung cấp cho RMI client như sau:
 * 
 * 
 * Giao diện từ xa:
 * 
 * 
 * public interface ObjectService extends Remote {
 *     public Serializable requestObject(String studentCode, String qCode) throws RemoteException;
 *     public void submitObject(String studentCode, String qCode, Serializable object) throws RemoteException;
 * }
 * 
 * 
 * Lớp Student gồm các thuộc tính: id String, name String, enrollmentYear int, code String.
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20241130L;
 * 
 * 
 * 02 hàm khởi dựng:
 * 
 * 
 * public Student()
 * public Student(String id, String name, int enrollmentYear)
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface ObjectService và lớp Student được viết trong package RMI.
 * 
 * 
 * Đối tượng cài đặt giao diện từ xa ObjectService được đăng ký với RegistryServer: RMIObjectService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với đối tượng sinh viên được nhận từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestObject để nhận đối tượng Student ngẫu nhiên từ server.
 * 
 * 
 * b. Thực hiện
 * 
 * 
 * Tạo mã code cho sinh viên dựa trên các quy tắc sau:
 * 
 * 
 * Bắt đầu bằng ký tự 
 * B
 * .
 * Kế đến là hai chữ số cuối của enrollmentYear.
 * Sau đó là TÊN của sinh viên, tất cả các ký tự của tên viết in hoa.
 * Kết thúc với các chữ cái đầu tiên của Họ và Họ lót, đều viết in hoa.
 * 
 * 
 * Ví dụ:
 *  Nếu sinh viên có tên là "Nguyen Van Tuan", năm nhập học là 2022, mã code sẽ là 
 * B22TUAN_NV
 * 
 * 
 * Chuẩn hóa tên (name) theo quy tắc:
 * 
 * 
 * Chữ cái đầu tiên của Tên, Họ, và Tên lót phải viết hoa, các chữ cái khác viết thường.
 * Ví dụ: Nếu name ban đầu là "nguYEn van tAi tUAN", sau khi chuẩn hóa sẽ trở thành "Nguyen Van Tai Tuan".
 * 
 * 
 * c. Cập nhật giá trị mã (code) và tên (name) trong đối tượng Student và
 * 
 * 
 * d. Triệu gọi phương thức submitObject để gửi đối tượng Student đã được xử lý trở lại server.
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1344_Tao_ma_code_sinh_vien {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1344";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Chuẩn hóa tên và tạo mã code
            Student s = (Student) obj;
            String[] words = s.getName().trim().split("\\s+");
            StringBuilder normName = new StringBuilder();
            StringBuilder initials = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                String w = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
                if (i > 0) normName.append(" ");
                normName.append(w);
                if (i < words.length - 1) initials.append(w.charAt(0));
            }
            String givenName = words[words.length - 1].toUpperCase();
            String code = "B" + String.format("%02d", s.getEnrollmentYear() % 100) + givenName + "_" + initials;

            // c. Cập nhật code và name
            s.setName(normName.toString());
            s.setCode(code);

            // d. Gửi lại đối tượng
            service.submitObject(sCode, qCode, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
