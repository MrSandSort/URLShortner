package com.islington.urlshortener.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateShortUrlRequest(
        @NotBlank
        @Size(max = 2048)
        String longUrl,

        @Size(min = 4, max = 32)
        @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "customAlias can only contain letters, numbers, underscores, and hyphens")
        String customAlias,

        @Future
        Instant expiresAt
) {
}
