# 05. Triển khai lên server (deploy) và vận hành

Hướng dẫn đưa Âu Lạc Spa lên một máy chủ Linux (VPS) có tên miền riêng, chạy HTTPS, tự khởi động lại, có sao lưu.

## 1. Ứng dụng gồm những gì khi chạy thật

```
Khách / Quản trị ──HTTPS──▶ Nginx (cổng 443, chứng chỉ Let's Encrypt)
                              │  proxy
                              ▼
                     aulacswsc.jar  (Spring Boot, cổng 8080, chỉ nghe nội bộ)
                      │  ├─ website khách /vi /en + trang quản trị (Angular đã đóng gói sẵn trong jar)
                      │  └─ API /api/**
                      ├──▶ MySQL 8 (cơ sở dữ liệu aulacswsc, chỉ nghe localhost)
                      └──▶ /opt/aulacswsc/uploads  (ảnh, nhạc admin tải lên — PHẢI sao lưu)
```

- **Chỉ một file `.jar`** chứa cả backend lẫn frontend — không cần cài Node.js trên server.
- Cấu trúc bảng tự tạo/cập nhật bằng **Liquibase** mỗi lần ứng dụng khởi động.
- Profile `prod`: không nạp dữ liệu giả (faker), nén gzip, cache tệp tĩnh.

## 2. Chuẩn bị

| Hạng mục | Khuyến nghị                                                                                     |
| -------- | ----------------------------------------------------------------------------------------------- |
| VPS      | Ubuntu 22.04 / 24.04, **2 GB RAM** (1 GB vẫn chạy được nếu thêm swap), 20 GB ổ đĩa              |
| Tên miền | Bản ghi **A** của `aulacspa.vn` và `www.aulacspa.vn` trỏ về IP VPS (đổi thành tên miền của bạn) |
| Máy bạn  | Đã chạy được dự án (Java 17+, Node), có `ssh` / `scp` (Windows 10+ có sẵn trong PowerShell)     |

> Trong tài liệu, thay `aulacspa.vn` bằng tên miền thật và `1.2.3.4` bằng IP VPS.

## 3. Bước 1 — Đóng gói ứng dụng (trên máy bạn)

```bash
./mvnw -Pprod clean verify -DskipTests
```

Kết quả: `target/aulacswsc-0.0.1-SNAPSHOT.jar` (~100 MB). Lệnh này tự build Angular ở chế độ production và gói vào jar.

> Nếu gặp lỗi frontend không được build lại: xóa `target/checksums.csv` rồi chạy lại.

## 4. Bước 2 — Cài đặt server (chỉ làm lần đầu)

Đăng nhập: `ssh root@1.2.3.4`

```bash
# phần mềm cần thiết
apt update && apt -y upgrade
apt -y install openjdk-21-jre-headless mysql-server nginx certbot python3-certbot-nginx ufw
timedatectl set-timezone Asia/Ho_Chi_Minh

# tường lửa: chỉ mở SSH, HTTP, HTTPS (MySQL và cổng 8080 không mở ra ngoài)
ufw allow OpenSSH && ufw allow 'Nginx Full' && ufw --force enable

# người dùng riêng để chạy ứng dụng + thư mục
useradd --system --home /opt/aulacswsc --shell /usr/sbin/nologin aulac
mkdir -p /opt/aulacswsc/{app,uploads,backup}
chown -R aulac:aulac /opt/aulacswsc

# VPS 1 GB RAM: thêm 2 GB swap
fallocate -l 2G /swapfile && chmod 600 /swapfile && mkswap /swapfile && swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
```

### 4.1 Cơ sở dữ liệu MySQL

```bash
mysql -u root <<'SQL'
CREATE DATABASE aulacswsc CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'aulac'@'localhost' IDENTIFIED BY 'DAT-MAT-KHAU-MANH-O-DAY';
GRANT ALL PRIVILEGES ON aulacswsc.* TO 'aulac'@'localhost';
FLUSH PRIVILEGES;
SQL
```

### 4.2 File cấu hình bí mật `/opt/aulacswsc/aulacswsc.env`

Tạo khóa JWT ngẫu nhiên: `openssl rand -base64 64 | tr -d '\n'` rồi dán vào dòng `JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET`.

```ini
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/aulacswsc?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=aulac
SPRING_DATASOURCE_PASSWORD=DAT-MAT-KHAU-MANH-O-DAY
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=DAN-KHOA-VUA-TAO-O-DAY
APPLICATION_UPLOAD_DIR=/opt/aulacswsc/uploads
JHIPSTER_MAIL_BASE_URL=https://aulacspa.vn
SERVER_ADDRESS=127.0.0.1
JAVA_TOOL_OPTIONS=-Xms256m -Xmx768m -Duser.timezone=Asia/Ho_Chi_Minh
```

```bash
chown aulac:aulac /opt/aulacswsc/aulacswsc.env && chmod 600 /opt/aulacswsc/aulacswsc.env
```

> **Bắt buộc** đổi khóa JWT: khóa mặc định trong `application-prod.yml` ai cũng đọc được trên GitHub → có thể giả mạo đăng nhập. `SERVER_ADDRESS=127.0.0.1` để cổng 8080 chỉ nhận kết nối từ Nginx.

### 4.3 Dịch vụ tự chạy `/etc/systemd/system/aulacswsc.service`

```ini
[Unit]
Description=Au Lac Spa (aulacswsc)
After=network.target mysql.service
Requires=mysql.service

[Service]
User=aulac
WorkingDirectory=/opt/aulacswsc
EnvironmentFile=/opt/aulacswsc/aulacswsc.env
ExecStart=/usr/bin/java -jar /opt/aulacswsc/app/aulacswsc.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
systemctl daemon-reload && systemctl enable aulacswsc
```

### 4.4 Nginx + HTTPS `/etc/nginx/sites-available/aulacswsc`

```nginx
server {
    listen 80;
    server_name aulacspa.vn www.aulacspa.vn;

    client_max_body_size 25m;          # ảnh/nhạc tải lên từ trang quản trị

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host              $host;
        proxy_set_header X-Real-IP         $remote_addr;
        proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }
}
```

```bash
ln -s /etc/nginx/sites-available/aulacswsc /etc/nginx/sites-enabled/
rm -f /etc/nginx/sites-enabled/default
nginx -t && systemctl reload nginx
certbot --nginx -d aulacspa.vn -d www.aulacspa.vn --redirect -m email-cua-ban@gmail.com --agree-tos
```

Certbot tự thêm cấu hình HTTPS, chuyển hướng http → https và **tự gia hạn** chứng chỉ.

## 5. Bước 3 — Đưa jar lên và chạy

Trên máy bạn (PowerShell hoặc Git Bash, tại thư mục dự án):

```bash
scp target/aulacswsc-0.0.1-SNAPSHOT.jar root@1.2.3.4:/opt/aulacswsc/app/aulacswsc.jar
```

Trên server:

```bash
chown aulac:aulac /opt/aulacswsc/app/aulacswsc.jar
systemctl start aulacswsc
journalctl -u aulacswsc -f        # xem log; chờ dòng "Application 'aulacswsc' is running!" (~30–60 giây)
```

Mở `https://aulacspa.vn/vi` để kiểm tra website, `https://aulacspa.vn/login` để vào quản trị.

## 6. Việc phải làm ngay sau lần chạy đầu

1. Đăng nhập `admin` / `admin` → **Đổi mật khẩu** ngay (menu tài khoản góc phải). Vào **Hệ thống → Tài khoản đăng nhập**: khóa (bỏ _Activated_) hoặc đổi mật khẩu tài khoản `user`.
2. Tạo tài khoản cho nhân viên lễ tân với quyền `ROLE_STAFF`.
3. Nhập nội dung thật theo [04 Hướng dẫn quản trị](04-huong-dan-quan-tri-noi-dung.md): **Cài đặt website** → **Cơ sở** → **Danh mục** → **Dịch vụ** → ảnh, bài viết.
4. **Không chạy `scripts/seed-demo.mjs` trên server thật** — ảnh demo trong `images/` là ảnh có bản quyền, chỉ dùng thử ở máy cá nhân. Dùng ảnh do spa cung cấp.
5. Kiểm tra `https://aulacspa.vn/sitemap.xml` hiện đúng tên miền `https://`, rồi khai báo sitemap trong Google Search Console.

## 7. Cập nhật phiên bản mới

```bash
# máy bạn
git pull                                        # lấy code mới nhất
./mvnw -Pprod clean verify -DskipTests
scp target/aulacswsc-0.0.1-SNAPSHOT.jar root@1.2.3.4:/opt/aulacswsc/app/aulacswsc.jar.new

# server
/opt/aulacswsc/backup.sh                        # sao lưu trước khi cập nhật (mục 8)
cd /opt/aulacswsc/app && mv aulacswsc.jar aulacswsc.jar.old && mv aulacswsc.jar.new aulacswsc.jar
chown aulac:aulac aulacswsc.jar && systemctl restart aulacswsc && journalctl -u aulacswsc -f
```

- Bảng/cột mới (changeset Liquibase) được áp dụng tự động khi khởi động.
- Lỗi thì quay lại bản cũ: `mv aulacswsc.jar.old aulacswsc.jar && systemctl restart aulacswsc` (nếu bản mới đã đổi cấu trúc DB thì khôi phục DB từ bản sao lưu).
- Thời gian gián đoạn ~30–60 giây; nên cập nhật ngoài giờ mở cửa.

## 8. Sao lưu tự động (quan trọng)

Cần sao lưu **cả 2**: cơ sở dữ liệu và thư mục `uploads` (ảnh không nằm trong DB).

`/opt/aulacswsc/backup.sh`:

```bash
#!/bin/sh
set -e
D=$(date +%F_%H%M)
B=/opt/aulacswsc/backup
mysqldump --single-transaction -u root aulacswsc | gzip > $B/db_$D.sql.gz
tar -czf $B/uploads_$D.tar.gz -C /opt/aulacswsc uploads
find $B -type f -mtime +14 -delete      # giữ 14 ngày
```

```bash
chmod +x /opt/aulacswsc/backup.sh
(crontab -l 2>/dev/null; echo "30 2 * * * /opt/aulacswsc/backup.sh") | crontab -   # 2:30 sáng mỗi ngày
```

Nên định kỳ tải bản sao lưu về máy khác (hoặc Google Drive): `scp root@1.2.3.4:/opt/aulacswsc/backup/* D:\backup-aulac\`.

Khôi phục: `gunzip < db_xxx.sql.gz | mysql -u root aulacswsc` và `tar -xzf uploads_xxx.tar.gz -C /opt/aulacswsc`.

## 9. Lệnh vận hành thường dùng

| Việc                   | Lệnh                                                       |
| ---------------------- | ---------------------------------------------------------- |
| Xem trạng thái         | `systemctl status aulacswsc`                               |
| Xem log trực tiếp      | `journalctl -u aulacswsc -f`                               |
| Log 1 giờ gần nhất     | `journalctl -u aulacswsc --since "1 hour ago"`             |
| Khởi động lại / dừng   | `systemctl restart aulacswsc` / `systemctl stop aulacswsc` |
| Kiểm tra sức khỏe      | `curl -s http://127.0.0.1:8080/management/health`          |
| Dung lượng ổ đĩa / RAM | `df -h` / `free -m`                                        |
| Gia hạn HTTPS (thử)    | `certbot renew --dry-run`                                  |

## 10. Xử lý sự cố

| Hiện tượng                                      | Nguyên nhân / cách xử lý                                                                                         |
| ----------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| `502 Bad Gateway`                               | Ứng dụng chưa chạy xong hoặc đã dừng → `journalctl -u aulacswsc -n 200`                                          |
| Log báo `Access denied for user`                | Sai `SPRING_DATASOURCE_USERNAME/PASSWORD` trong file `.env`                                                      |
| Ứng dụng tự tắt, log có `OutOfMemory` / bị kill | Thiếu RAM → thêm swap, giảm `-Xmx`, hoặc nâng VPS                                                                |
| Tải ảnh lên báo lỗi `413`                       | Thiếu `client_max_body_size 25m;` trong Nginx                                                                    |
| Tải ảnh lên lỗi quyền ghi                       | `chown -R aulac:aulac /opt/aulacswsc/uploads`                                                                    |
| Ảnh cũ mất sau khi cập nhật                     | `APPLICATION_UPLOAD_DIR` không trỏ tới `/opt/aulacswsc/uploads` (đang ghi vào thư mục tương đối)                 |
| Sitemap / link chia sẻ ra `http://localhost`    | Nginx thiếu `proxy_set_header Host / X-Forwarded-Proto` (profile prod đã bật `forward-headers-strategy: native`) |
| Giờ đặt lịch lệch 7 tiếng                       | Đặt múi giờ server và `-Duser.timezone=Asia/Ho_Chi_Minh` như mục 4                                               |

## 11. Cách khác: chạy bằng Docker

Dự án có sẵn cấu hình Docker (JHipster). Hợp khi server đã quen dùng Docker:

```bash
npm run java:docker                        # tạo image aulacswsc:latest (cần Docker Desktop trên máy bạn)
docker save aulacswsc:latest | gzip > aulacswsc.tar.gz
scp aulacswsc.tar.gz root@1.2.3.4:/opt/   # rồi trên server: docker load < /opt/aulacswsc.tar.gz
```

Sau đó dùng `src/main/docker/app.yml` làm mẫu, **nhưng phải sửa trước khi chạy thật**: đặt mật khẩu MySQL (file mẫu cho phép mật khẩu rỗng), thêm biến JWT secret, gắn volume cho `/var/lib/mysql` và thư mục uploads (`APPLICATION_UPLOAD_DIR`), bỏ profile `api-docs`. Nginx + Certbot đặt phía trước giống mục 4.4. Với một spa, cách chạy jar + systemd ở trên đơn giản và tốn ít RAM hơn.

## 12. Danh sách kiểm tra khi bàn giao

- [ ] HTTPS hoạt động, `http://` tự chuyển sang `https://`
- [ ] Đã đổi mật khẩu `admin`, khóa tài khoản `user`, có tài khoản nhân viên
- [ ] Khóa JWT riêng, mật khẩu MySQL mạnh, file `.env` quyền 600
- [ ] Tường lửa chỉ mở 22 / 80 / 443
- [ ] Sao lưu chạy hằng ngày, đã thử khôi phục một lần
- [ ] Nội dung, ảnh là của spa (không còn dữ liệu / ảnh demo)
- [ ] Đặt thử 1 lịch trên website → hiện ở **Bảng lịch hẹn**
- [ ] Đã gửi sitemap lên Google Search Console
