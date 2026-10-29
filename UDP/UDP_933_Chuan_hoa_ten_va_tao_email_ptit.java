package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 933] UDP - Object
 * Loại: UDP_OBJECT
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản sau: Đối tượng trao đổi là thể hiện của lớp UDP.Student được mô tả:
 * 
 * 
 * Tên đầy đủ lớp:
 *  UDP.Student
 * Các thuộc tính:
 *  id String,code String, name String, email String
 * 02 Hàm khởi tạo:
 * public Student(String id, String code, String name, String email)
 * public Student(String code)
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20171107
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
 * ;B15DCCN001;EE29C059
 * 
 * 
 * b. Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Student từ server. Trong đó, các thông tin được thiết lập gồm id và name.
 * 
 * 
 * c. Yêu cầu:
 * 
 * 
 * Chuẩn hóa tên theo quy tắc: Chữ cái đầu tiên in hoa, các chữ cái còn lại in thường và gán lại thuộc tính name của đối tượng
 * Tạo email ptit.edu.vn từ tên người dùng bằng cách lấy tên và các chữ cái bắt đầu của họ và tên đệm.
 * 
 * 
 * Ví dụ:
 *  nguyen van tuan nam -> namnvt@ptit.edu.vn. Gán giá trị này cho thuộc tính email của đối tượng nhận được
 * 
 * 
 * Gửi thông điệp chứa đối tượng xử lý ở bước c lên Server với cấu trúc: 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Student đã được sửa đổi.
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_933_Chuan_hoa_ten_va_tao_email_ptit {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-933";

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
            Student s = (Student) ois.readObject();
            StringBuilder sb = new StringBuilder();
            for (String w : s.getName().trim().toLowerCase().split("\\s+")) {
                if (w.isEmpty()) continue;
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
            }
            String name = sb.toString();
            s.setName(name);
            // Email: tên + các chữ cái đầu của họ và tên đệm, ví dụ nguyen van tuan nam -> namnvt@ptit.edu.vn
            String[] nw = name.toLowerCase().split(" ");
            StringBuilder em = new StringBuilder(nw[nw.length - 1]);
            for (int i = 0; i < nw.length - 1; i++) em.append(nw[i].charAt(0));
            s.setEmail(em + "@ptit.edu.vn");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(reqIdBytes);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(s);
            oos.flush();

            // d. Gửi lại kết quả
            byte[] ansData = baos.toByteArray();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
