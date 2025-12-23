import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.example.CourierData.Courier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.CourierUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static util.CourierUtil.LOGIN_1;
import static util.CourierUtil.NAME_1;
import static util.CourierUtil.PASSWORD_1;


public class CreateCourierTest {

    private String id;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        id = CourierUtil.login(LOGIN_1, PASSWORD_1);
        tearDown();
    }

    @Test      //курьера можно создать - Passed
    @Step("Проверяем, успешное создание курьера с валидными данными")
    @DisplayName("Проверка успешного создания курьера")
    @Description("Создание курьера с валидно заполеннными полями")
    public void createCourierTest() {
        var courier = new Courier(LOGIN_1, PASSWORD_1, NAME_1);
        var response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    @Test
    @Step("Авторизация курьера с валидными данными")
    @DisplayName("Авторизация курьера")
    @Description("Проверка авторизации курьера")
    public void authorizationSuccessCourier() {
        var courier = new Courier(LOGIN_1, PASSWORD_1, NAME_1);
        CourierUtil.create(courier);
        var loginResponse = given()
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
    //проверка, что нельзя создать двух одинаковых курьеров.
    @Step("Проверяем повторное создание курьера с существующими данными")
    @DisplayName("Проверка создания двух одинаковых курьеров")
    @Description("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void testCreateSameCourier_thenStatusCode409() {
        var courier = new Courier(LOGIN_1, PASSWORD_1, NAME_1);
        CourierUtil.create(courier);
        var response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @Step("Проверяем, что при создании курьера, если одного из полей нет (отсутствует поле Логин), запрос возвращает корректную ошибку")
    @DisplayName("Создание курьера без логина")
    @Description("Создание курьера только с паролем и именем")
    public void createCourierWithoutLogin() {
        var courier = new Courier(null, "12345", "Ivan");
        var response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Step("Проверяем, что при создании курьера, если одного из полей нет (отсутствует поле Имя), запрос возвращает корректную ошибку")
    @DisplayName("Проверка, что если одного из полей нет, запрос возвращает ошибку")
    @Description("Создание курьера с двумя полями логин и пароль")
    public void createCourierWithoutField() {
        var courier = new Courier(null, PASSWORD_1, NAME_1);
        var response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @AfterEach
    @Step("Удаление тестового курьера после выполнения теста")
    @DisplayName("Удаляем тестовые данные после каждого теста")
    public void tearDown() {
        CourierUtil.delete(id);
        id = null;
    }
}