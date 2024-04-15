package client;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static defaults.RestConfig.BASE_URI;
import static io.restassured.RestAssured.given;

public abstract class RestClient {
    protected RequestSpecification getDefaultRequestSpecification() {
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON);
    }
}