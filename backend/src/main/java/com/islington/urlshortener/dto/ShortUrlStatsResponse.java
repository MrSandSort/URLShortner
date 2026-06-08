package com.islington.urlshortener.dto;

import java.time.Instant;

public record ShortUrlStatsResponse(
        String shortCode,
        String longUrl,
        long clickCount,
        Instant createdAt,
        Instant expiresAt,
        Instant lastAccessedAt,
        boolean active
) {
}
