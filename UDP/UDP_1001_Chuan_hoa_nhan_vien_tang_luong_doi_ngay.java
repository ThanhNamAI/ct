package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 1001] UDP - Object
 * Loại: UDP_OBJECT
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản sau:
 * 
 * 
 * Đối tượng trao đổi là thể hiện của lớp UDP.Employee được mô tả:
 * 
 * 
 * Tên đầy đủ lớp:
 *  UDP.Employee
 * 
 * 
 * Các thuộc tính:
 *  id (String), name (String), salary (double), hireDate (String)
 * 
 * 
 * Hàm khởi tạo:
 * 
 * 
 * public Employee(String id, String name, double salary, String hireDate)
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20261107L
 * 
 * 
 * Thực hiện:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B23DCCN006;ITleSdqV
 * 
 * 
 * b. Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Employee từ server. Trong đó, các thuộc tính id, name, salary và hireDate đã được thiết lập sẵn.
 * 
 * 
 * c. Thực hiện:
 * 
 * 
 * Chuẩn hóa name: viết hoa chữ cái đầu của mỗi từ, ví dụ "john doe" thành "John Doe".
 * 
 * 
 * Tăng salary: tăng x% lương, với x bằng tổng các chữ số của năm trong hireDate (ví dụ hireDate 2023-07-15 thì x = 2 + 0 + 2 + 3 = 7), làm tròn 2 chữ số thập phân.
 * 
 * 
 * Chuyển đổi hireDate từ định dạng yyyy-mm-dd sang dd/mm/yyyy.
 * 
 * 
 * Ví dụ:
 *  "2023-07-15" thành 
 * 15/07/2023
 * 
 * 
 * Gửi lại đối tượng đã được chuẩn hóa về server với cấu trúc: 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Employee đã được sửa đổi.
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_1001_Chuan_hoa_nhan_vien_tang_luong_doi_ngay {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1001";

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
