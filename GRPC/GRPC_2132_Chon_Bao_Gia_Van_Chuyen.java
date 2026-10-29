package GRPC;

import java.net.Socket;
import java.io.*;
import java.util.*;

/**
 * [Mã câu hỏi: 2132] [gRPC Object] Chọn báo giá vận chuyển
 * Loại: GRPC_OBJECT
 *
 * Đề bài:
 * Một dịch vụ gRPC 
 * TypedJudgeService
 *  được triển khai trên server tại 
 * <Exam_IP>:2240
 *  để xử lý các bài toán với đối tượng có cấu trúc qua Protocol Buffers.
 * 
 * 
 * Yêu cầu: Viết chương trình tại máy trạm (gRPC client, plaintext, không TLS) để giao tiếp với TypedJudgeService và thực hiện các công việc sau.
 * 
 * 
 * a. Tạo gRPC client plaintext tới cổng 
 * 2240
 *  và gọi 
 * TypedJudgeService.RequestTyped
 *  với mã sinh viên và alias runtime được giao.
 * 
 * 
 * b. Server trả về 
 * request_id
 *  và dữ liệu báo giá vận chuyển gồm 
 * order_id
 * , 
 * weight_kg
 * , 
 * max_eta_days
 * , 
 * quotes
 * .
 * 
 * 
 * c. Chỉ xét quote có 
 * eta_days <= max_eta_days
 * . Tính 
 * total_fee = base_fee + weight_kg * per_kg_fee
 * , làm tròn 2 chữ số thập phân.
 * 
 * 
 * d. Chọn quote có 
 * total_fee
 *  nhỏ nhất; nếu bằng nhau, chọn quote có 
 * reliability
 *  cao hơn.
 * 
 * 
 * e. Gọi 
 * TypedJudgeService.SubmitTyped
 *  với 
 * carrier
 * , 
 * total_fee
 * , 
 * eta_days
 * .
 * 
 * 
 * 
 * 
 * Ví dụ
 * 
 * 
 * Input:  {"orderId":"ORD-69834","weightKg":13.46,"maxEtaDays":5,"quotes":[{"carrier":"DHL","baseFee":3.71,"perKgFee":1.96,"etaDays":2,"reliability":0.91},{"carrier":"GHN","baseFee":6.61,"perKgFee":1.73,"etaDays":7,"reliability":0.96}]}
 * Output: {"carrier":"DHL","totalFee":30.09,"etaDays":2}
 */
public class GRPC_2132_Chon_Bao_Gia_Van_Chuyen {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2240;
        String sCode = "B23DCCN861";
        String qCode = "Q2132";

        // Tên package trong file .proto (để rỗng nếu service không có package), ví dụ "judge"
        String grpcPackage = "";
        String authority = host + ":" + port;
        String svc = "/" + (grpcPackage.isEmpty() ? "" : grpcPackage + ".") + "TypedJudgeService/";

        try {
            // a. Tạo kết nối gRPC plaintext (HTTP/2, không TLS) và gọi RequestTyped
            connect(host, port);
            ByteArrayOutputStream rq = new ByteArrayOutputStream();
            putString(rq, 1, sCode);
            putString(rq, 2, qCode);
            Pb resp = new Pb(unary(authority, svc + "RequestTyped", rq.toByteArray()));

            // b. Đọc request_id và dữ liệu
            Object requestId = resp.raw(1);
            Pb payload = resp.payload();

            // c. Chỉ xét quote có eta_days <= max_eta_days; total_fee = base_fee + weight_kg * per_kg_fee
            double weightKg = payload.dbl(2);
            long maxEta = payload.lng(3);
            String bestCarrier = null;
            double bestFee = 0, bestRel = 0;
            long bestEta = 0;
            for (Pb q : payload.msgs(4)) {
                long eta = q.lng(4);
                if (eta > maxEta) continue;
                double fee = round2(q.dbl(2) + weightKg * q.dbl(3));
                double rel = q.dbl(5);
                // d. Chọn total_fee nhỏ nhất; bằng nhau thì reliability cao hơn
                if (bestCarrier == null || fee < bestFee || (fee == bestFee && rel > bestRel)) {
                    bestCarrier = q.str(1);
                    bestFee = fee;
                    bestRel = rel;
                    bestEta = eta;
                }
            }
            System.out.println("carrier=" + bestCarrier + ", total_fee=" + bestFee + ", eta_days=" + bestEta);

            // Gọi SubmitTyped với student_code, question_alias, request_id và answer
            ByteArrayOutputStream sb = new ByteArrayOutputStream();
            putString(sb, 1, sCode);
            putString(sb, 2, qCode);
            putAny(sb, 3, requestId);
            // answer: 1 carrier, 2 total_fee, 3 eta_days
            ByteArrayOutputStream ans = new ByteArrayOutputStream();
            if (bestCarrier != null) putString(ans, 1, bestCarrier);
            putDouble(ans, 2, bestFee);
            putLong(ans, 3, bestEta);
            putBytes(sb, 4, ans.toByteArray());
            byte[] result = unary(authority, svc + "SubmitTyped", sb.toByteArray());
            System.out.println("Phản hồi submit: " + new Pb(result).f.keySet());
            sock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== gRPC client tự viết (HTTP/2 plaintext + Protocol Buffers), không dùng thư viện ngoài =====
    // Giả định schema (đánh số trường theo đúng thứ tự mô tả trong đề), nếu .proto thật khác thì chỉ cần sửa số trường:
    //   Request/RequestTyped : 1 student_code, 2 question_alias
    //   Response             : 1 request_id, 2 data (chuỗi) hoặc payload (message)
    //   Submit/SubmitTyped   : 1 student_code, 2 question_alias, 3 request_id, 4 answer (chuỗi hoặc message)

    static Socket sock;
    static DataInputStream in;
    static OutputStream out;
    static int streamId = 1;

    static void connect(String host, int port) throws Exception {
        sock = new Socket(host, port);
        sock.setSoTimeout(10000);
        in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
        out = new BufferedOutputStream(sock.getOutputStream());
        out.write("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes("US-ASCII"));
        frame(4, 0, 0, new byte[0]); // SETTINGS rỗng
        out.flush();
    }

    static void frame(int type, int flags, int stream, byte[] payload) throws Exception {
        out.write(new byte[] {(byte) (payload.length >> 16), (byte) (payload.length >> 8), (byte) payload.length,
            (byte) type, (byte) flags,
            (byte) (stream >> 24), (byte) (stream >> 16), (byte) (stream >> 8), (byte) stream});
        out.write(payload);
    }

    static void hpackLiteral(ByteArrayOutputStream o, int nameIndex, String name, String value) throws Exception {
        if (nameIndex >= 15) { o.write(0x0f); o.write(nameIndex - 15); } else { o.write(nameIndex); }
        if (nameIndex == 0) { o.write(name.length()); o.write(name.getBytes("US-ASCII")); }
        o.write(value.length());
        o.write(value.getBytes("US-ASCII"));
    }

    /** Gọi unary RPC: path dạng /package.Service/Method, trả về message protobuf trong phản hồi. */
    static byte[] unary(String authority, String path, byte[] msg) throws Exception {
        int sid = streamId;
        streamId += 2;
        ByteArrayOutputStream h = new ByteArrayOutputStream();
        h.write(0x83); // :method POST
        h.write(0x86); // :scheme http
        hpackLiteral(h, 4, null, path);
        hpackLiteral(h, 1, null, authority);
        hpackLiteral(h, 31, null, "application/grpc");
        hpackLiteral(h, 0, "te", "trailers");
        frame(1, 0x4, sid, h.toByteArray()); // HEADERS | END_HEADERS
        ByteArrayOutputStream d = new ByteArrayOutputStream();
        d.write(0);
        d.write(new byte[] {(byte) (msg.length >> 24), (byte) (msg.length >> 16), (byte) (msg.length >> 8), (byte) msg.length});
        d.write(msg);
        frame(0, 0x1, sid, d.toByteArray()); // DATA | END_STREAM
        out.flush();

        ByteArrayOutputStream body = new ByteArrayOutputStream();
        StringBuilder trailers = new StringBuilder();
        boolean done = false;
        while (!done) {
            byte[] hd = new byte[9];
            in.readFully(hd);
            int len = ((hd[0] & 0xff) << 16) | ((hd[1] & 0xff) << 8) | (hd[2] & 0xff);
            int type = hd[3] & 0xff, flags = hd[4] & 0xff;
            int sidIn = ((hd[5] & 0x7f) << 24) | ((hd[6] & 0xff) << 16) | ((hd[7] & 0xff) << 8) | (hd[8] & 0xff);
            byte[] p = new byte[len];
            in.readFully(p);
            if (type == 4 && (flags & 1) == 0) { frame(4, 1, 0, new byte[0]); out.flush(); }       // SETTINGS -> ACK
            else if (type == 6 && (flags & 1) == 0) { frame(6, 1, 0, p); out.flush(); }            // PING -> ACK
            else if (type == 7) throw new IOException("GOAWAY: " + new String(p, "ISO-8859-1"));
            else if (type == 3 && sidIn == sid) throw new IOException("RST_STREAM");
            else if (sidIn == sid && type == 0) {
                int off = 0, end = len;
                if ((flags & 0x8) != 0) { off = 1; end = len - (p[0] & 0xff); }
                body.write(p, off, end - off);
                if ((flags & 1) != 0) done = true;
            } else if (sidIn == sid && (type == 1 || type == 9)) {
                trailers.append(new String(p, "ISO-8859-1")).append(' ');
                if ((flags & 1) != 0) done = true;
            }
        }
        byte[] all = body.toByteArray();
        if (all.length < 5) throw new IOException("Không có dữ liệu phản hồi, header/trailer: " + trailers);
        int n = ((all[1] & 0xff) << 24) | ((all[2] & 0xff) << 16) | ((all[3] & 0xff) << 8) | (all[4] & 0xff);
        return Arrays.copyOfRange(all, 5, 5 + n);
    }

    // ----- Protocol Buffers: mã hóa -----
    static void putVarint(ByteArrayOutputStream o, long v) {
        while ((v & ~0x7FL) != 0) { o.write((int) ((v & 0x7F) | 0x80)); v >>>= 7; }
        o.write((int) v);
    }

    static void putTag(ByteArrayOutputStream o, int field, int wireType) {
        putVarint(o, ((long) field << 3) | wireType);
    }

    static void putBytes(ByteArrayOutputStream o, int field, byte[] b) {
        putTag(o, field, 2);
        putVarint(o, b.length);
        o.write(b, 0, b.length);
    }

    static void putString(ByteArrayOutputStream o, int field, String s) throws Exception {
        putBytes(o, field, s.getBytes("UTF-8"));
    }

    static void putLong(ByteArrayOutputStream o, int field, long v) {
        putTag(o, field, 0);
        putVarint(o, v);
    }

    static void putDouble(ByteArrayOutputStream o, int field, double v) {
        putTag(o, field, 1);
        long bits = Double.doubleToLongBits(v);
        for (int i = 0; i < 8; i++) o.write((int) ((bits >>> (8 * i)) & 0xff));
    }

    /** Ghi lại request_id đúng kiểu dây (varint hoặc chuỗi) như server đã trả về. */
    static void putAny(ByteArrayOutputStream o, int field, Object v) {
        if (v instanceof byte[]) putBytes(o, field, (byte[]) v);
        else if (v != null) putLong(o, field, (Long) v);
    }

    // ----- Protocol Buffers: giải mã -----
    static class Pb {
        Map<Integer, List<Object>> f = new LinkedHashMap<>();

        Pb(byte[] b) {
            int i = 0;
            while (i < b.length) {
                long[] t = varint(b, i);
                i = (int) t[1];
                int field = (int) (t[0] >>> 3), wt = (int) (t[0] & 7);
                Object val;
                if (wt == 0) { long[] v = varint(b, i); i = (int) v[1]; val = v[0]; }
                else if (wt == 1) { long bits = 0; for (int k = 7; k >= 0; k--) bits = (bits << 8) | (b[i + k] & 0xff); i += 8; val = bits; }
                else if (wt == 2) { long[] l = varint(b, i); i = (int) l[1]; val = Arrays.copyOfRange(b, i, i + (int) l[0]); i += (int) l[0]; }
                else if (wt == 5) { long bits = 0; for (int k = 3; k >= 0; k--) bits = (bits << 8) | (b[i + k] & 0xff); i += 4; val = bits; }
                else throw new IllegalArgumentException("wire type không hỗ trợ: " + wt);
                f.computeIfAbsent(field, k -> new ArrayList<>()).add(val);
            }
        }

        static long[] varint(byte[] b, int i) {
            long r = 0;
            int shift = 0;
            while (true) {
                int c = b[i++] & 0xff;
                r |= (long) (c & 0x7f) << shift;
                if ((c & 0x80) == 0) break;
                shift += 7;
            }
            return new long[] {r, i};
        }

        Object raw(int n) { List<Object> l = f.get(n); return l == null || l.isEmpty() ? null : l.get(0); }
        String str(int n) throws Exception { Object o = raw(n); return o instanceof byte[] ? new String((byte[]) o, "UTF-8") : ""; }
        List<String> strs(int n) throws Exception {
            List<String> r = new ArrayList<>();
            for (Object o : f.getOrDefault(n, new ArrayList<>())) r.add(new String((byte[]) o, "UTF-8"));
            return r;
        }
        Pb msg(int n) { Object o = raw(n); return new Pb(o instanceof byte[] ? (byte[]) o : new byte[0]); }
        List<Pb> msgs(int n) {
            List<Pb> r = new ArrayList<>();
            for (Object o : f.getOrDefault(n, new ArrayList<>())) r.add(new Pb((byte[]) o));
            return r;
        }
        double dbl(int n) { Object o = raw(n); return o instanceof Long ? Double.longBitsToDouble((Long) o) : 0.0; }
        long lng(int n) { Object o = raw(n); return o instanceof Long ? (Long) o : 0L; }

        /** Message payload của TypedJudgeService: trường message đầu tiên khác request_id (trường 1). */
        Pb payload() {
            for (Map.Entry<Integer, List<Object>> e : f.entrySet()) {
                if (e.getKey() != 1 && e.getValue().get(0) instanceof byte[]) return new Pb((byte[]) e.getValue().get(0));
            }
            return new Pb(new byte[0]);
        }
    }

    static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
