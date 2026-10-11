package com.mycompany.myapp.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.mycompany.myapp.security.*;
import com.mycompany.myapp.web.filter.SpaWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicyHeader(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                    .requestMatchers(mvc.pattern("/index.html"), mvc.pattern("/*.js"), mvc.pattern("/*.txt"), mvc.pattern("/*.json"), mvc.pattern("/*.map"), mvc.pattern("/*.css")).permitAll()
                    .requestMatchers(mvc.pattern("/*.ico"), mvc.pattern("/*.png"), mvc.pattern("/*.svg"), mvc.pattern("/*.webapp")).permitAll()
                    .requestMatchers(mvc.pattern("/app/**")).permitAll()
                    .requestMatchers(mvc.pattern("/i18n/**")).permitAll()
                    .requestMatchers(mvc.pattern("/content/**")).permitAll()
                    .requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    // ===== Website spa: nội dung công khai cho khách vãng lai =====
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/service-categories/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/spa-services/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/promotions/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/staff/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/reviews/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/contact-messages")).permitAll()
                    // ===== Danh mục, dịch vụ, khuyến mãi, nhân viên, liên hệ: chỉ ADMIN quản lý =====
                    .requestMatchers(mvc.pattern("/api/service-categories/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/api/spa-services/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/api/promotions/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/api/staff/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/api/contact-messages/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    // ===== Khách hàng: ADMIN & STAFF =====
                    .requestMatchers(mvc.pattern("/api/customers/**")).hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.STAFF)
                    // ===== Lịch hẹn: CUSTOMER chỉ được tạo & xem lịch của mình (lọc trong BookingResource) =====
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/bookings/**")).hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.STAFF, AuthoritiesConstants.CUSTOMER)
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/bookings")).hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.STAFF, AuthoritiesConstants.CUSTOMER)
                    .requestMatchers(mvc.pattern("/api/bookings/**")).hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.STAFF)
                    .requestMatchers(mvc.pattern("/api/booking-items/**")).hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.STAFF)
                    // ===== Đánh giá: CUSTOMER viết đánh giá, ADMIN sửa/xóa/duyệt =====
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/reviews")).hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.CUSTOMER)
                    .requestMatchers(mvc.pattern("/api/reviews/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/api/**")).authenticated()
                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
