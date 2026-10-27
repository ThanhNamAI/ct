package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1343] RMI - Object
 * Loại: RMI_OBJECT
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để xử lý mã hóa dữ liệu tác phẩm trong hệ thống quản lý thư viện. Chương trình sẽ ngẫu nhiên tạo ra đối tượng BookX với các giá trị ban đầu và cung cấp cho RMI client như sau:
 * 
 * 
 * Giao diện từ xa:
 * 
 * 
 * public interface ObjectService extends Remote {
 *     public Serializable requestObject(String studentCode, String qCode) throws RemoteException;
 *     public void submitObject(String studentCode, String qCode, Serializable object) throws RemoteException;
 * }
 * 
 * 
 * Lớp BookX gồm các thuộc tính: id String, title String, author String, yearPublished int, genre String, code String.
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20241124L;
 * 
 * 
 * 02 hàm khởi dựng:
 * 
 * 
 * public BookX()
 * public BookX(String id, String title, String author, int yearPublished, String genre)
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface ObjectService và lớp BookX được viết trong package RMI.
 * 
 * 
 * Đối tượng cài đặt giao diện từ xa ObjectService được đăng ký với RegistryServer: RMIObjectService.
 * 
 * 
 * Yêu cầu:
 * 
 * 
 * Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với đối tượng sách được nhận từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestObject để nhận đối tượng BookX ngẫu nhiên từ server.
 * 
 * 
 * b. Tạo mã code cho sách dựa trên các quy tắc mã hóa sau:
 * 
 * 
 * Lấy ký tự đầu tiên và ký tự cuối cùng của chuỗi author, giữ nguyên hoa/thường.
 * Lấy hai chữ số cuối cùng của yearPublished.
 * Số lượng chữ cái trong genre của sách.
 * Độ dài của title chia lấy dư cho 10 (ví dụ: với tiêu đề dài 32 ký tự, giá trị này sẽ là 2).
 * 
 * 
 * Kết hợp tất cả các thành phần trên để tạo ra mã code theo định dạng: [Ký tự đầu và cuối của author][Hai chữ số cuối của năm xuất bản][Số chữ cái của genre][Độ dài title modulo 10]. Ví dụ, nếu tác giả là "Mark Twain", năm xuất bản là 1884, thể loại là 
 * Fiction
 *  với 7 ký tự, và tiêu đề có 24 ký tự, mã code sẽ là: 
 * Mn8474
 * .
 * 
 * 
 * c. Cập nhật giá trị code trong đối tượng BookX.
 * 
 * 
 * d. Triệu gọi phương thức submitObject để gửi đối tượng BookX đã được xử lý trở lại server.
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1343_Ma_hoa_du_lieu_tac_pham_bookx {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1343";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Tạo mã code cho sách
            BookX b = (BookX) obj;
            String author = b.getAuthor().trim();
            int genreLetters = 0;
            for (char ch : b.getGenre().toCharArray()) if (Character.isLetter(ch)) genreLetters++;
            String code = "" + author.charAt(0) + author.charAt(author.length() - 1)
                    + String.format("%02d", b.getYearPublished() % 100) + genreLetters
                    + (b.getTitle().length() % 10);

            // c. Cập nhật code
            b.setCode(code);

            // d. Gửi lại đối tượng
            service.submitObject(sCode, qCode, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
