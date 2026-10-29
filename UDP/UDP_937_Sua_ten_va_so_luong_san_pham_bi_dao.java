package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 937] UDP - Object
 * Loại: UDP_OBJECT
 *
 * Đề bài:
 * Thông tin sản phẩm vì một lý do nào đó đã bị sửa đổi thành không đúng, cụ thể:
 * 
 * 
 * a. Tên sản phẩm bị đổi ngược từ đầu tiên và từ cuối cùng, ví dụ: “lenovo thinkpad T520” bị chuyển thành “T520 thinkpad lenovo”
 * 
 * 
 * b. Số lượng sản phẩm cũng bị đảo ngược giá trị, ví dụ từ 9981 thành 1899
 * 
 * 
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. Yêu cầu là xây dựng một chương trình client giao tiếp với server để gửi/nhận các sản phẩm theo mô tả dưới đây:
 * 
 * 
 * a. Đối tượng trao đổi là thể hiện của lớp Product được mô tả như sau
 * 
 * 
 * Tên đầy đủ của lớp:
 *  UDP.Product
 * Các thuộc tính:
 *  id String, code String, name String, quantity int
 * Một hàm khởi tạo có đầy đủ các thuộc tính được liệt kê ở trên
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20161107;
 * 
 * 
 * b. Giao tiếp với server theo kịch bản
 * 
 * 
 * Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN001;EE29C059
 * 
 * 
 * Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Product từ server. Trong đối tượng này, các thuộc tính id, name và quantity đã được thiết lập giá trị.
 * Sửa các thông tin sai của đối tượng về tên và số lượng như mô tả ở trên và gửi đối tượng vừa được sửa đổi lên server theo cấu trúc:
 * 
 * 
 * 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Product đã được sửa đổi.
 * 
 * 
 * Đóng socket và kết thúc chương trình.
 */
public class UDP_937_Sua_ten_va_so_luong_san_pham_bi_dao {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-937";

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
            Product p = (Product) ois.readObject();
            // a. Đổi lại từ đầu và từ cuối của tên
            String[] pw = p.getName().trim().split("\\s+");
            if (pw.length > 1) {
                String tmp = pw[0];
                pw[0] = pw[pw.length - 1];
                pw[pw.length - 1] = tmp;
            }
            p.setName(String.join(" ", pw));
            // b. Đảo ngược lại giá trị số lượng
            p.setQuantity(Integer.parseInt(new StringBuilder(String.valueOf(p.getQuantity())).reverse().toString()));
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
