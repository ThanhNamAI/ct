package SOAP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [Mã câu hỏi: 2223] [SOAP Character] Che dữ liệu nhạy cảm trong mảng log
 * Loại: SOAP_CHARACTER
 *
 * Đề bài:
 * Một dịch vụ web hỗ trợ SOAP version 1.1 được định nghĩa trong CharacterService?wsdl và triển khai tại http://<Exam_IP>:2221/CharacterService?wsdl.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi các phương thức SOAP. Ví dụ: studentCode="B15DCCN999", qCode="BAA62945".
 * 
 * 
 * b. Triệu gọi phương thức requestStringArray(studentCode, qCode) để nhận về danh sách chuỗi log List<String> từ server.
 * 
 * 
 * c. Che email, số điện thoại và token trong từng dòng log, giữ nguyên thứ tự phần tử trong danh sách.
 * 
 * 
 * d. Triệu gọi submitStringArray(studentCode, qCode, data) để gửi danh sách chuỗi đã xử lý về server.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * INFO user=minh email=minh0@example.com phone=0166658754 token=WXSGPi57SI action=login
 *  thì dữ liệu nộp lại là 
 * INFO user=minh email=[EMAIL] phone=[PHONE] token=[TOKEN] action=login
 * .
 */
public class SOAP_2223_Che_Du_Lieu_Nhay_Cam_Trong_Mang_Log {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "Q2223";
        String endpoint = "http://" + host + ":" + port + "/CharacterService";

        try {
            // 0. Lấy namespace của dịch vụ từ WSDL (dự phòng "http://soap/")
            String ns = discoverNamespace(endpoint + "?wsdl", "http://soap/");

            // 1. Gọi requestStringArray(studentCode, qCode) để nhận List<String>
            String resp = callSoap(endpoint, ns, "requestStringArray", sCode, qCode, "");
            List<String> lines = new ArrayList<>();
            for (String it : returnsOf(resp)) lines.add(unesc(it));

            // 2. Che email, phone, token trong từng dòng, giữ nguyên thứ tự
            StringBuilder params = new StringBuilder();
            for (String ln : lines) params.append("<data>").append(esc(maskLine(ln))).append("</data>");

            // 3. Gọi submitStringArray(studentCode, qCode, data)
            String result = callSoap(endpoint, ns, "submitStringArray", sCode, qCode, params.toString());

            System.out.println("Kết quả server: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Hàm tiện ích SOAP 1.1 (chỉ dùng java.net, không thêm thư viện ngoài) =====

    /** Đọc nội dung phản hồi HTTP (cả trường hợp lỗi 500 chứa SOAP Fault). */
    static String readAll(HttpURLConnection conn) throws Exception {
        int status = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
            status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /** Lấy targetNamespace trong WSDL để gọi đúng namespace của operation. */
    static String discoverNamespace(String wsdlUrl, String fallback) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(wsdlUrl).openConnection();
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            Matcher m = Pattern.compile("targetNamespace=\"([^\"]+)\"").matcher(readAll(c));
            if (m.find()) return m.group(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fallback;
    }

    /** Gửi SOAP Envelope: operation(studentCode, qCode, ...params) và trả về XML phản hồi. */
    static String callSoap(String endpoint, String ns, String op, String sCode, String qCode, String params) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        String soapRequest =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"" + ns + "\">" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
            "<soap:" + op + ">" +
            "<studentCode>" + sCode + "</studentCode>" +
            "<qCode>" + qCode + "</qCode>" +
            params +
            "</soap:" + op + ">" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapRequest.getBytes("UTF-8"));
            os.flush();
        }
        String xml = readAll(conn);
        System.out.println(op + " -> " + xml);
        return xml;
    }

    /** Lấy nội dung (thô) của mọi phần tử return trong phản hồi, theo thứ tự xuất hiện. */
    static List<String> returnsOf(String xml) {
        List<String> list = new ArrayList<>();
        Matcher m = Pattern.compile("<(?:\\w+:)?return(?:\\s[^>]*)?>(.*?)</(?:\\w+:)?return>", Pattern.DOTALL).matcher(xml);
        while (m.find()) list.add(m.group(1));
        return list;
    }

    /** Lấy nội dung phần tử con đầu tiên có tên tag (bỏ qua prefix namespace). */
    static String tagOf(String xml, String tag) {
        Matcher m = Pattern.compile("<(?:\\w+:)?" + tag + "(?:\\s[^>]*)?>(.*?)</(?:\\w+:)?" + tag + ">", Pattern.DOTALL).matcher(xml);
        return m.find() ? m.group(1).trim() : "";
    }

    static String unesc(String s) {
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&");
    }

    static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /** Che email, số điện thoại và token trong một dòng log. */
    static String maskLine(String s) {
        s = s.replaceAll("(?i)email=\\S+", "email=[EMAIL]");
        s = s.replaceAll("(?i)phone=\\S+", "phone=[PHONE]");
        s = s.replaceAll("(?i)token=\\S+", "token=[TOKEN]");
        s = s.replaceAll("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+", "[EMAIL]");
        return s;
    }
}
