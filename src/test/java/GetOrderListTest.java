import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class GetOrderListTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Проверка, что в теле ответа возвращается список заказов")
    @Description("Получение списка всех заказов через GET /api/v1/orders")
    @Step("Отправка запроса GET /api/v1/orders и проверка тела ответа")
    public void getOrderListTest() {
        // Отправляем GET-запрос без тела
        Response response = given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200) // Проверка, что запрос успешен
                .and()
                .body("orders", notNullValue()) // Проверка, что поле "orders" присутствует
                .extract()
                .response();

        // Извлекаю список заказов
        List<Map<String, Object>> orders = response.jsonPath().getList("orders");

        // Проверка, что список заказов не пуст
        assertFalse(orders.isEmpty(), "Ошибка: список заказов пуст!");

    }
}
