package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

class TeaStore_ModelC_Self_RobustnessTest extends TeaStoreBaseTest {

    @Test
    void test_R1_GetRoot() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/");
        assertNoServerError(response);
    }

    @Test
    void test_R1_GetCategoryValidId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=1");
        assertNoServerError(response);
    }

    @Test
    void test_R1_GetCategoryInvalidId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=abc");
        assertNoServerError(response);
    }

    @Test
    void test_R1_GetProductValidId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=1");
        assertNoServerError(response);
    }

    @Test
    void test_R1_GetProductInvalidId() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=abc");
        assertNoServerError(response);
    }

    @Test
    void test_R1_GetCart() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/cart");
        assertNoServerError(response);
    }

    @Test
    void test_R1_GetProfile() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/profile");
        assertNoServerError(response);
    }

    @Test
    void test_R1_LoginActionValid() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=admin&password=1234");
        assertNoServerError(response);
    }

    @Test
    void test_R1_LoginActionEmptyUsername() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=&password=1234");
        assertNoServerError(response);
    }

    @Test
    void test_R1_LoginActionEmptyPassword() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=admin&password=");
        assertNoServerError(response);
    }

    @Test
    void test_R1_LoginActionInvalidCredentials() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=invalid&password=invalid");
        assertNoServerError(response);
    }

    @Test
    void test_R1_CartActionAddToCartValid() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "addToCart=&productid=1");
        assertNoServerError(response);
    }

    @Test
    void test_R1_CartActionAddToCartInvalid() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "addToCart=&productid=abc");
        assertNoServerError(response);
    }

    @Test
    void test_R1_CartActionRemoveProductValid() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "removeProduct=&productid=1");
        assertNoServerError(response);
    }

    @Test
    void test_R1_CartActionRemoveProductInvalid() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "removeProduct=&productid=abc");
        assertNoServerError(response);
    }

    @Test
    void test_R1_OrderConfirmValid() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/order", "confirm=");
        assertNoServerError(response);
    }

    @Test
    void test_R1_OrderConfirmInvalid() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/order", "confirm=invalid");
        assertNoServerError(response);
    }
}

