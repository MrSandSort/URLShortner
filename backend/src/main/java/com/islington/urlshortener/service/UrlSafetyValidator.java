package com.islington.urlshortener.service;

import com.islington.urlshortener.config.UrlShortenerProperties;
import com.islington.urlshortener.exception.UnsafeUrlException;
import java.net.IDN;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class UrlSafetyValidator {

    private final UrlShortenerProperties properties;

    public UrlSafetyValidator(UrlShortenerProperties properties) {
        this.properties = properties;
    }

    public URI validate(String rawUrl) {
        URI uri;

        try {
            uri = new URI(rawUrl).normalize();
        } catch (URISyntaxException ex) {
            throw new UnsafeUrlException("URL is not syntactically valid");
        }

        String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);

        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new UnsafeUrlException("Only HTTP and HTTPS URLs are supported");
        }

        if (uri.getUserInfo() != null) {
            throw new UnsafeUrlException("URLs with user-info are not allowed");
        }

        String host = uri.getHost();

        if (host == null || host.isBlank()) {
            throw new UnsafeUrlException("URL host is required");
        }

        String asciiHost = IDN.toASCII(host).toLowerCase(Locale.ROOT);
        if (properties.blockedHosts().contains(asciiHost)) {
            throw new UnsafeUrlException("URL host is blocked");
        }
        if (asciiHost.equals("localhost") || asciiHost.endsWith(".localhost")) {
            throw new UnsafeUrlException("Localhost URLs are not allowed");
        }

        rejectUnsafeLiteralIp(asciiHost);
        return uri;
    }

    private void rejectUnsafeLiteralIp(String host) {
        if (!host.matches("^\\d{1,3}(\\.\\d{1,3}){3}$") && !host.contains(":")) {
            return;
        }

        try {
            InetAddress address = InetAddress.getByName(host);
            if (address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress())
            {
                throw new UnsafeUrlException("Private and local IP URLs are not allowed");
            }
        } catch (UnknownHostException ex) {
            throw new UnsafeUrlException("URL host is not valid");
        }
    }
}
