package WS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [Mã câu hỏi: 1440] WS - ObjectService
 * Loại: WS_OBJECT
 *
 * Đề bài:
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp ObjectService.wsdl, được triển khai trên server tại URL http://<Exam_IP>:2221/ObjectService?wsdl để xử lý các bài toán với đối tượng.
 * 
 * 
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (WS client) để giao tiếp với ObjectService thực hiện các công việc sau:
 * 
 * 
 * a. Triệu gọi phương thức requestListCustomerY với tham số đầu vào là mã sinh viên (studentCode) và mã câu hỏi (qCode) để nhận về danh sách đối tượng CustomerY từ server. Mỗi đối tượng CustomerY có các thuộc tính:
 * 
 * 
 * customerId: kiểu String, đại diện cho mã khách hàng.
 * lastTransactionDate: kiểu Date, đại diện cho ngày giao dịch gần nhất của khách hàng.
 * 
 * 
 * b. Lọc và giữ lại các khách hàng chưa có giao dịch nào trong vòng 6 tháng qua (so với ngày hiện tại).
 * 
 * 
 * c. Triệu gọi phương thức submitListCustomerY(String studentCode, String qCode, List<CustomerY> customerYs) để gửi danh sách các khách hàng không hoạt động trong 6 tháng trở lại server.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class WS_1440_Loc_khach_hang_khong_hoat_dong {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1440";
        String service = "ObjectService";

        String endpoint = "http://" + host + ":" + port + "/" + service;

        try {
            // 1. Lấy namespace từ WSDL rồi triệu gọi requestListCustomerY(studentCode, qCode)
            String ns = getNamespace(endpoint + "?wsdl");
            String resp = call(endpoint, ns, "requestListCustomerY", p("studentCode", sCode) + p("qCode", qCode));
            List<String> items = values(resp, "return");
            System.out.println("Nhận được: " + items);

            // 2. Xử lý theo yêu cầu đề bài
            // b. Giữ khách chưa có giao dịch trong 6 tháng qua
            LocalDate limit = LocalDate.now().minusMonths(6);
            List<String> result = new ArrayList<>();
            for (String o : items) {
                String last = field(o, "lastTransactionDate");
                if (last == null || last.trim().isEmpty() || date(last).isBefore(limit)) result.add(o);
            }

            // 3. Gửi kết quả trở lại server
            String submitResp = call(endpoint, ns, "submitListCustomerY", p("studentCode", sCode) + p("qCode", qCode) + objs("data", result));
            System.out.println("Kết quả gửi: " + submitResp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Hàm hỗ trợ gọi SOAP / xử lý XML =====

    // Lấy targetNamespace từ WSDL (dùng làm namespace của operation)
    static String getNamespace(String wsdlUrl) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(wsdlUrl).openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            Matcher m = Pattern.compile("targetNamespace=\"([^\"]+)\"").matcher(readAll(c.getInputStream()));
            if (m.find()) return m.group(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://ws/";
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

    // Danh sách đối tượng (giữ nguyên XML bên trong của từng đối tượng)
    static String objs(String name, List<String> inners) {
        StringBuilder sb = new StringBuilder();
        for (String in : inners) sb.append("<").append(name).append(">").append(in).append("</").append(name).append(">");
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
