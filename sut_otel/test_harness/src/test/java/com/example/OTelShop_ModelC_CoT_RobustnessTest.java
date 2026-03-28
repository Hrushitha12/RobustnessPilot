package com.example;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

public class OTelShop_ModelC_CoT_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_ProductNonExistentId() throws Exception {
        HttpResponse<String> response = get("/api/products/12345");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductNumericId() throws Exception {
        HttpResponse<String> response = get("/api/products/12345");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductEmptyPath() throws Exception {
        HttpResponse<String> response = get("/api/products/");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductWhitespacePath() throws Exception {
        HttpResponse<String> response = get("/api/products/   ");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductVeryLongId() throws Exception {
        String longId = "A".repeat(500);
        HttpResponse<String> response = get("/api/products/" + longId);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartGetEmptySessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartGetMissingSessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartGetVeryLongSessionId() throws Exception {
        String longId = "A".repeat(500);
        HttpResponse<String> response = get("/api/cart?sessionId=" + longId);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartDeleteEmptySessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartDeleteMissingSessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartDeleteVeryLongSessionId() throws Exception {
        String longId = "A".repeat(500);
        HttpResponse<String> response = delete("/api/cart?sessionId=" + longId);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostMissingUserId() throws Exception {
        String body = "{\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostEmptyUserId() throws Exception {
        String body = "{\"userId\":\"\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostMissingItem() throws Exception {
        String body = "{\"userId\":\"user123\"}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostEmptyProductId() throws Exception {
        String body = "{\"userId\":\"user123\",\"item\":{\"productId\":\"\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostQuantityZero() throws Exception {
        String body = "{\"userId\":\"user123\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":0}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostQuantityNegative() throws Exception {
        String body = "{\"userId\":\"user123\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":-1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostQuantityMaxInt() throws Exception {
        String body = "{\"userId\":\"user123\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":2147483647}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }
}

