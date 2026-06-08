package com.islington.urlshortener.service;

import com.islington.urlshortener.config.UrlShortenerProperties;
import com.islington.urlshortener.domain.ShortUrl;
import com.islington.urlshortener.dto.CreateShortUrlRequest;
import com.islington.urlshortener.dto.ShortUrlResponse;
import com.islington.urlshortener.dto.ShortUrlStatsResponse;
import com.islington.urlshortener.exception.AliasAlreadyExistsException;
import com.islington.urlshortener.exception.ShortUrlNotFoundException;
import com.islington.urlshortener.repository.ShortUrlRepository;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ShortUrlService {

    private final ShortUrlRepository repository;
    private final ShortCodeGenerator codeGenerator;
    private final UrlSafetyValidator urlSafetyValidator;
    private final UrlShortenerProperties properties;
    private final Clock clock;

    public ShortUrlService(
            ShortUrlRepository repository,
            ShortCodeGenerator codeGenerator,
            UrlSafetyValidator urlSafetyValidator,
            UrlShortenerProperties properties,
            Clock clock
    ) {
        this.repository = repository;
        this.codeGenerator = codeGenerator;
        this.urlSafetyValidator = urlSafetyValidator;
        this.properties = properties;
        this.clock = clock;
    }

    @Transactional
    public ShortUrlResponse create(CreateShortUrlRequest request)
    {
        URI safeUri = urlSafetyValidator.validate(request.longUrl());
        Instant now = Instant.now(clock);
        Instant expiresAt = request.expiresAt() != null ? request.expiresAt() : now.plus(properties.defaultTtl());
        String requestedAlias = normalizeAlias(request.customAlias());

        ShortUrl saved = requestedAlias == null
                ? createWithGeneratedCode(safeUri.toString(), now, expiresAt)
                : createWithCustomAlias(requestedAlias, safeUri.toString(), now, expiresAt);

        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(cacheNames = "shortUrls", key = "#shortCode")
    public URI resolveAndRecordClick(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortCode));
        Instant now = Instant.now(clock);
        if (!shortUrl.isActive() || shortUrl.isExpired(now)) {
            throw new ShortUrlNotFoundException(shortCode);
        }
        shortUrl.registerClick(now);
        return URI.create(shortUrl.getLongUrl());
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "shortUrls", key = "#shortCode")
    public ShortUrlStatsResponse stats(String shortCode) {
        ShortUrl shortUrl = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortCode));
        return new ShortUrlStatsResponse(
                shortUrl.getShortCode(),
                shortUrl.getLongUrl(),
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt(),
                shortUrl.getLastAccessedAt(),
                shortUrl.isActive()
        );
    }

    private ShortUrl createWithCustomAlias(String alias, String longUrl, Instant now, Instant expiresAt) {
        if (repository.existsByShortCode(alias)) {
            throw new AliasAlreadyExistsException(alias);
        }
        try {
            return repository.save(new ShortUrl(alias, longUrl, now, expiresAt));
        } catch (DataIntegrityViolationException ex) {
            throw new AliasAlreadyExistsException(alias);
        }
    }

    private ShortUrl createWithGeneratedCode(String longUrl, Instant now, Instant expiresAt) {
        for (int attempt = 0; attempt < properties.maxCreateAttempts(); attempt++) {
            String code = codeGenerator.generate(properties.codeLength());
            try {
                return repository.save(new ShortUrl(code, longUrl, now, expiresAt));
            } catch (DataIntegrityViolationException ignored) {
                // Retry on rare code collision.
            }
        }
        throw new IllegalStateException("Could not allocate a unique short code");
    }

    private String normalizeAlias(String alias) {
        return StringUtils.hasText(alias) ? alias.trim() : null;
    }

    private ShortUrlResponse toResponse(ShortUrl shortUrl) {
        String baseUrl = properties.publicBaseUrl().replaceAll("/+$", "");
        return new ShortUrlResponse(
                shortUrl.getShortCode(),
                baseUrl + "/" + shortUrl.getShortCode(),
                shortUrl.getLongUrl(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt()
        );
    }
}
