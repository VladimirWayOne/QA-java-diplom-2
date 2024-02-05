package step;

import client.UserClient;
import dto.CreateUserRequest;
import dto.LoginUserRequest;
import dto.UpdateUserRequest;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

public class UserSteps {
    private final UserClient userClient;
    public UserSteps(UserClient userClient) {
        this.userClient = userClient;
    }

    @Step("Корректное создание пользователя")
    public ValidatableResponse createUser(String email, String password, String name){
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();


        return userClient.createUser(createUserRequest)
                .then();
    }

    @Step("Создание пользователя без пароля")
    public ValidatableResponse createUserWithoutPassword(String email,String name) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .email(email)
                .name(name)
                .build();


        return userClient.createUser(createUserRequest)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(String email, String password){
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .email(email)
                .password(password)
                .build();

        return userClient.loginUser(loginUserRequest)
                .then();
    }

    @Step("Получение токена")
    public String getUserToken(String email, String password) {
        String token = loginUser(email, password)
                .extract()
                .path("accessToken");
        return token.substring(7);
    }
    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String email, String password) {
        String bearerToken = getUserToken(email, password);
        return userClient.deleteUser(bearerToken)
                .then();
    }

    @Step("Обновление имени пользователя с авторизацией")
    public ValidatableResponse updateName(String email, String password, String newName){
        String bearerToken = getUserToken(email, password);
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .name(newName).build();
        return userClient.updateUser(bearerToken, updateUserRequest)
                .then();
    }

    @Step("Обновление почты пользователя с авторизацией")
    public ValidatableResponse updateEmail(String email, String password, String newEmail){
        String bearerToken = getUserToken(email, password);
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .email(newEmail)
                .build();
        return userClient.updateUser(bearerToken, updateUserRequest)
                .then();
    }

    @Step("Обновление данных без авторизации")
    public ValidatableResponse updateDataWithoutAuth(String newEmail, String newName){
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .email(newEmail)
                .name(newName)
                .build();
        return userClient.updateUserWithoutAuth(updateUserRequest)
                .then();
    }

}
