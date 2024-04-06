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
                .header("authorization", accessToken)
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

}
