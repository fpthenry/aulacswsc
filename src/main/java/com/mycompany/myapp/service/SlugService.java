package com.mycompany.myapp.service;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

/**
 * Sinh slug (đường dẫn thân thiện) cho bài viết, dịch vụ, danh mục, cơ sở.
 *
 * <p>Dạng: {@code <tiêu-đề-không-dấu>-<yyyyMMdd>}, vd "Gội đầu dưỡng sinh" ngày 11/10/2026 →
 * {@code goi-dau-duong-sinh-20261011}. Trùng thì thêm {@code -2}, {@code -3}...
 * Slug chỉ sinh khi tạo mới; khi sửa bản ghi slug được giữ nguyên để link đã chia sẻ không hỏng.
 */
@Service
public class SlugService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter DATE = DateTimeFormatter.BASIC_ISO_DATE;
    /** chừa chỗ cho "-yyyyMMdd" và hậu tố "-99" */
    private static final int RESERVED = 12;

    /**
     * @param title     tiêu đề / tên (tiếng Việt)
     * @param date      ngày gắn vào slug; null = hôm nay
     * @param maxLength độ dài tối đa của cột slug
     * @param exists    kiểm tra slug đã tồn tại chưa (vd repository::existsBySlug)
     * @param fallback  phần chữ dùng khi tiêu đề rỗng, vd "bai-viet"
     */
    public String create(String title, LocalDate date, int maxLength, Predicate<String> exists, String fallback) {
        String base = slugify(title);
        if (base.isEmpty()) {
            base = fallback;
        }
        base = trimDashes(base.substring(0, Math.min(base.length(), maxLength - RESERVED)));
        String slug = base + "-" + (date != null ? date : LocalDate.now(ZONE)).format(DATE);
        String candidate = slug;
        for (int i = 2; exists.test(candidate); i++) {
            candidate = slug + "-" + i;
        }
        return candidate;
    }

    /** Ngày theo giờ Việt Nam của một thời điểm (vd ngày đăng bài). */
    public static LocalDate dateOf(Instant instant) {
        return instant == null ? null : LocalDate.ofInstant(instant, ZONE);
    }

    /** "Gội đầu – Ngải cứu!" → "goi-dau-ngai-cuu" */
    public static String slugify(String text) {
        if (text == null) {
            return "";
        }
        String noAccent = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").replace('đ', 'd').replace('Đ', 'D');
        return trimDashes(noAccent.toLowerCase().replaceAll("[^a-z0-9]+", "-"));
    }

    private static String trimDashes(String s) {
        return s.replaceAll("^-+|-+$", "");
    }
}
