import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import step.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserTest {
    private UserSteps userSteps;
    private String name;
    private String password;
    private String email;

    @Before
    public void setUp() {
        userSteps = new UserSteps(new UserClient());
        name = RandomStringUtils.randomAlphabetic(10);
        password = RandomStringUtils.randomAlphabetic(10);
        email = RandomStringUtils.randomAlphabetic(10) + "@mail.test";
    }

    @After
    public void tearDown() {
        // Конструкция нужна для
        // избежания NullPointer,
        // когда мы пытаемся удалить несуществующего пользователя
        try {

            userSteps.deleteUser(email, password);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUserValidParams() {
        userSteps.createUser(email, password, name)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание дубликата пользователя")
    public void createDuplicateUser() {
        userSteps.createUser(email, password, name);
        userSteps.createUser(email, password, name)
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Создание пользователя без обязательного параметра password")
    public void createUserWithoutPasswordParam() {
        userSteps.createUserWithoutPassword(email, name)
                .assertThat()
                .statusCode(SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Корректный вход пользователя")
    public void loginUserValidData() {
        userSteps.createUser(email, password, name);
        userSteps.loginUser(email, password)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход рользователя с некорректным паролем")
    public void loginUserInvalidPassword() {
        userSteps.createUser(email, password, name);
        userSteps.loginUser(email, "некорректный пароль")
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Вход рользователя с некорректным именем")
    public void loginUserInvalidEmail() {
        userSteps.createUser(email, password, name);
        userSteps.loginUser("некорректный@mail.mail", password)
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Обновление имени пользователя с авторизацией")
    public void updateUserName() {
        userSteps.createUser(email, password, name);
        userSteps.updateName(email, password, "новое имя")
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.name", equalTo("новое имя"));
    }

    @Test
    @DisplayName("Обновление почты пользователя с авторизацией")
    public void updateUserEmail() {
        userSteps.createUser(email, password, name);
        String newEmail = RandomStringUtils.randomAlphabetic(10) + "@mail.test";
        userSteps.updateEmail(email, password, newEmail)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.email", equalTo(newEmail.toLowerCase()));
        email = newEmail;
    }

    @Test
    @DisplayName("Обновление данных без авторизации")
    public void updateUserWithoutAuth(){
        userSteps.createUser(email, password, name);
        String newEmail = RandomStringUtils.randomAlphabetic(10) + "@mail.test";
        userSteps.updateDataWithoutAuth(newEmail, "Новое имя")
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);

    }
    @Test
    @DisplayName("Обновление почты пользователя на существующую")
    public void updateUserEmailOnExistEmail() {
        userSteps.createUser(email, password, name);
        String newUserEmail = RandomStringUtils.randomAlphabetic(10) + "@mail.test";
        userSteps.createUser(newUserEmail, password, name);
        userSteps.updateEmail(newUserEmail, password, email)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User with such email already exists"));
    }

}
