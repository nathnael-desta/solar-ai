package com.solar.ai.solar_ai_backend.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ContextHasherTest {

    @Test
    void stable_hash_for_same_logical_object() {
        var a = Map.of("b", 2, "a", 1);
        var b = Map.of("a", 1, "b", 2);

        String h1 = ContextHasher.sha256Hex(a);
        String h2 = ContextHasher.sha256Hex(b);

        assertThat(h1).isEqualTo(h2); // sorted props -> same hash
    }
}
