package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class OTelShop_ModelE_Structured_RobustnessTest extends OTelShopBaseTest {

    @Test
    public void test_R1_GetProduct_NonExistentId() throws Exception {
        HttpResponse<String> response = get("/api/products/DOESNOTEXIST999");
        assertNoServerError(response);
    }

    @Test
    public void test_R2_GetProduct_EmptyStringSegmentInPath() throws Exception {
        HttpResponse<String> response = get("/api/products/");
        assertNoServerError(response);
    }

    @Test
    public void test_R3_GetProduct_NumericStringAsIdWrongType() throws Exception {
        HttpResponse<String> response = get("/api/products/12345");
        assertNoServerError(response);
    }

    @Test
    public void test_R4_GetProduct_ExtremelyLongIdString1000Chars() throws Exception {
        StringBuilder idBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            idBuilder.append('a');
        }
        HttpResponse<String> response = get("/api/products/" + idBuilder.toString());
        assertNoServerError(response);
    }

    @Test
    public void test_R5_GetCart_EmptySessionIdQueryParam() throws Exception {
        HttpResponse<String> response = get("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R6_GetCart_SessionIdMissingEntirelyFromQuery() throws Exception {
        HttpResponse<String> response = get("/api/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R7_DeleteCart_EmptySessionIdQueryParam() throws Exception {
        HttpResponse<String> response = delete("/api/cart?sessionId=");
        assertNoServerError(response);
    }

    @Test
    public void test_R8_PostCart_UserIdFieldIsEmptyString() throws Exception {
        String jsonBody = "{\"userId\":\"\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R9_PostCart_ProductIdFieldIsEmptyString() throws Exception {
        String jsonBody = "{\"userId\":\"test-user\",\"item\":{\"productId\":\"\",\"quantity\":1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R10_PostCart_QuantityIsZero() throws Exception {
        String jsonBody = "{\"userId\":\"test-user\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":0}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R11_PostCart_QuantityIsNegativeInteger() throws Exception {
        String jsonBody = "{\"userId\":\"test-user\",\"item\":{\"productId\":\"OLJCESPC7Z\",\"quantity\":-1}}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R12_PostCart_ItemFieldIsNull() throws Exception {
        String jsonBody = "{\"userId\":\"test-user\",\"item\":null}";
        HttpResponse<String> response = postJson("/api/cart", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R13_PostCheckout_EmptyUserId() throws Exception {
        String jsonBody = "{\"userId\":\"\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2022,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R14_PostCheckout_InvalidNonExistentCurrencyCode() throws Exception {
        String jsonBody = "{\"userId\":\"test-user\",\"userCurrency\":\"ZZZ\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2022,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R15_PostCheckout_CreditCardNumberIsEmptyString() throws Exception {
        String jsonBody = "{\"userId\":\"test-user\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2022,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R16_PostCheckout_CreditCardExpirationYearIsInThePast() throws Exception {
        String jsonBody = "{\"userId\":\"test-user\",\"userCurrency\":\"USD\",\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"email\":\"\",\"creditCard\":{\"creditCardNumber\":\"\",\"creditCardCvv\":0,\"creditCardExpirationYear\":2000,\"creditCardExpirationMonth\":1}}";
        HttpResponse<String> response = postJson("/api/checkout", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R17_PostCurrency_UnknownFromCodeCurrency() throws Exception {
        String jsonBody = "{\"from\":{\"currencyCode\":\"ZZZ\",\"units\":10,\"nanos\":0},\"toCode\":\"USD\"}";
        HttpResponse<String> response = postJson("/api/currency", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R18_PostCurrency_UnknownToCodeCurrency() throws Exception {
        String jsonBody = "{\"from\":{\"currencyCode\":\"USD\",\"units\":10,\"nanos\":0},\"toCode\":\"ZZZ\"}";
        HttpResponse<String> response = postJson("/api/currency", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R19_PostCurrency_UnitsFieldIsNegative() throws Exception {
        String jsonBody = "{\"from\":{\"currencyCode\":\"USD\",\"units\":-1,\"nanos\":0},\"toCode\":\"EUR\"}";
        HttpResponse<String> response = postJson("/api/currency", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R20_PostShipping_AllAddressFieldsEmptyStrings() throws Exception {
        String jsonBody = "{\"address\":{\"streetAddress\":\"\",\"state\":\"\",\"country\":\"\",\"city\":\"\",\"zipCode\":\"\"},\"items\":[{\"productId\":\"OLJCESPC7Z\",\"quantity\":1}]}";
        HttpResponse<String> response = postJson("/api/shipping", jsonBody);
        assertNoServerError(response);
    }

    @Test
    public void test_R21_GetRecommendations_NonExistentProductIdsValue() throws Exception {
        HttpResponse<String> response = get("/api/recommendations?productIds=FAKE999&sessionId=s1&currencyCode=USD");
        assertNoServerError(response);
    }
}