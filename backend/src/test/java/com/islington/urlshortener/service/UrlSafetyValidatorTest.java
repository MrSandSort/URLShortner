package com.islington.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.islington.urlshortener.config.UrlShortenerProperties;
import com.islington.urlshortener.exception.UnsafeUrlException;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UrlSafetyValidatorTest {

    private final UrlSafetyValidator validator = new UrlSafetyValidator(
            new UrlShortenerProperties("http://localhost:8080", 8, 10, Duration.ofDays(365), Set.of("blocked.example"))
    );

    @Test
    void acceptsPublicHttpUrl() {
        assertThat(validator.validate("https://spring.io/projects/spring-boot").toString())
                .isEqualTo("https://spring.io/projects/spring-boot");
    }

    @Test
    void rejectsNonHttpSchemes() {
        assertThatThrownBy(() -> validator.validate("file:///etc/passwd"))
                .isInstanceOf(UnsafeUrlException.class)
                .hasMessageContaining("Only HTTP and HTTPS");
    }

    @Test
    void rejectsLocalIpAddresses() {
        assertThatThrownBy(() -> validator.validate("http://127.0.0.1/admin"))
                .isInstanceOf(UnsafeUrlException.class)
                .hasMessageContaining("Private and local IP");
    }

    @Test
    void rejectsPrivateIpAddresses() {
        assertThatThrownBy(() -> validator.validate("http://10.0.0.5/admin"))
                .isInstanceOf(UnsafeUrlException.class)
                .hasMessageContaining("Private and local IP");
    }
}
