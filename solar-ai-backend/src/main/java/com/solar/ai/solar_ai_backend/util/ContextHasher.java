package com.solar.ai.solar_ai_backend.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Stable SHA-256 hashing for context objects.
 * - Canonical JSON: sorted props, no nulls, ISO-8601 dates
 * - Overloads for Object, String, and byte[]
 */
public final class ContextHasher {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Java time (Instant, LocalDateTime, etc.)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) // ISO-8601
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)    // stable field order
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);         // drop nulls

    private ContextHasher() {}

    /* ===================== Public API (choose one) ===================== */

    /** Hash any object (DTO, Map, List...) into URL-safe Base64 (no padding). */
    public static String sha256Base64(Object obj) {
        byte[] bytes = toCanonicalJsonBytes(obj);
        return base64UrlNoPadding(digest(bytes));
    }

    /** Hash any object into lowercase hex string. */
    public static String sha256Hex(Object obj) {
        byte[] bytes = toCanonicalJsonBytes(obj);
        return toHex(digest(bytes));
    }

    /** Hash a text payload (Base64 URL-safe). */
    public static String sha256Base64(String text) {
        return base64UrlNoPadding(digest(text.getBytes(StandardCharsets.UTF_8)));
    }

    /** Hash a text payload (hex). */
    public static String sha256Hex(String text) {
        return toHex(digest(text.getBytes(StandardCharsets.UTF_8)));
    }

    /** Hash raw bytes (Base64 URL-safe). */
    public static String sha256Base64(byte[] bytes) {
        return base64UrlNoPadding(digest(bytes));
    }

    /** Hash raw bytes (hex). */
    public static String sha256Hex(byte[] bytes) {
        return toHex(digest(bytes));
    }

    /* ===================== Internals ===================== */

    private static byte[] toCanonicalJsonBytes(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (Exception e) {
            // As a fallback (shouldn’t happen), hash toString()
            return String.valueOf(obj).getBytes(StandardCharsets.UTF_8);
        }
    }

    private static byte[] digest(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(bytes);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String base64UrlNoPadding(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >>> 4) & 0xF, 16))
                    .append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }
}