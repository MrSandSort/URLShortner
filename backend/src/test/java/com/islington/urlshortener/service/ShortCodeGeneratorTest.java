package com.islington.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    @Test
    void generatesBase62CodeWithRequestedLength() {
        String code = generator.generate(10);

        assertThat(code).hasSize(10);
        assertThat(code).matches("^[A-Za-z0-9]+$");
    }
}
