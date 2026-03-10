package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class TeaStore_ModelD_CoT_v2_RobustnessTest extends TeaStoreBaseTest {

    @Test
    public void test_R1_InvalidCategoryId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=-1");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_NonIntegerProductId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=abc");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyProductId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_MaxIntegerCategoryId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=2147483647");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_SqlInjectionInLogin() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=' OR '1'='1&password=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_XssAttackInProductId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=<script>alert('xss')</script>");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyCartActionBody() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_MissingProductidInCartAction() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "addToCart=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_UnauthenticatedProfileAccess() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/profile");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_UnauthenticatedCartAction() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "addToCart=&productid=1");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_EmptyOrderConfirmationBody() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/order", "");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_UnknownEndpoint() throws Exception {
        HttpResponse<String> response = get("/unknown-endpoint");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_OversizedStringInLogin() throws Exception {
        StringBuilder largeString = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            largeString.append("a");
        }
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=" + largeString.toString() + "&password=");
        assertNoServerError(response);
    }

    @Test
    public void test_R1_WhitespaceOnlyProductId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=   ");
        assertNoServerError(response);
    }
}