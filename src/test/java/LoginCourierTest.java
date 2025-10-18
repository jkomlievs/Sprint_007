import org.example.CourierData.Courier;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static io.restassured.RestAssured.*;
import io.qameta.allure.Step;
import static org.hamcrest.CoreMatchers.equalTo;


public class LoginCourierTest {
    private String id; //переменная для хранения id созданного курьера

    @BeforeEach
    public void SetUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        id = null; // Переменная для хранения id курьера и обнуления
    }

    @Test      //курьер может авторизоваться
    @Step("Проверяем, что курьер может авторизоваться")
    @DisplayName("Проверка авторизации созданного курьера")
    @Description("Успешная авторизация курьера в системе")

    public void loginCourierTest() {
        Courier courier = new Courier("Oleg117", "12345", "Oleg");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        //Авторизация курьера

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"Oleg117\", \"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        id = loginResponse.jsonPath().getString("id");
        System.out.println("Создан курьер с ID: " + id);
        }


    @Test //успешный запрос возвращает id, проверяем, что в ответе приходит id - Passed
    @Step("Проверяем, что успешный запрос возвращает id на авторизацию курьера по существующему логину")
    @DisplayName("Проверка, что при авторизации созданного курьера приходит id")
    @Description("Авторизация курьера в системе и получение id")
    public void loginCourierAndGetIdTest() {

        Courier courier = new Courier("Evgeny118", "12345", "Evgeny");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        //Авторизация курьера

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"Evgeny118\", \"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        id = loginResponse.jsonPath().getString("id");
        System.out.println("Создан курьер с ID: " + id);
    }


    @Test //Тест на авторизацию курьера без логина, проверяем, что в ответе приходит правильная ошибка - Passed
    @Step("Проверяем, что в ответе приходит правильная ошибка ")
    @DisplayName("Авторизация курьера без логина")
    @Description("Для авторизации курьера необходимо передать все обязательные поля")
    public void authorizationCourierWithoutLoginTest() {

        Courier courier = new Courier("Aleksandr116", "12345", "Aleksandr");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        //Авторизация курьера

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login");
        loginResponse.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));

        id = loginResponse.jsonPath().getString("id");
        System.out.println("Создан курьер с ID: " + id);
    }


    @Test   //Тест на авторизацию курьера без пароля. Баг - текущий результат: ответ 504 Service unavailable- Failed
    @Step("Проверяем, что при авторизации только по логину приходит корректная ошибка")
    @DisplayName("Авторизация курьера без пароля")
    @Description("Для авторизации курьера необходимо передать все обязательные поля. Передается пустой пароль курьера")
    public void authCourierWithoutPasswordTest() {
        Courier courier = new Courier("Sergey119", "12345", "Sergey");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        //Авторизация курьера

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"Sergey119\"}")
                .when()
                .post("/api/v1/courier/login");
        loginResponse.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));

        id = loginResponse.jsonPath().getString("id");
        System.out.println("Создан курьер с ID: " + id);
    }
    @Test //Тест на авторизацию курьера с неправильным логином - Passed
    @Step("Проверяем, что при авторизации с некорректным логином приходит корректная ошибка")
    @DisplayName("Авторизация курьера c неправильным логином")
    @Description("Для авторизации курьера необходимо передать существующие данные. Передается неправильный логин")
    public void authCourierWithNonExistentLoginTest() {
        Courier courier = new Courier("Vyacheslav112", "12345", "Vyacheslav");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        //Авторизация курьера

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"Anna88\", \"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login");
        loginResponse.then().assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));

        id = loginResponse.jsonPath().getString("id");
        System.out.println("Создан курьер с ID: " + id);
    }

    @Test //Тест на авторизацию курьера с неправильным паролем - Passed
    @Step("Проверяем, что при авторизации с некорректным паролем приходит корректная ошибка")
    @DisplayName("Авторизация курьера c неправильным паролем")
    @Description("Для авторизации курьера необходимо передать существующие данные. Передается неверный пароль курьера")
    public void authCourierWithNonExistentPasswordTest() {
        Courier courier = new Courier("Nikolai111", "12345", "Nikolai");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        //Авторизация курьера

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"Nikolai111\", \"password\": \"88888\"}")
                .when()
                .post("/api/v1/courier/login");
        loginResponse.then().assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));

        id = loginResponse.jsonPath().getString("id");
        System.out.println("Создан курьер с ID: " + id);
    }

    @AfterEach
    @Step("Удаление тестового курьера после выполнения теста")
    @DisplayName("Удаляем тестовые данные после каждого теста")
    public void tearDown() {
        if (id != null) {
            given()
                    .when()
                    .delete("/api/v1/courier/" + id)
                    .then()
                    .statusCode(200);
            System.out.println("Курьер с ID " + id + " успешно удалён");
        }
    }
}












