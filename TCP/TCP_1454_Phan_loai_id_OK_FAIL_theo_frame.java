package TCP;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

/**
 * [Mã câu hỏi: 1454] TCP - NIO Stream
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
 * B16DCCN999;H4EDJn1o
 * 
 * 
 * b. Nhận dữ liệu từ server gồm đúng 2 frame liên tiếp. Payload của mỗi frame là một phần của cùng một danh sách bản ghi phân tách bởi dấu 
 * |
 *  và trong mỗi bản ghi các trường phân tách bởi dấu 
 * ,
 *  theo dạng 
 * id,action,result
 * . Client phải nối 2 payload theo đúng thứ tự, thêm dấu 
 * |
 *  vào giữa hai payload, để thu được danh sách hoàn chỉnh. Ví dụ: frame 1 là 
 * 101,A,OK|102,B,FAIL
 * , frame 2 là 
 * 103,C,OK
 *  thì danh sách là 
 * 101,A,OK|102,B,FAIL|103,C,OK
 * .
 * 
 * 
 * c. Phân loại và gửi lại danh sách các ID lên server theo định dạng OK=<list_id_ok>;FAIL=<list_id_fail>. Trong đó <list_id_...> là danh sách các ID tương ứng phân tách bởi dấu phẩy, giữ nguyên thứ tự xuất hiện trong dữ liệu gốc.
 * 
 * 
 * Ví dụ:
 *  Nếu input là 101,A,OK|102,B,FAIL|103,C,OK thì output là OK=101,103;FAIL=102
 * 
 * 
 * d. Đóng kết nối và kết thúc chương trình.
 */
public class TCP_1454_Phan_loai_id_OK_FAIL_theo_frame {
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
        String qCode = "JNP-1454";

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
            String data = String.join("|", frames);
            List<String> ok = new ArrayList<>();
            List<String> fail = new ArrayList<>();
            for (String rec : data.split("\\|")) {
                String[] f = rec.trim().split(",");
                if (f.length < 3) continue;
                if (f[2].trim().equals("OK")) ok.add(f[0].trim());
                else if (f[2].trim().equals("FAIL")) fail.add(f[0].trim());
            }
            String ans = "OK=" + String.join(",", ok) + ";FAIL=" + String.join(",", fail);

            // d. Gửi kết quả
            writeFrame(channel, ans);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
