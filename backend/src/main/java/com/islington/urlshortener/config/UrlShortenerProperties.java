package com.islington.urlshortener.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.shortener")
public record UrlShortenerProperties(
        @NotBlank String publicBaseUrl,
        @Min(6) @Max(16) int codeLength,
        @Min(1) @Max(25) int maxCreateAttempts,
        Duration defaultTtl,
        Set<String> blockedHosts
) {
}
