import io.qameta.allure.Step;
import org.example.OrderData.Order;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;


public class CreateOrderTest {

    private String firstName = "Николай";
    private String lastName = "Петров";
    private String address = "Москва, ул. Неизвестная";
    private String metroStation = "Сокольники";
    private String phone = "+79000456452";
    private int rentTime = 5;
    private String deliveryDate = "2025-10-18";
    private String comment = "Скорее!";

    public String track; // хранит track созданного заказа
    public String color;


    @BeforeEach
    public void SetUp() {

        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @ParameterizedTest(name = "Создание заказа с цветами: {0}")
    @MethodSource("getColor")
    @DisplayName("Создание заказа с разными вариантами цвета")
    public static Object[] getColor() {
        return new Object[][]{
                {"BLACK"},
                {"GREY"},
                {"BLACK, GREY"},
                {""}
        };
    }

    @Test
    @Step("Проверяем можно ли заказать с цветом BLACK")
    @DisplayName("Создание заказа с цветом BLACK")
    @Description("Заказ можно создать с указанием только одного цвета или обоих цветов - заказ цвет BLACK")
    public void createOrderWithBlackColour() {
        // Создаю заказ с указанным цветом
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, new String[]{"BLACK"});
        // Выполняю запрос на создание заказа
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
        response.then()
                .statusCode(201)
                .and()
                .body("track", notNullValue()); // проверяем, что track присутствует

        // Сохраняем track для удаления заказа
        track = response.jsonPath().getString("track");
    }

    @Test
    @Step("Проверяем можно ли заказать с цветом GRAY - Passed")
    @DisplayName("Создание заказа с цветом GRAY")
    @Description("Заказ можно создать с указанием только одного цвета или обоих цветов - заказ цвет GRAY")
    public void createOrderWithGrayColour() {
        // Создаю заказ с указанным цветом
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, new String[]{"GRAY"});
        // Выполняю запрос на создание заказа
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
        response.then()
                .statusCode(201)
                .and()
                .body("track", notNullValue()); // проверяем, что track присутствует

        // Сохраняем track для удаления заказа
        track = response.jsonPath().getString("track");
    }

    @Test
    @Step("Проверяем можно ли заказать два цвета одновременно -Passed")
    @DisplayName("Создание заказа с двумя цветами")
    @Description("Заказ можно создать с указанием только одного цвета или обоих цветов - заказ с выбором двух цветов")
    public void createOrderWithDoubleColour() {
        // Создаю заказ с указанным цветом
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, new String[]{"BLACK, GREY"});
        // Выполняю запрос на создание заказа
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
        response.then()
                .statusCode(201)
                .and()
                .body("track", notNullValue()); // проверяем, что track присутствует

        // Сохраняем track для удаления заказа
        track = response.jsonPath().getString("track");
    }

    @Test
    @Step("Тест для создания заказа без указания цвета - Passed")
    @DisplayName("Создание заказа без указания цвета")
    @Description("Заказ можно создать, если не указать цвет в заказе")
    public void createOrderWithoutColor() {    // Создаю заказ без указания цвета
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment);
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
        response.then()
                .statusCode(201)
                .and()
                .body("track", notNullValue()); // проверяем, что track присутствует

        // Сохраняем track для удаления заказа
        track = response.jsonPath().getString("track");
    }

    @AfterEach
    @Step("Удаляем данные после всех тестов")
    @DisplayName("Отмена созданного заказа после теста")
    public void tearDown() {
        if (track != null) {
            given()
                    .header("Content-type", "application/json")
                    .when()
                    .put("/api/v1/orders/cancel?track=" + track)
                    .then()
                    .statusCode(200);

            track = null;
        }
    }
}