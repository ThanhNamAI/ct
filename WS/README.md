# Các bài WS

Mỗi bài nằm trong một file Java độc lập, dùng thư viện chuẩn Java 8 trở lên. Đề bài được giữ ở đầu file; các hàm hỗ trợ không dùng đến đã được bỏ.

Bài 961 sắp xếp trực tiếp các chuỗi bằng `(y + x).compareTo(x + y)`, rồi ghép bằng `String.join("", strs)`. Không cần đổi sang số nguyên rồi đổi lại chuỗi.

Phần SOAP dùng bộ đọc XML của Java để đọc đúng namespace, thẻ rỗng, CDATA và ký tự được mã hóa như `&amp;`, `&#65;`. Các bài đối tượng giữ XML của các trường khi gửi lại. Phần này có thêm code để xử lý đúng XML.

Khi gặp lỗi HTTP, SOAP Fault hoặc không đọc được namespace từ WSDL, chương trình dừng xử lý thay vì tiếp tục tính và submit dữ liệu sai. Luồng đọc và kết nối được đóng sau khi dùng.

Một số sửa lỗi khác:

- Bài 1419 làm tròn từ chuỗi thập phân bằng `BigDecimal`, tránh sai số khi chuyển qua `double`.
- Bài 1420 tìm cặp ước tới căn bậc hai rồi sắp xếp.
- Bài 1422 xoay mảng bằng `Collections.rotate`.
- Các bài chuỗi giải mã XML đúng một lần, giữ ký tự xuống dòng và xử lý kết quả rỗng.
- Các bài submit chỉ gửi tham số theo chữ ký ghi trong đề, bỏ tham số kết quả bị gửi lặp dưới tên khác.

Trước khi chạy với server, sửa `host`, `port`, `sCode`, `qCode` trong bài tương ứng. Các mã `JNP-...` có sẵn là mã trong bộ lời giải; cần thay bằng mã câu hỏi thực tế. Tên tham số SOAP đang theo đề trong file, chưa được xác nhận với WSDL của server thi thực tế.

Từ thư mục gốc dự án, biên dịch và chạy một bài:

```powershell
javac -encoding UTF-8 -d out WS/WS_961_Ghep_so_lon_nhat_tu_day_so.java
java -cp out WS.WS_961_Ghep_so_lon_nhat_tu_day_so
```

Chạy kiểm tra bằng Python 3 và JDK hỗ trợ `--release 8`:

```powershell
python WS/tests/verify.py
```

Bộ kiểm tra biên dịch bản sao của cả 30 bài trong thư mục tạm và chạy với server SOAP trên `127.0.0.1`. Nó kiểm tra kết quả, tên tham số, namespace, trường rỗng và các lỗi HTTP/XML. Các file nguồn và cấu hình kết nối thật không bị sửa khi kiểm tra.
