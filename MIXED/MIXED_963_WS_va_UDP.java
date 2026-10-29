package MIXED;

import java.io.*;
import java.net.*;
import java.rmi.registry.*;
import java.util.*;

/**
 * [Mã câu hỏi: 963] WS + UDP
 * Loại: MIXED
 *
 * Đề bài:
 * Một hệ thống hỗ trợ các sản phẩm khuyến mãi ngẫu nhiên cho sinh viên gồm 02 thành phần được mô tả như sau:
 * 
 * 
 * a. WebService triển khai tại địa chỉ http://10.170.46.xx:22084/ProductService/ProductS?wsdl cung cấp chức năng cho phép client nhận thông tin sản phẩm khuyến mãi (getProduct) và đặt hàng (order)
 * 
 * 
 * b. UDP Server cung cấp chức năng cho phép client lấy thông tin khuyến mãi theo mã sản phẩm
 * 
 * 
 * Viết chương trình client thực hiện mô phỏng chức năng đặt hàng theo kịch bản dưới đây:
 * 
 * 
 * a. Triệu gọi phương thức getProduct trên WS với tham số đầu vào là mã sinh viên để nhận về một đối tượng Product với giá trị của id và code được thiết lập
 * 
 * 
 * b. Gửi thông điệp là một chuỗi chứa id và code sản phẩm vừa nhận được tới UDP Server tại port 2208 theo định dạng 
 * id;code
 * để nhận về một chuỗi mã giảm giá ngẫu nhiên. Tổng các chữ số nằm trong chuỗi mã giảm giá này chính là giá trị được khuyến mãi của sản phẩm
 * 
 * 
 * c. Triệu gọi phương thức order trên WS với tham số đầu vào là đối tượng Product với đầy đủ thông tin cần thiết để đặt sản phẩm
 * 
 * 
 * d. Kết thúc chương trình
 */
public class MIXED_963_WS_va_UDP {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int wsPort = 22084;
        int udpPort = 2208;
        String sCode = "B23DCCN861";
        String qCode = "JNP-963";

        String endpoint = "http://" + host + ":" + wsPort + "/ProductService/ProductS";

        try {
            // 0. Lấy namespace của dịch vụ từ WSDL (dự phòng "http://ws/")
            String ns = "http://ws/";
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(endpoint + "?wsdl").openConnection();
                c.setConnectTimeout(5000);
                c.setReadTimeout(5000);
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("targetNamespace=\"([^\"]+)\"").matcher(readAll(c));
                if (m.find()) ns = m.group(1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // a. WS: gọi getProduct(studentCode) để nhận Product (id, code, discountCode, discount)
            // Tên phần tử tham số lấy từ WSDL/XSD (mặc định JAX-WS là arg0 nếu server không đặt @WebParam)
            String pGet = paramName(endpoint + "?wsdl", "getProduct", 0, "arg0");
            String pOrder = paramName(endpoint + "?wsdl", "order", 0, "arg0");
            String resp = callSoap(endpoint, ns, "getProduct", "<" + pGet + ">" + sCode + "</" + pGet + ">");
            java.util.regex.Matcher rm = java.util.regex.Pattern
                .compile("<(?:\\w+:)?return(?:\\s[^>]*)?>(.*?)</(?:\\w+:)?return>", java.util.regex.Pattern.DOTALL).matcher(resp);
            if (!rm.find()) throw new IllegalStateException("Không nhận được Product từ WS");
            String inner = rm.group(1);
            String id = tagOf(inner, "id");
            String code = tagOf(inner, "code");
            System.out.println("Nhận Product: id=" + id + ", code=" + code);

            // b. UDP: gửi "id;code" tới UDP Server cổng 2208, nhận về chuỗi mã giảm giá
            byte[] sendData = (id + ";" + code).getBytes("UTF-8");
            String discountCode;
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(5000);
                socket.send(new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), udpPort));
                byte[] buf = new byte[4096];
                DatagramPacket reply = new DatagramPacket(buf, buf.length);
                socket.receive(reply);
                discountCode = new String(reply.getData(), 0, reply.getLength(), "UTF-8").trim();
            }
            System.out.println("Mã giảm giá: " + discountCode);

            // Tổng các chữ số trong mã giảm giá = giá trị khuyến mãi
            int sum = 0;
            for (char ch : discountCode.toCharArray()) {
                if (Character.isDigit(ch)) sum += ch - '0';
            }

            // c. WS: gọi order(product) với đối tượng Product đã cập nhật discountCode và discount
            inner = setTag(inner, "discountCode", discountCode);
            inner = setTag(inner, "discount", String.valueOf(sum));
            callSoap(endpoint, ns, "order", "<" + pOrder + ">" + inner + "</" + pOrder + ">");
            System.out.println("Đã order, discount = " + sum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Hàm tiện ích SOAP 1.1 (chỉ dùng java.net) =====

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

    /** Gửi SOAP Envelope gọi operation với phần tham số params và trả về XML phản hồi. */
    static String callSoap(String endpoint, String ns, String op, String params) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        String soapRequest =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"" + ns + "\">" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
            "<ws:" + op + ">" + params + "</ws:" + op + ">" +
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

    static String tagOf(String xml, String tag) {
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("<(?:\\w+:)?" + tag + "(?:\\s[^>]*)?>(.*?)</(?:\\w+:)?" + tag + ">", java.util.regex.Pattern.DOTALL).matcher(xml);
        return m.find() ? m.group(1).trim() : "";
    }

    /** Tìm tên phần tử con thứ index của operation op trong WSDL (và các XSD được import); không thấy thì trả về fallback. */
    static String paramName(String wsdlUrl, String op, int index, String fallback) {
        try {
            List<org.w3c.dom.Document> docs = new ArrayList<>();
            javax.xml.parsers.DocumentBuilderFactory f = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            docs.add(f.newDocumentBuilder().parse(wsdlUrl));
            org.w3c.dom.NodeList imps = docs.get(0).getElementsByTagNameNS("*", "import");
            for (int i = 0; i < imps.getLength(); i++) {
                String loc = ((org.w3c.dom.Element) imps.item(i)).getAttribute("schemaLocation");
                if (!loc.isEmpty()) docs.add(f.newDocumentBuilder().parse(loc));
            }
            for (org.w3c.dom.Document d : docs) {
                for (String kind : new String[] {"complexType", "element"}) {
                    org.w3c.dom.NodeList ns = d.getElementsByTagNameNS("*", kind);
                    for (int i = 0; i < ns.getLength(); i++) {
                        org.w3c.dom.Element e = (org.w3c.dom.Element) ns.item(i);
                        if (!op.equals(e.getAttribute("name"))) continue;
                        org.w3c.dom.NodeList kids = e.getElementsByTagNameNS("*", "element");
                        if (kids.getLength() > index) return ((org.w3c.dom.Element) kids.item(index)).getAttribute("name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fallback;
    }

    /** Đặt lại nội dung phần tử tag (thêm mới nếu chưa có). */
    static String setTag(String xml, String tag, String value) {
        xml = xml.replaceAll("<((?:\\w+:)?" + tag + ")\\s*/>", "<$1></$1>"); // <tag/> -> <tag></tag>
        java.util.regex.Pattern p = java.util.regex.Pattern
            .compile("(<(?:\\w+:)?" + tag + "(?:\\s[^>]*)?>).*?(</(?:\\w+:)?" + tag + ">)", java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher m = p.matcher(xml);
        if (m.find()) return m.replaceFirst(java.util.regex.Matcher.quoteReplacement(m.group(1) + value + m.group(2)));
        return xml + "<" + tag + ">" + value + "</" + tag + ">";
    }
}
