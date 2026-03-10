package com.example;

import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

public class TeaStore_ModelE_ZeroShot_v2_RobustnessTest extends TeaStoreBaseTest {

    @Test
    public void test_R1_GoodGet() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/");
        assertNoServerError(response);
    }

    @Test
    public void test_R2_EmptyPathGet() throws Exception {
        HttpResponse<String> response = get("");
        assertNoServerError(response);
    }

    @Test
    public void test_R3_NullPathGet() throws Exception {
        HttpResponse<String> response = get(null);
        assertNoServerError(response);
    }

    @Test
    public void test_R4_InvalidCategoryGet() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=abc");
        assertNoServerError(response);
    }

    @Test
    public void test_R5_GoodProductGet() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=1");
        assertNoServerError(response);
    }

    @Test
    public void test_R6_InvalidProductGet() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/product?id=-1");
        assertNoServerError(response);
    }

    @Test
    public void test_R7_CartGet() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/cart");
        assertNoServerError(response);
    }

    @Test
    public void test_R8_ProfileGet() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/profile");
        assertNoServerError(response);
    }

    @Test
    public void test_R9_LoginPostGoodCredentials() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=admin&password=password");
        assertNoServerError(response);
    }

    @Test
    public void test_R10_LoginPostBadCredentials() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=abc&password=def");
        assertNoServerError(response);
    }

    @Test
    public void test_R11_AddToCartPost() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "addToCart=&productid=1");
        assertNoServerError(response);
    }

    @Test
    public void test_R12_RemoveProductPost() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/cartAction", "removeProduct=&productid=1");
        assertNoServerError(response);
    }

    @Test
    public void test_R13_OrderPostConfirm() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/order", "confirm=");
        assertNoServerError(response);
    }

    @Test
    public void test_R14_GarbageInput() throws Exception {
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/" + System.nanoTime());
        assertNoServerError(response);
    }

    @Test
    public void test_R15_VeryLongPath() throws Exception {
        StringBuilder longPath = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longPath.append("a");
        }
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/" + longPath.toString());
        assertNoServerError(response);
    }

    @Test
    public void test_R16_VeryLongQueryParameter() throws Exception {
        StringBuilder longQueryParam = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longQueryParam.append("a");
        }
        HttpResponse<String> response = get("/tools.descartes.teastore.webui/category?id=" + longQueryParam.toString());
        assertNoServerError(response);
    }

    @Test
    public void test_R17_VeryLongPostBody() throws Exception {
        StringBuilder longPostBody = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longPostBody.append("a");
        }
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", "username=" + longPostBody.toString() + "&password=password");
        assertNoServerError(response);
    }

    @Test
    public void test_R18_NullPostBody() throws Exception {
        HttpResponse<String> response = post("/tools.descartes.teastore.webui/loginAction", null);
        assertNoServerError(response);
    }
}