package com.example;

import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;

/**
 * CanaryTest
 *
 * Verifies that the OpenTelemetry Astronomy Shop is alive and the product
 * catalogue endpoint returns a healthy response.
 *
 * This test is run by the pipeline harness (run_suite.py) after each
 * robustness test to detect system-wide crashes (Catastrophic class in
 * CRASH taxonomy). If this test fails, all subsequent results in that
 * run are marked SYSTEM_UNAVAILABLE.
 *
 * Equivalent to CanaryTest.java used in the TeaStore SUT.
 *
 * Canary endpoint: GET /api/products
 *   - Always returns 200 with the full product list when the system is healthy.
 *   - Requires the frontend proxy, frontend service, and product-catalog
 *     service to all be running — making it a reliable system-health indicator.
 */
public class CanaryTest extends OTelShopBaseTest {

    @Test
    public void test_canary() throws Exception {
        HttpResponse<String> response = get("/api/products");
        assertNoServerError(response);
    }
}
