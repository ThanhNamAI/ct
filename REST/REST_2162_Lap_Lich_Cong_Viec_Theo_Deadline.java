package REST;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.*;
import java.util.zip.CRC32;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * [Mã câu hỏi: 2162] [REST Object] Lập lịch công việc theo deadline
 * Loại: REST_OBJECT
 *
 * Đề bài:
 * Một dịch vụ REST được triển khai trên server tại URL http://<Exam_IP>:2230/api/rest/object để xử lý các bài toán với đối tượng. Yêu cầu: Viết chương trình tại máy trạm (REST client) để giao tiếp với dịch vụ và thực hiện các công việc sau.
 * 
 * 
 * a. Gửi 
 * GET /api/rest/object?studentCode=<mã_sinh_viên>&qCode=<qAlias>
 *  để nhận JSON gồm 
 * requestId
 *  và 
 * data
 * .
 * 
 * 
 * b. Server trả về `data` là object gồm danh sách `jobs`.
 * 
 * 
 * c. Chọn nhiều job không chồng lấp nhất bằng greedy theo thời điểm kết thúc.
 * 
 * 
 * d. Gửi 
 * POST /api/rest/object/submit
 *  với body JSON gồm 
 * studentCode
 * , 
 * qCode
 * , 
 * requestId
 *  và 
 * answer
 * .
 * 
 * 
 * e. Ví dụ: nếu chọn được `J1`, `J3`, `J5` thì `answer` là `J1,J3,J5`.
 * 
 * 
 * Ví dụ
 * 
 * 
 * Input:  {"jobs":[{"id":"J7","start":6,"end":7},{"id":"J3","start":1,"end":3},{"id":"J5","start":3,"end":7},{"id":"J6","start":6,"end":8}]}
 * Output: J3,J5
 */
public class REST_2162_Lap_Lich_Cong_Viec_Theo_Deadline {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2230;
        String sCode = "B23DCCN861";
        String qCode = "Q2162";

        String urlStr = "http://" + host + ":" + port + "/api/rest/object";

        try {
            // 1. GET dữ liệu từ REST API
            Map<String, String> respHeaders = new HashMap<>();
            String jsonResponse = httpCall("GET", urlStr + "?studentCode=" + sCode + "&qCode=" + qCode, null, null, respHeaders);
            System.out.println("Received: " + jsonResponse);
            Map<String, Object> root = map(parseJson(jsonResponse));
            Object requestId = root.get("requestId");
            Object data = root.get("data");

            // 2. Xử lý thuật toán và gửi lại kết quả (POST / PUT)
            Object answer = null;
            String method = "POST";
            String target = urlStr + "/submit";
            Map<String, String> extraHeaders = null;
            boolean withAnswer = true;
            {
            Map<String, Object> d = map(data);
            List<Map<String, Object>> jobs = new ArrayList<>();
            for (Object o : list(d.get("jobs"))) jobs.add(map(o));
            jobs.sort((a, b) -> {
                int c = Double.compare(num(a.get("end")), num(b.get("end")));
                if (c != 0) return c;
                c = Double.compare(num(a.get("start")), num(b.get("start")));
                return c != 0 ? c : str(a.get("id")).compareTo(str(b.get("id")));
            });
            List<String> chosen = new ArrayList<>();
            double lastEnd = Double.NEGATIVE_INFINITY;
            for (Map<String, Object> j : jobs) {
                if (num(j.get("start")) >= lastEnd) {
                    chosen.add(str(j.get("id")));
                    lastEnd = num(j.get("end"));
                }
            }
            answer = String.join(",", chosen);
            }

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("studentCode", sCode);
            payload.put("qCode", qCode);
            payload.put("requestId", requestId);
            if (withAnswer) payload.put("answer", answer);
            System.out.println("Submit: " + method + " " + target);
            String result = httpCall(method, target, "GET".equals(method) ? null : toJson(payload), extraHeaders, null);
            System.out.println("Submit response: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Hàm tiện ích: gọi HTTP, đọc/ghi JSON =====
    static String httpCall(String method, String url, String body, Map<String, String> reqHeaders,
                           Map<String, String> respHeaders) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json");
        if (body != null) b.header("Content-Type", "application/json");
        if (reqHeaders != null) {
            for (Map.Entry<String, String> e : reqHeaders.entrySet()) b.header(e.getKey(), e.getValue());
        }
        b.method(method, body == null ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        HttpResponse<String> resp = HttpClient.newHttpClient()
                .send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        System.out.println(method + " " + url + " -> HTTP " + resp.statusCode());
        if (respHeaders != null) {
            for (Map.Entry<String, List<String>> e : resp.headers().map().entrySet()) {
                respHeaders.put(e.getKey().toLowerCase(), e.getValue().get(0));
            }
        }
        return resp.body();
    }

    static int jp;

    static Object parseJson(String s) {
        jp = 0;
        return readVal(s);
    }

    static void skipWs(String s) {
        while (jp < s.length() && Character.isWhitespace(s.charAt(jp))) jp++;
    }

    static Object readVal(String s) {
        skipWs(s);
        char c = s.charAt(jp);
        if (c == '{') {
            Map<String, Object> m = new LinkedHashMap<>();
            jp++;
            skipWs(s);
            if (s.charAt(jp) == '}') { jp++; return m; }
            while (true) {
                skipWs(s);
                String k = (String) readVal(s);
                skipWs(s);
                jp++; // dấu ':'
                m.put(k, readVal(s));
                skipWs(s);
                if (s.charAt(jp++) == '}') return m;
            }
        }
        if (c == '[') {
            List<Object> l = new ArrayList<>();
            jp++;
            skipWs(s);
            if (s.charAt(jp) == ']') { jp++; return l; }
            while (true) {
                l.add(readVal(s));
                skipWs(s);
                if (s.charAt(jp++) == ']') return l;
            }
        }
        if (c == '"') {
            StringBuilder sb = new StringBuilder();
            jp++;
            while (s.charAt(jp) != '"') {
                char ch = s.charAt(jp++);
                if (ch == '\\') {
                    char e = s.charAt(jp++);
                    switch (e) {
                        case 'n': sb.append('\n'); break;
                        case 't': sb.append('\t'); break;
                        case 'r': sb.append('\r'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'u': sb.append((char) Integer.parseInt(s.substring(jp, jp + 4), 16)); jp += 4; break;
                        default: sb.append(e);
                    }
                } else {
                    sb.append(ch);
                }
            }
            jp++;
            return sb.toString();
        }
        if (s.startsWith("true", jp)) { jp += 4; return Boolean.TRUE; }
        if (s.startsWith("false", jp)) { jp += 5; return Boolean.FALSE; }
        if (s.startsWith("null", jp)) { jp += 4; return null; }
        int st = jp;
        while (jp < s.length() && "+-0123456789.eE".indexOf(s.charAt(jp)) >= 0) jp++;
        return Double.valueOf(s.substring(st, jp));
    }

    static String toJson(Object o) {
        if (o == null) return "null";
        if (o instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(String.valueOf(e.getKey()))).append(":").append(toJson(e.getValue()));
            }
            return sb.append("}").toString();
        }
        if (o instanceof List) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object x : (List<?>) o) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(x));
            }
            return sb.append("]").toString();
        }
        if (o instanceof Double) return numStr((Double) o);
        if (o instanceof Number || o instanceof Boolean) return o.toString();
        String s = o.toString();
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '"' || ch == '\\') sb.append('\\').append(ch);
            else if (ch == '\n') sb.append("\\n");
            else if (ch == '\r') sb.append("\\r");
            else if (ch == '\t') sb.append("\\t");
            else if (ch < 0x20) sb.append(String.format("\\u%04x", (int) ch));
            else sb.append(ch);
        }
        return sb.append("\"").toString();
    }

    static String numStr(double d) {
        if (d == Math.rint(d) && Math.abs(d) < 1e15) return Long.toString((long) d);
        return BigDecimal.valueOf(d).toPlainString();
    }

    static String str(Object o) {
        if (o == null) return "";
        if (o instanceof Double) return numStr((Double) o);
        return o.toString();
    }

    static double num(Object o) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        return Double.parseDouble(String.valueOf(o).trim());
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> map(Object o) {
        return (Map<String, Object>) o;
    }

    @SuppressWarnings("unchecked")
    static List<Object> list(Object o) {
        return (List<Object>) o;
    }

    static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
