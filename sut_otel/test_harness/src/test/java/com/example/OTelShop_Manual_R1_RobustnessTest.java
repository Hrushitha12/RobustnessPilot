package com.example;

import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;

/**
 * OTelShop_Manual_R1_RobustnessTest
 *
 * Manually written robustness tests for the OpenTelemetry Astronomy Shop.
 * These are the seed tests — written by a human tester without LLM assistance.
 * They serve as the baseline for comparison against LLM-generated test suites.
 *
 * Coverage rationale:
 *   Each test targets one endpoint with one violation type.
 *   Violations are chosen to represent what a competent tester would naturally
 *   include when testing from the API documentation alone — without reading
 *   the source code of any backend service.
 *
 * Oracle: HTTP response < 500 = PASS (system handled input gracefully).
 *         HTTP response >= 500 = FAIL (robustness failure).
 */
public class OTelShop_Manual_R1_RobustnessTest extends OTelShopBaseTest {

    // ── PRODUCT CATALOGUE ────────────────────────────────────────────────────

    @Test
    public void test_R1_ProductNonExistentId() throws Exception {
        // Non-existent but correctly formatted product ID
        HttpResponse<String> response = get("/api/products/DOESNOTEXIST999");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductNumericId() throws Exception {
        // Numeric string where product codes are expected (e.g., "12345")
        HttpResponse<String> response = get("/api/products/12345");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductEmptyPathSegment() throws Exception {
        // Empty path segment after /api/products/
        HttpResponse<String> response = get("/api/products/");
        assertNoServerError(response);
    }

    // ── CART (GET / DELETE) ──────────────────────────────────────────────────

    @Test
    public void test_R1_CartGetEmptySessionId() throws Exception {
        // sessionId present but empty string
        HttpResponse<String> response = get("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartGetMissingSessionId() throws Exception {
        // sessionId query parameter absent entirely
        HttpResponse<String> response = get("/api/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartDeleteEmptySessionId() throws Exception {
        // DELETE with empty sessionId
        HttpResponse<String> response = delete("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    // ── CART (POST) ──────────────────────────────────────────────────────────

    @Test
    public void test_R1_CartAddNegativeQuantity() throws Exception {
        // Negative quantity — natural boundary test for any add-to-cart operation
        String body = "{\"userId\":\"manual-test-user\","
                + "\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":-1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartAddZeroQuantity() throws Exception {
        // Zero quantity
        String body = "{\"userId\":\"manual-test-user\","
                + "\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":0}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartAddNonExistentProductId() throws Exception {
        // Valid quantity, but product does not exist
        String body = "{\"userId\":\"manual-test-user\","
                + "\"item\":{\"productId\":\"DOESNOTEXIST999\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartAddEmptyUserId() throws Exception {
        // userId is empty string
        String body = "{\"userId\":\"\","
                + "\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    // ── CHECKOUT ─────────────────────────────────────────────────────────────

    @Test
    public void test_R1_CheckoutInvalidCurrencyCode() throws Exception {
        // Unknown currency code — standard boundary for currency fields
        String body = "{\"userId\":\"manual-test-user\","
                + "\"userCurrency\":\"ZZZ\","
                + "\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
                + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
                + "\"email\":\"test@example.com\","
                + "\"creditCard\":{\"creditCardNumber\":\"4111-1111-1111-1111\","
                + "\"creditCardCvv\":123,\"creditCardExpirationYear\":2030,"
                + "\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutExpiredCreditCard() throws Exception {
        // Expiration year in the past
        String body = "{\"userId\":\"manual-test-user\","
                + "\"userCurrency\":\"USD\","
                + "\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
                + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
                + "\"email\":\"test@example.com\","
                + "\"creditCard\":{\"creditCardNumber\":\"4111-1111-1111-1111\","
                + "\"creditCardCvv\":123,\"creditCardExpirationYear\":2000,"
                + "\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    // ── CURRENCY ─────────────────────────────────────────────────────────────

    @Test
    public void test_R1_CurrencyUnknownFromCode() throws Exception {
        // Unknown source currency code
        String body = "{\"from\":{\"currencyCode\":\"ZZZ\",\"units\":10,\"nanos\":0},"
                + "\"toCode\":\"USD\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CurrencyUnknownToCode() throws Exception {
        // Unknown target currency code
        String body = "{\"from\":{\"currencyCode\":\"USD\",\"units\":10,\"nanos\":0},"
                + "\"toCode\":\"ZZZ\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    // ── SHIPPING ─────────────────────────────────────────────────────────────

    @Test
    public void test_R1_ShippingEmptyItemsList() throws Exception {
        // Empty items array — no products in the shipment
        String body = "{\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
                + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
                + "\"items\":[]}";
        HttpResponse<String> response = postJson("/api/shipping", body);
        assertNoServerError(response);
    }

    // ── RECOMMENDATIONS ──────────────────────────────────────────────────────

    @Test
    public void test_R1_RecommendationsNonExistentProductId() throws Exception {
        // Non-existent productId in recommendations query
        HttpResponse<String> response = get(
                "/api/recommendations?productIds=DOESNOTEXIST999"
                        + "&sessionId=manual-session&currencyCode=USD");
        assertNoServerError(response);
    }
}
