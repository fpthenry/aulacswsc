# Nhật ký thay đổi (CHANGELOG)

Tài liệu **duy nhất** ghi lại mọi chức năng được thêm / sửa trong dự án aulacswsc.

**Quy ước cập nhật**

1. Mỗi lần thêm hoặc sửa chức năng → ghi vào mục **[Chưa commit]** ở đầu file (nhóm: Thêm / Thay đổi / Sửa lỗi / Lưu ý vận hành).
2. Khi commit → đổi tiêu đề **[Chưa commit]** thành `## <ngày> — <mã commit> <tiêu đề commit>`, rồi tạo mục **[Chưa commit]** mới, trống.
3. Mục **[Chưa commit]** luôn có phần **Nội dung commit** viết sẵn — copy dùng cho `git commit`.

Chi tiết kỹ thuật xem thêm: [01 Database & phân quyền](01-database-va-phan-quyen.md) · [02 Luồng xử lý](02-luong-xu-ly-crud.md) · [03 Quy ước dự án](03-quy-uoc-du-an.md) · [04 Hướng dẫn quản trị nội dung](04-huong-dan-quan-tri-noi-dung.md)

---

## [Chưa commit]

_Chưa có thay đổi._

---

## 2026-10-12 — `0bc4158` feat(site): website khách spa, đặt lịch online, quản trị nội dung song ngữ và hiệu ứng giao diện

> Commit trước đó `2f13502` (cùng tiêu đề, bản ngắn) chỉ chứa việc xóa `src/main/webapp/robots.txt` (robots.txt nay sinh động ở server); toàn bộ thay đổi bên dưới nằm trong `0bc4158`.

### Thay đổi giao diện (2026-10-12)

- **Chữ dễ đọc hơn**: mọi con số (giá, số liệu, tổng tiền, mã đặt lịch, thời lượng) dùng font Be Vietnam Pro đậm với chữ số thẳng hàng (font tiêu đề Cormorant dùng chữ số kiểu cổ, "1" trông như "ı"); tên thẻ dịch vụ, tiêu đề nhỏ, dải chữ chạy đậm hơn; màu chữ phụ đậm hơn (tương phản ~7:1).
- **Trang Cơ sở gọn hơn**: danh sách thẻ nhỏ (số thứ tự, ảnh thu nhỏ, địa chỉ, giờ, điện thoại, nút Đặt lịch / Chỉ đường / WhatsApp / Zalo) bên trái + **một bản đồ dùng chung** dính khi cuộn bên phải; bấm cơ sở nào bản đồ đổi theo cơ sở đó.

### Thêm

**Website khách (`/vi`, `/en`)**

- Trang chủ: hero nền tối dạng **slideshow Ken Burns** (ảnh tự chuyển 7 giây, zoom chậm, nút tạm dừng, chấm chọn ảnh), tiêu đề 2 dòng (dòng sau in nghiêng màu đồng), header trong suốt chữ sáng.
- Hiệu ứng khi cuộn trang chủ: thanh tiến trình cuộn; tiêu đề mục trồi lên + đường kẻ dài ra; ảnh giới thiệu "vén rèm"; thẻ nổi bật có vòng viền vàng; **dải số liệu đếm tăng dần** (liệu trình, cơ sở, đánh giá, điểm TB); **dải chữ chạy ngang** tên liệu trình; danh mục phóng ra + mô tả hiện khi rê chuột; **mục đánh giá nền tối** với dấu ngoặc kép lớn; **2 hàng ảnh trôi ngược chiều** theo tốc độ cuộn; CTA cuối trang có ảnh nền parallax; thẻ cơ sở trượt xen kẽ trái/phải.
- **Khối đầu trang tối dùng chung** (`site-page-hero`) cho Dịch vụ, Cơ sở, Đánh giá, Thư viện ảnh, Tin tức, Chi tiết bài viết, Đặt lịch, Tra cứu, Chính sách, 404: ảnh nền zoom chậm, lớp phủ tối, hoa văn lá, chữ hiện lần lượt. Ảnh tự lấy từ nội dung (ảnh cơ sở, ảnh bìa bài mới nhất, ảnh thư viện, hoặc lần lượt từ Hero Slides).
- Trang: dịch vụ (lọc nhóm / giá / thời lượng, tìm không dấu), chi tiết dịch vụ, cơ sở + bản đồ + form liên hệ, đánh giá, thư viện ảnh (masonry + lightbox phím/vuốt), tin tức (phân trang, chuyên mục), chi tiết bài viết, chính sách, 404.
- Header cố định, chuyển VI/EN, chế độ sáng/tối, **nhạc nền** bật/tắt (mặc định tắt, nhớ lựa chọn), nút WhatsApp / Zalo / gọi điện nổi.
- SEO: title, meta description, Open Graph, canonical, hreflang theo từng trang; JSON-LD (DaySpa, Service+Offer, Review, BlogPosting); `sitemap.xml`, `robots.txt` sinh động.
- Khả năng truy cập: điều hướng bàn phím, focus rõ, nhãn form, lỗi `role="alert"`, tôn trọng `prefers-reduced-motion`.

**Đặt lịch**

- Luồng 7 bước: cơ sở → dịch vụ (nhiều) → ngày (không cho chọn ngày đã qua) → giờ (khung 15 phút, khóa giờ đã qua / đã đầy) → số người → thông tin → xác nhận. Thanh tiến trình, quay lại không mất dữ liệu, lưu nháp localStorage.
- Chế độ **Đặt nhanh** (dịch vụ + ngày + giờ + SĐT), trang **Tra cứu đặt lịch** (mã + SĐT).
- Sau khi đặt: mã đặt lịch, tóm tắt, tải `.ics`, gửi qua WhatsApp (điền sẵn nội dung) / Zalo (sao chép nội dung).
- Server: tính khung giờ trống theo **sức chứa cơ sở**, tính giá, tự tạo hồ sơ khách theo SĐT, tạo mã `AL+yyMMdd+XXXX`; chống spam bằng honeypot + giới hạn tần suất theo IP.

**Dữ liệu & quản trị**

- Bảng mới: `SiteSetting`, `Branch`, `GalleryImage`, `BlogPost`; nội dung song ngữ cột Vi/En.
- API công khai `/api/public/**` (chỉ trả dữ liệu đang bật / đã duyệt / đã xuất bản), upload ảnh `/api/admin/uploads`.
- Form quản trị: chọn ảnh từ máy (nén WebP 3 cỡ ngay trên trình duyệt); ô **Hero Slides** quản lý danh sách ảnh slideshow (thêm, xóa, đổi thứ tự).
- **Slug tự sinh ở server** (tiêu đề không dấu + ngày, trùng thì thêm `-2`), giữ nguyên khi sửa; bỏ ô Slug khỏi form.
- Menu quản trị tiếng Việt; trang chủ quản trị chuyển sang `/dashboard`, nút "Xem website".
- Liquibase: role `ROLE_STAFF`/`ROLE_CUSTOMER`, dữ liệu mẫu dev, cột `hero_slides`; tắt context `faker`.

**Công cụ**

- `npm run seed:demo`: nạp dữ liệu demo (ảnh trong `images/anh_spa`, dịch vụ, thư viện, đánh giá, bài viết, khuyến mãi, nhân viên, lịch hẹn, tin nhắn), **nhạc nền tự tổng hợp** (WAV 60 giây, lặp liền mạch), slideshow hero; `--clean` xóa toàn bộ demo; `--hero-only`, `--music-only`.
- `npm run commit`: commit theo quy ước Conventional Commits (`--push` để đẩy lên GitHub).

**Tài liệu**

- `docs/01`–`03` cập nhật; `docs/04` hướng dẫn quản trị nội dung; **`docs/CHANGELOG.md`** (file này).

### Sửa lỗi

- Menu header xuống 2 dòng ở màn hình ~1100px → chữ menu không xuống dòng, chuyển sang menu hamburger từ 1240px.
- Tạo bài viết / Cài đặt website lỗi `Table ... doesn't exist`: database dev cũ lệch changeset → xóa và tạo lại database (hướng dẫn trong docs/03).
- Trang web trắng sau `./mvnw`: Maven bỏ qua build frontend vì checksum cũ (`target/checksums.csv`) → xóa checksum / `npm run webapp:build`.
- Ô Slug bắt buộc gây lỗi lưu → slug do server sinh.
- Ảnh `.webp` upload ngoài form quản trị bị vỡ (thiếu bản `-800`/`-24`) → script demo gửi đủ 3 cỡ.
- Hiệu ứng tiêu đề mục không hiện → đổi sang chỉ dùng `transform`/`opacity` (mượt hơn, chạy trên GPU).
- Nhạc nền demo có tiếng "tách" khi lặp → làm tròn tần số theo chu kỳ đoạn nhạc.

### Lưu ý vận hành

- Ảnh trong `images/` tải từ website khác (bản quyền) → chỉ dùng xem thử; `.gitignore` đã bỏ qua `images/` và `uploads/`. Trước khi chạy thật: `npm run seed:demo -- --clean` và upload ảnh của spa.
- Chạy lại `npx jhipster jdl` sẽ ghi đè các file đã sửa tay → xem bảng "Sửa file do JHipster sinh" trong docs/03.
- Test tích hợp (`*ResourceIT`) chưa cập nhật theo slug tự sinh / phân quyền mới.

### Nội dung commit

```text
feat(site): website khách spa, đặt lịch online, quản trị nội dung song ngữ và hiệu ứng giao diện

- Website /vi /en: hero slideshow nền tối, hiệu ứng cuộn (đếm số, chữ chạy, ảnh trôi, parallax),
  khối đầu trang tối cho mọi trang con, dịch vụ, cơ sở (danh sách + bản đồ dùng chung), đánh giá,
  thư viện, tin tức, chính sách; số/giá dùng font sans chữ số thẳng hàng cho dễ đọc
- Đặt lịch 7 bước + đặt nhanh + tra cứu; server tính giờ trống theo sức chứa, giá, mã đặt lịch;
  .ics, WhatsApp/Zalo; chống spam honeypot + giới hạn tần suất
- Bảng SiteSetting/Branch/GalleryImage/BlogPost song ngữ; API /api/public/**; upload ảnh WebP;
  Hero Slides; slug tự sinh ở server; menu quản trị tiếng Việt, /dashboard
- SEO (meta, OG, hreflang, JSON-LD, sitemap, robots), khả năng truy cập, reduced-motion
- scripts/seed-demo.mjs (ảnh, nhạc nền tự tổng hợp, dữ liệu demo, --clean); docs 01–04 + CHANGELOG
```

---

## 2026-10-11 — `1e11f31` feat: sinh entity spa, phân quyền admin/staff/customer và tài liệu dự án

- Sinh entity spa từ JDL: ServiceCategory, SpaService, Customer, Staff, Booking, BookingItem, Promotion, Review, ContactMessage.
- Phân quyền `ROLE_ADMIN` / `ROLE_STAFF` / `ROLE_CUSTOMER`; khách chỉ xem/tạo lịch của mình; đánh giá cần duyệt.
- Tài liệu docs/01–03; lệnh `npm run commit`.

## 2026-10-11 — `4b29b4e` cập nhật JDL cho website spa

- Viết lại `jhipster-jdl.jdl` cho nghiệp vụ spa (thông điệp commit gốc bị nhập nhầm).

## 2026-10-11 — `599b9f4` init · `0beeef8` khởi tạo dự án JHipster 8.11.0

- Ứng dụng monolith Spring Boot + Angular, JWT, MySQL, song ngữ en/vi.
