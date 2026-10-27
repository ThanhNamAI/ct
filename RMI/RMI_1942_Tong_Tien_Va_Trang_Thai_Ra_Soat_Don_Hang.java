package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1942] [RMI Object] Tổng tiền và trạng thái rà soát đơn hàng
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
 * studentCode="B21DCCN001"
 * , 
 * qCode="5AD2B818"
 * .
 * 
 * 
 * b. Triệu gọi 
 * requestObject(studentCode, qCode)
 *  để nhận đối tượng 
 * RMI.OrderTotal
 * . Ví dụ: 
 * itemsSubtotal=1200; shippingFee=20; discountRate=10
 * .
 * 
 * 
 * c. Tính 
 * total = round2(itemsSubtotal * (1 - discountRate/100) + shippingFee)
 * , rồi thiết lập 
 * status
 * :
 * 
 * 
 * 
 * 
 * VIP_REVIEW
 *  nếu 
 * total >= 1500
 * 
 * 
 * REVIEW
 *  nếu 
 * 800 <= total < 1500
 * 
 * 
 * READY
 *  trong các trường hợp còn lại
 * 
 * 
 * 
 * 
 * d. Triệu gọi 
 * submitObject(studentCode, qCode, object)
 *  để gửi lại đối tượng đã cập nhật. Ví dụ: 
 * total=1100.00; status=REVIEW
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1942_Tong_Tien_Va_Trang_Thai_Ra_Soat_Don_Hang {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "Q1942";

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
            if (total >= 1500) ot.setStatus("VIP_REVIEW");
            else if (total >= 800) ot.setStatus("REVIEW");
            else ot.setStatus("READY");

            // c. Gửi lại đối tượng
            service.submitObject(sCode, qCode, ot);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
