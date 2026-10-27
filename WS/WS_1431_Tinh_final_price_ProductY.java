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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * [Mã câu hỏi: 1431] WS - ObjectService
 * Loại: WS_OBJECT
 *
 * Đề bài:
 * Một dịch vụ web (hỗ trợ SOAP version 1.1) được định nghĩa và mô tả trong tệp ObjectService.wsdl, được triển khai trên server tại URL http://<Exam_IP>:2221/ObjectService?wsdl để xử lý các bài toán với đối tượng.
 *
 * Yêu cầu:
 *  Viết chương trình tại máy trạm (WS client) để giao tiếp với ObjectService thực hiện các công việc sau:
 *
 * a. Triệu gọi phương thức requestProductY với tham số đầu vào là mã sinh viên (studentCode) và mã câu hỏi (qCode) để nhận về một đối tượng ProductY từ server. Đối tượng này có các thuộc tính:
 *
 * price (giá gốc): float, đại diện cho giá của sản phẩm.
 * taxRate (thuế): float, đại diện cho phần trăm thuế áp dụng trên giá gốc.
 * discount (chiết khấu): float, đại diện cho phần trăm chiết khấu áp dụng trên giá gốc.
 *
 * b. Thực hiện
 *
 * Tính toán giá cuối cùng của sản phẩm (finalPrice) theo công thức:
 *
 * finalPrice = price * (1 + taxRate / 100) * (1 - discount / 100)
 *
 * Cập nhật thuộc tính finalPrice trong đối tượng ProductY với giá trị đã tính toán.
 *
 * c. Triệu gọi phương thức submitProductY(String studentCode, String qCode, ProductY object) để gửi đối tượng ProductY với giá cuối cùng đã được tính toán trở lại server.
 *
 * Ví dụ:
 *  Nếu đối tượng ProductY có các thuộc tính price = 100.0, taxRate = 10.0, và discount = 5.0, thì finalPrice sẽ được tính là:
 *
 * finalPrice = 100 * (1 + 10/100) * (1 - 5/100) = 104.5
 *
 * Đối tượng ProductY với giá trị finalPrice = 104.5 sẽ được gửi lại server qua phương thức submitProductY.
 *
 * d. Kết thúc chương trình client.
 */
public class WS_1431_Tinh_final_price_ProductY {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "JNP-1431";
        String service = "ObjectService";

        String endpoint = "http://" + host + ":" + port + "/" + service;

        try {
            // 1. Lấy namespace từ WSDL rồi triệu gọi requestProductY(studentCode, qCode)
            String ns = getNamespace(endpoint + "?wsdl");
            String resp = call(endpoint, ns, "requestProductY", p("studentCode", sCode) + p("qCode", qCode));
            List<String> items = values(resp, "return");
            System.out.println("Nhận được: " + items);

            // 2. Xử lý theo yêu cầu đề bài
            String obj = items.get(0);
            // b. finalPrice = price * (1 + taxRate / 100) * (1 - discount / 100)
            float price = Float.parseFloat(field(obj, "price"));
            float taxRate = Float.parseFloat(field(obj, "taxRate"));
            float discount = Float.parseFloat(field(obj, "discount"));
            float finalPrice = price * (1 + taxRate / 100) * (1 - discount / 100);
            List<String> result = Arrays.asList(setField(obj, "finalPrice", String.valueOf(finalPrice)));

            // 3. Gửi kết quả trở lại server
            String submitResp = call(endpoint, ns, "submitProductY", p("studentCode", sCode) + p("qCode", qCode) + objs("object", result));
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

    static String setField(String obj, String tag, String value) throws Exception {
        Document doc = xml("<root>" + obj + "</root>");
        NodeList nodes = doc.getElementsByTagNameNS("*", tag);
        Element e;
        if (nodes.getLength() == 0) {
            Element first = (Element) doc.getDocumentElement().getElementsByTagName("*").item(0);
            e = doc.createElementNS(first == null ? null : first.getNamespaceURI(), tag);
            doc.getDocumentElement().appendChild(e);
        } else {
            e = (Element) nodes.item(0);
        }
        e.removeAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil");
        e.setTextContent(value);
        return innerXml(doc.getDocumentElement());
    }
}
