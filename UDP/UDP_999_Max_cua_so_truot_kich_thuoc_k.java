package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * [Mã câu hỏi: 999] UDP - Data Type
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
 * ;B21DCCN795;ylrhZ6UM
 * 
 * 
 * b. Nhận thông điệp là một chuỗi từ server theo định dạng 
 * requestId;n;k;z1,z2,...,zn
 * , trong đó:
 * 
 * 
 * requestId là chuỗi ngẫu nhiên duy nhất.
 * n là số phần tử của mảng.
 * k là kích thước cửa sổ trượt (k < n).
 * z1 đến zn là n phần tử là số nguyên của mảng.
 * 
 * 
 * c. Thực hiện tìm giá trị lớn nhất trong mỗi cửa sổ trượt với kích thước k trên mảng số nguyên nhận được, và gửi thông điệp lên server theo định dạng 
 * requestId;max1,max2,...,maxm
 * , trong đó max1 đến maxm là các giá trị lớn nhất tương ứng trong mỗi cửa sổ.
 * 
 * 
 * Ví dụ:
 *  
 * requestId;5;3;1,5,2,3,4
 * 
 * 
 * Kết quả: 
 * requestId;5,5,4
 * 
 * 
 * d. Đóng socket và kết thúc chương trình.
 */
public class UDP_999_Max_cua_so_truot_kich_thuoc_k {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2207;
        String sCode = "B23DCCN861";
        String qCode = "JNP-999";

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

            // c. Tìm giá trị lớn nhất trong mỗi cửa sổ trượt kích thước k
            String[] sub = data.split(";");
            int k = Integer.parseInt(sub[1].trim());
            String[] numStrs = sub[2].split(",");
            int[] arr = new int[numStrs.length];
            for (int i = 0; i < numStrs.length; i++) arr[i] = Integer.parseInt(numStrs[i].trim());
            List<String> maxList = new ArrayList<>();
            for (int i = 0; i <= arr.length - k; i++) {
                int max = arr[i];
                for (int j = 1; j < k; j++) max = Math.max(max, arr[i + j]);
                maxList.add(String.valueOf(max));
            }
            String result = String.join(",", maxList);

            // d. Gửi lại kết quả
            String ans = requestId + ";" + result;
            byte[] ansData = ans.getBytes();
            socket.send(new DatagramPacket(ansData, ansData.length, ip, port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
