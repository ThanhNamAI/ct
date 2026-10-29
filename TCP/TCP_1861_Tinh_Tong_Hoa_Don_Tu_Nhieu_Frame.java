package TCP;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1861] [TCP NIO] Tính tổng hóa đơn từ nhiều frame
 * Loại: TCP_NIO
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2211, sử dụng SocketChannel và ByteBuffer để trao đổi dữ liệu theo frame.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi frame đầu tiên gồm 04 byte độ dài big-endian và payload UTF-8 là chuỗi 
 * studentCode;qCode
 * . Ví dụ payload: 
 * B21DCCN001;9F8E7D6C
 * .
 * 
 * 
 * b. Nhận đúng 03 frame liên tiếp, nối payload theo thứ tự để được danh sách bản ghi 
 * invoiceId:quantity:unitPrice
 *  phân tách bằng 
 * |
 * . Ví dụ: 
 * I400:2:120.50|I401:6:90.00
 * .
 * 
 * 
 * c. Tính tổng tiền tất cả bản ghi và đếm số dòng có 
 * quantity * unitPrice >= 500
 * .
 * 
 * 
 * d. Gửi một frame kết quả theo định dạng 
 * total=<tổng>;large=<n>
 * , tổng làm tròn 02 chữ số thập phân. Ví dụ: 
 * total=781.00;large=1
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1861_Tinh_Tong_Hoa_Don_Tu_Nhieu_Frame {
    private static void readFully(SocketChannel channel, ByteBuffer buffer) throws Exception {
        while (buffer.hasRemaining()) {
            int n = channel.read(buffer);
            if (n == -1) throw new Exception("End of stream");
        }
    }

    private static String readFrame(SocketChannel channel) throws Exception {
        ByteBuffer lenBuf = ByteBuffer.allocate(4);
        readFully(channel, lenBuf);
        lenBuf.flip();
        int len = lenBuf.getInt();

        ByteBuffer dataBuf = ByteBuffer.allocate(len);
        readFully(channel, dataBuf);
        dataBuf.flip();
        return StandardCharsets.UTF_8.decode(dataBuf).toString();
    }

    private static void writeFrame(SocketChannel channel, String data) throws Exception {
        byte[] payload = data.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + payload.length);
        buffer.putInt(payload.length);
        buffer.put(payload);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    // Nối các frame: thử nối thẳng, nếu bản ghi không hợp lệ thì nối bằng dấu |
    private static String joinFrames(List<String> frames, String sep, int fields) {
        String direct = String.join("", frames);
        if (valid(direct, sep, fields)) return direct;
        return String.join("|", frames);
    }

    private static boolean valid(String s, String sep, int fields) {
        for (String rec : s.split("\\|")) {
            if (rec.isEmpty()) continue;
            if (rec.split(sep, -1).length != fields) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2211;
        String sCode = "B23DCCN861";
        String qCode = "Q1861";

        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress(host, port));
            channel.socket().setSoTimeout(5000);

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            writeFrame(channel, req);

            // b. Nhận 3 frame liên tiếp
            List<String> frames = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                frames.add(readFrame(channel));
            }

            // c. Xử lý dữ liệu
            String data = joinFrames(frames, ":", 3);
            double total = 0;
            int large = 0;
            for (String rec : data.split("\\|")) {
                String[] f = rec.trim().split(":");
                if (f.length < 3) continue;
                double value = Integer.parseInt(f[1].trim()) * Double.parseDouble(f[2].trim());
                total += value;
                if (value >= 500) large++;
            }
            String ans = String.format(Locale.US, "total=%.2f;large=%d", total, large);

            // d. Gửi kết quả
            writeFrame(channel, ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
