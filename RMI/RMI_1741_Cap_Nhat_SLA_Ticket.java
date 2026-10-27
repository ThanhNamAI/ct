package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1741] [RMI Object] Cập nhật SLA ticket
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
 * studentCode="B15DCCN999"
 * , 
 * qCode="5AD2B818"
 * .
 * 
 * 
 * b. Triệu gọi 
 * requestObject(studentCode, qCode)
 *  để nhận đối tượng 
 * RMI.TicketSla
 *  từ server.
 * 
 * 
 * c. Thiết lập 
 * breached = true
 *  nếu thỏa một trong các điều kiện sau, ngược lại 
 * false
 * :
 * 
 * 
 * 
 * 
 * priority
 *  là 
 * CRITICAL
 *  và 
 * openedHoursAgo > 4
 * 
 * 
 * priority
 *  là 
 * HIGH
 *  và 
 * openedHoursAgo > 12
 * 
 * 
 * mọi 
 * priority
 *  có 
 * openedHoursAgo > 48
 * 
 * 
 * 
 * 
 * Thiết lập 
 * action
 *  là 
 * ESCALATE
 *  nếu 
 * breached
 * , ngược lại 
 * MONITOR
 * .
 * 
 * 
 * d. Triệu gọi 
 * submitObject(studentCode, qCode, object)
 *  để gửi lại đối tượng 
 * TicketSla
 *  đã cập nhật.
 * 
 * 
 * e. Kết thúc chương trình client.
 * 
 * 
 * Ví dụ:
 *  nhận 
 * ticketId=T6287; priority=LOW; openedHoursAgo=47; breached=false; action=""
 *  thì gửi lại 
 * ticketId=T6287; priority=LOW; openedHoursAgo=47; breached=false; action=MONITOR
 * .
 */
public class RMI_1741_Cap_Nhat_SLA_Ticket {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1741";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Thiết lập breached và action
            TicketSla t = (TicketSla) obj;
            String p = t.getPriority();
            int h = t.getOpenedHoursAgo();
            boolean breached = ("CRITICAL".equals(p) && h > 4)
                    || ("HIGH".equals(p) && h > 12)
                    || h > 48;
            t.setBreached(breached);
            t.setAction(breached ? "ESCALATE" : "MONITOR");

            // c. Gửi lại đối tượng
            service.submitObject(sCode, qCode, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
