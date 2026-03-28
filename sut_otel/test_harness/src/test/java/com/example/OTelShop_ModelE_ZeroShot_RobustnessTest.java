package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class OTelShop_ModelE_ZeroShot_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_getProducts() throws Exception {
        HttpResponse<String> response = get("/api/products");
        assertNoServerError(response);
    }

    @Test
    public void test_R2_getProductById() throws Exception {
        HttpResponse<String> response = get("/api/products/OLJCESPC7Z");
        assertNoServerError(response);
    }

    @Test
    public void test_R3_getProductById_InvalidId() throws Exception {
        HttpResponse<String> response = get("/api/products/INVALID_ID");
        assertNoServerError(response);
    }

    @Test
    public void test_R4_getCart() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=SESSION_ID");
        assertNoServerError(response);
    }

    @Test
    public void test_R5_getCart_InvalidSessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=INVALID_SESSION_ID");
        assertNoServerError(response);
    }

    @Test
    public void test_R6_addItemToCart() throws Exception {
        String jsonBody = "{\"userId\":\"USER_ID\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R7_addItemToCart_InvalidQuantity() throws Exception {
        String jsonBody = "{\"userId\":\"USER_ID\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":-1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R8_emptyCart() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=SESSION_ID");
        assertNoServerError(response);
    }

    @Test
    public void test_R9_emptyCart_InvalidSessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=INVALID_SESSION_ID");
        assertNoServerError(response);
    }

    @Test
    public void test_R10_placeOrder() throws Exception {
        String jsonBody = "{\"userId\":\"USER_ID\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"STREET_ADDRESS\",\"state\":\"STATE\",\"country\":\"COUNTRY\",\"city\":\"CITY\",\"zipCode\":\"ZIP_CODE\"},\"email\":\"EMAIL\",\"creditCard\":{\"creditCardNumber\":\"CREDIT_CARD_NUMBER\",\"creditCardCvv\":123,\"creditCardExpirationYear\":2025,\"creditCardExpirationMonth\":12}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R11_placeOrder_InvalidCreditCard() throws Exception {
        String jsonBody = "{\"userId\":\"USER_ID\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"STREET_ADDRESS\",\"state\":\"STATE\",\"country\":\"COUNTRY\",\"city\":\"CITY\",\"zipCode\":\"ZIP_CODE\"},\"email\":\"EMAIL\",\"creditCard\":{\"creditCardNumber\":\"INVALID_CREDIT_CARD_NUMBER\",\"creditCardCvv\":123,\"creditCardExpirationYear\":2025,\"creditCardExpirationMonth\":12}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R12_convertCurrency() throws Exception {
        String jsonBody = "{\"from\":{\"currencyCode\":\"USD\",\"units\":100,\"nanos\":0},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R13_convertCurrency_InvalidFromCurrency() throws Exception {
        String jsonBody = "{\"from\":{\"currencyCode\":\"INVALID_FROM_CURRENCY\",\"units\":100,\"nanos\":0},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R14_calculateShippingCost() throws Exception {
        String jsonBody = "{\"address\":{\"streetAddress\":\"STREET_ADDRESS\",\"state\":\"STATE\",\"country\":\"COUNTRY\",\"city\":\"CITY\",\"zipCode\":\"ZIP_CODE\"},\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}]}";
        HttpResponse<String> response = postJson("/api/shipping", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R15_calculateShippingCost_InvalidAddress() throws Exception {
        String jsonBody = "{\"address\":{\"streetAddress\":\"INVALID_STREET_ADDRESS\",\"state\":\"STATE\",\"country\":\"COUNTRY\",\"city\":\"CITY\",\"zipCode\":\"ZIP_CODE\"},\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}]}";
        HttpResponse<String> response = postJson("/api/shipping", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R16_getRecommendations() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=OLJCESPC7Z&sessionId=SESSION_ID&currencyCode=USD");
        assertNoServerError(response);
    }

    @Test
    public void test_R17_getRecommendations_InvalidProductIds() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=INVALID_PRODUCT_IDS&sessionId=SESSION_ID&currencyCode=USD");
        assertNoServerError(response);
    }
}