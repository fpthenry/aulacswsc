package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.enumeration.BlogCategory;
import com.mycompany.myapp.service.PublicSiteService;
import com.mycompany.myapp.service.RateLimiterService;
import com.mycompany.myapp.service.dto.BlogPostDTO;
import com.mycompany.myapp.service.dto.GalleryImageDTO;
import com.mycompany.myapp.service.dto.ReviewDTO;
import com.mycompany.myapp.service.dto.site.ContactRequestDTO;
import com.mycompany.myapp.service.dto.site.SiteBootstrapDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * Nội dung website công khai (không cần đăng nhập). Chỉ trả dữ liệu đang bật / đã xuất bản / đã duyệt.
 */
@RestController
@RequestMapping("/api/public")
public class PublicSiteResource {

    /** Nội dung ít thay đổi: cho trình duyệt cache 1 phút. */
    private static final CacheControl SHORT_CACHE = CacheControl.maxAge(Duration.ofMinutes(1)).cachePublic();
    private static final int MAX_PAGE_SIZE = 50;

    private final PublicSiteService publicSiteService;
    private final RateLimiterService rateLimiterService;

    public PublicSiteResource(PublicSiteService publicSiteService, RateLimiterService rateLimiterService) {
        this.publicSiteService = publicSiteService;
        this.rateLimiterService = rateLimiterService;
    }

    /** Cài đặt website + cơ sở + danh mục + dịch vụ, tải một lần khi mở website. */
    @GetMapping("/site")
    public ResponseEntity<SiteBootstrapDTO> site() {
        return ResponseEntity.ok().cacheControl(SHORT_CACHE).body(publicSiteService.bootstrap());
    }

    @GetMapping("/gallery")
    public ResponseEntity<List<GalleryImageDTO>> gallery() {
        return ResponseEntity.ok().cacheControl(SHORT_CACHE).body(publicSiteService.gallery());
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO>> reviews(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "12") int size
    ) {
        Sort sort = Sort.by(Sort.Order.desc("featured"), Sort.Order.desc("reviewDate"), Sort.Order.desc("id"));
        Page<ReviewDTO> result = publicSiteService.reviews(PageRequest.of(Math.max(page, 0), clamp(size), sort));
        return pageResponse(result);
    }

    @GetMapping("/blog")
    public ResponseEntity<List<BlogPostDTO>> blog(
        @RequestParam(required = false) BlogCategory category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "9") int size
    ) {
        Sort sort = Sort.by(Sort.Order.desc("publishedAt"), Sort.Order.desc("id"));
        Page<BlogPostDTO> result = publicSiteService.blogPosts(category, PageRequest.of(Math.max(page, 0), clamp(size), sort));
        return pageResponse(result);
    }

    @GetMapping("/blog/{slug}")
    public ResponseEntity<BlogPostDTO> blogPost(@PathVariable("slug") String slug) {
        return ResponseUtil.wrapOrNotFound(publicSiteService.blogPost(slug));
    }

    /** Form liên hệ: honeypot + tối đa 3 lần / 10 phút / IP. */
    @PostMapping("/contact")
    public ResponseEntity<Void> contact(@Valid @RequestBody ContactRequestDTO request, HttpServletRequest http) {
        if (StringUtils.hasText(request.website())) {
            throw new BadRequestAlertException("Invalid request", "contactMessage", "spam");
        }
        rateLimiterService.check("contact", http.getRemoteAddr(), 3, Duration.ofMinutes(10));
        publicSiteService.saveContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private static int clamp(int size) {
        return Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }

    private static <T> ResponseEntity<List<T>> pageResponse(Page<T> page) {
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).cacheControl(SHORT_CACHE).body(page.getContent());
    }
}
