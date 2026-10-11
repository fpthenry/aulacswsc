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

| Việc                            | Lệnh                                                 | Ghi chú                                    |
| ------------------------------- | ---------------------------------------------------- | ------------------------------------------ |
| Cài thư viện frontend           | `npm install`                                        | Lần đầu hoặc khi `package.json` đổi        |
| Chạy backend (dev)              | `./mvnw`                                             | http://localhost:8080, tự chạy Liquibase   |
| Chạy frontend (dev, hot reload) | `npm start`                                          | http://localhost:9000, proxy API sang 8080 |
| Test backend                    | `./mvnw verify`                                      |                                            |
| Test frontend                   | `npm test`                                           |                                            |
| Kiểm tra định dạng              | `npm run prettier:check` / `npm run prettier:format` |                                            |
| Lint frontend                   | `npm run lint`                                       |                                            |
| Build production                | `./mvnw -Pprod clean verify`                         | Ra file `target/*.jar`                     |
| Commit                          | `npm run commit -- "feat: mô tả"`                    | Xem mục 6                                  |

**Cấu hình dev** ([`application-dev.yml`](../src/main/resources/config/application-dev.yml)):
MySQL `localhost:3306`, database `aulacswsc` (tự tạo), user `root`, mật khẩu điền vào `spring.datasource.password`.
Mật khẩu thật của từng máy **không commit** — dùng biến môi trường `SPRING_DATASOURCE_PASSWORD` thay vì sửa file.

**Lỗi hay gặp**

| Lỗi                                           | Nguyên nhân           | Cách xử lý                                                                                  |
| --------------------------------------------- | --------------------- | ------------------------------------------------------------------------------------------- |
| `Port 8080 was already in use`                | App cũ còn chạy       | Ctrl+C terminal cũ hoặc `taskkill /PID <pid> /F` (tìm pid: `netstat -ano \| findstr :8080`) |
| `Access denied for user 'root'`               | Sai mật khẩu MySQL    | Sửa `password` / biến môi trường                                                            |
| `Validation Failed: ... checksum` (Liquibase) | Sửa changeset đã chạy | Hoàn tác changeset cũ, viết changeset mới (dev: có thể xóa DB)                              |
| 401 sau khi đổi role                          | Role nằm trong JWT cũ | Đăng xuất, đăng nhập lại                                                                    |

## 3. Cấu trúc thư mục

```
aulacswsc/
├── jhipster-jdl.jdl            ★ Thiết kế entity — nguồn sự thật của data model
├── .yo-rc.json                 Cấu hình JHipster (không sửa tay)
├── .jhipster/*.json            Mô tả entity do JHipster sinh (không sửa tay, sửa JDL)
├── docs/                       Tài liệu dự án (bạn đang đọc)
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
    │   └── web/rest/           REST controller (+ errors/, vm/)
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
    │   │   ├── entities/<x>/   CRUD từng entity: list/ detail/ update/ delete/ service/ route/
    │   │   ├── home/, login/
    │   │   └── app.routes.ts   Route gốc
    │   ├── i18n/{en,vi}/*.json Chuỗi giao diện
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

| Quy tắc                                                                                                         |                                                                                      |
| --------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| Gọi API qua `XService` trong `entities/x/service/`, không dùng `HttpClient` trong component                     |                                                                                      |
| Mọi chữ hiển thị dùng `jhiTranslate` / `translate`; thêm key vào **cả** `i18n/en` và `i18n/vi`                  |                                                                                      |
| Route cần quyền: `data: { authorities: [...] }` + `canActivate: [UserRouteAccessService]`                       |                                                                                      |
| Ẩn nút/menu theo quyền: `*jhiHasAnyAuthority="['ROLE_ADMIN']"`                                                  | Chỉ là ẩn giao diện — backend vẫn phải chặn                                          |
| Ngày giờ dùng `dayjs`                                                                                           |                                                                                      |
| Trang công khai cho khách (trang chủ spa, danh sách dịch vụ, đặt lịch) đặt ngoài `entities/` (vd `app/public/`) | `entities/` là màn hình quản trị do JHipster sinh, có thể bị ghi đè khi chạy lại JDL |

### 4.4 Sửa file do JHipster sinh

Chạy lại `jhipster jdl` có thể ghi đè. Danh sách file **đã sửa tay** — kiểm tra kỹ khi JHipster hỏi ghi đè:

| File                                              | Phần đã sửa                  |
| ------------------------------------------------- | ---------------------------- |
| `config/SecurityConfiguration.java`               | Luật phân quyền spa          |
| `security/AuthoritiesConstants.java`              | `STAFF`, `CUSTOMER`          |
| `service/UserService.java`                        | Đăng ký → `ROLE_CUSTOMER`    |
| `repository/CustomerRepository.java`              | `findOneByUserLogin`         |
| `web/rest/BookingResource.java`                   | Lọc dữ liệu theo khách       |
| `web/rest/ReviewResource.java`                    | Duyệt đánh giá, tự gán khách |
| `webapp/app/entities/entity.routes.ts`            | `authorities` từng route     |
| `webapp/app/layouts/navbar/navbar.component.html` | `*jhiHasAnyAuthority` menu   |

Khi thêm sửa tay vào file sinh sẵn, **bổ sung vào bảng này**.

## 5. Test

- Backend: `src/test/java/.../web/rest/XResourceIT.java` (test tích hợp, dùng Testcontainers MySQL → cần Docker), `XTest`, `XDTOTest`, `XMapperTest`.
- Frontend: `*.spec.ts` cạnh component, chạy bằng Jest (`npm test`).
- Test mặc định giả lập user `ROLE_USER`; sau khi siết quyền, các `*ResourceIT` cần `@WithMockUser(authorities = AuthoritiesConstants.ADMIN)` cho thao tác quản trị.
- Sửa logic phân quyền → thêm test cho cả trường hợp **được phép** và **bị chặn**.

## 6. Git & commit

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
