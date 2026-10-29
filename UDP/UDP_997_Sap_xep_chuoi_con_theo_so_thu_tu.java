package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 997] UDP - Data Type
 * Loại: UDP_DATA
 *
 * Đề bài:
 * Một chương trình server cho phép giao tiếp qua giao thức UDP tại cổng 2207. Yêu cầu là xây dựng một chương trình client trao đổi thông tin với server theo kịch bản:
 * 
 * 
 * a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi theo định dạng 
 * ;studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ;B15DCCN009;F3E8B2D4
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;string
 * , với:
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất.
 * string là một chuỗi chứa các chuỗi con bị thay đổi vị trí.
 * 
 * 
 * Ví dụ:
 *  
 * veM3xgA1g:4,IPFfgEanY:5,aWXlSzDwe:2,PHupvPc:3,PR3gH8ahN:6,UEEKHLIt:7,M6dpWTE:1
 * 
 * 
 * c. Xử lý chuỗi xáo trộn và gửi về chuỗi sau khi sắp xếp: 
 * requestId;string
 * . Ví dụ chuỗi đã được xử lý: 
 * M6dpWTE,aWXlSzDwe,PHupvPc,veM3xgA1g,IPFfgEanY,PR3gH8ahN,UEEKHLIt
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_997_Sap_xep_chuoi_con_theo_so_thu_tu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-997";

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress ip = InetAddress.getByName(host);

            // a. Gửi thông điệp
            String req = ";" + sCode + ";" + qCode;
            byte[] reqData = req.getBytes();
            socket.send(new DatagramPacket(reqData, reqData.length, ip, port));

            // b. Nhận phản hồi
            byte[] buf = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            String res = new String(packet.getData(), 0, packet.getLength()).trim();
            String[] parts = res.split(";", 2);
            String requestId = parts[0];
            String data = parts.length > 1 ? parts[1] : "";

            // c. Sắp xếp các chuỗi con theo số thứ tự phía sau dấu ':'
            String[] items = data.split(",");
            Arrays.sort(items, (x, y) -> Integer.compare(
                    Integer.parseInt(x.substring(x.lastIndexOf(':') + 1).trim()),
                    Integer.parseInt(y.substring(y.lastIndexOf(':') + 1).trim())));
            List<String> out = new ArrayList<>();
            for (String it : items) out.add(it.substring(0, it.lastIndexOf(':')).trim());
            String result = String.join(",", out);

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
