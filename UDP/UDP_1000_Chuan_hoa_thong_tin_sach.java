package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * [Mã câu hỏi: 1000] UDP - Object
 * Loại: UDP_OBJECT
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2209. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản sau:
 * 
 * 
 * Đối tượng trao đổi là thể hiện của lớp UDP.Book được mô tả:
 * 
 * 
 * Tên đầy đủ lớp:
 *  UDP.Book
 * 
 * 
 * Các thuộc tính:
 *  id (String), title (String), author (String), isbn (String), publishDate (String)
 * 
 * 
 * Hàm khởi tạo:
 * 
 * 
 * public Book(String id, String title, String author, String isbn, String publishDate)
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20251107L
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
 * ;B23DCCN005;eQkvAeId
 * 
 * 
 * b. Nhận thông điệp chứa: 08 byte đầu chứa chuỗi requestId, các byte còn lại chứa một đối tượng là thể hiện của lớp Book từ server. Trong đó, các thuộc tính id, title, author, isbn, và publishDate đã được thiết lập sẵn.
 * 
 * 
 * c. Thực hiện:
 * 
 * 
 * Chuẩn hóa title: viết hoa chữ cái đầu của mỗi từ.
 * 
 * 
 * Chuẩn hóa author theo định dạng "HỌ, Tên".
 * 
 * 
 * Chuẩn hóa mã ISBN theo định dạng "978-3-16-148410-0"
 * 
 * 
 * Chuyển đổi publishDate từ yyyy-mm-dd sang mm/yyyy.
 * 
 * 
 * d. Gửi lại đối tượng đã được chuẩn hóa về server với cấu trúc: 08 byte đầu chứa chuỗi requestId và các byte còn lại chứa đối tượng Book đã được sửa đổi.
 * 
 * 
 * Đóng socket và kết thúc chương trình.
 */
public class UDP_1000_Chuan_hoa_thong_tin_sach {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2209;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1000";

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
            Book b = (Book) ois.readObject();
            // Chuẩn hóa title: viết hoa chữ cái đầu mỗi từ
            StringBuilder sb = new StringBuilder();
            for (String w : b.getTitle().trim().toLowerCase().split("\\s+")) {
                if (w.isEmpty()) continue;
                if (sb.length() > 0) sb.append(' ');
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1));
            }
            String title = sb.toString();
            b.setTitle(title);
            // Chuẩn hóa author theo định dạng "HỌ, Tên"
            String[] aw = b.getAuthor().trim().toLowerCase().split("\\s+");
            StringBuilder ab = new StringBuilder();
            for (int i = 0; i < aw.length - 1; i++) {
                if (aw[i].isEmpty()) continue;
                if (ab.length() > 0) ab.append(' ');
                ab.append(Character.toUpperCase(aw[i].charAt(0))).append(aw[i].substring(1));
            }
            b.setAuthor(aw[aw.length - 1].toUpperCase() + (ab.length() > 0 ? ", " + ab : ""));
            // Chuẩn hóa ISBN: 978-3-16-148410-0
            String digits = b.getIsbn().replaceAll("[^0-9Xx]", "");
            if (digits.length() == 13) {
                b.setIsbn(digits.substring(0, 3) + "-" + digits.substring(3, 4) + "-" + digits.substring(4, 6)
                        + "-" + digits.substring(6, 12) + "-" + digits.substring(12));
            }
            // Chuyển publishDate yyyy-mm-dd sang mm/yyyy
            String[] pd = b.getPublishDate().trim().split("-");
            if (pd.length == 3) b.setPublishDate(pd[1] + "/" + pd[0]);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(reqIdBytes);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(b);
            oos.flush();

            // d. Gửi lại kết quả
            byte[] ansData = baos.toByteArray();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
