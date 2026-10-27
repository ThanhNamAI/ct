package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1347] RMI - Object
 * Loại: RMI_OBJECT
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để quản lý thông tin vé trong hệ thống bán vé sự kiện. Chương trình sẽ ngẫu nhiên tạo ra đối tượng Ticket với các giá trị ban đầu và cung cấp cho RMI client như sau:
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
 * Lớp Ticket gồm các thuộc tính: id String, eventName String, saleDate String, ticketCode String.
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20241133L;
 * 02 hàm khởi dựng:
 * 
 * 
 * public Ticket()
 * public Ticket(String id, String eventName, String saleDate)
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface ObjectService và lớp Ticket được viết trong package RMI.
 * Đối tượng cài đặt giao diện từ xa ObjectService được đăng ký với RegistryServer: RMIObjectService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với đối tượng vé được nhận từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestObject để nhận đối tượng Ticket ngẫu nhiên từ server.
 * 
 * 
 * b. Tạo mã ticketCode cho vé dựa trên các quy tắc sau:
 * 
 * 
 * Bắt đầu với hai chữ cái là: Chữ cái đầu tiên và cuối cùng của eventName, viết hoa.
 * Thêm ngày và tháng từ saleDate (theo định dạng 
 * MMdd
 * )
 * Kết thúc bằng hai chữ số là: Chữ số lớn nhất và nhỏ nhất không xuất hiện trong saleDate.
 * 
 * 
 * Ví dụ:
 *  tên sự kiện là "Charity Concert", và ngày bán là 
 * 15/06/2024
 * , mã ticketCode sẽ là 
 * CT061593
 * 
 * 
 * c. Cập nhật giá trị ticketCode trong đối tượng Ticket.
 * 
 * 
 * d. Triệu gọi phương thức submitObject để gửi đối tượng Ticket đã được xử lý trở lại server.
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1347_Tao_ma_ve_ticket {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1347";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Tạo mã ticketCode
            Ticket t = (Ticket) obj;
            String name = t.getEventName().trim();
            String saleDate = t.getSaleDate().trim();
            String[] d = saleDate.split("\\D+");
            // yyyy-MM-dd hoặc dd/MM/yyyy
            String dd = d[0].length() == 4 ? d[2] : d[0];
            String mm = d[1];
            boolean[] present = new boolean[10];
            for (char ch : saleDate.toCharArray()) if (Character.isDigit(ch)) present[ch - '0'] = true;
            char maxMissing = '?', minMissing = '?';
            for (int i = 0; i <= 9; i++) if (!present[i]) { minMissing = (char) ('0' + i); break; }
            for (int i = 9; i >= 0; i--) if (!present[i]) { maxMissing = (char) ('0' + i); break; }
            String code = ("" + name.charAt(0) + name.charAt(name.length() - 1)).toUpperCase()
                    + String.format("%02d", Integer.parseInt(mm)) + String.format("%02d", Integer.parseInt(dd))
                    + maxMissing + minMissing;

            // c. Cập nhật ticketCode
            t.setTicketCode(code);

            // d. Gửi lại đối tượng
            service.submitObject(sCode, qCode, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
