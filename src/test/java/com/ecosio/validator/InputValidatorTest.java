package com.ecosio.validator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InputValidatorTest {

    @Test
    void testValidHttpUrl() {
        assertDoesNotThrow(() -> InputValidator.validateUrl("http://example.com"));
    }

    @Test
    void testValidHttpsUrl() {
        assertDoesNotThrow(() -> InputValidator.validateUrl("https://example.com"));
    }

    @Test
    void testValidUrlWithPathAndQuery() {
        assertDoesNotThrow(() -> InputValidator.validateUrl("https://example.com/page?param=value"));
    }

    @Test
    void testUrlWithoutProtocolThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.validateUrl("example.com"));
        assertTrue(ex.getMessage().contains("Invalid URL"));
    }

    @Test
    void testUrlWithEmptyHostThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.validateUrl("http:///page"));
        assertTrue(ex.getMessage().contains("Invalid URL"));
    }

    @Test
    void testMalformedUrlThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.validateUrl("ht!tp://bad^url"));
        assertTrue(ex.getMessage().contains("Invalid URL"));
    }

    @Test
    void testNullInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUrl(null));
    }

    @Test
    void testEmptyStringThrows() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.validateUrl(""));
        assertTrue(ex.getMessage().contains("Invalid URL"));
    }

    @Test
    void testFileUrlAcceptedUnlessExcluded() {
        assertThrows(IllegalArgumentException.class, () -> InputValidator.validateUrl("file:///tmp/data.html"));
    }
}