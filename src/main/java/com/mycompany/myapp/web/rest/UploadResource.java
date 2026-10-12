package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.config.ApplicationProperties;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Upload ảnh cho trang quản trị (đường dẫn /api/admin/** → chỉ ROLE_ADMIN).
 *
 * <p>Trình duyệt admin đã nén ảnh sang WebP và gửi 3 cỡ:
 * {@code image} (≤1600px), {@code medium} (≤800px), {@code placeholder} (~24px, làm nền mờ khi đang tải).
 * Server lưu thành {@code <uuid>.webp}, {@code <uuid>-800.webp}, {@code <uuid>-24.webp} và trả về URL ảnh chính.
 */
@RestController
@RequestMapping("/api/admin/uploads")
public class UploadResource {

    private static final Logger LOG = LoggerFactory.getLogger(UploadResource.class);
    private static final String ENTITY_NAME = "upload";

    private final ApplicationProperties applicationProperties;

    public UploadResource(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> upload(
        @RequestParam("image") MultipartFile image,
        @RequestParam(value = "medium", required = false) MultipartFile medium,
        @RequestParam(value = "placeholder", required = false) MultipartFile placeholder
    ) throws IOException {
        LocalDate today = LocalDate.now();
        String folder = "%d/%02d".formatted(today.getYear(), today.getMonthValue());
        Path dir = Path.of(applicationProperties.getUploadDir()).toAbsolutePath().normalize().resolve(folder);
        Files.createDirectories(dir);
        String base = UUID.randomUUID().toString();

        Map<String, String> result = new LinkedHashMap<>();
        result.put("url", save(image, dir, base, "", folder));
        if (medium != null && !medium.isEmpty()) {
            result.put("mediumUrl", save(medium, dir, base, "-800", folder));
        }
        if (placeholder != null && !placeholder.isEmpty()) {
            result.put("placeholderUrl", save(placeholder, dir, base, "-24", folder));
        }
        LOG.debug("Uploaded image {}", result.get("url"));
        return ResponseEntity.ok(result);
    }

    private String save(MultipartFile file, Path dir, String base, String suffix, String folder) throws IOException {
        String ext = detectExtension(file);
        String name = base + suffix + "." + ext;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, dir.resolve(name));
        }
        return "/uploads/" + folder + "/" + name;
    }

    /** Xác định loại ảnh theo "magic bytes" (không tin tên file / content-type client gửi). */
    private String detectExtension(MultipartFile file) throws IOException {
        byte[] h = new byte[12];
        try (InputStream in = file.getInputStream()) {
            if (in.readNBytes(h, 0, 12) < 12) {
                throw new BadRequestAlertException("File is not an image", ENTITY_NAME, "notimage");
            }
        }
        if (h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F' && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P') {
            return "webp";
        }
        if ((h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8) {
            return "jpg";
        }
        if ((h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G') {
            return "png";
        }
        if (h[4] == 'f' && h[5] == 't' && h[6] == 'y' && h[7] == 'p' && h[8] == 'a' && h[9] == 'v' && h[10] == 'i') {
            return "avif";
        }
        throw new BadRequestAlertException("Only WebP, AVIF, JPEG or PNG images are allowed", ENTITY_NAME, "notimage");
    }
}
