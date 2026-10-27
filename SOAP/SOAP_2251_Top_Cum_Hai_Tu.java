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
 * [Mã câu hỏi: 2251] [SOAP Character] Top cụm hai từ
 * Loại: SOAP_CHARACTER
 *
 * Đề bài:
 * Một dịch vụ web hỗ trợ SOAP version 1.1 được định nghĩa trong CharacterService?wsdl và triển khai tại http://<Exam_IP>:2221/CharacterService?wsdl.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi các phương thức SOAP. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="BAA62945"
 * .
 * 
 * 
 * b. Triệu gọi phương thức 
 * requestString(studentCode, qCode)
 *  để nhận một câu tiếng Anh từ server. Ví dụ: 
 * payment ticket payment ticket refund.
 * .
 * 
 * 
 * c. Chuẩn hóa câu về chữ thường, loại bỏ ký tự không thuộc 
 * [a-z0-9 ]
 * , đếm tần suất cụm hai từ liên tiếp.
 * 
 * 
 * d. Triệu gọi 
 * submitString(studentCode, qCode, data)
 *  để gửi tối đa 03 cụm có tần suất cao nhất theo định dạng 
 * word_word=count|word_word=count
 * . Ví dụ: 
 * payment_ticket=2|ticket_payment=1|ticket_refund=1
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class SOAP_2251_Top_Cum_Hai_Tu {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "Q2251";
        String endpoint = "http://" + host + ":" + port + "/CharacterService";

        try {
            // 0. Lấy namespace của dịch vụ từ WSDL (dự phòng "http://soap/")
            String ns = discoverNamespace(endpoint + "?wsdl", "http://soap/");

            // 1. Gọi requestString(studentCode, qCode) để nhận chuỗi
            String resp = callSoap(endpoint, ns, "requestString", sCode, qCode, "");
            List<String> rs = returnsOf(resp);
            String input = rs.isEmpty() ? "" : unesc(rs.get(0));

            // 2. Chuẩn hóa: chữ thường, chỉ giữ [a-z0-9 ], đếm tần suất cụm hai từ liên tiếp
            String norm = input.toLowerCase().replaceAll("[^a-z0-9 ]", "").trim();
            String[] w = norm.isEmpty() ? new String[0] : norm.split("\\s+");
            Map<String, Integer> freq = new LinkedHashMap<>();
            for (int i = 0; i + 1 < w.length; i++) {
                freq.merge(w[i] + "_" + w[i + 1], 1, Integer::sum);
            }
            List<Map.Entry<String, Integer>> es = new ArrayList<>(freq.entrySet());
            // Tần suất giảm dần, bằng nhau thì theo thứ tự từ điển
            es.sort((a, b) -> !b.getValue().equals(a.getValue())
                ? Integer.compare(b.getValue(), a.getValue()) : a.getKey().compareTo(b.getKey()));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, es.size()); i++) {
                if (i > 0) sb.append("|");
                sb.append(es.get(i).getKey()).append("=").append(es.get(i).getValue());
            }
            String data = sb.toString();
            System.out.println("Top cụm hai từ = " + data);

            // 3. Gọi submitString(studentCode, qCode, data)
            String result = callSoap(endpoint, ns, "submitString", sCode, qCode, "<data>" + esc(data) + "</data>");

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
