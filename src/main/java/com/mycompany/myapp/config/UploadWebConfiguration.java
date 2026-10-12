package com.mycompany.myapp.config;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Phục vụ ảnh admin upload tại /uploads/**. Tên file là UUID nên nội dung không đổi → cache 1 năm.
 */
@Configuration
public class UploadWebConfiguration implements WebMvcConfigurer {

    private final ApplicationProperties applicationProperties;

    public UploadWebConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(applicationProperties.getUploadDir()).toAbsolutePath().normalize().toUri().toString();
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations(location.endsWith("/") ? location : location + "/")
            .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable());
    }
}
