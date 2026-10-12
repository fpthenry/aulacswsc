package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.PublicSiteService;
import com.mycompany.myapp.service.dto.BlogPostDTO;
import com.mycompany.myapp.service.dto.SpaServiceDTO;
import com.mycompany.myapp.service.dto.site.SiteBootstrapDTO;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * sitemap.xml và robots.txt sinh theo nội dung thật và tên miền đang truy cập.
 */
@RestController
public class SeoResource {

    private static final List<String> LANGS = List.of("vi", "en");
    private static final List<String> STATIC_PAGES = List.of("", "/services", "/branches", "/gallery", "/blog", "/booking");
    private static final DateTimeFormatter W3C_DATE = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneOffset.UTC);
    private static final CacheControl CACHE = CacheControl.maxAge(Duration.ofHours(1)).cachePublic();

    private final PublicSiteService publicSiteService;

    public SeoResource(PublicSiteService publicSiteService) {
        this.publicSiteService = publicSiteService;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap() {
        String base = baseUrl();
        SiteBootstrapDTO site = publicSiteService.bootstrap();
        StringBuilder xml = new StringBuilder(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\">\n"
        );
        for (String page : STATIC_PAGES) {
            appendUrl(xml, base, page, null);
        }
        for (SpaServiceDTO service : site.services()) {
            appendUrl(xml, base, "/services/" + service.getSlug(), null);
        }
        for (BlogPostDTO post : publicSiteService.allPublishedPosts()) {
            String lastMod = post.getPublishedAt() == null ? null : W3C_DATE.format(post.getPublishedAt());
            appendUrl(xml, base, "/blog/" + post.getSlug(), lastMod);
        }
        xml.append("</urlset>\n");
        return ResponseEntity.ok().cacheControl(CACHE).contentType(MediaType.APPLICATION_XML).body(xml.toString());
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots() {
        String body =
            """
            User-agent: *
            Allow: /vi
            Allow: /en
            Disallow: /api/
            Disallow: /management/
            Disallow: /v3/api-docs/
            Disallow: /admin
            Disallow: /account
            Disallow: /login
            Disallow: /dashboard

            Sitemap: %s/sitemap.xml
            """.formatted(baseUrl());
        return ResponseEntity.ok().cacheControl(CACHE).contentType(MediaType.TEXT_PLAIN).body(body);
    }

    /** Mỗi trang có bản /vi và /en, khai báo hreflang để Google hiểu đây là 2 bản dịch. */
    private static void appendUrl(StringBuilder xml, String base, String path, String lastMod) {
        for (String lang : LANGS) {
            xml.append("  <url>\n    <loc>").append(escape(base + "/" + lang + path)).append("</loc>\n");
            if (lastMod != null) {
                xml.append("    <lastmod>").append(lastMod).append("</lastmod>\n");
            }
            for (String alt : LANGS) {
                xml
                    .append("    <xhtml:link rel=\"alternate\" hreflang=\"")
                    .append(alt)
                    .append("\" href=\"")
                    .append(escape(base + "/" + alt + path))
                    .append("\"/>\n");
            }
            xml.append("  </url>\n");
        }
    }

    private static String baseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
