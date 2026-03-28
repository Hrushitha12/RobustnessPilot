package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class OTelShop_ModelD_Structured_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_NonExistentProductId() throws Exception {
        HttpResponse<String> response = get("/api/products/DOESNOTEXIST999");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyStringSegmentInPath() throws Exception {
        HttpResponse<String> response = get("/api/products/");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_NumericStringAsId() throws Exception {
        HttpResponse<String> response = get("/api/products/12345");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ExtremelyLongIdString() throws Exception {
        StringBuilder longId = new StringBuilder("A".repeat(1000));
        HttpResponse<String> response = get("/api/products/" + longId.toString());
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptySessionIdQueryParam() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_SessionIdMissingEntirely() throws Exception {
        HttpResponse<String> response = get("/api/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptySessionIdInDelete() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_UserIdEmptyString() throws Exception {
        String body = "{\"userId\":\"\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ProductIdEmptyString() throws Exception {
        String body = "{\"userId\":\"test-user\",\"item\":{\"productId\":\"\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_QuantityZero() throws Exception {
        String body = "{\"userId\":\"test-user\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":0}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_QuantityNegativeInteger() throws Exception {
        String body = "{\"userId\":\"test-user\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":-1}}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_ItemFieldNull() throws Exception {
        String body = "{\"userId\":\"test-user\",\"item\":null}";
        HttpResponse<String> response = postJson("/api/cart", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyUserIdInCheckout() throws Exception {
        String body = "{\"userId\":\"\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2023,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_InvalidCurrencyCode() throws Exception {
        String body = "{\"userId\":\"test-user\",\"userCurrency\":\"ZZZ\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"4111111111111111\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2023,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyCreditCardNumber() throws Exception {
        String body = "{\"userId\":\"test-user\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2023,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_CreditCardExpired() throws Exception {
        String body = "{\"userId\":\"test-user\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"4111111111111111\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2000,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_UnknownFromCodeCurrency() throws Exception {
        String body = "{\"from\":{\"currencyCode\":\"ZZZ\",\"units\":10,\"nanos\":0},\"toCode\":\"USD\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_UnknownToCodeCurrency() throws Exception {
        String body = "{\"from\":{\"currencyCode\":\"USD\",\"units\":10,\"nanos\":0},\"toCode\":\"ZZZ\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_UnitsFieldNegative() throws Exception {
        String body = "{\"from\":{\"currencyCode\":\"USD\",\"units\":-1,\"nanos\":0},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_AllAddressFieldsEmptyStrings() throws Exception {
        String body = "{\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"items\":[]}";
        HttpResponse<String> response = postJson("/api/shipping", body);
        assertNoServerError(response);
    }

    @Test
    public void test_R1_NonExistentProductIds() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=FAKE999&sessionId=s1&currencyCode=USD");
        assertNoServerError(response);
    }
}