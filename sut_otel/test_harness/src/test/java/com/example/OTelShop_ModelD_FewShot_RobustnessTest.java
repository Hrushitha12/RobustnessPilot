package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class OTelShop_ModelD_FewShot_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_ProductInvalidId() throws Exception {
        HttpResponse<String> response = get("/api/products/INVALIDID");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartNonExistentSessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=DOESNOTEXIST999");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartAddZeroQuantity() throws Exception {
        String body = "{\"userId\":\"user-1\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":0}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutInvalidEmail() throws Exception {
        String body = "{\"userId\":\"user-1\",\"userCurrency\":\"USD\","
            + "\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
            + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
            + "\"email\":\"invalid-email\","
            + "\"creditCard\":{\"creditCardNumber\":\"4111-1111-1111-1111\","
            + "\"creditCardCvv\":123,\"creditCardExpirationYear\":2030,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CurrencyLargeUnits() throws Exception {
        String body = "{\"from\":{\"currencyCode\":\"USD\",\"units\":999999999,\"nanos\":0},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ShippingInvalidCountry() throws Exception {
        String body = "{\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
            + "\"country\":\"UNKNOWN\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}]}";
        HttpResponse<String> response = postJson("/api/shipping", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_RecommendationsNonExistentProductId() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=DOESNOTEXIST999&sessionId=session-1&currencyCode=USD");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductsEmptyPath() throws Exception {
        HttpResponse<String> response = get("");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartAddMissingUserId() throws Exception {
        String body = "{\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutNegativeAmount() throws Exception {
        String body = "{\"userId\":\"user-1\",\"userCurrency\":\"USD\","
            + "\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
            + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
            + "\"email\":\"test@example.com\","
            + "\"creditCard\":{\"creditCardNumber\":\"4111-1111-1111-1111\","
            + "\"creditCardCvv\":123,\"creditCardExpirationYear\":2030,\"creditCardExpirationMonth\":-1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CurrencyLargeNanos() throws Exception {
        String body = "{\"from\":{\"currencyCode\":\"USD\",\"units\":10,\"nanos\":999999999},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ShippingMissingStreetAddress() throws Exception {
        String body = "{\"address\":{\"state\":\"CA\","
            + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}]}";
        HttpResponse<String> response = postJson("/api/shipping", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_RecommendationsNegativeQuantity() throws Exception {
        String body = "{\"productIds\":\"OLJCESPC7Z\",\"sessionId\":\"session-1\",\"currencyCode\":\"USD\",\"quantity\":-1}";
        HttpResponse<String> response = postJson("/api/recommendations", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_DeleteCartNonExistentSessionId() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=DOESNOTEXIST999");
        assertNoServerError(response);
    }
}