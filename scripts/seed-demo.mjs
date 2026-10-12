#!/usr/bin/env node
/**
 * Nạp DỮ LIỆU DEMO (giả) vào website để xem thử mọi chức năng trên máy dev.
 *
 *   npm run seed:demo                                   # app ở http://localhost:8080, ảnh trong images/anh_spa
 *   npm run seed:demo -- --base http://localhost:8081 --images images/anh_spa --user admin --pass admin
 *   npm run seed:demo -- --force                        # chạy lại dù đã có dữ liệu demo
 *   npm run seed:demo -- --hero ANA-MANDARA             # chọn ảnh hero (một phần tên file trong thư mục ảnh)
 *   npm run seed:demo -- --clean                        # XÓA toàn bộ dữ liệu demo đã nạp (theo file manifest)
 *   npm run seed:demo -- --music-only                   # chỉ tạo/cập nhật nhạc nền demo
 *   npm run seed:demo -- --hero-only                    # chỉ cập nhật slideshow ảnh đầu trang
 *   npm run seed:demo -- --hero-only --hero-slides "088_,065_,014_,050_"   # chọn ảnh slideshow theo tên file
 *   npm run seed:demo -- --music path/to/nhac.mp3       # dùng file nhạc của bạn thay cho nhạc tự tổng hợp
 *
 * Script đăng nhập admin rồi gọi API quản trị như người dùng thật:
 *   upload ảnh (→ thư mục uploads/, không commit) · ảnh hero/cơ sở/danh mục/dịch vụ · thêm dịch vụ, cơ sở
 *   · thư viện ảnh · đánh giá · bài viết · khuyến mãi · nhân viên · khách hàng · lịch hẹn · tin nhắn liên hệ.
 *
 * ⚠ Ảnh trong images/ là ảnh tải từ website khác → CHỈ dùng xem thử trên máy, không đưa lên production / GitHub.
 *    Production: xóa dữ liệu demo và upload ảnh của chính spa trong trang quản trị.
 */
import { copyFileSync, existsSync, mkdirSync, readFileSync, readdirSync, writeFileSync } from 'node:fs';
import { basename, extname, join } from 'node:path';

// ===================== tham số =====================
const args = process.argv.slice(2);
const opt = (name, def) => {
  const i = args.indexOf(`--${name}`);
  return i >= 0 && args[i + 1] ? args[i + 1] : def;
};
const BASE = opt('base', 'http://localhost:8080').replace(/\/$/, '');
const IMAGE_DIR = opt('images', 'images/anh_spa');
const USER = opt('user', 'admin');
const PASS = opt('pass', 'admin');
const FORCE = args.includes('--force');
const CLEAN = args.includes('--clean');
const HERO = opt('hero', '');
const MUSIC_FILE = opt('music', '');
const MUSIC_ONLY = args.includes('--music-only');
const HERO_ONLY = args.includes('--hero-only');
/**
 * Ảnh slideshow đầu trang (một phần tên file, cách nhau dấu phẩy). Mặc định: các ảnh tông tối, không có chữ
 * đã chọn tay trong bộ ảnh demo; không tìm thấy thì tự chọn ảnh ngang lớn nhất có tên gợi ý không gian spa.
 */
const HERO_SLIDES = opt('hero-slides', '088_,065_,014_,050_')
  .split(',')
  .map(x => x.trim())
  .filter(Boolean);
/** Thư mục uploads của app (application.upload-dir mặc định, tính từ thư mục gốc dự án) */
const UPLOAD_DIR = opt('upload-dir', 'uploads');
/** Ghi lại mọi bản ghi demo đã tạo/sửa → dùng cho --clean */
const MANIFEST = 'uploads/.demo-seed.json';
/** Ảnh không hợp làm ảnh website: banner bài báo có chữ, menu in, voucher, đồng phục, clipart, khóa học... */
const SKIP =
  /voucher|menu|dong-phuc|pngtree|nganh-spa|hoc-nghe|mo-spa-can-mua|bao-gia|dong-thue|spa-la-gi|phan-biet|in-bac-viet|caption|crawler|media$|\.gif$/i;
/** Ảnh ưu tiên làm hero (không gian spa / resort, thường không có chữ) */
const HERO_HINT = /resort|treatment|spa-service|couple|plumeria|ana-mandara|lake-george|antarra|hotelspa|vincharm/i;

let token = '';
/** hậu tố theo lần chạy: số điện thoại khách / mã lịch demo không trùng khi chạy lại với --force */
const RUN = String(Date.now()).slice(-4);

// ===================== HTTP =====================
async function api(method, path, body, contentType = 'application/json') {
  const res = await fetch(`${BASE}/api/${path}`, {
    method,
    headers: { Authorization: `Bearer ${token}`, ...(body && !(body instanceof FormData) ? { 'Content-Type': contentType } : {}) },
    body: body instanceof FormData ? body : body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  if (!res.ok) {
    throw new Error(`${method} ${path} → ${res.status}: ${text.slice(0, 300)}`);
  }
  return text ? JSON.parse(text) : null;
}
const manifest = { created: [], patched: [] };
const get = path => api('GET', path);
/** POST + ghi id vào manifest để --clean xóa được */
const post = async (path, body) => {
  const res = await api('POST', path, body);
  if (res?.id) {
    manifest.created.push(`${path}/${res.id}`);
  }
  return res;
};
/** PATCH ảnh vào bản ghi có sẵn + ghi lại để --clean trả về null */
const patch = async (path, body) => {
  const res = await api('PATCH', path, body, 'application/merge-patch+json');
  manifest.patched.push({ path, fields: Object.keys(body).filter(k => k !== 'id') });
  return res;
};
const saveManifest = () => {
  const old = existsSync(MANIFEST) ? JSON.parse(readFileSync(MANIFEST, 'utf8')) : { created: [], patched: [] };
  writeFileSync(
    MANIFEST,
    JSON.stringify({ created: [...old.created, ...manifest.created], patched: [...old.patched, ...manifest.patched] }, null, 2),
  );
};

/** Xóa dữ liệu demo theo manifest (ngược thứ tự tạo để không vướng khóa ngoại). */
async function clean() {
  if (!existsSync(MANIFEST)) {
    console.log(`Không có ${MANIFEST} — chưa nạp demo bằng script này trên máy này.`);
    return;
  }
  const m = JSON.parse(readFileSync(MANIFEST, 'utf8'));
  let ok = 0;
  for (const path of [...m.created].reverse()) {
    try {
      await api('DELETE', path);
      ok++;
    } catch (e) {
      console.log(`  không xóa được ${path}: ${e.message.slice(0, 120)}`);
    }
  }
  // PATCH của JHipster bỏ qua giá trị null → lấy bản ghi, xóa trường ảnh rồi PUT lại cả bản ghi
  for (const { path, fields } of m.patched) {
    try {
      const entity = await api('GET', path);
      fields.forEach(f => (entity[f] = null));
      await api('PUT', path, entity);
    } catch {
      // bản ghi đã bị xóa
    }
  }
  writeFileSync(MANIFEST, JSON.stringify({ created: [], patched: [] }));
  console.log(`✔ Đã xóa ${ok} bản ghi demo và gỡ ảnh khỏi bản ghi có sẵn. (File ảnh trong uploads/ vẫn còn — xóa tay nếu muốn.)`);
}

async function login() {
  const res = await fetch(`${BASE}/api/authenticate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: USER, password: PASS }),
  });
  if (!res.ok) {
    throw new Error(`Đăng nhập thất bại (${res.status}). Kiểm tra --user/--pass và app đang chạy ở ${BASE}`);
  }
  token = (await res.json()).id_token;
}

// ===================== ảnh =====================
/** Loại ảnh + kích thước từ header file (JPEG/PNG/WebP), không cần thư viện. */
function inspectImage(buf) {
  const s = (a, b) => buf.toString('latin1', a, b);
  if (buf[0] === 0x89 && s(1, 4) === 'PNG') {
    return { type: 'image/png', ext: 'png', width: buf.readUInt32BE(16), height: buf.readUInt32BE(20) };
  }
  if (s(0, 4) === 'RIFF' && s(8, 12) === 'WEBP') {
    const chunk = s(12, 16);
    let width = 0;
    let height = 0;
    if (chunk === 'VP8X') {
      width = 1 + buf.readUIntLE(24, 3);
      height = 1 + buf.readUIntLE(27, 3);
    } else if (chunk === 'VP8 ') {
      width = buf.readUInt16LE(26) & 0x3fff;
      height = buf.readUInt16LE(28) & 0x3fff;
    } else if (chunk === 'VP8L') {
      const b = buf.readUInt32LE(21);
      width = (b & 0x3fff) + 1;
      height = ((b >> 14) & 0x3fff) + 1;
    }
    return { type: 'image/webp', ext: 'webp', width, height };
  }
  if (buf[0] === 0xff && buf[1] === 0xd8) {
    let i = 2;
    while (i < buf.length - 9) {
      if (buf[i] !== 0xff) {
        i++;
        continue;
      }
      const marker = buf[i + 1];
      const len = buf.readUInt16BE(i + 2);
      // SOF0..SOF15 (trừ DHT/JPG/DAC) chứa kích thước
      if (marker >= 0xc0 && marker <= 0xcf && ![0xc4, 0xc8, 0xcc].includes(marker)) {
        return { type: 'image/jpeg', ext: 'jpg', height: buf.readUInt16BE(i + 5), width: buf.readUInt16BE(i + 7) };
      }
      i += 2 + len;
    }
    return { type: 'image/jpeg', ext: 'jpg', width: 0, height: 0 };
  }
  return null;
}

async function uploadImages(only = null) {
  const files = readdirSync(IMAGE_DIR)
    .filter(f => (only ? only.some(part => f.includes(part)) : !SKIP.test(f)))
    .sort();
  const uploaded = [];
  for (const file of files) {
    const buf = readFileSync(join(IMAGE_DIR, file));
    const info = inspectImage(buf);
    if (!info || buf.length > 9.5 * 1024 * 1024) {
      console.log(`  bỏ qua ${file} (không phải ảnh JPEG/PNG/WebP hoặc quá lớn)`);
      continue;
    }
    const form = new FormData();
    const blob = new Blob([buf], { type: info.type });
    form.append('image', blob, `image.${info.ext}`);
    if (info.ext === 'webp') {
      // website coi ảnh .webp trong /uploads là có sẵn bản -800 và -24 (như form quản trị tạo) → gửi kèm cùng file
      form.append('medium', blob, 'medium.webp');
      form.append('placeholder', blob, 'placeholder.webp');
    }
    try {
      const res = await api('POST', 'admin/uploads', form);
      uploaded.push({
        url: res.url,
        width: info.width || null,
        height: info.height || null,
        landscape: info.width > info.height * 1.2,
        file,
      });
    } catch (e) {
      console.log(`  lỗi upload ${file}: ${e.message}`);
    }
  }
  return uploaded;
}

// ===================== dữ liệu giả =====================
const VN = 'Asia/Ho_Chi_Minh';
const todayVn = () => new Intl.DateTimeFormat('en-CA', { timeZone: VN }).format(new Date());
const addDays = (iso, n) => {
  const d = new Date(`${iso}T00:00:00Z`);
  d.setUTCDate(d.getUTCDate() + n);
  return d.toISOString().slice(0, 10);
};
/** giờ Việt Nam (UTC+7) → Instant ISO */
const vnInstant = (date, time) => new Date(`${date}T${time}:00+07:00`).toISOString();
const pick = (arr, i) => arr[i % arr.length];

const EXTRA_SERVICES = [
  // [categorySlug, nameVi, nameEn, shortVi, shortEn, price, minutes, sale, priceMax, featured]
  [
    'massage',
    'Massage Thái cổ truyền',
    'Traditional Thai massage',
    'Kéo giãn và bấm huyệt theo phong cách Thái, giúp cơ thể dẻo dai.',
    'Thai-style stretching and acupressure for a more supple body.',
    500000,
    90,
    450000,
    null,
    true,
  ],
  [
    'massage',
    'Massage cổ vai gáy',
    'Neck & shoulder massage',
    'Tập trung giải tỏa căng cứng vùng cổ, vai, gáy cho dân văn phòng.',
    'Focused relief for tight neck and shoulders — perfect after desk work.',
    280000,
    45,
    null,
    null,
    false,
  ],
  [
    'massage',
    'Massage tre nóng',
    'Warm bamboo massage',
    'Ống tre làm ấm lăn đều giúp thư giãn sâu các nhóm cơ lớn.',
    'Warm bamboo rollers release deep tension in large muscle groups.',
    600000,
    75,
    null,
    null,
    false,
  ],
  [
    'cham-soc-toc',
    'Gội đầu thảo dược bưởi',
    'Pomelo herbal hair wash',
    'Tinh dầu vỏ bưởi giúp tóc chắc khỏe, giảm gãy rụng.',
    'Pomelo peel oil for stronger hair and less breakage.',
    300000,
    50,
    260000,
    null,
    false,
  ],
  [
    'cham-soc-toc',
    'Ủ tóc phục hồi keratin',
    'Keratin repair hair mask',
    'Phục hồi tóc khô xơ, hư tổn do nhuộm uốn.',
    'Restores dry hair damaged by colouring or perming.',
    350000,
    40,
    null,
    450000,
    false,
  ],
  [
    'cham-soc-da',
    'Chăm sóc da mụn',
    'Acne care facial',
    'Làm sạch sâu, lấy nhân mụn chuẩn y khoa và làm dịu da.',
    'Deep cleanse, gentle extraction and soothing mask.',
    600000,
    75,
    520000,
    null,
    true,
  ],
  [
    'cham-soc-da',
    'Đắp mặt nạ vàng 24K',
    '24K gold facial mask',
    'Mặt nạ vàng giúp da sáng mịn, căng bóng tức thì.',
    'Gold mask for instantly bright, glowing skin.',
    800000,
    60,
    null,
    null,
    false,
  ],
  [
    'combo',
    'Combo Hạnh Phúc cho cặp đôi',
    'Couple happiness combo',
    'Massage toàn thân cho 2 người + gội đầu dưỡng sinh + trà thảo mộc.',
    'Full-body massage for two + herbal head spa + herbal tea.',
    1800000,
    120,
    1600000,
    null,
    true,
  ],
  [
    'combo',
    'Combo Thư Giãn Cuối Tuần',
    'Weekend unwind combo',
    'Xông hơi thảo dược, massage đá nóng và chăm sóc da cấp ẩm.',
    'Herbal steam, hot stone massage and hydrating facial.',
    1300000,
    150,
    1150000,
    null,
    false,
  ],
];

const NAMES = [
  'Nguyễn Thu Hà',
  'Trần Minh Anh',
  'Lê Hoàng Nam',
  'Phạm Ngọc Lan',
  'Emily Carter',
  'Đỗ Quỳnh Chi',
  'Kenji Watanabe',
  'Vũ Thanh Tâm',
  'Hoàng Bảo Ngọc',
  'Sophie Martin',
  'Bùi Đức Huy',
  'Ngô Mai Phương',
];
const REVIEW_TEXTS = [
  [5, 'Không gian yên tĩnh, thơm mùi thảo mộc. Kỹ thuật viên bấm huyệt rất chuẩn, về ngủ ngon cả tuần.'],
  [5, 'Lovely staff and spotless rooms. The herbal head spa was the highlight of our Hanoi trip!'],
  [4, 'Dịch vụ tốt, giá hợp lý. Cuối tuần hơi đông nên nhớ đặt lịch trước nhé.'],
  [5, 'Đặt lịch online rất nhanh, đến nơi không phải chờ. Combo cho cặp đôi rất đáng tiền.'],
  [5, 'Best massage in the Old Quarter. Booked through the website in under a minute.'],
  [4, 'Chăm sóc da mụn nhẹ nhàng, da dịu hẳn sau 2 buổi. Sẽ quay lại.'],
  [5, 'Gội đầu bồ kết thơm, nước ấm vừa phải, được massage cổ vai rất đã.'],
  [5, 'The hot stone massage melted away all my jet lag. Highly recommended.'],
  [4, 'Nhân viên thân thiện, có trà gừng sau liệu trình. Phòng hơi nhỏ nhưng sạch sẽ.'],
  [5, 'Lần thứ 3 quay lại, chất lượng vẫn ổn định. Rất thích mùi tinh dầu sả chanh.'],
];
const BLOG = [
  ['NEWS', '5 lợi ích của massage thảo mộc với dân văn phòng', '5 benefits of herbal massage for office workers'],
  ['TIPS', 'Chăm sóc da mùa hanh khô: 6 bước đơn giản tại nhà', 'Dry-season skincare: 6 easy steps at home'],
  ['EVENT', 'Tuần lễ tri ân khách hàng – giảm 20% mọi liệu trình', 'Customer appreciation week – 20% off all treatments'],
  ['REVIEW', 'Trải nghiệm combo cho cặp đôi: một buổi chiều chậm lại', 'Couple combo review: a slow afternoon together'],
  ['TIPS', 'Gội đầu dưỡng sinh bao lâu một lần là tốt?', 'How often should you get a herbal head spa?'],
  ['NEWS', 'Tinh dầu sả chanh, gừng, quế: chọn mùi nào cho bạn?', 'Lemongrass, ginger or cinnamon: which oil suits you?'],
  ['EVENT', 'Ra mắt liệu trình massage tre nóng', 'Introducing our warm bamboo massage'],
  ['TIPS', 'Những điều nên tránh trước và sau khi massage', 'What to avoid before and after a massage'],
];
const GALLERY_CAPTIONS = [
  ['Phòng trị liệu ấm áp với ánh đèn vàng', 'Warm treatment room with soft lighting'],
  ['Không gian thư giãn chờ trước liệu trình', 'Relaxation lounge before your treatment'],
  ['Đá nóng và tinh dầu chuẩn bị cho liệu trình', 'Hot stones and oils ready for treatment'],
  ['Khoảnh khắc thư giãn của khách hàng', 'A guest enjoying a quiet moment'],
  ['Góc chăm sóc da', 'Facial care corner'],
  ['Phòng đôi cho cặp đôi', 'Double room for couples'],
];

// ===================== nhạc nền demo =====================
/**
 * Tự tổng hợp 60 giây nhạc thư giãn (WAV mono 22 kHz): nền hợp âm êm + tiếng chuông pentatonic ngẫu nhiên,
 * lặp liền mạch. Do script tạo ra nên không vướng bản quyền.
 */
function makeAmbientWav(seconds = 60, rate = 22050) {
  const total = seconds * rate;
  const out = new Float32Array(total);
  const midi = m => 440 * 2 ** ((m - 69) / 12);
  // làm tròn tần số để mỗi sóng có số chu kỳ nguyên trong cả đoạn → điểm cuối khớp điểm đầu, lặp không "tách"
  const loopable = f => Math.max(1, Math.round(f * seconds)) / seconds;
  // 4 hợp âm, mỗi hợp âm 15 giây: Dmaj9 → Bm7 → Gmaj7 → A(sus2) → quay về Dmaj9 (lặp tròn)
  const chords = [
    [50, 57, 62, 66, 69, 76],
    [47, 54, 62, 66, 69, 74],
    [43, 50, 59, 62, 66, 71],
    [45, 52, 59, 64, 69, 71],
  ];
  const seg = total / chords.length;
  const xfade = rate * 3;
  for (let c = 0; c < chords.length; c++) {
    for (const note of chords[c]) {
      const f = loopable(midi(note));
      const f2 = loopable(midi(note) * 1.0025); // lệch nhẹ tạo độ "dày" cho âm nền
      const fLfo = loopable(0.07);
      const gain = note < 52 ? 0.18 : 0.09;
      for (let k = -xfade; k < seg + xfade; k++) {
        const i = Math.round(c * seg + k);
        const idx = ((i % total) + total) % total; // bọc vòng để đoạn cuối nối mượt vào đoạn đầu
        const env = k < xfade ? (k + xfade) / (2 * xfade) : k > seg - xfade ? (seg + xfade - k) / (2 * xfade) : 1;
        const e = Math.sin((Math.PI / 2) * Math.min(1, Math.max(0, env))) ** 2;
        const t = idx / rate;
        const tremolo = 0.85 + 0.15 * Math.sin(2 * Math.PI * fLfo * t + note);
        const v = Math.sin(2 * Math.PI * f * t) + 0.6 * Math.sin(2 * Math.PI * f2 * t) + 0.12 * Math.sin(4 * Math.PI * f * t);
        out[idx] += gain * e * tremolo * v;
      }
    }
  }
  // tiếng chuông (pentatonic D) – tần suất thưa, tắt dần
  let seed = 7;
  const rand = () => (seed = (seed * 16807) % 2147483647) / 2147483647;
  const bells = [74, 76, 78, 81, 83, 86, 88];
  for (let t = 1.5; t < seconds - 4; t += 2.2 + rand() * 3) {
    const f = midi(bells[Math.floor(rand() * bells.length)]);
    const start = Math.floor(t * rate);
    const len = Math.floor(3.5 * rate);
    for (let k = 0; k < len && start + k < total; k++) {
      const tt = k / rate;
      const env = Math.min(1, tt / 0.01) * Math.exp(-tt * 1.6);
      out[start + k] += 0.11 * env * (Math.sin(2 * Math.PI * f * tt) + 0.3 * Math.sin(2 * Math.PI * f * 2.76 * tt));
    }
  }
  // chuẩn hóa về -6 dB, xuất PCM 16-bit
  let peak = 0;
  for (const v of out) {
    peak = Math.max(peak, Math.abs(v));
  }
  const scale = (0.5 / (peak || 1)) * 32767;
  const buf = Buffer.alloc(44 + total * 2);
  buf.write('RIFF', 0);
  buf.writeUInt32LE(36 + total * 2, 4);
  buf.write('WAVEfmt ', 8);
  buf.writeUInt32LE(16, 16);
  buf.writeUInt16LE(1, 20);
  buf.writeUInt16LE(1, 22);
  buf.writeUInt32LE(rate, 24);
  buf.writeUInt32LE(rate * 2, 28);
  buf.writeUInt16LE(2, 32);
  buf.writeUInt16LE(16, 34);
  buf.write('data', 36);
  buf.writeUInt32LE(total * 2, 40);
  for (let i = 0; i < total; i++) {
    buf.writeInt16LE(Math.max(-32768, Math.min(32767, Math.round(out[i] * scale))), 44 + i * 2);
  }
  return buf;
}

/** Ghi nhạc vào uploads/demo/ (app phục vụ tại /uploads/demo/...) rồi gắn vào Cài đặt website. */
async function setupMusic() {
  const dir = join(UPLOAD_DIR, 'demo');
  mkdirSync(dir, { recursive: true });
  let name;
  if (MUSIC_FILE) {
    name = `music${extname(MUSIC_FILE).toLowerCase() || '.mp3'}`;
    copyFileSync(MUSIC_FILE, join(dir, name));
    console.log(`   dùng file nhạc: ${basename(MUSIC_FILE)}`);
  } else {
    name = 'ambient-demo.wav';
    writeFileSync(join(dir, name), makeAmbientWav());
    console.log('   đã tổng hợp nhạc thư giãn 60 giây (ambient-demo.wav)');
  }
  const url = `/uploads/demo/${name}`;
  const check = await fetch(`${BASE}${url}`, { method: 'HEAD' });
  if (!check.ok) {
    console.log(
      `   ⚠ app chưa phục vụ được ${url} (HTTP ${check.status}). Kiểm tra application.upload-dir hoặc chạy script từ thư mục gốc dự án.`,
    );
  }
  const settings = await get('site-settings');
  if (settings.length) {
    await patch(`site-settings/${settings[0].id}`, { id: settings[0].id, backgroundMusicUrl: url });
  }
  return url;
}

/** Chọn ảnh slideshow đầu trang và ghi vào Cài đặt website (heroSlides + heroImageUrl = ảnh đầu). */
async function setupHero(images) {
  const byWidth = (a, b) => (b.width ?? 0) - (a.width ?? 0);
  const wide = images.filter(i => i.landscape);
  let slides = HERO_SLIDES.map(part => images.find(i => i.file.includes(part))).filter(Boolean);
  if (HERO) {
    slides = [images.find(i => i.file.includes(HERO)), ...slides].filter(Boolean);
  }
  if (!slides.length) {
    slides = [...wide]
      .filter(i => HERO_HINT.test(i.file))
      .sort(byWidth)
      .slice(0, 4);
  }
  if (!slides.length) {
    slides = [...(wide.length ? wide : images)].sort(byWidth).slice(0, 4);
  }
  if (!slides.length) {
    console.log('   không có ảnh phù hợp cho slideshow');
    return;
  }
  console.log(`   slideshow: ${slides.map(i => i.file).join(', ')}`);
  const settings = await get('site-settings');
  if (settings.length) {
    await patch(`site-settings/${settings[0].id}`, {
      id: settings[0].id,
      heroImageUrl: slides[0].url,
      heroSlides: slides.map(i => i.url).join('\n'),
    });
  }
}

// ===================== chạy =====================
async function main() {
  console.log(`→ ${BASE}, ảnh từ ${IMAGE_DIR}`);
  await login();
  if (CLEAN) {
    await clean();
    return;
  }
  if (HERO_ONLY) {
    console.log('Slideshow đầu trang…');
    const imgs = await uploadImages(HERO_SLIDES);
    await setupHero(imgs);
    return;
  }
  if (MUSIC_ONLY) {
    console.log('Nhạc nền…');
    const url = await setupMusic();
    console.log(`✔ Đã gắn nhạc nền: ${url} — bấm nút ♪ trên header website để nghe.`);
    return;
  }

  const existingGallery = await get('gallery-images?size=1');
  if (existingGallery.length && !FORCE) {
    console.log('Đã có ảnh trong thư viện → có vẻ đã nạp demo trước đó. Thêm --force để nạp tiếp.');
    return;
  }

  console.log('1/10 Upload ảnh…');
  const images = await uploadImages();
  if (images.length < 10) {
    throw new Error(`Chỉ upload được ${images.length} ảnh — cần ít nhất 10.`);
  }
  const wide = images.filter(i => i.landscape);
  const all = images;
  let n = 0;
  const nextWide = () => pick(wide.length ? wide : all, n++).url;
  console.log(`   ${images.length} ảnh`);

  console.log('2/10 Cài đặt website (slideshow đầu trang)…');
  await setupHero(images);
  console.log('   nhạc nền…');
  await setupMusic();

  console.log('3/10 Cơ sở…');
  let branches = await get('branches?size=50');
  if (branches.length < 3) {
    await post('branches', {
      nameVi: 'Âu Lạc Spa – Ba Đình',
      nameEn: 'Âu Lạc Spa – Ba Dinh',
      addressVi: 'Số 3 phố Mẫu, Ba Đình, Hà Nội (địa chỉ mẫu)',
      addressEn: '3 Sample St, Ba Dinh, Hanoi (sample address)',
      phone: '0903456789',
      whatsapp: '84903456789',
      zalo: '0903456789',
      openTime: '09:00',
      closeTime: '21:30',
      lastBookingTime: '20:30',
      capacity: 5,
      mapEmbedUrl: 'https://www.google.com/maps?q=Ba+Dinh+Square,Hanoi&output=embed',
      mapDirectionUrl: 'https://www.google.com/maps/dir/?api=1&destination=Ba+Dinh+Square,Hanoi',
      displayOrder: 3,
      active: true,
    });
    branches = await get('branches?size=50');
  }
  for (const b of branches) {
    await patch(`branches/${b.id}`, { id: b.id, imageUrl: nextWide() });
  }

  console.log('4/10 Danh mục & dịch vụ…');
  const categories = await get('service-categories?size=50');
  for (const c of categories) {
    await patch(`service-categories/${c.id}`, { id: c.id, imageUrl: nextWide() });
  }
  const bySlug = Object.fromEntries(categories.map(c => [c.slug, c]));
  let order = 10;
  for (const [cat, nameVi, nameEn, shortVi, shortEn, price, minutes, sale, priceMax, featured] of EXTRA_SERVICES) {
    if (!bySlug[cat]) {
      continue;
    }
    await post('spa-services', {
      nameVi,
      nameEn,
      shortDescriptionVi: shortVi,
      shortDescriptionEn: shortEn,
      descriptionVi: `${shortVi}\n\nLiệu trình bắt đầu bằng trà thảo mộc và ngâm chân nước gừng ấm. (Nội dung demo)`,
      descriptionEn: `${shortEn}\n\nEvery treatment begins with herbal tea and a warm ginger foot soak. (Demo content)`,
      benefitsVi: 'Thư giãn sâu\nGiảm căng thẳng, mệt mỏi\nCải thiện giấc ngủ',
      benefitsEn: 'Deep relaxation\nLess stress and fatigue\nBetter sleep',
      notesVi: 'Vui lòng đến trước 10 phút.\nBáo trước nếu bạn đang mang thai hoặc có bệnh lý.',
      notesEn: 'Please arrive 10 minutes early.\nTell us if you are pregnant or have a medical condition.',
      price,
      salePrice: sale,
      priceMax,
      durationMinutes: minutes,
      featured,
      displayOrder: order++,
      active: true,
      category: { id: bySlug[cat].id },
    });
  }
  const services = await get('spa-services?size=100');
  for (const s of services) {
    await patch(`spa-services/${s.id}`, { id: s.id, imageUrl: nextWide() });
  }

  console.log('5/10 Thư viện ảnh…');
  let g = 0;
  for (const img of images) {
    const [capVi, capEn] = pick(GALLERY_CAPTIONS, g);
    await post('gallery-images', {
      imageUrl: img.url,
      altVi: capVi,
      altEn: capEn,
      captionVi: g % 3 === 0 ? capVi : null,
      captionEn: g % 3 === 0 ? capEn : null,
      width: img.width,
      height: img.height,
      displayOrder: g,
      active: true,
      branch: { id: pick(branches, g).id },
    });
    g++;
  }

  console.log('6/10 Đánh giá…');
  for (let i = 0; i < REVIEW_TEXTS.length; i++) {
    const [rating, comment] = REVIEW_TEXTS[i];
    await post('reviews', {
      authorName: pick(NAMES, i + 3),
      rating,
      comment,
      reviewDate: addDays(todayVn(), -(i * 4 + 2)),
      approved: true,
      featured: i < 2,
      createdDate: new Date().toISOString(),
      service: { id: pick(services, i).id },
    });
  }

  console.log('7/10 Bài viết…');
  for (let i = 0; i < BLOG.length; i++) {
    const [category, titleVi, titleEn] = BLOG[i];
    await post('blog-posts', {
      titleVi,
      titleEn,
      excerptVi: 'Bài viết demo để xem giao diện tin tức. Thay bằng nội dung thật của spa trong trang quản trị.',
      excerptEn: 'Demo article to preview the news layout. Replace it with your own content in the admin pages.',
      contentVi:
        'Đây là nội dung demo.\n\nMột buổi trị liệu đúng cách giúp cơ thể thả lỏng, tinh thần nhẹ nhõm và ngủ sâu hơn. ' +
        'Hãy dành cho mình ít nhất 60 phút mỗi tuần để chăm sóc bản thân.\n\nĐặt lịch online để được giữ chỗ vào khung giờ bạn thích.',
      contentEn:
        'This is demo content.\n\nA proper treatment relaxes the body, clears the mind and helps you sleep better. ' +
        'Give yourself at least 60 minutes a week to slow down.\n\nBook online to secure your favourite time slot.',
      coverImageUrl: nextWide(),
      category,
      published: true,
      publishedAt: vnInstant(addDays(todayVn(), -(i * 6 + 1)), '09:00'),
    });
  }

  console.log('8/10 Khuyến mãi & nhân viên…');
  const promos = [
    ['CHAOTHU', 'Chào thu – giảm 15%', 15],
    ['CAPDOI', 'Ưu đãi cặp đôi – giảm 10%', 10],
    ['SINHNHAT', 'Sinh nhật vui – giảm 20%', 20],
  ];
  for (const [code, name, pct] of promos) {
    await post('promotions', {
      code,
      name,
      description: 'Khuyến mãi demo',
      discountPercent: pct,
      startDate: vnInstant(addDays(todayVn(), -7), '00:00'),
      endDate: vnInstant(addDays(todayVn(), 30), '23:59'),
      active: true,
    }).catch(() => console.log(`  mã ${code} đã tồn tại, bỏ qua`));
  }
  const staffNames = ['Nguyễn Thị Hoa', 'Trần Văn Bình', 'Lê Thị Mai', 'Phạm Thu Trang', 'Đặng Minh Tuấn', 'Võ Ngọc Ánh'];
  const staff = [];
  for (let i = 0; i < staffNames.length; i++) {
    staff.push(
      await post('staff', {
        fullName: staffNames[i],
        phone: `09100000${String(i).padStart(2, '0')}`,
        position: i % 3 === 0 ? 'Kỹ thuật viên trưởng' : 'Kỹ thuật viên',
        bio: 'Nhân viên demo – hơn 3 năm kinh nghiệm massage và chăm sóc da.',
        active: true,
        branch: { id: pick(branches, i).id },
        services: services.filter((_, k) => k % staffNames.length === i || k % 4 === i % 4).map(s => ({ id: s.id })),
      }),
    );
  }

  console.log('9/10 Khách hàng & lịch hẹn…');
  const customers = [];
  for (let i = 0; i < 6; i++) {
    customers.push(
      await post('customers', {
        fullName: NAMES[i],
        phone: `09${RUN}${String(i).padStart(4, '0')}`,
        email: i % 2 ? `demo${i}@example.com` : null,
        createdDate: new Date().toISOString(),
      }),
    );
  }
  // Ngày mai, cơ sở thứ 2 (sức chứa nhỏ): kín khung 14:00–16:00 để thấy giờ "Đã đầy" trên website
  const tomorrow = addDays(todayVn(), 1);
  const small = branches.find(b => b.slug === 'tay-ho') ?? branches[1] ?? branches[0];
  const combo = services.find(s => s.durationMinutes >= 120) ?? services[0];
  const plans = [
    { branch: small, date: tomorrow, time: '14:00', people: small.capacity, service: combo, status: 'CONFIRMED', source: 'WEBSITE' },
    { branch: branches[0], date: tomorrow, time: '10:30', people: 2, service: services[0], status: 'PENDING', source: 'WEBSITE' },
    { branch: branches[0], date: tomorrow, time: '18:00', people: 1, service: services[3], status: 'PENDING', source: 'QUICK' },
    {
      branch: branches[0],
      date: addDays(todayVn(), -1),
      time: '15:00',
      people: 2,
      service: services[1],
      status: 'COMPLETED',
      source: 'PHONE',
    },
    { branch: small, date: addDays(todayVn(), -2), time: '11:00', people: 1, service: services[5], status: 'NO_SHOW', source: 'WEBSITE' },
    {
      branch: branches[0],
      date: addDays(todayVn(), 3),
      time: '16:15',
      people: 3,
      service: services[2],
      status: 'CONFIRMED',
      source: 'ADMIN',
    },
  ];
  const codes = [];
  for (let i = 0; i < plans.length; i++) {
    const p = plans[i];
    const start = vnInstant(p.date, p.time);
    const end = new Date(new Date(start).getTime() + p.service.durationMinutes * 60000).toISOString();
    const unit = p.service.salePrice ?? p.service.price;
    const code = `DEMO${RUN}${i + 1}`;
    const booking = await post('bookings', {
      code,
      startTime: start,
      endTime: end,
      numberOfPeople: p.people,
      status: p.status,
      source: p.source,
      totalAmount: unit * p.people,
      paymentStatus: p.status === 'COMPLETED' ? 'PAID' : 'UNPAID',
      paymentMethod: p.status === 'COMPLETED' ? 'CASH' : null,
      note: 'Lịch hẹn demo',
      createdDate: new Date().toISOString(),
      branch: { id: p.branch.id },
      customer: { id: customers[i].id },
    });
    await post('booking-items', {
      price: unit,
      quantity: p.people,
      durationMinutes: p.service.durationMinutes,
      booking: { id: booking.id },
      service: { id: p.service.id },
      staff: { id: pick(staff, i).id },
    });
    codes.push(`${code} / ${customers[i].phone} (${p.status}, ${p.date} ${p.time})`);
  }

  console.log('10/10 Tin nhắn liên hệ…');
  const messages = [
    ['Lê Thu Thảo', '0977000111', null, 'Spa có nhận đặt lịch cho nhóm 8 người vào chiều thứ 7 không ạ?'],
    ['John Smith', null, 'john@example.com', 'Do you offer gift vouchers? I would like to buy one for my wife.'],
    ['Phan Văn Long', '0966000222', null, 'Cho mình hỏi combo cặp đôi có áp dụng mã CAPDOI không?'],
  ];
  for (let i = 0; i < messages.length; i++) {
    const [fullName, phone, email, message] = messages[i];
    await post('contact-messages', { fullName, phone, email, message, handled: i === 2, createdDate: new Date().toISOString() });
  }

  console.log('\n✔ Xong. Mở website: ' + BASE + '/vi');
  console.log('  Tra cứu lịch demo tại /vi/booking/lookup (mã / số điện thoại):');
  codes.forEach(c => console.log('   - ' + c));
  console.log(`  Khung 14:00–16:00 ngày ${tomorrow} ở "${small.nameVi}" đã kín → thử đặt lịch để thấy giờ "Đã đầy".`);
}

main()
  .catch(e => {
    console.error(`\n✖ ${e.message}`);
    process.exitCode = 1;
  })
  .finally(() => {
    if (!CLEAN && (manifest.created.length || manifest.patched.length)) {
      saveManifest();
      console.log(`  (đã ghi ${MANIFEST} — chạy "npm run seed:demo -- --clean" để xóa dữ liệu demo)`);
    }
  });
