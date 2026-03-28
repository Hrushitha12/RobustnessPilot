package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class OTelShop_ModelE_Self_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_GetProductsById_NonExistentId() throws Exception {
        HttpResponse<String> response = get("/api/products/nonexistent");
        assertNoServerError(response);
    }

    @Test
    public void test_R2_GetProductsById_InvalidFormat() throws Exception {
        HttpResponse<String> response = get("/api/products/123abc");
        assertNoServerError(response);
    }

    @Test
    public void test_R3_GetProductsById_EmptyId() throws Exception {
        HttpResponse<String> response = get("/api/products/");
        assertNoServerError(response);
    }

    @Test
    public void test_R4_GetCart_MissingSessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R5_GetCart_EmptySessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R6_DeleteCart_MissingSessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R7_DeleteCart_EmptySessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R8_AddToCart_EmptyUserId() throws Exception {
        String jsonBody = "{\"userId\":\"\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R9_AddToCart_EmptyProductId() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"item\":{\"productId\":\"\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R10_AddToCart_NullItem() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"item\":null}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R11_AddToCart_MissingFields() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\"}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R12_AddToCart_ZeroQuantity() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":0}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R13_AddToCart_NegativeQuantity() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":-1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R14_AddToCart_VeryLargeQuantity() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1000000}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R15_AddToCart_NonExistentProductId() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"item\":{\"productId\":\"nonexistent\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R16_Checkout_EmptyUserId() throws Exception {
        String jsonBody = "{\"userId\":\"\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"123456789\",\"creditCardCvv\":123,\"creditCardExpirationYear\":2025,\"creditCardExpirationMonth\":12}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R17_Checkout_InvalidCurrencyCode() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"userCurrency\":\"nonexistent\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"123456789\",\"creditCardCvv\":123,\"creditCardExpirationYear\":2025,\"creditCardExpirationMonth\":12}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R18_Checkout_EmptyCreditCardNumber() throws Exception {
        String jsonBody = "{\"userId\":\"testUser\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"\",\"creditCardCvv\":123,\"creditCardExpirationYear\":2025,\"creditCardExpirationMonth\":12}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }
}