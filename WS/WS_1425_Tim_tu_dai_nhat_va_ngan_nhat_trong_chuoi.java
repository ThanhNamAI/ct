package WS;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/**
 * [Mã câu hỏi: 1425] WS - CharacterService
 * Loại: WS_CHARACTER
 *
 * Đề bài:
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp CharacterService.wsdl, được triển khai trên server tại URL http://<Exam_IP>:2221/CharacterService?wsdl để xử lý các bài toán về chuỗi và ký tự.
 *
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (WS client) để giao tiếp với CharacterService thực hiện các công việc sau:
 *
 * a. Triệu gọi phương thức requestString với tham số đầu vào là mã sinh viên (studentCode) và mã câu hỏi (qCode) để nhận về một chuỗi (String) từ server. Chuỗi này có thể chứa các từ và khoảng trắng.
 *
 * b. Xử lý chuỗi nhận được để tìm từ có độ dài lớn nhất và từ có độ dài nhỏ nhất trong chuỗi. Nếu có nhiều từ có cùng độ dài lớn nhất hoặc nhỏ nhất, chọn từ xuất hiện đầu tiên trong chuỗi.
 *
 * c. Tạo một chuỗi mới theo định dạng: "[từ lớn nhất]; [từ nhỏ nhất]".
 *
 * d. Triệu gọi phương thức submitCharacterString(String studentCode, String qCode, String data) để gửi chuỗi kết quả đã xử lý trở lại server.
 *
 * Ví dụ:
 *  Nếu chuỗi nhận được từ phương thức requestCharacter là "this is a sample test", từ có độ dài lớn nhất là 
 * sample
 *  và từ có độ dài nhỏ nhất là 
 * a
 * . Chuỗi kết quả sẽ là 
 * sample; a
 * , và được gửi lại server qua phương thức submitCharacter.
 *
 * e. Kết thúc chương trình client.
 */
public class WS_1425_Tim_tu_dai_nhat_va_ngan_nhat_trong_chuoi {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1425";
        String service = "CharacterService";

        String endpoint = "http://" + host + ":" + port + "/" + service;

        try {
            // 1. Lấy namespace từ WSDL rồi triệu gọi requestString(studentCode, qCode)
            String ns = getNamespace(endpoint + "?wsdl");
            String resp = call(endpoint, ns, "requestString", p("studentCode", sCode) + p("qCode", qCode));
            List<String> items = values(resp, "return");
            System.out.println("Nhận được: " + items);

            // 2. Xử lý theo yêu cầu đề bài
            String text = items.isEmpty() ? "" : items.get(0);
            // b. Từ dài nhất / ngắn nhất (nếu bằng nhau lấy từ xuất hiện đầu tiên)
            String longest = null, shortest = null;
            for (String w : text.trim().split("\\s+")) {
                if (w.isEmpty()) continue;
                if (longest == null || w.length() > longest.length()) longest = w;
                if (shortest == null || w.length() < shortest.length()) shortest = w;
            }
            if (longest == null) longest = shortest = "";
            // c. Định dạng "[từ lớn nhất]; [từ nhỏ nhất]"
            String result = longest + "; " + shortest;

            // 3. Gửi kết quả trở lại server
            String submitResp = call(endpoint, ns, "submitCharacterString", p("studentCode", sCode) + p("qCode", qCode) + p("data", result));
            System.out.println("Kết quả gửi: " + submitResp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== SOAP / XML: chi giu cac ham bai nay su dung =====

    static String getNamespace(String wsdlUrl) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(wsdlUrl).openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        try {
            String ns = xml(readAll(conn.getInputStream())).getDocumentElement().getAttribute("targetNamespace");
            if (ns.isEmpty()) throw new IOException("WSDL khong co targetNamespace");
            return ns;
        } finally {
            conn.disconnect();
        }
    }

    static String readAll(InputStream in) throws IOException {
        if (in == null) return "";
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[4096];
            int n;
            while ((n = reader.read(buffer)) != -1) sb.append(buffer, 0, n);
            return sb.toString();
        }
    }

    static String call(String endpoint, String ns, String op, String params) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        String request = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\""
                + " xmlns:ws=\"" + esc(ns) + "\"><s:Body><ws:" + op + ">" + params
                + "</ws:" + op + "></s:Body></s:Envelope>";
        try {
            try (OutputStream out = conn.getOutputStream()) {
                out.write(request.getBytes(StandardCharsets.UTF_8));
            }
            int status = conn.getResponseCode();
            String resp = readAll(status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream());
            if (status < 200 || status >= 300) throw new IOException("HTTP " + status + ": " + resp);
            if (!resp.trim().isEmpty()) {
                NodeList faults = xml(resp).getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
                if (faults.getLength() > 0) throw new IOException("SOAP Fault: " + faults.item(0).getTextContent());
            }
            return resp;
        } finally {
            conn.disconnect();
        }
    }

    static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    static String p(String name, String value) {
        return "<" + name + ">" + esc(value) + "</" + name + ">";
    }

    static Document xml(String text) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        f.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return f.newDocumentBuilder().parse(new InputSource(new StringReader(text)));
    }

    static List<String> values(String text, String tag) throws Exception {
        List<String> out = new ArrayList<>();
        NodeList nodes = xml(text).getElementsByTagNameNS("*", tag);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            out.add(e.getTextContent());
        }
        return out;
    }
}
