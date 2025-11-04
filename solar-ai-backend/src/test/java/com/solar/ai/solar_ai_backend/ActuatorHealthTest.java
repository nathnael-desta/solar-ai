package com.solar.ai.solar_ai_backend;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorHealthTest {

    @LocalServerPort
    int port;

    TestRestTemplate rest = new TestRestTemplate();

    @Test
    void actuatorHealth_isUp() {
        ResponseEntity<String> res = rest.getForEntity("http://localhost:" + port + "/actuator/health", String.class);
        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(res.getBody()).contains("\"status\":\"UP\"");
    }
}