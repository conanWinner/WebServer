# WebServer
Creation Web Server From Craft
## ============== API =============
#### Get
- [/](): _return index.html page_
- [/index.html](): _return index.html page_
- [/$name_of_page](): _return page ..._


#### POST
- [/api/users](): _create user_
- [/api/login](): _login user_

#### PUT
- [/api/users/$email_user](): _update user's information_

#### DELETE
- [/api/users/$email_user](): _delete user_

## =============== Đóng gói jar và chạy ứng dụng ============
- `mvn clean package`
- `java -jar target/httpserver-1.0-SNAPSHOT.jar`


## =============== Quy tắc Commit Messages ============

Dưới đây là các quy tắc để viết thông điệp commit. Hãy tuân thủ các mẫu này để giữ cho lịch sử commit của bạn rõ ràng và có tổ chức:

1. **Triển khai tính năng**
   - `feature/` **Create …**: Triển khai một tính năng nào đó.
   - **Ví dụ**: `feature/Create_user_authentication something ...`

2. **Sửa lỗi tính năng**
   - `fix/` **Fix …**: Sửa lỗi một tính năng nào đó.
   - **Ví dụ**: `fix/Fix user login bug`

3. **Triển khai test case**
   - `test/` **Implementing test for …**: Triển khai một test case nào đó.
   - **Ví dụ**: `test/Implementing test for user login`

4. **Sửa lỗi test case**
   - `fix/test/` **Fix …**: Sửa lỗi test case.
   - **Ví dụ**: `fix/test/Fix user login test`

5. **Cập nhật tài liệu**
   - `docs/` **Cập nhật …**: Cập nhật tài liệu.
   - **Ví dụ**: `docs/Update installation guide`

6. **Thay đổi định dạng**
   - `style/` **Modify …**: Thay đổi định dạng (không ảnh hưởng đến logic).
   - **Ví dụ**: `style/Modify CSS in contact.html page`

7. **Tái cấu trúc mã**
   - `refactor/` **Clean …**: Tái cấu trúc mã mà không sửa lỗi hay thêm tính năng mới.
   - **Ví dụ**: `refactor/Clean code in feature add user`

8. **Cập nhật thư viện phụ thuộc**
   - `chore/` **Update …**: Cập nhật thư viện phụ thuộc.
   - **Ví dụ**: `chore/Update version to JDK 21`
