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
    public ValidatableResponse getIngridients() {
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

    @Step("Получить список поледних 50 заказов")
    public ValidatableResponse getLast50Orders() {
        return orderClient.getAllOrders().then();
    }

    @Step("Получить список заказов пользователя c авторизацией")
    public ValidatableResponse getUserOrders(String accessToken) {
        return orderClient.getUserOrdersWithAuth(accessToken).then();
    }
    @Step("Получить список заказов пользователя без авторизации")
    public ValidatableResponse getUserOrdersWithoutAuth() {
        return orderClient.getUserOrdersWithoutAuth().then();
    }

    @Step("Наличие заказа в списке всех заказов по id или номеру")
    public boolean checkOrderInAllOrdersList(String order) {
        List<HashMap<String, Object>> orderList = getLast50Orders().extract().path("orders");
        for (HashMap<String, Object> stringObjectHashMap : orderList) {
            if (order.equals(stringObjectHashMap.get("_id").toString()) || order.equals(stringObjectHashMap.get("number").toString())) {
                return true;
            }
        }
        return false;
    }

    @Step("Наличие заказа в списке заказов пользователя")
    public boolean checkOrderInUserOrdersList(String order, String accessToken) {
        List<HashMap<String, Object>> orderList = getUserOrders(accessToken).extract().path("orders");
        for (HashMap<String, Object> stringObjectHashMap : orderList) {
            if (order.equals(stringObjectHashMap.get("_id").toString()) || order.equals(stringObjectHashMap.get("number").toString())) {
                return true;
            }
        }
        return false;
    }


}
