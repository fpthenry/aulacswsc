# 04. Hướng dẫn quản trị nội dung website

Dành cho người quản lý spa: sửa chữ, ảnh, giá, cơ sở, bài viết, đánh giá trên website và xử lý lịch hẹn khách đặt online. Không cần biết lập trình.

## 1. Vào trang quản trị

1. Mở website, kéo xuống cuối trang, bấm **Quản trị** (hoặc vào thẳng `/login`).
2. Đăng nhập bằng tài khoản có quyền **ADMIN** (nhân viên quyền STAFF chỉ xem/sửa lịch hẹn và khách hàng).
3. Sau khi đăng nhập bạn vào **/dashboard**. Menu **Nội dung & dữ liệu** chứa mọi mục bên dưới. Nút **Xem website** mở website khách.

> Mỗi mục đều có danh sách → bấm **Sửa** (bút chì) để chỉnh, **Thêm mới** để tạo, **Lưu** để cập nhật. Website hiển thị thay đổi sau khoảng **1 phút** (trình duyệt lưu tạm). Muốn thấy ngay: tải lại trang bằng Ctrl+F5.

## 2. Quy ước chung khi nhập

| Bạn thấy                                                    | Ý nghĩa                                                                                                                                                                                                                                                                                                        |
| ----------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Trường có đuôi **Vi** / **En** (vd _Name Vi_, _Name En_)    | Bản tiếng Việt / tiếng Anh. Website `/vi` dùng ô Vi, `/en` dùng ô En. **Để trống ô En thì website tiếng Anh hiện bản tiếng Việt**                                                                                                                                                                              |
| **Slug** (không có ô nhập)                                  | Đường dẫn trên website **do hệ thống tự tạo** khi thêm mới: tên/tiêu đề tiếng Việt bỏ dấu + ngày, vd "Massage đá nóng" tạo ngày 11/10/2026 → `/vi/services/massage-da-nong-20261011`. Bài viết dùng _ngày đăng_. Trùng thì thêm `-2`, `-3`. **Sửa tên sau này không đổi đường dẫn** (link đã chia sẻ vẫn chạy) |
| **Active**                                                  | Bỏ tích = ẩn khỏi website (dữ liệu vẫn giữ). Dùng thay cho Xóa                                                                                                                                                                                                                                                 |
| **Display Order**                                           | Số nhỏ hiện trước                                                                                                                                                                                                                                                                                              |
| Ô **mô tả nhiều dòng** (Benefits, Notes)                    | **Mỗi dòng một ý** — website tự hiện thành danh sách có dấu tích                                                                                                                                                                                                                                               |
| Ô **nội dung dài** (About Content, Content, Policy Content) | Cách đoạn bằng **một dòng trống**. Chỉ là chữ thường (không in đậm, không chèn link)                                                                                                                                                                                                                           |
| Ô **ảnh**                                                   | Bấm **Chọn ảnh từ máy** → ảnh tự nén sang WebP và tải lên. Hoặc dán link ảnh có sẵn                                                                                                                                                                                                                            |

**Ảnh — nên chuẩn bị:** ảnh của chính spa (không dùng ảnh lấy từ website khác), ảnh ngang tối thiểu 1200px, rõ nét, sáng. Hệ thống tự thu nhỏ còn tối đa 1600px và tạo bản nhỏ cho điện thoại.

## 3. Phần nào trên website sửa ở đâu

### 3.1 Cài đặt website (menu **Cài đặt website**)

Chỉ có **một bản ghi** — luôn bấm _Sửa_ bản ghi đầu tiên, **không tạo thêm**.

| Phần trên website                                                               | Trường cần sửa                                                                                                                                                                                               |
| ------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------- |
| Tên spa (header, footer, tiêu đề trang)                                         | Site Name                                                                                                                                                                                                    |
| Logo (thay chữ tên spa ở header)                                                | Logo Url — ảnh nền trong suốt, cao ~80px                                                                                                                                                                     |
| **Hero** – dòng chữ lớn đầu trang                                               | Slogan Vi / Slogan En. Website tách 2 dòng: dòng sau **in nghiêng màu vàng đồng**. Tự tách ở chữ "và"/"and"; muốn chọn chỗ tách thì đặt dấu `                                                                | `, vd `Hương gỗ, thảo mộc \| và một khoảng lặng cho riêng bạn` |
| Hero – dòng chữ nhỏ bên dưới                                                    | Hero Subtitle Vi / En                                                                                                                                                                                        |
| Hero – **slideshow ảnh nền** (tự chuyển ảnh 7 giây, zoom chậm, có nút tạm dừng) | **Hero Slides**: bấm _Chọn ảnh từ máy_ để thêm từng ảnh, dùng ←/→ để đổi thứ tự, ✕ để xóa. Nên chọn 3–5 ảnh **ngang, tông tối, không có chữ/logo** — chữ trên hero màu sáng nên ảnh tối trông sang và dễ đọc |
| Hero – ảnh nền dự phòng / ảnh chia sẻ Facebook                                  | Hero Image Url (dùng khi Hero Slides trống; nên là ảnh đầu tiên của slideshow)                                                                                                                               |
| Hero – video nền                                                                | Hero Video Url (file `.mp4` ngắn 10–20 giây, < 8MB, không tiếng) + tích **Hero Video Enabled**. Bỏ tích để chỉ dùng ảnh. Khách bật "giảm chuyển động" hoặc tiết kiệm dữ liệu sẽ chỉ thấy ảnh                 |
| Nhạc nền (nút ♪ trên header, mặc định tắt)                                      | Background Music Url (file `.mp3`). Để trống = ẩn nút nhạc                                                                                                                                                   |
| **Giới thiệu** – tiêu đề, đoạn văn                                              | About Title Vi/En, About Content Vi/En                                                                                                                                                                       |
| Giới thiệu – 3 điểm nổi bật có icon (vị trí, kỹ thuật viên, trải nghiệm riêng)  | Highlight 1/2/3 Title + Text (Vi/En). Để trống tiêu đề = ẩn ô đó                                                                                                                                             |
| Nút gọi điện nổi, footer                                                        | Hotline                                                                                                                                                                                                      |
| Nút WhatsApp nổi                                                                | Whatsapp Number (dạng `84901234567` hoặc `0901234567`)                                                                                                                                                       |
| Nút Zalo nổi                                                                    | Zalo Number                                                                                                                                                                                                  |
| Email ở footer                                                                  | Email                                                                                                                                                                                                        |
| Biểu tượng mạng xã hội ở footer                                                 | Facebook / Instagram / Tiktok / Youtube Url (để trống = ẩn)                                                                                                                                                  |
| Giờ mở cửa ở footer, trang Cơ sở                                                | Opening Hours Vi / En                                                                                                                                                                                        |
| Mô tả hiện trên Google, Facebook khi chia sẻ trang chủ                          | Meta Description Vi / En (120–160 ký tự)                                                                                                                                                                     |
| Trang **Chính sách đặt lịch**                                                   | Policy Content Vi / En                                                                                                                                                                                       |

Ảnh trong khối Giới thiệu và dải ảnh trang chủ lấy tự động từ **Thư viện ảnh** (3 và 8 ảnh đầu).

### 3.2 Cơ sở (menu **Cơ sở**)

| Trường                                         | Dùng cho                                                                                                                                        |
| ---------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| Name Vi/En, Address Vi/En, Phone, Email        | Trang Cơ sở, footer, bước 1 đặt lịch, email/lịch gửi khách                                                                                      |
| Whatsapp, Zalo                                 | Nút liên hệ riêng của từng cơ sở                                                                                                                |
| **Open Time / Close Time** (HH:mm, vd `10:00`) | Giờ mở – đóng cửa hiển thị                                                                                                                      |
| **Last Booking Time** (vd `21:30`)             | Khung giờ cuối khách được chọn khi đặt online                                                                                                   |
| **Capacity**                                   | **Số khách phục vụ cùng lúc** (≈ số giường/ghế). Hệ thống khóa khung giờ khi đủ người. Đặt sai số này sẽ nhận quá tải hoặc báo kín sớm          |
| Map Embed Url                                  | Bản đồ nhúng: Google Maps → _Chia sẻ_ → _Nhúng bản đồ_ → copy **phần link trong `src="..."`** (bắt đầu `https://www.google.com/maps/embed?...`) |
| Map Direction Url                              | Nút _Chỉ đường_: Google Maps → _Chia sẻ_ → _Sao chép đường liên kết_                                                                            |
| Image Url                                      | Ảnh mặt tiền (hiện trước khi khách bấm "Xem bản đồ")                                                                                            |
| Active                                         | Bỏ tích = ngừng nhận đặt lịch online ở cơ sở này                                                                                                |

### 3.3 Danh mục & dịch vụ

**Danh mục dịch vụ** — các ô Massage / Chăm sóc tóc / Chăm sóc da / Combo trên trang chủ và bộ lọc trang Dịch vụ: Name, Description (dòng nhỏ dưới tên), Image (ảnh vuông), Display Order.

**Dịch vụ** — mỗi thẻ dịch vụ và trang chi tiết:

| Trường                  | Hiển thị                                                                                                       |
| ----------------------- | -------------------------------------------------------------------------------------------------------------- |
| Name Vi/En              | Tên dịch vụ                                                                                                    |
| Short Description Vi/En | 1–2 câu trên thẻ dịch vụ và đầu trang chi tiết (cũng là mô tả Google)                                          |
| Description Vi/En       | Phần "Về liệu trình" ở trang chi tiết                                                                          |
| Benefits Vi/En          | "Công dụng" — mỗi dòng một ý                                                                                   |
| Notes Vi/En             | "Lưu ý" — mỗi dòng một ý                                                                                       |
| **Price**               | Giá gốc. Nếu là khoảng giá: giá thấp nhất                                                                      |
| **Sale Price**          | Giá khuyến mãi. Có giá này → website hiện giá KM + giá gốc gạch ngang + nhãn "Ưu đãi". Để trống nếu không giảm |
| **Price Max**           | Chỉ điền khi muốn hiện **khoảng giá** (vd 250.000 – 350.000đ). Không dùng chung với Sale Price                 |
| **Duration Minutes**    | Thời lượng (phút). Dùng để tính giờ kết thúc và chỗ trống — **phải đúng thực tế**                              |
| Image Url               | Ảnh thẻ dịch vụ (tỷ lệ 4:3)                                                                                    |
| **Featured**            | Tích = hiện ở "Dịch vụ nổi bật" trang chủ (tối đa 6)                                                           |
| Category                | Thuộc danh mục nào                                                                                             |
| Active                  | Bỏ tích = ẩn, khách không đặt được                                                                             |

> Giá khách thấy khi đặt lịch = (Sale Price nếu có, không thì Price) × số người. Đổi giá không ảnh hưởng lịch đã đặt (lịch lưu giá tại thời điểm đặt).

### 3.4 Đánh giá (menu **Đánh giá**)

| Trường                                          | Ghi chú                                                                                                        |
| ----------------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| Author Name, Rating (1–5), Comment, Review Date | Nội dung hiện trên slider trang chủ và trang Đánh giá                                                          |
| Video Url                                       | Link YouTube (hiện video ngay trong thẻ) hoặc TikTok (hiện nút "Xem video")                                    |
| **Approved**                                    | **Chỉ đánh giá được duyệt mới hiện trên website.** Khách có tài khoản viết đánh giá sẽ ở trạng thái chưa duyệt |
| Featured                                        | Ưu tiên hiện đầu tiên                                                                                          |
| Service                                         | Đánh giá cho dịch vụ nào (không bắt buộc)                                                                      |

Chỉ đăng đánh giá **thật** của khách, có sự đồng ý của khách.

### 3.5 Thư viện ảnh (menu **Thư viện ảnh**)

Image (bắt buộc), **Alt Vi** (bắt buộc — mô tả ngắn nội dung ảnh, giúp người khiếm thị và Google, vd "Phòng massage đôi với đèn gỗ ấm"), Alt En, Caption Vi/En (chú thích dưới ảnh, không bắt buộc), Display Order, Active. Width/Height tự điền khi tải ảnh lên — giữ nguyên.

### 3.6 Bài viết / Tin tức (menu **Bài viết / Tin tức**)

| Trường          | Ghi chú                                                     |
| --------------- | ----------------------------------------------------------- |
| Title Vi/En     | Tiêu đề (đường dẫn `/vi/blog/<tieu-de>-<ngay-dang>` tự tạo) |
| Excerpt Vi/En   | Tóm tắt 1–2 câu (hiện ở danh sách và Google)                |
| Content Vi/En   | Nội dung; cách đoạn bằng dòng trống                         |
| Cover Image Url | Ảnh bìa ngang 16:9                                          |
| Category        | Tin tức / Sự kiện / Review / Cẩm nang                       |
| **Published**   | Tích = hiện trên website. Bỏ tích = bản nháp                |
| Published At    | Ngày đăng (sắp xếp mới nhất trước)                          |

## 4. Xử lý lịch hẹn khách đặt online

Mỗi lịch đặt từ website có **mã** dạng `AL261011K7QM`, **Source** = `WEBSITE` (đặt đầy đủ) hoặc `QUICK` (đặt nhanh — thường chỉ có số điện thoại, cần gọi lại hỏi tên).

Quy trình đề xuất (menu **Lịch hẹn**, lọc theo trạng thái `PENDING`):

| Bước                           | Trạng thái (Status)                                        |
| ------------------------------ | ---------------------------------------------------------- |
| Lịch mới khách vừa đặt         | `PENDING`                                                  |
| Đã gọi/nhắn xác nhận với khách | `CONFIRMED`                                                |
| Khách đang làm dịch vụ         | `IN_PROGRESS`                                              |
| Xong, đã thanh toán            | `COMPLETED` + Payment Status `PAID` + Payment Method       |
| Khách hủy                      | `CANCELLED` — **khung giờ được mở lại** cho người khác đặt |
| Khách không đến                | `NO_SHOW` — khung giờ được mở lại                          |

- Dịch vụ của từng lịch nằm ở menu **Chi tiết lịch hẹn** (lọc theo mã lịch); có thể gán **Staff** (kỹ thuật viên).
- Khách nhận khách vãng lai / qua điện thoại: tạo lịch trong menu **Lịch hẹn** với Source = `PHONE` / `WALK_IN`, nhớ điền **End Time** để hệ thống tính đúng chỗ trống (không điền thì coi như 60 phút).
- Khách tự tra cứu lịch tại `/vi/booking/lookup` bằng mã + số điện thoại.

## 5. Tin nhắn liên hệ

Menu **Tin nhắn liên hệ**: tin khách gửi từ form ở trang Cơ sở. Sau khi trả lời, tích **Handled**.

## 6. Kiểm tra sau khi sửa

- Mở **Xem website**, kiểm tra cả **VI** và **EN** (nút VI/EN trên header).
- Kiểm tra trên điện thoại.
- Đổi giờ mở cửa / sức chứa → vào _Đặt lịch_ thử chọn ngày để xem khung giờ.
- Google cập nhật nội dung mới sau vài ngày; sitemap tự cập nhật tại `/sitemap.xml`.

## 7. Không nên

- Xóa cơ sở, dịch vụ, khách hàng đã có lịch hẹn → báo lỗi. Hãy bỏ tích **Active**.
- Tạo thêm bản ghi **Cài đặt website** (website chỉ dùng bản ghi đầu tiên).
- Dán số điện thoại có chữ (vd "0901 234 567 (Ms Lan)") vào ô Phone/Hotline — chỉ nhập số.
