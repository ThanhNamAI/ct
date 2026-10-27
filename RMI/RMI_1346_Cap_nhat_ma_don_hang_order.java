package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1346] RMI - Object
 * Loại: RMI_OBJECT
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để quản lý thông tin đơn hàng trong hệ thống thương mại điện tử. Chương trình sẽ ngẫu nhiên tạo ra đối tượng Order với các giá trị ban đầu và cung cấp cho RMI client như sau:
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
 * Lớp Order gồm các thuộc tính: id String, customerCode String, orderDate String, shippingType String, orderCode String.
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20241132L;
 * 02 hàm khởi dựng:
 * 
 * 
 * public Order()
 * public Order(String id, String customerCode, String orderDate, String shippingType)
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface ObjectService và lớp Order được viết trong package RMI.
 * Đối tượng cài đặt giao diện từ xa ObjectService được đăng ký với RegistryServer: RMIObjectService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với đối tượng đơn hàng được nhận từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestObject để nhận đối tượng Order ngẫu nhiên từ server.
 * 
 * 
 * b. Tạo mã orderCode cho đơn hàng dựa trên các quy tắc sau:
 * 
 * 
 * Bắt đầu bằng hai ký tự đầu của shippingType, viết in hoa.
 * Kế đến là ba ký tự cuối của customerCode.
 * Cuối cùng là ngày và tháng từ orderDate (theo định dạng 
 * ddMM
 * ).
 * 
 * 
 * Ví dụ:
 *  Nếu đơn hàng có mã khách hàng là 
 * C123456
 * , ngày đặt hàng là "2023-10-05", và loại giao hàng là 
 * Express
 * , thì mã orderCode sẽ là 
 * EX4560510
 * 
 * 
 * c. Cập nhật giá trị orderCode trong đối tượng Order.
 * 
 * 
 * d. Triệu gọi phương thức submitObject để gửi đối tượng Order đã được xử lý trở lại server.
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1346_Cap_nhat_ma_don_hang_order {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1346";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Tạo mã orderCode
            Order o = (Order) obj;
            String ship = o.getShippingType().trim();
            String cust = o.getCustomerCode().trim();
            String[] d = o.getOrderDate().trim().split("\\D+");
            // yyyy-MM-dd hoặc dd/MM/yyyy
            String dd = d[0].length() == 4 ? d[2] : d[0];
            String mm = d[1];
            String code = ship.substring(0, 2).toUpperCase() + cust.substring(cust.length() - 3)
                    + String.format("%02d", Integer.parseInt(dd)) + String.format("%02d", Integer.parseInt(mm));

            // c. Cập nhật orderCode
            o.setOrderCode(code);

            // d. Gửi lại đối tượng
            service.submitObject(sCode, qCode, o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
