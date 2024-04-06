package client;

import dto.CreateOrderRequest;
import io.restassured.response.Response;

public class OrderClient extends RestClient {

    public Response getIngridients() {
        return getDefaultRequestSpecification()
                .when()
                .get("/ingredients");
    }

    public Response createOrderWithAuth(CreateOrderRequest createOrderRequest, String accessToken) {
        return getDefaultRequestSpecification()
                .header("Authorization", accessToken)
                .body(createOrderRequest)
                .when()
                .post("/orders");
    }

    public Response createOrderWithoutAuth(CreateOrderRequest createOrderRequest) {
        return getDefaultRequestSpecification()

                .body(createOrderRequest)
                .when()
                .post("/orders");
    }

    public Response getUserOrdersWithoutAuth() {
        // Получить заказы без авторизации
        return getDefaultRequestSpecification()
                .when()
                .get("/orders");
    }

    public Response getUserOrdersWithAuth(String accessToken) {
        // Получить заказы с авторизацией
        return getDefaultRequestSpecification()
                .header("authorization", accessToken)
                .when()
                .get("/orders");
    }

    public Response getAllOrders() {
        // Получить все заказы (регистрация не требуется)
        return getDefaultRequestSpecification()
                .when()
                .get("/orders/all");
    }

}
