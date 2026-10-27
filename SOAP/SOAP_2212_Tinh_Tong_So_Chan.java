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
 * [Mã câu hỏi: 2212] [SOAP Data] Tính tổng số chẵn
 * Loại: SOAP_DATA
 *
 * Đề bài:
 * Một dịch vụ web hỗ trợ SOAP version 1.1 được định nghĩa trong DataService?wsdl và triển khai tại http://<Exam_IP>:2221/DataService?wsdl.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi các phương thức SOAP. Ví dụ: studentCode="B15DCCN999", qCode="A1B2C3D4".
 * 
 * 
 * b. Triệu gọi phương thức getData(studentCode, qCode) để nhận về một danh sách số nguyên List<Integer> từ server.
 * 
 * 
 * c. Tính tổng tất cả các số chẵn trong danh sách nhận được.
 * 
 * 
 * d. Triệu gọi submitDataInt(studentCode, qCode, data) để gửi tổng số chẵn về server.
 * 
 * 
 * e. Đóng kết nối hoặc kết thúc client sau khi nộp kết quả.
 * 
 * 
 * Ví dụ:
 *  nếu dữ liệu nhận được là 
 * [756, 932, 839, 68, 518, 531, 302, 82, 324]
 *  thì dữ liệu nộp lại là 
 * 2982
 * .
 */
public class SOAP_2212_Tinh_Tong_So_Chan {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "Q2212";
        String endpoint = "http://" + host + ":" + port + "/DataService";

        try {
            // 0. Lấy namespace của dịch vụ từ WSDL (dự phòng "http://soap/")
            String ns = discoverNamespace(endpoint + "?wsdl", "http://soap/");

            // 1. Gọi getData(studentCode, qCode) để nhận List<Integer>
            String resp = callSoap(endpoint, ns, "getData", sCode, qCode, "");
            List<Long> nums = new ArrayList<>();
            for (String it : returnsOf(resp)) nums.add(Long.parseLong(it.trim()));

            // 2. Tính tổng các số chẵn
            long sum = 0;
            for (long v : nums) if (v % 2 == 0) sum += v;
            System.out.println("Tổng số chẵn = " + sum);

            // 3. Gọi submitDataInt(studentCode, qCode, sum)
            String result = callSoap(endpoint, ns, "submitDataInt", sCode, qCode, "<data>" + sum + "</data>");

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
