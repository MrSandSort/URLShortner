package com.islington.urlshortener.dto;

import java.time.Instant;

public record ShortUrlResponse(
        String shortCode,
        String shortUrl,
        String longUrl,
        Instant createdAt,
        Instant expiresAt
) {
}
