package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1345] RMI - Object
 * Loại: RMI_OBJECT
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để quản lý thông tin các sự kiện trong hệ thống tổ chức sự kiện. Chương trình sẽ ngẫu nhiên tạo ra đối tượng Event với các giá trị ban đầu và cung cấp cho RMI client như sau:
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
 * Lớp Event gồm các thuộc tính: id String, eventName String, eventDate String, expectedAttendance int, eventCode String.
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20241131L;
 * 02 hàm khởi dựng:
 * 
 * 
 * public Event()
 * public Event(String id, String eventName, String eventDate, int expectedAttendance)
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface ObjectService và lớp Event được viết trong package RMI.
 * Đối tượng cài đặt giao diện từ xa ObjectService được đăng ký với RegistryServer: RMIObjectService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với đối tượng sự kiện được nhận từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestObject để nhận đối tượng Event ngẫu nhiên từ server.
 * 
 * 
 * b. Tạo mã eventCode cho sự kiện dựa trên các quy tắc sau:
 * 
 * 
 * Nếu expectedAttendance >= 1000, thêm 
 * L
 *  vào eventCode;
 * 
 * 
 * Nếu expectedAttendance từ 500 đến 999, thêm 
 * M
 *  vào eventCode;
 * 
 * 
 * Nếu expectedAttendance dưới 500, thêm 
 * S
 *  vào eventCode.
 * 
 * 
 * Thêm chữ cái đầu và cuối của eventName, tất cả viết hoa.
 * Thêm ngày và tháng từ eventDate (theo định dạng 
 * ddMM
 * ).
 * 
 * 
 * Ví dụ:
 *  Nếu sự kiện có tên là "Charity Run", ngày diễn ra là "2024-05-12" và số lượng người tham gia dự kiến là 1200, mã eventCode sẽ là 
 * LCN1205
 * 
 * 
 * c. Cập nhật giá trị eventCode trong đối tượng Event.
 * 
 * 
 * d. Triệu gọi phương thức submitObject để gửi đối tượng Event đã được xử lý trở lại server.
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1345_Tao_ma_su_kien_event {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1345";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Tạo mã eventCode
            Event ev = (Event) obj;
            int att = ev.getExpectedAttendance();
            String size = att >= 1000 ? "L" : (att >= 500 ? "M" : "S");
            String name = ev.getEventName().trim();
            String[] d = ev.getEventDate().trim().split("\\D+");
            // yyyy-MM-dd hoặc dd/MM/yyyy
            String dd = d[0].length() == 4 ? d[2] : d[0];
            String mm = d[1];
            String code = size + ("" + name.charAt(0) + name.charAt(name.length() - 1)).toUpperCase()
                    + String.format("%02d", Integer.parseInt(dd)) + String.format("%02d", Integer.parseInt(mm));

            // c. Cập nhật eventCode
            ev.setEventCode(code);

            // d. Gửi lại đối tượng
            service.submitObject(sCode, qCode, ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
