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
 * [Mã câu hỏi: 2231] SOAP Object — Calculate Final Price
 * Loại: SOAP_OBJECT
 *
 * Đề bài:
 * Một dịch vụ SOAP 
 * ObjectService
 *  được triển khai trên server.
 * 
 * 
 * WSDL:
 *  
 * http://<Exam_IP>:2221/ObjectService?wsdl
 * 
 * 
 * Yêu cầu: Viết chương trình Java (SOAP client) thực hiện các công việc sau:
 * 
 * 
 * 
 * 
 * 
 * 
 * Tạo Web Service Client từ WSDL URL trên (dùng wsimport hoặc NetBeans/IntelliJ Web Service Client wizard).
 * 
 * 
 * Gọi phương thức 
 * requestProductY(studentCode, qCode)
 *  với 
 * qCode = "<qCode trong đề bài>"
 *  để nhận về một đối tượng 
 * ProductY
 *  từ server, có các thuộc tính 
 * name
 * , 
 * price
 * , 
 * taxRate
 * , 
 * discount
 *  và 
 * finalPrice
 * .
 * 
 * 
 * Ví dụ:
 *  
 * ProductY{name="Laptop", price=1000.0, taxRate=10.0, discount=5.0, finalPrice=0.0}
 * 
 * 
 * 
 * 
 * 
 * 
 * Tính giá cuối cùng theo công thức:
 * 
 * 
 * finalPrice = price * (1 + taxRate / 100) * (1 - discount / 100)
 * 
 * 
 * Ví dụ:
 *  
 * price=1000, taxRate=10, discount=5
 *  -> 
 * finalPrice = 1000 * 1.10 * 0.95 = 1045.0
 * 
 * 
 * 
 * 
 * 
 * 
 * Gán 
 * finalPrice
 *  vào đối tượng 
 * ProductY
 * , sau đó gọi 
 * submitProductY(studentCode, qCode, productY)
 *  để gửi trở lại server.
 * 
 * 
 * Ví dụ:
 *  
 * submitProductY("B21DCCN001", "<qCode trong đề bài>", updatedProductY)
 * .
 * 
 * 
 * 
 * 
 * 
 * 
 * Kết thúc chương trình client. Server trả về status string (
 * "AC"
 *  hoặc 
 * "WA"
 * ).
 * 
 * 
 * 
 * 
 * 
 * 
 * Ví dụ
 * 
 * 
 * Input:  price=770.0;taxRate=16.0;discount=11.0
 * Output: 794.95
 */
public class SOAP_2231_Tinh_Final_Price_Theo_Thue_Giam_Gia {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "Q2231";
        String endpoint = "http://" + host + ":" + port + "/ObjectService";

        try {
            // 0. Lấy namespace của dịch vụ từ WSDL (dự phòng "http://soap/")
            String ns = discoverNamespace(endpoint + "?wsdl", "http://soap/");

            // 1. Gọi requestProductY(studentCode, qCode) để nhận đối tượng ProductY
            String resp = callSoap(endpoint, ns, "requestProductY", sCode, qCode, "");
            List<String> rs = returnsOf(resp);
            String inner = rs.isEmpty() ? "" : rs.get(0);
            double price = Double.parseDouble(tagOf(inner, "price"));
            double taxRate = Double.parseDouble(tagOf(inner, "taxRate"));
            double discount = Double.parseDouble(tagOf(inner, "discount"));

            // 2. finalPrice = price * (1 + taxRate / 100) * (1 - discount / 100)
            double finalPrice = round2(price * (1 + taxRate / 100) * (1 - discount / 100));
            System.out.println("finalPrice = " + finalPrice);

            // 3. Cập nhật finalPrice trong đối tượng rồi gọi submitProductY
            String fp = String.valueOf(finalPrice);
            if (inner.contains("finalPrice>")) {
                inner = inner.replaceAll("(<(?:\\w+:)?finalPrice(?:\\s[^>]*)?>).*?(</(?:\\w+:)?finalPrice>)", "$1" + fp + "$2");
            } else {
                inner = inner + "<finalPrice>" + fp + "</finalPrice>";
            }
            String result = callSoap(endpoint, ns, "submitProductY", sCode, qCode, "<data>" + inner + "</data>");

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
}
