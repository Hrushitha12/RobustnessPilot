package com.example;

import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;

public class OTelShop_ModelC_FewShot_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_ProductInvalidId() throws Exception {
        HttpResponse<String> response = get("/api/products/INVALID-ID-123!");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartGetInvalidSessionId() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=sessionId!@#");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostZeroQuantity() throws Exception {
        String body = "{\"userId\":\"user-1\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":0}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CartPostMissingProductId() throws Exception {
        String body = "{\"userId\":\"user-1\",\"item\":{\"productId\":\"\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutInvalidCreditCardNumber() throws Exception {
        String body = "{\"userId\":\"user-1\",\"userCurrency\":\"USD\","
            + "\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
            + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
            + "\"email\":\"test@example.com\","
            + "\"creditCard\":{\"creditCardNumber\":\"4111-1111-1111-1111A\","
            + "\"creditCardCvv\":123,\"creditCardExpirationYear\":2030,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutMissingAddressField() throws Exception {
        String body = "{\"userId\":\"user-1\",\"userCurrency\":\"USD\","
            + "\"address\":{\"streetAddress\":\"\",\"state\":\"CA\","
            + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
            + "\"email\":\"test@example.com\","
            + "\"creditCard\":{\"creditCardNumber\":\"4111-1111-1111-1111\","
            + "\"creditCardCvv\":123,\"creditCardExpirationYear\":2030,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CurrencyNegativeUnits() throws Exception {
        String body = "{\"from\":{\"currencyCode\":\"USD\",\"units\":-5,\"nanos\":0},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CurrencyMissingFromCode() throws Exception {
        String body = "{\"from\":{\"currencyCode\":\"\",\"units\":10,\"nanos\":0},\"toCode\":\"USD\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ShippingInvalidZipCode() throws Exception {
        String body = "{\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
            + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"ABCD\"},"
            + "\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}]}";
        HttpResponse<String> response = postJson("/api/shipping", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ShippingEmptyAddressFields() throws Exception {
        String body = "{\"address\":{\"streetAddress\":\"\",\"state\":\"\",\""
            + "country\":\"\",\"city\":\"\",\"zipCode\":\"\"},"
            + "\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}]}";
        HttpResponse<String> response = postJson("/api/shipping", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_RecommendationsInvalidProductIds() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=INVALID-123&sessionId=test&currencyCode=USD");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_RecommendationsEmptySessionId() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=OLJCESPC7Z&sessionId=&currencyCode=USD");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_RecommendationsInvalidCurrencyCode() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=OLJCESPC7Z&sessionId=test&currencyCode=XYZ");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_AdsInvalidContextKeys() throws Exception {
        HttpResponse<String> response = get("/api/ads?contextKeys=invalid,key");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CheckoutEmptyUserCurrency() throws Exception {
        String body = "{\"userId\":\"user-1\",\"userCurrency\":\"\","
            + "\"address\":{\"streetAddress\":\"1 Main St\",\"state\":\"CA\","
            + "\"country\":\"US\",\"city\":\"Springfield\",\"zipCode\":\"12345\"},"
            + "\"email\":\"test@example.com\","
            + "\"creditCard\":{\"creditCardNumber\":\"4111-1111-1111-1111\","
            + "\"creditCardCvv\":123,\"creditCardExpirationYear\":2030,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }
}

