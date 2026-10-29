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
 * [Mã câu hỏi: 1432] WS - ObjectService
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
 * a. Triệu gọi phương thức requestEmployee với tham số đầu vào là mã sinh viên (studentCode) và mã câu hỏi (qCode) để nhận về một đối tượng Employee từ server. Đối tượng này có các thuộc tính:
 * 
 * 
 * startDate: kiểu Date, đại diện cho ngày bắt đầu công việc của nhân viên.
 * endDate: kiểu Date, đại diện cho ngày kết thúc công việc của nhân viên.
 * 
 * 
 * b. Tính toán số ngày làm việc (workingDays) giữa startDate và endDate, loại trừ các ngày cuối tuần (thứ Bảy và Chủ Nhật). Cập nhật thuộc tính workingDays trong đối tượng Employee với giá trị đã tính toán.
 * 
 * 
 * c. Triệu gọi phương thức submitEmployee(String studentCode, String qCode, Employee object) để gửi đối tượng Employee với số ngày làm việc đã được tính toán trở lại server.
 * 
 * 
 * Ví dụ:
 *  Nếu đối tượng Employee có startDate là 01-09-2023 (thứ Sáu) và endDate là 07-09-2023 (thứ Năm), số ngày làm việc sẽ tính từ ngày 01-09 đến 07-09, loại trừ ngày cuối tuần, kết quả là 5 ngày làm việc.
 * 
 * 
 * Đối tượng Employee với giá trị workingDays = 5 sẽ được gửi lại server qua phương thức submitEmployee.
 * 
 * 
 * d. Kết thúc chương trình client.
 */
public class WS_1432_Tinh_so_ngay_lam_viec_Employee {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1432";
        String service = "ObjectService";

        String endpoint = "http://" + host + ":" + port + "/" + service;

        try {
            // 1. Lấy namespace từ WSDL rồi triệu gọi requestEmployee(studentCode, qCode)
            String ns = getNamespace(endpoint + "?wsdl");
            String resp = call(endpoint, ns, "requestEmployee", p("studentCode", sCode) + p("qCode", qCode));
            List<String> items = values(resp, "return");
            System.out.println("Nhận được: " + items);

            // 2. Xử lý theo yêu cầu đề bài
            String obj = items.get(0);
            // b. Đếm số ngày làm việc (bỏ thứ Bảy, Chủ Nhật) từ startDate đến endDate (gồm cả hai đầu)
            LocalDate start = date(field(obj, "startDate"));
            LocalDate end = date(field(obj, "endDate"));
            int workingDays = 0;
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                int w = d.getDayOfWeek().getValue();
                if (w != 6 && w != 7) workingDays++;
            }
            List<String> result = Arrays.asList(setField(obj, "workingDays", String.valueOf(workingDays)));

            // 3. Gửi kết quả trở lại server
            String submitResp = call(endpoint, ns, "submitEmployee", p("studentCode", sCode) + p("qCode", qCode) + objs("data", result));
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
