package com.example;

import org.junit.jupiter.api.Assertions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * OTelShopBaseTest
 *
 * Base class for all OpenTelemetry Astronomy Shop robustness tests.
 * Provides get(), postJson(), and delete() methods that send HTTP requests
 * to the frontend proxy at http://localhost:8080.
 *
 * Equivalent to TeaStoreBaseTest.java used in the TeaStore SUT.
 *
 * Key differences from TeaStore:
 *   - POST bodies are JSON (Content-Type: application/json), not form-encoded
 *   - DELETE method is supported (cart emptying endpoint)
 *   - Base path is /api/... (no /tools.descartes.teastore.webui/ prefix)
 */
public class OTelShopBaseTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final Duration TIMEOUT  = Duration.ofSeconds(30);

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * Send a GET request to the given path.
     * Path should start with /api/... e.g. "/api/products"
     */
    protected HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(TIMEOUT)
                .GET()
                .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Send a POST request with a JSON body to the given path.
     * Automatically sets Content-Type: application/json.
     * Path should start with /api/... e.g. "/api/cart"
     */
    protected HttpResponse<String> postJson(String path, String jsonBody) throws Exception {
        String body = jsonBody == null ? "" : jsonBody;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Send a DELETE request to the given path.
     * Path should start with /api/... e.g. "/api/cart?sessionId=abc"
     */
    protected HttpResponse<String> delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(TIMEOUT)
                .DELETE()
                .build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Robustness oracle: fails the test if the response status code is >= 500.
     * A 2xx, 3xx, or 4xx response means the system handled the input gracefully.
     * A 5xx response is a robustness failure.
     */
    protected void assertNoServerError(HttpResponse<?> response) {
        int status = response.statusCode();
        Assertions.assertTrue(
                status < 500,
                "Robustness failure: expected HTTP status < 500 but got " + status
                        + ". Response body: " + response.body()
        );
    }
}
