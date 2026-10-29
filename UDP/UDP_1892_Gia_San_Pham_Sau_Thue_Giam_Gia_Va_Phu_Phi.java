package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 1892] [UDP Object] Giá sản phẩm sau thuế, giảm giá và phụ phí
 * Loại: UDP_OBJECT
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209, sử dụng Java serialization để trao đổi đối tượng.
 * 
 * 
 * Lớp dữ liệu trao đổi
 * 
 * 
 * Tạo lớp dưới đây trong project client, đúng package, tên lớp, 
 * serialVersionUID
 * , tên và kiểu của từng thuộc tính. Nếu khác, server không đọc được đối tượng gửi lên.
 * 
 * 
 * package UDP;
 * 
 * public class PricedProduct implements java.io.Serializable {
 *     private static final long serialVersionUID = 20260517L;
 * 
 *     private String id;
 *     private String name;
 *     private double basePrice;
 *     private double taxRate;
 *     private double discountRate;
 *     private double finalPrice;
 * 
 *     public PricedProduct() { }
 *     public PricedProduct(String id, String name, double basePrice, double taxRate, double discountRate) { /* gán 5 thuộc tính * / }
 *     // getter/setter cho tất cả thuộc tính
 * }
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi datagram chứa chuỗi theo định dạng 
 * ;studentCode;qCode
 * . Ví dụ: 
 * ;B21DCCN001;EE29C059
 * .
 * 
 * 
 * b. Nhận datagram phản hồi: 08 byte đầu là chuỗi 
 * requestId
 * , các byte còn lại là đối tượng 
 * UDP.PricedProduct
 *  đã serialize. Ví dụ: 
 * basePrice=1200; taxRate=10; discountRate=5
 * .
 * 
 * 
 * c. Tính 
 * finalPrice = round2(basePrice * (1 + taxRate/100) * (1 - discountRate/100) + fee)
 * , trong đó 
 * fee = 25
 *  nếu 
 * basePrice >= 1000
 * , ngược lại 
 * fee = 0
 * .
 * 
 * 
 * d. Gửi datagram gồm 08 byte 
 * requestId
 *  ban đầu, theo sau là đối tượng 
 * PricedProduct
 *  đã cập nhật 
 * finalPrice
 * . Ví dụ: 
 * finalPrice=1279.00
 * .
 * 
 * 
 * e. Đóng socket và kết thúc chương trình.
 */
public class UDP_1892_Gia_San_Pham_Sau_Thue_Giam_Gia_Va_Phu_Phi {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "Q1892";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // a. Gửi thông điệp
            String req = ";" + sCode + ";" + qCode;
            byte[] reqData = req.getBytes();
            socket.send(new DatagramPacket(reqData, reqData.length, ip, port));

            // b. Nhận 8 byte requestId + object
            byte[] buf = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            byte[] reqIdBytes = Arrays.copyOfRange(packet.getData(), 0, 8);
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 8, packet.getLength() - 8);
            ObjectInputStream ois = new ObjectInputStream(bais);

            // c. Xử lý object
            PricedProduct p = (PricedProduct) ois.readObject();
            double fee = p.getBasePrice() >= 1000 ? 25 : 0;
            double fp = p.getBasePrice() * (1 + p.getTaxRate() / 100.0) * (1 - p.getDiscountRate() / 100.0) + fee;
            p.setFinalPrice(Math.round(fp * 100) / 100.0);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(reqIdBytes);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(p);
            oos.flush();

            // d. Gửi lại kết quả
            byte[] ansData = baos.toByteArray();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
