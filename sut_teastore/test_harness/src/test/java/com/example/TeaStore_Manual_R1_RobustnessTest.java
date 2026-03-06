package com.example;

import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;

/**
 * Hand-crafted HTTP robustness tests for TeaStore WebUI.
 * Validates the full pipeline with real HTTP calls.
 * Oracle: no 5xx response, system stays reachable after each test.
 */
public class TeaStore_Manual_R1_RobustnessTest extends TeaStoreBaseTest {

    @Test
    void test_R1_homepage_accessible() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/");
        assertNoServerError(r);
    }

    @Test
    void test_R1_category_negative_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category?id=-1");
        assertNoServerError(r);
    }

    @Test
    void test_R1_category_zero_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category?id=0");
        assertNoServerError(r);
    }

    @Test
    void test_R1_category_string_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category?id=abc");
        assertNoServerError(r);
    }

    @Test
    void test_R1_category_missing_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category");
        assertNoServerError(r);
    }

    @Test
    void test_R1_category_very_large_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/category?id=999999999");
        assertNoServerError(r);
    }

    @Test
    void test_R1_product_negative_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/product?id=-1");
        assertNoServerError(r);
    }

    @Test
    void test_R1_product_string_id() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/product?id=notanid");
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_empty_credentials() throws Exception {
        HttpResponse<String> r = post(
            "/tools.descartes.teastore.webui/loginAction",
            "username=&password="
        );
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_null_like_username() throws Exception {
        HttpResponse<String> r = post(
            "/tools.descartes.teastore.webui/loginAction",
            "username=null&password=null"
        );
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_sql_injection() throws Exception {
        HttpResponse<String> r = post(
            "/tools.descartes.teastore.webui/loginAction",
            "username=admin%27+OR+%271%27%3D%271&password=x"
        );
        assertNoServerError(r);
    }

    @Test
    void test_R1_login_very_long_input() throws Exception {
        String longVal = "a".repeat(5000);
        HttpResponse<String> r = post(
            "/tools.descartes.teastore.webui/loginAction",
            "username=" + longVal + "&password=" + longVal
        );
        assertNoServerError(r);
    }

    @Test
    void test_R1_cart_add_nonexistent_product() throws Exception {
        HttpResponse<String> r = post(
            "/tools.descartes.teastore.webui/cartAction",
            "addToCart=&productid=999999"
        );
        assertNoServerError(r);
    }

    @Test
    void test_R1_cart_add_negative_product() throws Exception {
        HttpResponse<String> r = post(
            "/tools.descartes.teastore.webui/cartAction",
            "addToCart=&productid=-1"
        );
        assertNoServerError(r);
    }

    @Test
    void test_R1_nonexistent_endpoint() throws Exception {
        HttpResponse<String> r = get("/tools.descartes.teastore.webui/doesnotexist");
        assertNoServerError(r);
    }
}
