package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

class TeaStore_ModelD_fewshot_RobustnessTest extends TeaStoreBaseTest {

    @Test
    void test_R1_category_missing_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category");
        assertNoServerError(r);
    }

    @Test
    void test_R1_product_missing_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/product");
        assertNoServerError(r);
    }

    @Test
    void test_R1_category_large_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category?id=999999");
        assertNoServerError(r);
    }

    @Test
    void test_R1_product_large_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/product?id=999999");
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_missing_username() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/loginAction", "password=password");
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_missing_password() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/loginAction", "username=username");
        assertNoServerError(r);
    }

    @Test
    void test_R1_cart_add_invalid_param_name() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/cartAction", "addtocart=&productid=1");
        assertNoServerError(r);
    }

    @Test
    void test_R1_cart_remove_invalid_param_name() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/cartAction", "removeproduct=&productid=1");
        assertNoServerError(r);
    }

    @Test
    void test_R1_order_missing_confirm_param() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/order", "");
        assertNoServerError(r);
    }

    @Test
    void test_R1_category_non_numeric_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category?id=abc");
        assertNoServerError(r);
    }

    @Test
    void test_R1_product_non_numeric_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/product?id=xyz");
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_invalid_special_chars_username() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/loginAction", "username=<>&password=password");
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_invalid_special_chars_password() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/loginAction", "username=username&password=<>!");
        assertNoServerError(r);
    }

    @Test
    void test_R1_cart_add_missing_productid_param() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/cartAction", "addToCart=");
        assertNoServerError(r);
    }

    @Test
    void test_R1_cart_remove_missing_productid_param() throws Exception {
        HttpResponse<String> r = post("/tools.descartes.teastore.webui/cartAction", "removeProduct=");
        assertNoServerError(r);
    }
}