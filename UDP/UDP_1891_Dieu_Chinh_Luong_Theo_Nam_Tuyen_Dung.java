package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 1891] [UDP Object] Điều chỉnh lương theo năm tuyển dụng
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
 * public class Employee implements java.io.Serializable {
 *     private static final long serialVersionUID = 20261107L;
 * 
 *     private String id;
 *     private String name;
 *     private double salary;
 *     private String hireDate;
 * 
 *     public Employee(String id, String name, double salary, String hireDate) { /* gán 4 thuộc tính * / }
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
 * UDP.Employee
 *  đã serialize. Ví dụ đối tượng: 
 * id=Ab12Cd34; name=le THI an; salary=15000; hireDate=2024-05-12
 * .
 * 
 * 
 * c. Cập nhật đối tượng:
 * 
 * 
 * 
 * 
 * Chuẩn hóa 
 * name
 * : viết hoa chữ cái đầu mỗi từ, các chữ còn lại viết thường.
 * 
 * 
 * Tăng 
 * salary
 *  thêm x%, với x là tổng các chữ số của năm trong 
 * hireDate
 * ; làm tròn 02 chữ số thập phân.
 * 
 * 
 * Chuyển 
 * hireDate
 *  từ 
 * yyyy-mm-dd
 *  sang 
 * dd/mm/yyyy
 * .
 * 
 * 
 * 
 * 
 * d. Gửi datagram gồm 08 byte 
 * requestId
 *  ban đầu, theo sau là đối tượng 
 * Employee
 *  đã cập nhật. Ví dụ: 
 * name=Le Thi An; salary=16200.00; hireDate=12/05/2024
 * .
 * 
 * 
 * e. Đóng socket và kết thúc chương trình.
 */
public class UDP_1891_Dieu_Chinh_Luong_Theo_Nam_Tuyen_Dung {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "Q1891";

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
            Employee emp = (Employee) ois.readObject();
            StringBuilder sb = new StringBuilder();
            for (String w : emp.getName().trim().toLowerCase().split("\\s+")) {
                if (w.isEmpty()) continue;
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
            }
            String name = sb.toString();
            emp.setName(name);
            // Tăng lương x%, x = tổng các chữ số của năm trong hireDate
            String[] hd = emp.getHireDate().trim().split("-");
            int x = 0;
            for (char ch : hd[0].toCharArray()) if (Character.isDigit(ch)) x += ch - '0';
            emp.setSalary(Math.round(emp.getSalary() * (1 + x / 100.0) * 100) / 100.0);
            // Chuyển hireDate yyyy-mm-dd sang dd/mm/yyyy
            if (hd.length == 3) emp.setHireDate(hd[2] + "/" + hd[1] + "/" + hd[0]);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(reqIdBytes);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(emp);
            oos.flush();

            // d. Gửi lại kết quả
            byte[] ansData = baos.toByteArray();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
