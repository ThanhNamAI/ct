package TCP;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1862] [TCP NIO] Phân loại điểm xử lý bản ghi
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
 * b. Nhận đúng 02 frame liên tiếp, nối payload theo thứ tự để được danh sách bản ghi 
 * id,type,score
 *  phân tách bằng 
 * |
 * . Ví dụ: 
 * R500,AUTH,90|R501,PAY,45
 * .
 * 
 * 
 * c. Phân loại id vào 
 * PASS
 *  nếu score >= 80, 
 * REVIEW
 *  nếu score từ 50 đến 79, và 
 * FAIL
 *  nếu score nhỏ hơn 50; giữ thứ tự xuất hiện.
 * 
 * 
 * d. Gửi một frame kết quả theo định dạng 
 * PASS=id,id;REVIEW=id;FAIL=id
 * . Ví dụ: 
 * PASS=R500;REVIEW=;FAIL=R501
 * .
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 */
public class TCP_1862_Phan_Loai_Diem_Xu_Ly_Ban_Ghi {
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
        String qCode = "Q1862";

        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress(host, port));
            channel.socket().setSoTimeout(5000);

            // a. Gửi mã sinh viên và mã câu hỏi
            String req = sCode + ";" + qCode;
            writeFrame(channel, req);

            // b. Nhận 2 frame liên tiếp
            List<String> frames = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                frames.add(readFrame(channel));
            }

            // c. Xử lý dữ liệu
            String data = joinFrames(frames, ",", 3);
            List<String> pass = new ArrayList<>();
            List<String> review = new ArrayList<>();
            List<String> fail = new ArrayList<>();
            for (String rec : data.split("\\|")) {
                String[] f = rec.trim().split(",");
                if (f.length < 3) continue;
                double score = Double.parseDouble(f[2].trim());
                if (score >= 80) pass.add(f[0].trim());
                else if (score >= 50) review.add(f[0].trim());
                else fail.add(f[0].trim());
            }
            String ans = "PASS=" + String.join(",", pass) + ";REVIEW=" + String.join(",", review) + ";FAIL=" + String.join(",", fail);

            // d. Gửi kết quả
            writeFrame(channel, ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
