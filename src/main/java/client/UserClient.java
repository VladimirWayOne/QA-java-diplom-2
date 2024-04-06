package client;

import dto.CreateUserRequest;
import dto.LoginUserRequest;
import dto.UpdateUserRequest;
import io.restassured.response.Response;

public class UserClient extends RestClient {

    public Response createUser(CreateUserRequest createUserRequest) {
        return getDefaultRequestSpecification()
                .body(createUserRequest)//.log().all()
                .when()
                .post("/auth/register");

    }

    public Response loginUser(LoginUserRequest loginUserRequest) {
        return getDefaultRequestSpecification()
                .body(loginUserRequest)
                .when()
                .post("/auth/login");
    }

    public Response deleteUser(String bearerToken) {
        return getDefaultRequestSpecification()
                .header("authorization", bearerToken)
                .when()
                .delete("/auth/user");
    }

    public Response updateUser(String bearerToken, UpdateUserRequest updateUserRequest) {
        return getDefaultRequestSpecification()
                .header("authorization", bearerToken)
                .body(updateUserRequest)//.log().all()
                .when()
                .patch("/auth/user");
    }

    public Response updateUserWithoutAuth(UpdateUserRequest updateUserRequest) {
        return getDefaultRequestSpecification()
                .body(updateUserRequest)//.log().all()
                .when()
                .patch("/auth/user");
    }
}
