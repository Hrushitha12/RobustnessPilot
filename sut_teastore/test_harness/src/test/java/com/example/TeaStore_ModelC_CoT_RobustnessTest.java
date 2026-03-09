package com.example;

import org.junit.jupiter.api.Test;
import java.net.http.HttpResponse;

public class TeaStore_ModelC_CoT_RobustnessTest extends TeaStoreBaseTest {

    @Test
    void test_R1_1() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=-5");
        assertNoServerError(response);
    }

    @Test
    void test_R1_2() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=0");
        assertNoServerError(response);
    }

    @Test
    void test_R1_3() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=abc");
        assertNoServerError(response);
    }

    @Test
    void test_R1_4() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=2147483647");
        assertNoServerError(response);
    }

    @Test
    void test_R1_5() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=abc");
        assertNoServerError(response);
    }

    @Test
    void test_R1_6() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=-5");
        assertNoServerError(response);
    }

    @Test
    void test_R1_7() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=&password=secret");
        assertNoServerError(response);
    }

    @Test
    void test_R1_8() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=alice&password=");
        assertNoServerError(response);
    }

    @Test
    void test_R1_9() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=' OR '1'='1&password=secret");
        assertNoServerError(response);
    }

    @Test
    void test_R1_10() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=alice&password=' OR '1'='1");
        assertNoServerError(response);
    }

    @Test
    void test_R1_11() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "addToCart=&productid=");
        assertNoServerError(response);
    }

    @Test
    void test_R1_12() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "addToCart=&productid=abc");
        assertNoServerError(response);
    }

    @Test
    void test_R1_13() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "removeProduct=&productid=1");
        assertNoServerError(response);
    }

    @Test
    void test_R1_14() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "removeProduct=&productid=-1");
        assertNoServerError(response);
    }

    @Test
    void test_R1_15() {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/order", "");
        assertNoServerError(response);
    }

    @Test
    void test_R1_16() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/profile");
        assertNoServerError(response);
    }

    @Test
    void test_R1_17() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/cart");
        assertNoServerError(response);
    }

    @Test
    void test_R1_18() {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/unknown");
        assertNoServerError(response);
    }
}
