package TCP;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1453] TCP - NIO Stream
 * Loại: TCP_NIO
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2211 (thời gian giao tiếp tối đa cho mỗi yêu cầu là 5s). Yêu cầu là xây dựng một chương trình client tương tác tới server ở trên sử dụng SocketChannel và ByteBuffer để trao đổi thông tin theo giao thức frame: 4 byte độ dài (int32) + payload (UTF-8).
 * 
 * 
 * Lưu ý:
 *  server & client đều phải đọc đủ dữ liệu bằng vòng lặp (readFully) do server luôn chia nhỏ dữ liệu khi gửi. Trình tự trao đổi như sau:
 * 
 * 
 * a. Gửi mã sinh viên và mã câu hỏi theo định dạng 
 * studentCode;qCode
 * .
 * 
 * 
 * Ví dụ:
 *  
 * B16DCCN999;fVHUYiI4
 * 
 * 
 * b. Nhận dữ liệu từ server gồm đúng 3 frame liên tiếp. Payload của mỗi frame là một phần của cùng một chuỗi log dạng các cặp 
 * key=value
 *  phân tách bởi dấu 
 * ;
 *  (các cặp có thể xuất hiện không theo thứ tự). Client phải nối 3 payload theo đúng thứ tự để thu được chuỗi log hoàn chỉnh.
 * 
 * 
 * c. Trích xuất và gửi lại lên server chuỗi theo định dạng cố định 
 * ip=<ip>;path=<path>;status=<status>
 *  (bỏ qua các trường khác nếu có).
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1453_Trich_ip_path_status_tu_log {
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

    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2211;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1453";

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
            String data = String.join("", frames).trim();
            Map<String, String> map = new HashMap<>();
            for (String pair : data.split(";")) {
                int idx = pair.indexOf('=');
                if (idx > 0) map.put(pair.substring(0, idx).trim(), pair.substring(idx + 1).trim());
            }
            String ans = "ip=" + map.getOrDefault("ip", "") + ";path=" + map.getOrDefault("path", "")
                    + ";status=" + map.getOrDefault("status", "");

            // d. Gửi kết quả
            writeFrame(channel, ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
