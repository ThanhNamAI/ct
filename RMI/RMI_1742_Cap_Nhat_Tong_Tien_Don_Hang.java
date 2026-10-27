package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1742] [RMI Object] Cập nhật tổng tiền đơn hàng
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
 * public class OrderTotal implements java.io.Serializable {
 *     private static final long serialVersionUID = 20260517L;
 * 
 *     private String orderId;
 *     private double itemsSubtotal;
 *     private double shippingFee;
 *     private double discountRate;
 *     private double total;
 *     private String status;
 * 
 *     public OrderTotal() { }
 *     public OrderTotal(String orderId, double itemsSubtotal, double shippingFee, double discountRate) { /* gán 4 thuộc tính * / }
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
 * RMI.OrderTotal
 *  từ server.
 * 
 * 
 * c. Tính 
 * total = round2(itemsSubtotal * (1 - discountRate/100) + shippingFee)
 * . Thiết lập 
 * status
 *  là 
 * REVIEW
 *  nếu 
 * total >= 1000
 * , ngược lại 
 * READY
 * .
 * 
 * 
 * d. Triệu gọi 
 * submitObject(studentCode, qCode, object)
 *  để gửi lại đối tượng 
 * OrderTotal
 *  đã cập nhật.
 * 
 * 
 * e. Kết thúc chương trình client.
 * 
 * 
 * Ví dụ:
 *  nhận 
 * orderId=O7613; itemsSubtotal=593.77; shippingFee=17.71; discountRate=23.0; total=0.0; status=PENDING
 *  thì gửi lại 
 * orderId=O7613; itemsSubtotal=593.77; shippingFee=17.71; discountRate=23.0; total=474.91; status=READY
 * .
 */
public class RMI_1742_Cap_Nhat_Tong_Tien_Don_Hang {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1742";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Tính total và status
            OrderTotal ot = (OrderTotal) obj;
            double total = ot.getItemsSubtotal() * (1 - ot.getDiscountRate() / 100.0) + ot.getShippingFee();
            total = Math.round(total * 100.0) / 100.0;
            ot.setTotal(total);
            ot.setStatus(total >= 1000 ? "REVIEW" : "READY");

            // c. Gửi lại đối tượng
            service.submitObject(sCode, qCode, ot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
