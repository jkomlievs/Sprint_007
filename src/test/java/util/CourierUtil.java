package org.example.service;

import io.restassured.response.Response;
import lombok.Getter;
import org.example.CourierData.Courier;

import static io.restassured.RestAssured.given;

public class CourierService {

    public static final String LOGIN_1 = "Ivanna127";
    public static final String PASSWORD_1 = "12345";
    public static final String NAME_1 = "Ivan";
    @Getter
    private String id; //переменная для хранения id созданного курьера

    public void create(Courier courier) {
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    public void login() {
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", LOGIN_1, PASSWORD_1))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();
        id = loginResponse.jsonPath().getString("id");
    }

    public void clear() {
        if (id != null) {
            given()
                    .header("Content-type", "application/json")
                    .when()
                    .delete("/api/v1/courier/" + id)
                    .then()
                    .statusCode(200);
            id = null;
        }
    }
}
