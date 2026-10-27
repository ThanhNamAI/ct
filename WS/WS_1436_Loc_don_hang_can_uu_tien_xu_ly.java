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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * [Mã câu hỏi: 1436] WS - ObjectService
 * Loại: WS_OBJECT
 *
 * Đề bài:
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp ObjectService.wsdl, được triển khai trên server tại URL http://<Exam_IP>:2221/ObjectService?wsdl để xử lý các bài toán với đối tượng.
 *
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (WS client) để giao tiếp với ObjectService thực hiện các công việc sau:
 *
 * a. Triệu gọi phương thức requestListOrderY với tham số đầu vào là mã sinh viên (studentCode) và mã câu hỏi (qCode) để nhận về danh sách đối tượng OrderY từ server. Mỗi đối tượng Order có các thuộc tính:
 *
 * orderId: kiểu String, đại diện cho mã đơn hàng.
 * orderDate: kiểu Date, đại diện cho ngày đặt hàng.
 * status: kiểu String, đại diện cho trạng thái đơn hàng, với các trạng thái có thể là 
 * pending
 * , 
 * processing
 * , hoặc 
 * completed
 * .
 * customerId: kiểu String, đại diện cho mã khách hàng.
 *
 * b. Lọc ra các đơn hàng có trạng thái là 
 * pending
 *  hoặc 
 * processing
 *  và có ngày đặt hàng (orderDate) quá 30 ngày so với ngày hiện tại -> để tạo danh sách các đơn hàng cần được ưu tiên xử lý.
 *
 * c. Triệu gọi phương thức submitListOrderY(String studentCode, String qCode, List<OrderY> orderYs) để gửi danh sách đơn hàng đã được lọc trở lại server.
 *
 * d. Kết thúc chương trình client.
 */
public class WS_1436_Loc_don_hang_can_uu_tien_xu_ly {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1436";
        String service = "ObjectService";

        String endpoint = "http://" + host + ":" + port + "/" + service;

        try {
            // 1. Lấy namespace từ WSDL rồi triệu gọi requestListOrderY(studentCode, qCode)
            String ns = getNamespace(endpoint + "?wsdl");
            String resp = call(endpoint, ns, "requestListOrderY", p("studentCode", sCode) + p("qCode", qCode));
            List<String> items = values(resp, "return");
            System.out.println("Nhận được: " + items);

            // 2. Xử lý theo yêu cầu đề bài
            // b. Giữ đơn pending/processing có orderDate quá 30 ngày so với hôm nay
            LocalDate today = LocalDate.now();
            List<String> result = new ArrayList<>();
            for (String o : items) {
                String st = field(o, "status");
                if (("pending".equals(st) || "processing".equals(st))
                        && ChronoUnit.DAYS.between(date(field(o, "orderDate")), today) > 30) result.add(o);
            }

            // 3. Gửi kết quả trở lại server
            String submitResp = call(endpoint, ns, "submitListOrderY", p("studentCode", sCode) + p("qCode", qCode) + objs("orderYs", result));
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

    static String objs(String name, List<String> inners) {
        StringBuilder sb = new StringBuilder();
        for (String inner : inners) sb.append("<").append(name).append(">").append(inner).append("</").append(name).append(">");
        return sb.toString();
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
            out.add(innerXml(e));
        }
        return out;
    }

    static String innerXml(Element e) throws Exception {
        TransformerFactory f = TransformerFactory.newInstance();
        f.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        f.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer t = f.newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter out = new StringWriter();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling())
            t.transform(new DOMSource(n), new StreamResult(out));
        return out.toString();
    }

    static String field(String obj, String tag) {
        try {
            NodeList nodes = xml("<root>" + obj + "</root>").getElementsByTagNameNS("*", tag);
            if (nodes.getLength() == 0) return null;
            Element e = (Element) nodes.item(0);
            String nil = e.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil");
            return "true".equals(nil) || "1".equals(nil) ? null : e.getTextContent().trim();
        } catch (Exception e) {
            throw new IllegalArgumentException("Khong doc duoc truong " + tag, e);
        }
    }

    static LocalDate date(String s) {
        return LocalDate.parse(s.trim().substring(0, 10));
    }
}
