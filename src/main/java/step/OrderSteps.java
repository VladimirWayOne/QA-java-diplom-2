package step;

import client.OrderClient;

import dto.CreateOrderRequest;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OrderSteps {

    private final OrderClient orderClient;
    public OrderSteps(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Step("Получить ингридиенты для заказа")
    public ValidatableResponse getIngridients(){
        return orderClient.getIngridients()
                .then();
    }

    @Step("Выбрать хэши N рандомных ингридиентов")
    public List<String> chooseIngridients(ValidatableResponse ingredientsResponse, int ingredientsCount) {
        Random random = new Random();
        List<String> ingredients = new ArrayList<>();
        ArrayList<HashMap<String, Object>> ingridientsInfo = ingredientsResponse.extract().path("data");
        for (int i = 0; i < ingredientsCount; i++) {
            ingredients.add((String) ingridientsInfo.get(random.nextInt(ingridientsInfo.size())).get("_id"));
        }

        return ingredients;

    }

    @Step("Создать заказ с авторизацией")
    public ValidatableResponse createOrderWithAuth(List<String> ingredientsList, String accessToken) {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .ingredients(ingredientsList)
                .build();
        return orderClient.createOrderWithAuth(createOrderRequest, accessToken).then();
    }

    @Step("Создать заказ без авторизации")
    public ValidatableResponse createOrderWithoutAuth(List<String> ingredientsList) {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .ingredients(ingredientsList)
                .build();
        return orderClient.createOrderWithoutAuth(createOrderRequest).then();
    }

}
