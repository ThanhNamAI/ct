package TCP;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1451] TCP - NIO Stream
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
 * B16DCCN999;fkdRJYuX
 * 
 * 
 * b. Nhận dữ liệu từ server gồm đúng 3 frame liên tiếp. Payload của mỗi frame là một phần của cùng một HTTP request, client phải nối 3 payload theo đúng thứ tự để thu được chuỗi HTTP request hoàn chỉnh (các dòng phân tách bởi 
 * \r\n
 *  và kết thúc bằng 
 * \r\n\r\n
 * ).
 * 
 * 
 * c. Từ chuỗi HTTP request hoàn chỉnh, trích xuất và gửi lại lên server theo định dạng 
 * METHOD;PATH;HOST
 *  trong đó PATH luôn bao gồm query-string.
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1451_Trich_METHOD_PATH_HOST_http_request {
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
        String qCode = "JNP-1451";

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
            String data = String.join("", frames);
            String[] lines = data.split("\r\n");
            String[] first = lines[0].trim().split("\\s+");
            String method = first[0];
            String path = first.length > 1 ? first[1] : "";
            String hostHeader = "";
            for (String l : lines) {
                if (l.toLowerCase().startsWith("host:")) {
                    hostHeader = l.substring(5).trim();
                    break;
                }
            }
            String ans = method + ";" + path + ";" + hostHeader;

            // d. Gửi kết quả
            writeFrame(channel, ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
