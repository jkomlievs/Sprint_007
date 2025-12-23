import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.CourierData.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.CourierUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static util.CourierUtil.LOGIN_1;
import static util.CourierUtil.NAME_1;
import static util.CourierUtil.PASSWORD_1;


public class LoginCourierTest {

    private String id;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        id = CourierUtil.login(LOGIN_1, PASSWORD_1);
        tearDown();
    }

    @Test
    @Step("Создаем курьера")
    @DisplayName("Создание курьера для проверки авторизации")
    @Description("Создаем курьера для авторизации в системе")
    public void loginCourierGet201Test() {
        var courier = new Courier(LOGIN_1, PASSWORD_1, NAME_1);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    //Авторизация курьера
    @Test
    @Step("Проверяем, что курьер может авторизоваться и что в ответе приходит id")
    @DisplayName("Проверка получения id при авторизации курьера")
    @Description("Успешная авторизация курьера в системе")
    public void testLogin_returnsId() {
        var courier = new Courier(LOGIN_1, PASSWORD_1, NAME_1);
        CourierUtil.create(courier);
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", LOGIN_1, PASSWORD_1))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .response();
        var id = loginResponse.jsonPath().getString("id");
        assertNotNull(id);
    }

    @Test
    @Step("Проверяем получение ошибки 400 на авторизацию курьера без логина")
    @DisplayName("Авторизация курьера без логина")
    @Description("Для авторизации курьера необходимо передать все обязательные поля")
    public void authCourierWithoutLoginTest() {

        var courier = new Courier(null, PASSWORD_1, NAME_1);
        CourierUtil.create(courier);

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Проверяем получение ошибки 400 на авторизацию курьера без пароля")
    @DisplayName("Авторизация без пароля")
    @Description("Проверяем, что для авторизации необходимо передать все необходмые поля")//Авторизация курьера
    public void authWithoutPasswordTest() {
        var courier = new Courier(LOGIN_1, PASSWORD_1, NAME_1);
        CourierUtil.create(courier);

        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"\"}", LOGIN_1))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }


    @Test
    @Step("Проверяем, что при авторизации несуществующего пользователя приходит корректная ошибка")
    @DisplayName("Авторизация несуществующего курьера")
    @Description("Для авторизации курьера необходимо передать все обязательные поля. Передается пустой пароль курьера")
    public void loginWithoutPasswordGet400() {
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", LOGIN_1, PASSWORD_1))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"))
                .extract()
                .response();

        // Проверка, что ID не вернулся
        String id = loginResponse.jsonPath().getString("id");
        assertNull(id, "ID должен быть null при ошибке авторизации");
    }

    @AfterEach
    @Step("Удаление тестового курьера после выполнения теста")
    @DisplayName("Удаляем тестовые данные после каждого теста")
    public void tearDown() {
        CourierUtil.delete(id);
        id = null;
    }
}













