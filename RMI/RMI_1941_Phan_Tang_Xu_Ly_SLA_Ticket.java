package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1941] [RMI Object] Phân tầng xử lý SLA ticket
 * Loại: RMI_OBJECT
 *
 * Đề bài:
 * Một chương trình RMI Server cung cấp giao diện từ xa 
 * ObjectService
 * , được đăng ký với RegistryServer bằng tên 
 * RMIObjectService
 * .
 * 
 * 
 * Giao diện từ xa và lớp dữ liệu
 * 
 * 
 * Tạo interface và lớp dưới đây trong package 
 * RMI
 *  của project client, đúng tên, chữ ký phương thức, 
 * serialVersionUID
 * , tên và kiểu của từng thuộc tính.
 * 
 * 
 * package RMI;
 * 
 * public interface ObjectService extends java.rmi.Remote {
 *     java.io.Serializable requestObject(String studentCode, String qCode) throws java.rmi.RemoteException;
 *     void submitObject(String studentCode, String qCode, java.io.Serializable object) throws java.rmi.RemoteException;
 * }
 * 
 * 
 * package RMI;
 * 
 * public class TicketSla implements java.io.Serializable {
 *     private static final long serialVersionUID = 20260517L;
 * 
 *     private String ticketId;
 *     private String priority;      // LOW, MEDIUM, HIGH, CRITICAL
 *     private int openedHoursAgo;
 *     private boolean breached;
 *     private String action;
 * 
 *     public TicketSla() { }
 *     public TicketSla(String ticketId, String priority, int openedHoursAgo) { /* gán 3 thuộc tính * / }
 *     // getter/setter cho tất cả thuộc tính
 * }
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Dùng mã sinh viên và mã câu hỏi làm tham số khi gọi RMI. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="5AD2B818"
 * .
 * 
 * 
 * b. Triệu gọi 
 * requestObject(studentCode, qCode)
 *  để nhận đối tượng 
 * RMI.TicketSla
 * . Ví dụ: 
 * priority=HIGH; openedHoursAgo=10
 * .
 * 
 * 
 * c. Thiết lập 
 * breached = true
 *  khi vượt ngưỡng theo 
 * priority
 * :
 * 
 * 
 * 
 * 
 * CRITICAL
 * : 
 * openedHoursAgo > 2
 * 
 * 
 * HIGH
 * : 
 * openedHoursAgo > 8
 * 
 * 
 * MEDIUM
 * : 
 * openedHoursAgo > 24
 * 
 * 
 * LOW
 * : 
 * openedHoursAgo > 72
 * 
 * 
 * 
 * 
 * Thiết lập 
 * action
 * :
 * 
 * 
 * 
 * 
 * MONITOR
 *  nếu không vi phạm
 * 
 * 
 * ESCALATE_L2
 *  nếu vi phạm và (
 * priority
 *  là 
 * CRITICAL
 *  hoặc 
 * openedHoursAgo > 96
 * )
 * 
 * 
 * ESCALATE_L1
 *  cho các trường hợp vi phạm còn lại
 * 
 * 
 * 
 * 
 * d. Triệu gọi 
 * submitObject(studentCode, qCode, object)
 *  để gửi lại đối tượng đã cập nhật. Ví dụ: 
 * breached=true; action=ESCALATE_L1
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1941_Phan_Tang_Xu_Ly_SLA_Ticket {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1941";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Thiết lập breached và action theo ngưỡng của priority
            TicketSla t = (TicketSla) obj;
            String p = t.getPriority();
            int h = t.getOpenedHoursAgo();
            int limit;
            if ("CRITICAL".equals(p)) limit = 2;
            else if ("HIGH".equals(p)) limit = 8;
            else if ("MEDIUM".equals(p)) limit = 24;
            else limit = 72;
            boolean breached = h > limit;
            t.setBreached(breached);
            if (!breached) t.setAction("MONITOR");
            else if ("CRITICAL".equals(p) || h > 96) t.setAction("ESCALATE_L2");
            else t.setAction("ESCALATE_L1");

            // c. Gửi lại đối tượng
            service.submitObject(sCode, qCode, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
