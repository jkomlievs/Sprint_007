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


public class CreateCourierTest {
    private String id; //переменная для хранения id созданного курьера

    @BeforeEach
    public void SetUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        id = null; // Переменная для хранения id курьера и обнуления
    }

    @Test      //курьера можно создать - Passed
    @Step("Проверяем, успешное создание курьера с валидными данными")
    @DisplayName("Проверка успешного создания курьера")
    @Description("Создание курьера с валидно заполеннными полями")
    public void createCourierTest() {
        Courier courier = new Courier("Ivan113", "12345", "Ivan");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        //Авторизация курьера для получения id

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"Ivan113\", \"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        id = loginResponse.jsonPath().getString("id");
        System.out.println("Создан курьер с ID: " + id);
    }

    @Test
    //проверка, что нельзя создать двух одинаковых курьеров. Тут баг в описании ошибки. Текущий результат: Этот логин уже используется. Попробуйте другой- Failed
    @Step("Проверяем повторное создание курьера с существующими данными")
    @DisplayName("Проверка создания двух одинаковых курьеров")
    @Description("Если создать пользователя с логином, который уже есть, возвращается ошибка")
    public void CreateSameCourier() {
        Courier courier = new Courier("Ivan111", "12345", "Ivan");

        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test //если создать курьера только с паролем и именем появляется ошибка - Passed
    @Step("Проверяем, что при создании курьера, если одного из полей нет (отсутствует поле Логин), запрос возвращает корректную ошибку")
    @DisplayName("Создание курьера без логина")
    @Description("Создание курьера только с паролем и именем")
    public void createCourierWithoutLogin() {
        Courier courier = new Courier(null, "12345", "Ivan1");
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test //тут баг, курьера с логином и паролем без поля Имя можно создать (201 Created) - Failed
    @Step("Проверяем, что при создании курьера, если одного из полей нет (отсутствует поле Имя), запрос возвращает корректную ошибку")
    @DisplayName("Проверка, что если одного из полей нет, запрос возвращает ошибку")
    @Description("Создание курьера с двумя полями логин и пароль")
    public void createCourierWithoutField() {
        Courier courier = new Courier("Ivan112", "12345", null);
        Response response = given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
        response.then().assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"Ivan112\", \"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

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










