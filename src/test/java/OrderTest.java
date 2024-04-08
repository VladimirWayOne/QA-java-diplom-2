import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import step.OrderSteps;
import step.UserSteps;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;


public class OrderTest {
    private OrderSteps orderSteps;
    private UserSteps userSteps;
    private String name;
    private String password;
    private String email;

    @Before
    @Step("Инициализация клиентов и подготовка тестовых данных")
    public void setUp() {
        orderSteps = new OrderSteps(new OrderClient());
        userSteps = new UserSteps(new UserClient());
        name = RandomStringUtils.randomAlphabetic(10);
        password = RandomStringUtils.randomAlphabetic(10);
        email = RandomStringUtils.randomAlphabetic(10) + "@mail.test";

    }

    @After
    @Step("Удаление пользователя, если он был создан")
    public void tearDown() {
        // Конструкция нужна для
        // избежания NPE,
        // когда мы пытаемся удалить несуществующего пользователя
        try {

            userSteps.deleteUser(email, password);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("Получить список ингредиентов")
    public void getIngredients() {
        orderSteps.getIngridients().assertThat().body("data", notNullValue()).and().body("data", isA(ArrayList.class));

    }

    @Test
    @DisplayName("Создать заказ без авторизации с валидными ингредиентами")
    public void createOrderValidIngredientsWithoutAutn() {
        ValidatableResponse ingredientsInfo = orderSteps.getIngridients();
        List<String> ingredientsList = orderSteps.chooseIngridients(ingredientsInfo, 5);
        // System.out.println(ingredientsList);
        orderSteps.createOrderWithoutAuth(ingredientsList).assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Создать заказ с авторизацией с валидными ингредиентами")
    public void createOrderValidIngredientsWithAutn() {
        userSteps.createUser(email, password, name);
        String accessToken = userSteps.getUserToken(email, password);
        ValidatableResponse ingredientsInfo = orderSteps.getIngridients();
        List<String> ingredientsList = orderSteps.chooseIngridients(ingredientsInfo, 5);
        // System.out.println(ingredientsList);
        orderSteps.createOrderWithAuth(ingredientsList, accessToken).assertThat().body("success", equalTo(true));

    }

    @Test
    @DisplayName("Создать заказ с пустым списком ингредиентов ингредиентами")
    public void createOrderNoneIngredients() {

        orderSteps.createOrderWithoutAuth(new ArrayList<>()).assertThat().statusCode(SC_BAD_REQUEST);

    }

    @Test
    @DisplayName("Создать заказ с некорректным списком ингредиентов ингредиентами")
    public void createOrderInvalidIngredients() {
        ArrayList<String> invalidIngredient = new ArrayList<>();
        invalidIngredient.add("invalidIngredient");
        orderSteps.createOrderWithoutAuth(invalidIngredient).assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);

    }

    @Test
    @DisplayName("Получить заказ, сделанный без авторизации в списке всех заказов")
    public void createOrderWithoutAuthAndGetItInAllOrdersListNotFound() {
        ValidatableResponse ingredientsInfo = orderSteps.getIngridients();
        List<String> ingredientsList = orderSteps.chooseIngridients(ingredientsInfo, 2);
        String orderId = orderSteps.createOrderWithoutAuth(ingredientsList)
                .extract()
                .path("order.number")
                .toString();
        Assert.assertFalse("Заказ, сделанный без авторизации не должен отображаться в общем списке", orderSteps.checkOrderInAllOrdersList(orderId));
    }


    @Test
    @DisplayName("Получить заказ, сделанный c авторизацией в списке всех заказов")
    public void createOrderWithAuthAndGetItInAllOrdersListSuccessfullyFound() {
        ValidatableResponse ingredientsInfo = orderSteps.getIngridients();
        List<String> ingredientsList = orderSteps.chooseIngridients(ingredientsInfo, 2);
        userSteps.createUser(email, password, name);
        String accessToken = userSteps.getUserToken(email, password);
        String orderId = orderSteps.createOrderWithAuth(ingredientsList, accessToken)
                .extract()
                .path("order.number")
                .toString();
        Assert.assertTrue("Заказ, сделанный c авторизацией должен отображаться в общем списке", orderSteps.checkOrderInAllOrdersList(orderId));
    }

    @Test
    @DisplayName("Получить заказ, сделанный c авторизацией в списке заказов пользователя")
    public void createOrderWithAuthAndGetItInUserlOrdersListSuccessfullyFound() {
        ValidatableResponse ingredientsInfo = orderSteps.getIngridients();
        List<String> ingredientsList = orderSteps.chooseIngridients(ingredientsInfo, 2);
        userSteps.createUser(email, password, name);
        String accessToken = userSteps.getUserToken(email, password);
        String orderId = orderSteps.createOrderWithAuth(ingredientsList, accessToken)
                .extract()
                .path("order.number")
                .toString();
        Assert.assertTrue("Заказ, сделанный с авторизацией должен отображаться в списке заказов пользователя", orderSteps.checkOrderInUserOrdersList(orderId, accessToken));
    }

    @Test
    @DisplayName("Попытка получить список заказов пользователя без авторизации")
    public void getUserOrdersWithoutAuth() {
        orderSteps.getUserOrdersWithoutAuth()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }
}