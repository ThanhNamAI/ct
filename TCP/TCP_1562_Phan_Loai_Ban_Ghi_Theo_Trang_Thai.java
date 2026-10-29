package TCP;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1562] [TCP NIO] Phân loại bản ghi theo trạng thái
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
 * b. Nhận 02 frame liên tiếp, mỗi frame chứa một phần của danh sách bản ghi. Bản ghi có định dạng id,operation,status và được phân tách bởi ký tự |.
 * 
 * 
 * c. Gom các id theo từng trạng thái OK, FAIL và RETRY, giữ nguyên thứ tự xuất hiện trong dữ liệu.
 * 
 * 
 * d. Gửi một frame kết quả theo định dạng OK=id,id;FAIL=id;RETRY=id.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * 300,op0,OK|301,op1,FAIL|302,op2,RETRY
 *  thì dữ liệu nộp lại là 
 * OK=300;FAIL=301;RETRY=302
 * .
 */
public class TCP_1562_Phan_Loai_Ban_Ghi_Theo_Trang_Thai {
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
        String qCode = "Q1562";

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
            List<String> ok = new ArrayList<>();
            List<String> fail = new ArrayList<>();
            List<String> retry = new ArrayList<>();
            for (String rec : data.split("\\|")) {
                String[] f = rec.trim().split(",");
                if (f.length < 3) continue;
                String status = f[2].trim();
                if (status.equals("OK")) ok.add(f[0].trim());
                else if (status.equals("FAIL")) fail.add(f[0].trim());
                else if (status.equals("RETRY")) retry.add(f[0].trim());
            }
            String ans = "OK=" + String.join(",", ok) + ";FAIL=" + String.join(",", fail) + ";RETRY=" + String.join(",", retry);

            // d. Gửi kết quả
            writeFrame(channel, ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
