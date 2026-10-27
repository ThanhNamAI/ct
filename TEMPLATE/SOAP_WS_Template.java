package TEMPLATE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TEMPLATE dùng chung cho mọi bài WS (SOAP RPC/Literal) và SOAP (XML Envelope / HTTP POST).
 *
 * Cách dùng: copy file này, đổi tên class, rồi chỉ sửa 3 chỗ trong main (đánh dấu >>> SỬA <<<):
 *   1. Cấu hình: port, service, namespace dự phòng (WS: "http://ws/", SOAP: "http://soap/").
 *   2. Tên operation lấy dữ liệu (getData / requestString / requestListXxx ...).
 *   3. Hàm solve(...) xử lý theo đề, và tên operation + cách đóng gói tham số `data` khi submit.
 * Phần "Hàm hỗ trợ" phía dưới giữ nguyên, không cần động tới.
 *
 * Bảng tra nhanh (service, cổng 2221):
 *   DataService      : getData                -> submitDataInt / submitDataString / submitDataIntArray ...
 *   CharacterService : requestString          -> submitCharacterString ; requestStringArray -> submitCharacterStringArray
 *   ObjectService    : requestXxx / requestListXxx -> submitXxx / submitListXxx
 *   (tên submit chính xác lấy trong đề bài)
 *
 * Đóng gói tham số `data` khi submit:
 *   1 giá trị (int/String/double...) : p("data", String.valueOf(v))
 *   Danh sách giá trị (List<Integer>, String[]...) : list("data", danhSach)  -> <data>v1</data><data>v2</data>...
 *                                      (KHÔNG dùng p("data", list.toString()) vì sẽ thành 1 chuỗi "[1, 2]")
 *   1 đối tượng                      : obj("data", o.xml)
 *   Danh sách đối tượng              : objs("data", danhSachXml)    (mỗi phần tử là inner XML của 1 đối tượng)
 *
 * Làm việc với đối tượng: dùng class lồng Obj, ví dụ
 *   Obj o = new Obj(items.get(0));  o.get("name");  o.set("finalPrice", "123.45");  o.xml
 */
public class SOAP_WS_Template {
    public static void main(String[] args) {
        // >>> SỬA <<< 1. Cấu hình
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "JNP-xxxx";
        String service = "DataService";       // DataService | CharacterService | ObjectService
        String defaultNs = "http://ws/";      // WS: "http://ws/"  |  SOAP: "http://soap/"

        String endpoint = "http://" + host + ":" + port + "/" + service;

        try {
            // 1. Lấy namespace từ WSDL rồi triệu gọi operation lấy dữ liệu
            String ns = getNamespace(endpoint + "?wsdl", defaultNs);
            // >>> SỬA <<< 2. Tên operation lấy dữ liệu
            String getOp = "getData";
            String resp = call(endpoint, ns, getOp, p("studentCode", sCode) + p("qCode", qCode));
            List<String> items = values(resp, "return");   // mỗi phần tử: 1 giá trị hoặc inner XML của 1 đối tượng
            System.out.println("Nhận được: " + items);

            // 2. Xử lý theo yêu cầu đề bài
            // >>> SỬA <<< 3a. Hàm xử lý (xem solve bên dưới), trả về phần tham số `data` đã đóng gói
            String dataParams = solve(items);

            // 3. Gửi kết quả trở lại server
            // >>> SỬA <<< 3b. Tên operation submit
            String submitOp = "submitDataInt";
            String submitResp = call(endpoint, ns, submitOp, p("studentCode", sCode) + p("qCode", qCode) + dataParams);
            System.out.println("Kết quả gửi: " + submitResp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== >>> SỬA <<< Hàm xử lý theo đề bài =====
    // Đầu vào: items = danh sách phần tử nhận được từ <return>. Đầu ra: chuỗi XML của tham số `data`.
    static String solve(List<String> items) {
        // --- Ví dụ A: List<Integer> -> tổng (submitDataInt) ---
        List<Integer> a = new ArrayList<>();
        for (String s : items) a.add(Integer.parseInt(s.trim()));
        int sum = 0;
        for (int v : a) sum += v;
        return p("data", String.valueOf(sum));

        // --- Ví dụ B: String -> đảo chuỗi (submitCharacterString) ---
        // String s = unesc(items.get(0).trim());
        // return p("data", new StringBuilder(s).reverse().toString());

        // --- Ví dụ C: List<đối tượng> -> sắp xếp theo ngày, giữ ổn định (submitListXxx) ---
        // List<Obj> objs = new ArrayList<>();
        // for (String s : items) objs.add(new Obj(s));
        // objs.sort(Comparator.comparing(o -> date(o.get("startDate"))));
        // List<String> xmls = new ArrayList<>();
        // for (Obj o : objs) xmls.add(o.xml);
        // return objs("data", xmls);

        // --- Ví dụ D: 1 đối tượng -> gán thuộc tính (submitXxx) ---
        // Obj o = new Obj(items.get(0));
        // double price = Double.parseDouble(o.get("price"));
        // o.set("finalPrice", String.format(Locale.US, "%.2f", price * 1.1));
        // return obj("data", o.xml);

        // --- Ví dụ E: List<Integer> -> trả về danh sách (submitCharacterCharArray, submitDataIntArray...) ---
        // List<Integer> result = new ArrayList<>();
        // for (String s : items) result.add(Integer.parseInt(s.trim()));
        // Collections.reverse(result);
        // return list("data", result);
    }

    // ===== Class lồng bọc 1 đối tượng XML (thêm class lồng khác ở đây nếu cần) =====
    static class Obj {
        String xml;   // inner XML của đối tượng, ví dụ <name>A</name><price>10</price>

        Obj(String xml) {
            this.xml = xml;
        }

        String get(String tag) {
            return field(xml, tag);
        }

        void set(String tag, String value) {
            xml = setField(xml, tag, value);
        }
    }

    // ===== Hàm hỗ trợ gọi SOAP / xử lý XML (giữ nguyên) =====

    // Lấy targetNamespace từ WSDL (dùng làm namespace của operation), lỗi thì dùng defaultNs
    static String getNamespace(String wsdlUrl, String defaultNs) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(wsdlUrl).openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            Matcher m = Pattern.compile("targetNamespace=\"([^\"]+)\"").matcher(readAll(c.getInputStream()));
            if (m.find()) return m.group(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultNs;
    }

    static String readAll(java.io.InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    // Gửi 1 request SOAP tới operation và trả về XML phản hồi
    static String call(String endpoint, String ns, String op, String params) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        String soapRequest =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"" + ns + "\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"
            + "<soapenv:Header/><soapenv:Body><ws:" + op + ">" + params + "</ws:" + op + ">"
            + "</soapenv:Body></soapenv:Envelope>";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapRequest.getBytes("UTF-8"));
            os.flush();
        }
        int status = conn.getResponseCode();
        String resp = readAll(status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream());
        if (status >= 300) System.out.println("HTTP " + status + ": " + resp);
        return resp;
    }

    static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static String unesc(String s) {
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&");
    }

    // <name>value</name>
    static String p(String name, String value) {
        return "<" + name + ">" + esc(value) + "</" + name + ">";
    }

    // Danh sách giá trị đơn: <name>v1</name><name>v2</name>...
    static String list(String name, List<?> vals) {
        StringBuilder sb = new StringBuilder();
        for (Object v : vals) sb.append(p(name, String.valueOf(v)));
        return sb.toString();
    }

    // 1 đối tượng (giữ nguyên XML bên trong): <name>inner</name>
    static String obj(String name, String inner) {
        return "<" + name + ">" + inner + "</" + name + ">";
    }

    // Danh sách đối tượng: <name>inner1</name><name>inner2</name>...
    static String objs(String name, List<String> inners) {
        StringBuilder sb = new StringBuilder();
        for (String in : inners) sb.append(obj(name, in));
        return sb.toString();
    }

    // Lấy nội dung (inner XML) của mọi thẻ <tag> trong xml, không phân biệt prefix namespace
    static List<String> values(String xml, String tag) {
        List<String> out = new ArrayList<>();
        Matcher m = Pattern.compile("<(?:\\w+:)?" + tag + "(?:\\s[^>]*)?>(.*?)</(?:\\w+:)?" + tag + ">", Pattern.DOTALL).matcher(xml);
        while (m.find()) out.add(m.group(1));
        return out;
    }

    // Giá trị 1 trường trong đối tượng (null nếu không có)
    static String field(String obj, String tag) {
        List<String> v = values(obj, tag);
        return v.isEmpty() ? null : unesc(v.get(0).trim());
    }

    // Gán giá trị cho 1 trường trong đối tượng (thêm mới nếu chưa có)
    static String setField(String obj, String tag, String value) {
        Pattern pt = Pattern.compile("<(?:\\w+:)?" + tag + "(?:\\s[^>]*)?(?:/>|>.*?</(?:\\w+:)?" + tag + ">)", Pattern.DOTALL);
        Matcher m = pt.matcher(obj);
        String rep = "<" + tag + ">" + esc(value) + "</" + tag + ">";
        if (m.find()) return m.replaceFirst(Matcher.quoteReplacement(rep));
        return obj + rep;
    }

    // Ngày dạng yyyy-MM-dd... (xsd:date / xsd:dateTime)
    static LocalDate date(String s) {
        return LocalDate.parse(s.trim().substring(0, 10));
    }
}
