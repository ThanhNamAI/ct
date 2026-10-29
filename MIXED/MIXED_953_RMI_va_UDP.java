package MIXED;

import java.io.*;
import java.net.*;
import java.rmi.registry.*;
import java.util.*;
import RMI.*;

/**
 * [Mã câu hỏi: 953] RMI + UDP
 * Loại: MIXED
 *
 * Đề bài:
 * Một hệ thống phần mềm cung cấp các mã khuyến mãi sản phẩm ngẫu nhiên cho sinh viên, bao gồm 02 phần được mô tả như sau:
 * 
 * 
 * a. Phần thứ nhất gọi là RMI Server cung cấp chức năng cho phép client nhận thông tin sản phẩm khuyến mãi và đặt hàng. Mô tả chi tiết dưới đây:
 * 
 * 
 * Giao diện từ xa
 * 
 * 
 * public interface IProduct953 extends Remote{
 *     public Product953 getProduct(String studentCode) throws RemoteException;
 *     public void order(Product953 product) throws RemoteException;
 * }
 * 
 * 
 * Lớp Product953 gồm các thuộc tính id String, code String, discountCode String, discount int.
 * Một hàm khởi dựng với đầy đủ các thuộc tính liệt kê ở trên
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 953;
 * Đối tượng triệu gọi từ xa được đăng ký RegistryServer với tên: product953
 * Tất cả các lớp được viết trong package RMI
 * 
 * 
 * b. Phần thứ hai gọi là UDP Server cung cấp chức năng cho phép client lấy thông tin khuyến mãi theo mã sản phẩm
 * 
 * 
 * Yêu cầu là xây dựng một chương trình client thực hiện các tương tác với hệ thống phần mềm ở trên theo kịch bản dưới đây:
 * 
 * 
 * 1. Triệu gọi phương thức từ xa getProduct từ RMI Server với tham số đầu vào là mã sinh viên để nhận về một đối tượng Product953
 * 
 * 
 * 2. Nhận về đối tượng sản phẩm từ RMI Server với giá trị của id và code đã được thiết lập
 * 
 * 
 * 3. Gửi thông điệp là một chuỗi chứa id và code của sản phẩm vừa nhận được tới UDP Server tại port 2208 theo định dạng 
 * id;code
 *  để nhận về một chuỗi mã giảm giá ngẫu nhiên.
 * 
 * 
 * 4. Tính tổng các chữ số nằm trong chuỗi mã giảm giá để ra giá trị được khuyến mãi của sản phẩm
 * 
 * 
 * 5. Triệu gọi phương thức từ xa order từ RMI Server với tham số đầu vào là đối tượng Product có thông tin giảm giá đã được tính toán ở bước 4. 6. Kết thúc chương trình
 */
public class MIXED_953_RMI_va_UDP {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int rmiPort = 1099;
        int udpPort = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-953";

        try {
            // 1. RMI: lấy IProduct953 (đăng ký tên product953) và gọi getProduct(studentCode)
            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            IProduct953 service = (IProduct953) registry.lookup("product953");
            Product953 product = service.getProduct(sCode);
            System.out.println("Nhận Product953: id=" + product.id + ", code=" + product.code);

            // 2. UDP: gửi "id;code" tới UDP Server cổng 2208, nhận về chuỗi mã giảm giá
            String message = product.id + ";" + product.code;
            byte[] sendData = message.getBytes("UTF-8");
            String discountCode;
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(5000);
                socket.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), udpPort));
                byte[] buf = new byte[4096];
                DatagramPacket reply = new DatagramPacket(buf, buf.length);
                socket.receive(reply);
                discountCode = new String(reply.getData(), 0, reply.getLength(), "UTF-8").trim();
            }
            System.out.println("Mã giảm giá: " + discountCode);

            // 3. Tổng các chữ số trong chuỗi mã giảm giá = giá trị khuyến mãi
            int sum = 0;
            for (char ch : discountCode.toCharArray()) {
                if (Character.isDigit(ch)) sum += ch - '0';
            }
            product.discountCode = discountCode;
            product.discount = sum;

            // 4. RMI: gọi order(product) với thông tin giảm giá đã tính
            service.order(product);
            System.out.println("Đã order, discount = " + sum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
