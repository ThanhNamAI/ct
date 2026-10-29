package TCP;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1561] [TCP NIO] Nối frame và tính checksum
 * Loại: TCP_NIO
 *
 * Đề bài:
 * Một chương trình server cho phép kết nối qua giao thức TCP tại cổng 2211, sử dụng SocketChannel và ByteBuffer để trao đổi dữ liệu theo giao thức frame.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Gửi frame đầu tiên gồm 4 byte độ dài big-endian và payload UTF-8 là chuỗi theo định dạng "studentCode;qCode". Ví dụ payload: "B15DCCN999;9F8E7D6C".
 * 
 * 
 * b. Sau khi gửi frame chứa studentCode;qCode, nhận đúng 03 frame liên tiếp. Mỗi frame gồm 4 byte độ dài big-endian và payload UTF-8.
 * 
 * 
 * c. Nối payload của 03 frame theo đúng thứ tự, tính độ dài chuỗi và tổng mã ký tự theo modulo 100000.
 * 
 * 
 * d. Gửi một frame kết quả theo định dạng len=<độ_dài>;checksum=<checksum>.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * frame1=abc;frame2=def;frame3=ghi
 *  thì dữ liệu nộp lại là 
 * len=9;checksum=909
 * .
 */
public class TCP_1561_Noi_Frame_Va_Tinh_Checksum {
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
        String qCode = "Q1561";

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
            int sum = 0;
            for (char ch : data.toCharArray()) sum = (sum + ch) % 100000;
            String ans = "len=" + data.length() + ";checksum=" + sum;

            // d. Gửi kết quả
            writeFrame(channel, ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
