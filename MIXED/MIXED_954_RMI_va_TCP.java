package MIXED;

import java.io.*;
import java.net.*;
import java.rmi.registry.*;
import java.util.*;
import RMI.*;

/**
 * [Mã câu hỏi: 954] RMI + TCP
 * Loại: MIXED
 *
 * Đề bài:
 * Một hệ thống phần mềm cung cấp các khuyến mãi sản phẩm ngẫu nhiên cho sinh viên, gồm 02 phần được mô tả như sau:
 * 
 * 
 * a. Phần thứ nhất gọi là RMI Server, cho phép client nhận thông tin sản phẩm và đặt hàng
 * 
 * 
 * Giao diện từ xa
 * 
 * 
 * public interface IProduct954 extends Remote{
 *     public Product954 getProduct(String studentCode) throws RemoteException;
 *     public void order(Product954 product) throws RemoteException;
 * }
 * 
 * 
 * Lớp Product954 gồm các thông tin id String, code String, discountCode String, discount int.
 * Một hàm khởi tạo có đầy đủ các thuộc tính liệt kê ở trên
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 954;
 * Đối tượng triệu gọi từ xa được đăng ký với RegistryServer với tên: product954
 * Tất cả các lớp được viết trong package RMI
 * 
 * 
 * b. Phần thứ hai gọi là TCP Server, cho phép client lấy thông tin khuyến mãi theo mã sản phẩm.
 * 
 * 
 * Yêu cầu là xây dựng một chương trình client thực hiện các tương tác với hệ thống phần mềm ở trên theo kịch bản dưới đây:
 * 
 * 
 * 1. Triệu gọi phương thức từ xa getProduct từ RMI Server với tham số đầu vào là mã sinh viên để nhận về một đối tượng Product với giá trị của id và code được thiết lập.
 * 
 * 
 * 2. Tương tác với TCP Server tại cổng 2208, sử dụng các luồng ký tự (BufferedReader/BufferedWriter):
 * 
 * 
 * Gửi chuỗi chứa id sản phẩm vừa nhận được và mã câu hỏi theo định dạng 
 * id;qCode
 * Nhận về một chuỗi mã giảm giá ngẫu nhiên.
 * 
 * 
 * 4. Tổng các chữ số nằm trong chuỗi mã giảm giá này chính là giá trị được khuyến mãi của sản phẩm. Tính toán giá trị này.
 * 
 * 
 * 3. Triệu gọi phương thức từ xa order trên RMI Server với tham số đầu vào là đối tượng Product với thông tin khuyến mãi đã được tính toán tại bước 4 ở trên.
 * 
 * 
 * 4. Kết thúc chương trình
 */
public class MIXED_954_RMI_va_TCP {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int rmiPort = 1099;
        int tcpPort = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-954";

        try {
            // 1. RMI: lấy IProduct954 (đăng ký tên product954) và gọi getProduct(studentCode)
            Registry registry = LocateRegistry.getRegistry(host, rmiPort);
            IProduct954 service = (IProduct954) registry.lookup("product954");
            Product954 product = service.getProduct(sCode);
            System.out.println("Nhận Product954: id=" + product.id + ", code=" + product.code);

            // 2. TCP (luồng ký tự): gửi "id;qCode", nhận về chuỗi mã giảm giá
            String discountCode;
            try (Socket socket = new Socket(host, tcpPort)) {
                socket.setSoTimeout(5000);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                writer.write(product.id + ";" + qCode);
                writer.newLine();
                writer.flush();
                discountCode = reader.readLine();
            }
            if (discountCode == null) discountCode = "";
            discountCode = discountCode.trim();
            System.out.println("Mã giảm giá: " + discountCode);

            // 3. Tổng các chữ số trong chuỗi mã giảm giá = giá trị khuyến mãi
            int sum = 0;
            for (char ch : discountCode.toCharArray()) {
                if (Character.isDigit(ch)) sum += ch - '0';
            }
            product.discountCode = discountCode;
            product.discount = sum;

            // 4. RMI: gọi order(product) với thông tin khuyến mãi đã tính
            service.order(product);
            System.out.println("Đã order, discount = " + sum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
