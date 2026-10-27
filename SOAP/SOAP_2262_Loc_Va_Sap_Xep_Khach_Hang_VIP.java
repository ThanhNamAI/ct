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
 * [Mã câu hỏi: 2262] [SOAP Object] Lọc và sắp xếp khách hàng VIP
 * Loại: SOAP_OBJECT
 *
 * Đề bài:
 * Một dịch vụ web hỗ trợ SOAP version 1.1 được định nghĩa trong ObjectService?wsdl và triển khai tại http://<Exam_IP>:2221/ObjectService?wsdl.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi các phương thức SOAP. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="5AD2B818"
 * .
 * 
 * 
 * b. Triệu gọi phương thức 
 * requestListCustomer(studentCode, qCode)
 *  để nhận danh sách đối tượng 
 * Customer
 *  từ server. Ví dụ một phần tử: 
 * customerId=HC700;purchaseCount=8;totalSpent=9000
 * .
 * 
 * 
 * c. Giữ lại khách hàng có 
 * purchaseCount >= 6
 *  và 
 * totalSpent >= 4000
 * , sau đó sắp xếp giảm dần theo 
 * totalSpent
 * ; nếu bằng nhau thì sắp xếp tăng dần theo 
 * customerId
 * .
 * 
 * 
 * d. Triệu gọi 
 * submitListCustomer(studentCode, qCode, data)
 *  để gửi danh sách 
 * Customer
 *  đã lọc và sắp xếp. Ví dụ danh sách nộp lại bắt đầu bằng khách hàng có tổng chi tiêu cao nhất.
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class SOAP_2262_Loc_Va_Sap_Xep_Khach_Hang_VIP {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "Q2262";
        String endpoint = "http://" + host + ":" + port + "/ObjectService";

        try {
            // 0. Lấy namespace của dịch vụ từ WSDL (dự phòng "http://soap/")
            String ns = discoverNamespace(endpoint + "?wsdl", "http://soap/");

            // 1. Gọi requestListCustomer(studentCode, qCode) để nhận List<Customer>
            String resp = callSoap(endpoint, ns, "requestListCustomer", sCode, qCode, "");
            List<String> customers = returnsOf(resp);

            // 2. Giữ purchaseCount >= 6 và totalSpent >= 4000; sắp xếp totalSpent giảm dần, customerId tăng dần
            List<String> kept = new ArrayList<>();
            for (String c : customers) {
                int purchaseCount = (int) Double.parseDouble(tagOf(c, "purchaseCount"));
                double totalSpent = Double.parseDouble(tagOf(c, "totalSpent"));
                if (purchaseCount >= 6 && totalSpent >= 4000) kept.add(c);
            }
            kept.sort((a, b) -> {
                int cmp = Double.compare(Double.parseDouble(tagOf(b, "totalSpent")), Double.parseDouble(tagOf(a, "totalSpent")));
                return cmp != 0 ? cmp : tagOf(a, "customerId").compareTo(tagOf(b, "customerId"));
            });
            StringBuilder params = new StringBuilder();
            for (String c : kept) params.append("<data>").append(c).append("</data>");
            System.out.println("Số khách hàng VIP = " + kept.size());

            // 3. Gọi submitListCustomer(studentCode, qCode, data)
            String result = callSoap(endpoint, ns, "submitListCustomer", sCode, qCode, params.toString());

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
