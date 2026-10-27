package RMI;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * [Mã câu hỏi: 1342] RMI - Object
 * Loại: RMI_OBJECT
 *
 * Đề bài:
 * Một chương trình (tạm gọi là RMI Server) cung cấp giao diện cho phép triệu gọi từ xa để gán mã code cho sách trong một hệ thống quản lý thư viện. Chương trình sẽ ngẫu nhiên tạo ra đối tượng Book với các giá trị ban đầu và cung cấp cho RMI client như sau:
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
 * Lớp Book gồm các thuộc tính: id String, title String, author String, yearPublished int, pageCount int, code String.
 * 
 * 
 * Trường dữ liệu:
 *  private static final long serialVersionUID = 20241123L;
 * 
 * 
 * 02 hàm khởi dựng:
 * 
 * 
 * public Book()
 * public Book(String id, String title, String author, int yearPublished, int pageCount)
 * 
 * 
 * Trong đó:
 * 
 * 
 * Interface ObjectService và lớp Book được viết trong package RMI.
 * 
 * 
 * Đối tượng cài đặt giao diện từ xa ObjectService được đăng ký với RegistryServer: RMIObjectService.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (RMI client) để thực hiện các công việc sau với đối tượng sách được nhận từ RMI Server:
 * 
 * 
 * a. Triệu gọi phương thức requestObject để nhận đối tượng Book ngẫu nhiên từ server.
 * 
 * 
 * b. Tạo mã code cho sách dựa trên các thành phần sau:
 * 
 * 
 * Lấy chữ cái đầu của họ và tên đầu của tác giả. Ví dụ, với tác giả "John Doe", hai chữ cái đầu sẽ là 
 * JD
 * .
 * Lấy hai chữ số cuối cùng của năm xuất bản (yearPublished). Ví dụ, nếu yearPublished là 1985, sẽ lấy 
 * 85
 * .
 * Đếm số chữ cái (a-z, A-Z) trong title, không tính dấu cách và ký tự khác.
 * Lấy giá trị pageCount (số trang), thêm số 0 ở đầu nếu chưa đủ 3 chữ số (ví dụ 7 thành 
 * 007
 * ).
 * 
 * 
 * Kết hợp tất cả các thành phần trên để tạo ra mã code theo định dạng: "[Chữ cái đầu tên tác giả][Hai chữ số cuối của năm xuất bản][Số chữ cái trong title][pageCount đủ 3 chữ số]". Ví dụ, nếu tác giả là "Jane Austen", yearPublished là 1813, title là "Pride and Prejudice" (17 chữ cái), và pageCount là 345, thì mã code sẽ là: "JA1317345".
 * 
 * 
 * c. Cập nhật giá trị code trong đối tượng Book.
 * 
 * 
 * d. Triệu gọi phương thức submitObject để gửi đối tượng Book đã được xử lý trở lại server.
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class RMI_1342_Tao_ma_code_cho_sach {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 1099;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1342";

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            // a. Nhận đối tượng
            Serializable obj = service.requestObject(sCode, qCode);

            // b. Tạo mã code cho sách
            Book b = (Book) obj;
            String[] authorWords = b.getAuthor().trim().split("\\s+");
            String initials = ("" + authorWords[0].charAt(0) + authorWords[authorWords.length - 1].charAt(0)).toUpperCase();
            int letters = 0;
            for (char ch : b.getTitle().toCharArray()) if (Character.isLetter(ch) && ch < 128) letters++;
            String code = initials + String.format("%02d", b.getYearPublished() % 100) + letters
                    + String.format("%03d", b.getPageCount());

            // c. Cập nhật code
            b.setCode(code);

            // d. Gửi lại đối tượng
            service.submitObject(sCode, qCode, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
