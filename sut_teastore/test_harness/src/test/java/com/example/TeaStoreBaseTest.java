package com.example;

import org.junit.jupiter.api.BeforeEach;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for all TeaStore HTTP robustness tests.
 *
 * Robustness oracle:
 *   1. No 5xx response from the system under test.
 *   2. System remains reachable after the test (canary run by pipeline).
 *
 * LLM-generated tests extend this class and use:
 *   get(path)                  -- HTTP GET
 *   post(path, body)           -- HTTP POST with form-encoded body
 *   assertNoServerError(resp)  -- applies the robustness oracle
 */
public abstract class TeaStoreBaseTest {

    protected static final String BASE_URL = "http://localhost:8080";
    protected static final int TIMEOUT_MS = 5000;
    protected HttpClient client;

    @BeforeEach
    void initClient() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(TIMEOUT_MS))
                .build();
    }

    protected HttpResponse<String> get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofMillis(TIMEOUT_MS))
                .GET()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> post(String path, String body) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofMillis(TIMEOUT_MS))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Robustness oracle: system must not return 5xx.
     * 4xx is acceptable -- bad input handled gracefully.
     */
    protected void assertNoServerError(HttpResponse<?> response) {
        assertTrue(response.statusCode() < 500,
                "Server returned 5xx for robustness input: " + response.statusCode());
    }
}
