package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 1631] [UDP Object] Cập nhật lương nhân viên
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
 * ;B15DCCN001;EE29C059
 * .
 * 
 * 
 * b. Nhận datagram phản hồi: 08 byte đầu là chuỗi 
 * requestId
 * , các byte còn lại là đối tượng 
 * UDP.Employee
 *  đã serialize (đọc bằng 
 * ObjectInputStream
 * ). Ví dụ đối tượng: 
 * id=yuAdfGfj; name=ngUYEN vAn khlp; salary=11516.9; hireDate=2024-02-15
 * .
 * 
 * 
 * c. Chuẩn hóa 
 * name
 * : viết hoa chữ cái đầu mỗi từ, các chữ còn lại viết thường. Tăng 
 * salary
 *  thêm 8%, làm tròn 02 chữ số thập phân. Giữ nguyên 
 * id
 *  và 
 * hireDate
 * .
 * 
 * 
 * d. Gửi datagram gồm 08 byte 
 * requestId
 *  ban đầu, theo sau là đối tượng 
 * Employee
 *  đã cập nhật (ghi bằng 
 * ObjectOutputStream
 * ). Ví dụ: 
 * name=Nguyen Van Khlp; salary=12438.25; hireDate=2024-02-15
 * .
 * 
 * 
 * e. Đóng socket và kết thúc chương trình.
 */
public class UDP_1631_Cap_Nhat_Luong_Nhan_Vien {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "Q1631";

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
            // Tăng salary 8%, làm tròn 2 chữ số thập phân
            emp.setSalary(Math.round(emp.getSalary() * 1.08 * 100) / 100.0);
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
