package com.mycompany.myapp.service.dto.site;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Form liên hệ trên website. {@code website} là ô honeypot ẩn.
 */
public record ContactRequestDTO(
    @NotBlank @Size(max = 150) String fullName,
    @Size(max = 20) String phone,
    @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 2000) String message,
    String website
) {}
