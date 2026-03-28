package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class OTelShop_ModelD_ZeroShot_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_GetAllProducts() throws Exception {
        HttpResponse<String> response = get("/api/products");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_GetProductByIdValidId() throws Exception {
        HttpResponse<String> response = get("/api/products/OLJCESPC7Z");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_GetProductByIdInvalidId() throws Exception {
        HttpResponse<String> response = get("/api/products/INVALIDID");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_GetCartWithValidSessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=validsessionid");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_GetCartWithInvalidSessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=invalidsessionid");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_AddItemToCartValidRequest() throws Exception {
        String jsonBody = "{\"userId\":\"user1\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":2}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_AddItemToCartMissingUserId() throws Exception {
        String jsonBody = "{\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":2}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_AddItemToCartMissingProductDetails() throws Exception {
        String jsonBody = "{\"userId\":\"user1\"}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyCartWithValidSessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=validsessionid");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyCartWithInvalidSessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=invalidsessionid");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutValidRequest() throws Exception {
        String jsonBody = "{\"userId\":\"user1\",\"userCurrency\":\"USD\"," +
                          "\"address\":{\"streetAddress\":\"123 Main St\",\"state\":\"NY\",\"country\":\"USA\",\"city\":\"New York\",\"zipCode\":\"10001\"}," +
                          "\"email\":\"user@example.com\"," +
                          "\"creditCard\":{\"creditCardNumber\":\"4111111111111111\",\"creditCardCvv\":123,\"creditCardExpirationYear\":2025,\"creditCardExpirationMonth\":12}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutMissingCreditCard() throws Exception {
        String jsonBody = "{\"userId\":\"user1\",\"userCurrency\":\"USD\"," +
                          "\"address\":{\"streetAddress\":\"123 Main St\",\"state\":\"NY\",\"country\":\"USA\",\"city\":\"New York\",\"zipCode\":\"10001\"}," +
                          "\"email\":\"user@example.com\"}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CurrencyConversionValidRequest() throws Exception {
        String jsonBody = "{\"from\":{\"currencyCode\":\"USD\",\"units\":10,\"nanos\":0},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CurrencyConversionMissingFromField() throws Exception {
        String jsonBody = "{\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ShippingCostValidRequest() throws Exception {
        String jsonBody = "{\"address\":{\"streetAddress\":\"123 Main St\",\"state\":\"NY\",\"country\":\"USA\",\"city\":\"New York\",\"zipCode\":\"10001\"}," +
                          "\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":2}]}";
        HttpResponse<String> response = postJson("/api/shipping", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ShippingCostMissingItems() throws Exception {
        String jsonBody = "{\"address\":{\"streetAddress\":\"123 Main St\",\"state\":\"NY\",\"country\":\"USA\",\"city\":\"New York\",\"zipCode\":\"10001\"}}";
        HttpResponse<String> response = postJson("/api/shipping", jsonBody);
        assertNoServerError(response);
    }
}