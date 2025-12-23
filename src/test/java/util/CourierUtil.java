package util;

import org.example.CourierData.Courier;

import static io.restassured.RestAssured.given;

public class CourierUtil {

    public static final String LOGIN_1 = "Ivanna1299";
    public static final String PASSWORD_1 = "1234567";
    public static final String NAME_1 = "Ivana";

    public static void create(Courier courier) {
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }


    public static String login(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("id");
    }


    public static void delete(String id) {
        if (id != null) {
            given()
                    .header("Content-type", "application/json")
                    .when()
                    .delete("/api/v1/courier/" + id)
                    .then()
                    .statusCode(200);
        }
    }
}
