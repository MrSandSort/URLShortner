package com.islington.urlshortener.api;

import com.islington.urlshortener.dto.CreateShortUrlRequest;
import com.islington.urlshortener.dto.ShortUrlResponse;
import com.islington.urlshortener.dto.ShortUrlStatsResponse;
import com.islington.urlshortener.service.ShortUrlService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ShortUrlController {

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<ShortUrlResponse> create(@Valid @RequestBody CreateShortUrlRequest request) {
        ShortUrlResponse response = service.create(request);
        return ResponseEntity.created(URI.create(response.shortUrl())).body(response);
    }

    @GetMapping("/api/v1/urls/{shortCode}/stats")
    public ShortUrlStatsResponse stats(@PathVariable String shortCode) {
        return service.stats(shortCode);
    }

    @GetMapping("/{shortCode:[A-Za-z0-9_-]+}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        URI target = service.resolveAndRecordClick(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, target.toString())
                .build();
    }
}
