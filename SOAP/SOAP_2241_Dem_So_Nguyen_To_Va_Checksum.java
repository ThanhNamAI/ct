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
 * [Mã câu hỏi: 2241] [SOAP Data] Đếm số nguyên tố và checksum
 * Loại: SOAP_DATA
 *
 * Đề bài:
 * Một dịch vụ web hỗ trợ SOAP version 1.1 được định nghĩa trong DataService?wsdl và triển khai tại http://<Exam_IP>:2221/DataService?wsdl.
 * 
 * 
 * Yêu cầu
 * 
 * 
 * a. Sử dụng mã sinh viên và mã câu hỏi làm tham số khi gọi các phương thức SOAP. Ví dụ: 
 * studentCode="B21DCCN001"
 * , 
 * qCode="A1B2C3D4"
 * .
 * 
 * 
 * b. Triệu gọi phương thức 
 * getData(studentCode, qCode)
 *  để nhận danh sách số nguyên 
 * List<Integer>
 *  từ server. Ví dụ: 
 * [11,12,13,20]
 * .
 * 
 * 
 * c. Đếm số nguyên tố trong danh sách và tính checksum theo công thức 
 * sum((i + 1) * value[i]) mod 100000
 * , với i bắt đầu từ 0.
 * 
 * 
 * d. Triệu gọi 
 * submitDataString(studentCode, qCode, data)
 *  để gửi chuỗi 
 * primeCount=<n>;checksum=<m>
 * . Ví dụ: 
 * primeCount=2;checksum=154
 * .
 * 
 * 
 * e. Kết thúc chương trình client.
 */
public class SOAP_2241_Dem_So_Nguyen_To_Va_Checksum {
    public static void main(String[] args) {
        String host = "36.50.135.242";
        int port = 2221;
        String sCode = "B23DCCN861";
        String qCode = "Q2241";
        String endpoint = "http://" + host + ":" + port + "/DataService";

        try {
            // 0. Lấy namespace của dịch vụ từ WSDL (dự phòng "http://soap/")
            String ns = discoverNamespace(endpoint + "?wsdl", "http://soap/");

            // 1. Gọi getData(studentCode, qCode) để nhận List<Integer>
            String resp = callSoap(endpoint, ns, "getData", sCode, qCode, "");
            List<Long> nums = new ArrayList<>();
            for (String it : returnsOf(resp)) nums.add(Long.parseLong(it.trim()));

            // 2. Đếm số nguyên tố và checksum = sum((i + 1) * value[i]) mod 100000
            int primeCount = 0;
            long checksum = 0;
            for (int i = 0; i < nums.size(); i++) {
                if (isPrime(nums.get(i))) primeCount++;
                checksum += (long) (i + 1) * nums.get(i);
            }
            checksum = ((checksum % 100000) + 100000) % 100000;
            String data = "primeCount=" + primeCount + ";checksum=" + checksum;
            System.out.println(data);

            // 3. Gọi submitDataString(studentCode, qCode, data)
            String result = callSoap(endpoint, ns, "submitDataString", sCode, qCode, "<data>" + data + "</data>");

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

    static boolean isPrime(long n) {
        if (n < 2) return false;
        for (long i = 2; i * i <= n; i++) if (n % i == 0) return false;
        return true;
    }
}
