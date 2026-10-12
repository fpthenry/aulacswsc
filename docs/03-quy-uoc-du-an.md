# 03. Quy ước & tổ chức dự án aulacswsc

## 1. Công nghệ

| Phần             | Công nghệ                                                                  | Phiên bản                    |
| ---------------- | -------------------------------------------------------------------------- | ---------------------------- |
| Generator        | JHipster                                                                   | 8.11.0                       |
| Backend          | Spring Boot, Spring Security (JWT), Spring Data JPA / Hibernate, MapStruct | Spring Boot 3.4.x            |
| Ngôn ngữ backend | Java                                                                       | ≥ 17 (máy dev đang dùng 21)  |
| Database         | MySQL (dev & prod), Liquibase quản lý schema                               | —                            |
| Frontend         | Angular (standalone components), Bootstrap, ng-bootstrap, ngx-translate    | Angular 19.2, TypeScript 5.8 |
| Build            | Maven (`./mvnw`), npm (`./npmw` hoặc `npm`)                                | Node ≥ 22                    |
| Đa ngôn ngữ      | `en` (mặc định), `vi`                                                      | —                            |

## 2. Chạy dự án

| Việc                            | Lệnh                                                                            | Ghi chú                                                                                                               |
| ------------------------------- | ------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| Cài thư viện frontend           | `npm install`                                                                   | Lần đầu hoặc khi `package.json` đổi                                                                                   |
| Chạy backend (dev)              | `./mvnw`                                                                        | http://localhost:8080 — website `/vi`, `/en`; quản trị `/login` → `/dashboard`; tự chạy Liquibase                     |
| Chạy frontend (dev, hot reload) | `npm start`                                                                     | http://localhost:9000, proxy API sang 8080                                                                            |
| Test backend                    | `./mvnw verify`                                                                 |                                                                                                                       |
| Test frontend                   | `npm test`                                                                      |                                                                                                                       |
| Kiểm tra định dạng              | `npm run prettier:check` / `npm run prettier:format`                            |                                                                                                                       |
| Lint frontend                   | `npm run lint`                                                                  |                                                                                                                       |
| Build production                | `./mvnw -Pprod clean verify`                                                    | Ra file `target/*.jar`                                                                                                |
| Commit                          | `npm run commit -- "feat: mô tả"`                                               | Xem mục 6                                                                                                             |
| Nạp dữ liệu demo (dev)          | `npm run seed:demo` (`-- --base http://localhost:8080 --images images/anh_spa`) | Upload ảnh trong `images/anh_spa` + tạo dịch vụ, ảnh, đánh giá, bài viết, lịch hẹn giả. Ghi `uploads/.demo-seed.json` |
| Xóa dữ liệu demo                | `npm run seed:demo -- --clean`                                                  | Xóa mọi bản ghi demo, gỡ ảnh khỏi dữ liệu có sẵn. **Chạy trước khi đưa lên production**                               |

**Cấu hình dev** ([`application-dev.yml`](../src/main/resources/config/application-dev.yml)):
MySQL `localhost:3306`, database `aulacswsc` (tự tạo), user `root`, mật khẩu điền vào `spring.datasource.password`.
Mật khẩu thật của từng máy **không commit** — dùng biến môi trường `SPRING_DATASOURCE_PASSWORD` thay vì sửa file.

**Địa chỉ**

| Trang              | URL                                                                                   |
| ------------------ | ------------------------------------------------------------------------------------- |
| Website khách      | `/vi`, `/en` (`/` tự chuyển)                                                          |
| Đăng nhập quản trị | `/login` (link "Quản trị" ở chân website)                                             |
| Trang chủ quản trị | `/dashboard` → menu _Entities_ để sửa nội dung, _Administration_ để quản lý tài khoản |
| Sitemap / robots   | `/sitemap.xml`, `/robots.txt` (sinh theo nội dung thật)                               |

**Lỗi hay gặp**

| Lỗi                                                      | Nguyên nhân                                                                                                                                                                                               | Cách xử lý                                                                                                                                                                              |
| -------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Port 8080 was already in use`                           | App cũ còn chạy                                                                                                                                                                                           | Ctrl+C terminal cũ hoặc `taskkill /PID <pid> /F` (tìm pid: `netstat -ano \| findstr :8080`)                                                                                             |
| `Access denied for user 'root'`                          | Sai mật khẩu MySQL                                                                                                                                                                                        | Sửa `password` / biến môi trường                                                                                                                                                        |
| `Validation Failed: ... checksum` (Liquibase)            | Sửa changeset đã chạy                                                                                                                                                                                     | Hoàn tác changeset cũ, viết changeset mới (dev: có thể xóa DB)                                                                                                                          |
| 401 sau khi đổi role                                     | Role nằm trong JWT cũ                                                                                                                                                                                     | Đăng xuất, đăng nhập lại                                                                                                                                                                |
| Liquibase báo checksum sau khi chạy lại `jhipster jdl`   | JHipster viết lại changeset của entity cũ                                                                                                                                                                 | Dev: xóa database `aulacswsc` (`DROP DATABASE aulacswsc;`) để tạo lại kèm dữ liệu mẫu. Production: viết changeset `ALTER` mới                                                           |
| Trang trắng / "An error has occurred" (backend vẫn chạy) | Frontend chưa được build vào `target/classes/static`. Hay gặp khi log Maven ghi `webapp build dev ... Skipping execution` (checksum trong `target/checksums.csv` báo "không đổi" dù thư mục build đã mất) | `npm run webapp:build` (không cần tắt app, tải lại trang là được) hoặc xóa `target/checksums.csv` rồi chạy lại `./mvnw`. Khi code giao diện: chạy `npm start` song song và mở cổng 9000 |
| Ảnh upload không hiện sau khi deploy                     | Thư mục `uploads/` nằm trong thư mục build bị xóa                                                                                                                                                         | Đặt `application.upload-dir` (biến môi trường `APPLICATION_UPLOAD_DIR`) ra ổ đĩa cố định, sao lưu thư mục này                                                                           |

## 3. Cấu trúc thư mục

```
aulacswsc/
├── jhipster-jdl.jdl            ★ Thiết kế entity — nguồn sự thật của data model
├── .yo-rc.json                 Cấu hình JHipster (không sửa tay)
├── .jhipster/*.json            Mô tả entity do JHipster sinh (không sửa tay, sửa JDL)
├── docs/                       Tài liệu dự án (bạn đang đọc) — 04 là hướng dẫn quản trị nội dung
├── uploads/                    Ảnh admin upload (không commit, cần sao lưu)
├── scripts/commit.mjs          Lệnh commit theo quy ước
├── pom.xml / package.json      Phụ thuộc backend / frontend
└── src/
    ├── main/java/com/mycompany/myapp/
    │   ├── config/             Cấu hình Spring: Security, DB, Jackson, Liquibase, CORS...
    │   ├── domain/             Entity JPA (+ enumeration/)
    │   ├── repository/         Spring Data JPA
    │   ├── security/           JWT, AuthoritiesConstants, SecurityUtils
    │   ├── service/            Interface service, QueryService
    │   │   ├── impl/           Cài đặt service
    │   │   ├── dto/            DTO trao đổi với client
    │   │   ├── mapper/         MapStruct DTO ⇄ Entity
    │   │   └── criteria/       Tham số lọc
    │   │   └── dto/site/       DTO của API công khai (đặt lịch, khung giờ, liên hệ...)
    │   ├── service/Public*Service, RateLimiterService   Nghiệp vụ website khách
    │   └── web/rest/           REST controller (+ errors/, vm/)
    │                           Public*Resource = /api/public/**, UploadResource, SeoResource
    ├── main/resources/
    │   ├── config/application*.yml       Cấu hình theo profile (dev, prod, tls...)
    │   ├── config/liquibase/             master.xml, changelog/, data/ (CSV seed)
    │   └── i18n/messages*.properties     Chuỗi email, thông báo server
    ├── main/webapp/
    │   ├── app/
    │   │   ├── core/           Auth, interceptor, cấu hình chung
    │   │   ├── shared/         Directive, pipe, component dùng chung (jhiHasAnyAuthority, jhiTranslate...)
    │   │   ├── layouts/        Navbar, footer, trang lỗi
    │   │   ├── account/        Đăng ký, kích hoạt, đổi/quên mật khẩu, settings
    │   │   ├── admin/          Quản lý user, metrics, logs, health (ROLE_ADMIN)
    │   │   ├── entities/<x>/   QUẢN TRỊ: CRUD từng entity (JHipster sinh): list/ detail/ update/ delete/ service/ route/
    │   │   ├── site/           WEBSITE KHÁCH (/vi, /en) — tự viết, JHipster không đụng tới
    │   │   │   ├── core/       state, API, nháp đặt lịch, SEO, hàm tiện ích, kiểu dữ liệu
    │   │   │   ├── layout/     header, footer, nút liên hệ nổi + site.scss (design system)
    │   │   │   ├── shared/     icon, ảnh lazy, thẻ dịch vụ, slider đánh giá, hiệu ứng reveal
    │   │   │   ├── pages/      home, services, service-detail, branches, reviews, gallery, blog, blog-detail, booking, booking-lookup, policy, not-found
    │   │   │   └── site.routes.ts
    │   │   ├── shared/image-upload/  ô chọn ảnh (nén WebP) dùng trong form quản trị
    │   │   ├── home/           trang /dashboard của quản trị
    │   │   ├── login/
    │   │   └── app.routes.ts   Route gốc
    │   ├── i18n/{en,vi}/*.json Chuỗi giao diện (website khách: site.json)
    │   └── content/            Ảnh, SCSS
    └── test/                   Test backend (java/) và cấu hình test
```

Chi tiết các tầng và luồng gọi: [02-luong-xu-ly-crud.md](02-luong-xu-ly-crud.md). Bảng & quyền: [01-database-va-phan-quyen.md](01-database-va-phan-quyen.md).

## 4. Quy tắc code

### 4.1 Chung

- **Data model chỉ đổi qua JDL** rồi `npx jhipster jdl jhipster-jdl.jdl`. Không thêm cột bằng cách sửa entity tay.
- **Schema DB chỉ đổi qua Liquibase**. Không sửa changeset đã chạy; tạo changeset mới, include trong `master.xml`. Không sửa `liquibase/data/*.csv` của changeset ban đầu.
- **Định dạng do Prettier quyết định** (`.prettierrc`: 140 ký tự/dòng, 2 space; Java 4 space). Husky tự format file khi commit.
- Comment nghiệp vụ viết **tiếng Việt**, tên biến/hàm/class **tiếng Anh**.
- Không commit: mật khẩu, khóa JWT production, file trong `target/`, `node_modules/`.

### 4.2 Backend

| Quy tắc                                                                                                                             | Lý do                                 |
| ----------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------- |
| Controller chỉ nhận/trả `XDTO`, **không trả Entity**                                                                                | Tránh lộ dữ liệu, tránh vòng lặp JSON |
| Ghi dữ liệu đi qua `XService`, không gọi `XRepository.save` trong Resource                                                          | Giữ transaction và nghiệp vụ một chỗ  |
| Logic nghiệp vụ trong `XServiceImpl` (`@Transactional`)                                                                             |                                       |
| Lỗi nghiệp vụ ném `BadRequestAlertException(message, ENTITY_NAME, "errorKey")` và thêm bản dịch `error.<errorKey>` ở `i18n/{en,vi}` | Client hiển thị đúng ngôn ngữ         |
| Tên role dùng hằng `AuthoritiesConstants`, không viết chuỗi `"ROLE_..."` rải rác                                                    |                                       |
| Thông tin người đăng nhập lấy qua `SecurityUtils` (`getCurrentUserLogin()`, `hasCurrentUserThisAuthority()`...)                     |                                       |
| Luật URL mới thêm vào `SecurityConfiguration` **trước** dòng `/api/**`                                                              | Spring dùng luật khớp đầu tiên        |
| Log dùng `LOG.debug("... {}", value)`, không `System.out`                                                                           |                                       |
| Tiền tệ dùng `BigDecimal`, thời điểm dùng `Instant` (UTC), ngày sinh `LocalDate`                                                    |                                       |

Đặt tên (entity `X`): `XResource`, `XService`, `XServiceImpl`, `XQueryService`, `XRepository`, `XDTO`, `XMapper`, `XCriteria`. URL REST: số nhiều, kebab-case (`/api/spa-services`). Bảng: snake_case (`spa_service`).

### 4.3 Frontend

| Quy tắc                                                                                                                                                  |                                                                                      |
| -------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| Gọi API qua `XService` trong `entities/x/service/`, không dùng `HttpClient` trong component                                                              |                                                                                      |
| Mọi chữ hiển thị dùng `jhiTranslate` / `translate`; thêm key vào **cả** `i18n/en` và `i18n/vi`                                                           |                                                                                      |
| Route cần quyền: `data: { authorities: [...] }` + `canActivate: [UserRouteAccessService]`                                                                |                                                                                      |
| Ẩn nút/menu theo quyền: `*jhiHasAnyAuthority="['ROLE_ADMIN']"`                                                                                           | Chỉ là ẩn giao diện — backend vẫn phải chặn                                          |
| Ngày giờ dùng `dayjs`                                                                                                                                    |                                                                                      |
| Trang công khai cho khách đặt trong `app/site/` (không đặt trong `entities/`)                                                                            | `entities/` là màn hình quản trị do JHipster sinh, có thể bị ghi đè khi chạy lại JDL |
| Website khách: component tiền tố `site-`, dùng `signal`/`computed`, chữ giao diện trong `i18n/*/site.json`, dữ liệu song ngữ qua `state.t(obj, 'field')` |                                                                                      |
| Website khách chỉ gọi `/api/public/**` qua `SiteApiService`                                                                                              | API quản trị có thể trả bản nháp / dữ liệu ẩn                                        |
| Màu, chữ, nút, form của website dùng biến CSS `--site-*` và class trong `site/layout/site.scss`; không thêm màu cứng                                     | Giữ đồng bộ + chế độ tối                                                             |
| Hiệu ứng chỉ dùng `transform`/`opacity`, xuất hiện bằng `siteReveal` (IntersectionObserver); luôn có nhánh `prefers-reduced-motion`                      | 60fps, khả năng truy cập                                                             |
| Ảnh hiển thị bằng `<site-image>` (lazy, khung tỷ lệ cố định, nền mờ)                                                                                     | Tránh nhảy bố cục (CLS)                                                              |
| Mọi ô form có `<label>`, lỗi có `role="alert"` + `aria-describedby`; nút chỉ có icon phải có `aria-label`                                                | WCAG AA                                                                              |

### 4.4 Sửa file do JHipster sinh

Chạy lại `jhipster jdl` có thể ghi đè. Danh sách file **đã sửa tay** — kiểm tra kỹ khi JHipster hỏi ghi đè:

| File                                                                                                                                                                                                                       | Phần đã sửa                                                                                                                                     |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `config/SecurityConfiguration.java`                                                                                                                                                                                        | Luật phân quyền spa                                                                                                                             |
| `security/AuthoritiesConstants.java`                                                                                                                                                                                       | `STAFF`, `CUSTOMER`                                                                                                                             |
| `service/UserService.java`                                                                                                                                                                                                 | Đăng ký → `ROLE_CUSTOMER`                                                                                                                       |
| `repository/CustomerRepository.java`                                                                                                                                                                                       | `findOneByUserLogin`                                                                                                                            |
| `web/rest/BookingResource.java`                                                                                                                                                                                            | Lọc dữ liệu theo khách                                                                                                                          |
| `web/rest/ReviewResource.java`                                                                                                                                                                                             | Duyệt đánh giá, tự gán khách                                                                                                                    |
| `webapp/app/entities/entity.routes.ts`                                                                                                                                                                                     | `authorities` từng route                                                                                                                        |
| `webapp/app/layouts/navbar/navbar.component.html`                                                                                                                                                                          | `*jhiHasAnyAuthority` menu, link "/dashboard", nút "Xem website"                                                                                |
| `config/ApplicationProperties.java`, `application.yml`                                                                                                                                                                     | `upload-dir`, giới hạn multipart, CSP cho Google Maps/YouTube/TikTok/Google Fonts                                                               |
| `application-dev.yml`                                                                                                                                                                                                      | Tắt context `faker`                                                                                                                             |
| `repository/*Repository.java` (Branch, ServiceCategory, SpaService, GalleryImage, BlogPost, Review, Booking, BookingItem, SiteSetting, Customer)                                                                           | Truy vấn cho website công khai                                                                                                                  |
| `webapp/app/entities/{blog-post,branch,gallery-image,service-category,site-setting,spa-service,staff}/update/*-update.component.{html,ts}`                                                                                 | Ô nhập URL ảnh → `<jhi-image-upload>`                                                                                                           |
| `webapp/app/app.routes.ts`, `app.config.ts`, `app-page-title-strategy.ts`, `layouts/main/*`, `login/login.component.ts`                                                                                                    | Route `/:lang` cho website, `/dashboard`, View Transitions, ẩn khung quản trị trên website                                                      |
| `webapp/index.html`                                                                                                                                                                                                        | Font, meta, màu nền, màn hình chờ                                                                                                               |
| `angular.json`, `webapp/robots.txt` (đã xóa)                                                                                                                                                                               | robots.txt do `SeoResource` sinh                                                                                                                |
| `eslint.config.mjs`                                                                                                                                                                                                        | Cho phép tiền tố `site`, tắt `member-ordering` trong `app/site`                                                                                 |
| `domain/SiteSetting.java`, `SiteSettingDTO.java`, `.jhipster/SiteSetting.json`, `entities/site-setting/**`                                                                                                                 | Trường `heroSlides` (changeset `20261012090000_added_site_setting_hero_slides.xml`) + ô `jhi-image-list-upload`                                 |
| `service/impl/{BlogPost,SpaService,ServiceCategory,Branch}ServiceImpl.java`, `*DTO.java` (bỏ `@NotNull` slug), `*Repository.existsBySlug`, `entities/{blog-post,branch,service-category,spa-service}/update/*` (bỏ ô Slug) | Slug do `SlugService` sinh khi tạo (tên không dấu + ngày), giữ nguyên khi sửa. Chạy lại `jhipster jdl` sẽ trả lại ô Slug bắt buộc → phải bỏ lại |

Khi thêm sửa tay vào file sinh sẵn, **bổ sung vào bảng này**.

## 5. Test

- Backend: `src/test/java/.../web/rest/XResourceIT.java` (test tích hợp, dùng Testcontainers MySQL → cần Docker), `XTest`, `XDTOTest`, `XMapperTest`.
- Frontend: `*.spec.ts` cạnh component, chạy bằng Jest (`npm test`).
- Test mặc định giả lập user `ROLE_USER`; sau khi siết quyền, các `*ResourceIT` cần `@WithMockUser(authorities = AuthoritiesConstants.ADMIN)` cho thao tác quản trị.
- Sửa logic phân quyền → thêm test cho cả trường hợp **được phép** và **bị chặn**.

## 6. Git & commit

### 6.0 Nhật ký thay đổi

Mỗi lần thêm / sửa chức năng: **cập nhật [CHANGELOG.md](CHANGELOG.md)** (mục _[Chưa commit]_), commit dùng phần _Nội dung commit_ trong đó, sau commit đổi tiêu đề mục thành ngày + mã commit.

### 6.1 Lệnh commit

```bash
npm run commit -- "feat(booking): thêm trang đặt lịch cho khách"
npm run commit -- "fix: sửa lỗi phân trang dịch vụ" --push   # commit rồi push
```

Script [`scripts/commit.mjs`](../scripts/commit.mjs) sẽ:

1. Kiểm tra message đúng quy ước (sai → dừng, không commit).
2. `git add -A` (mọi thay đổi, trừ file trong `.gitignore`).
3. In danh sách file sẽ commit.
4. `git commit` → husky `pre-commit` chạy `lint-staged` → Prettier tự định dạng file.
5. Có `--push` → `git push -u origin <nhánh hiện tại>` (repo cần có remote).

> Trước khi commit, xem lại `git status` — `git add -A` lấy **tất cả** file thay đổi, kể cả file bạn quên.

### 6.2 Quy ước message (Conventional Commits)

`<loại>(<phạm vi>): <mô tả ngắn, tiếng Việt hoặc Anh>`

| Loại       | Dùng khi                                  |
| ---------- | ----------------------------------------- |
| `feat`     | Thêm chức năng                            |
| `fix`      | Sửa lỗi                                   |
| `docs`     | Chỉ sửa tài liệu                          |
| `style`    | Định dạng, không đổi logic                |
| `refactor` | Sắp xếp lại code, không đổi hành vi       |
| `perf`     | Tối ưu hiệu năng                          |
| `test`     | Thêm/sửa test                             |
| `build`    | Maven, npm, cấu hình build                |
| `chore`    | Việc vặt khác (cập nhật JDL sinh code...) |
| `revert`   | Hoàn tác commit                           |

Phạm vi gợi ý: `booking`, `service`, `customer`, `staff`, `review`, `auth`, `security`, `db`, `ui`.

### 6.3 Nhánh

- `master`: nhánh chính, luôn build được.
- Chức năng mới: tạo nhánh `feature/<tên>` (vd `feature/dat-lich`), sửa lỗi: `fix/<tên>`; xong thì merge/PR về `master`.
- Mỗi commit một việc trọn vẹn, build được.
