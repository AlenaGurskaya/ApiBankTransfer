package ru.netology.test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.SQLHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiBankTransferTest {

    private static String token;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:9999";

        // Логин
        given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"vasya\", \"password\": \"qwerty123\"}")
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);

        // Верификация
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"login\": \"vasya\", \"code\": \"" + SQLHelper.getVerificationCode() + "\"}")
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract().response(); //Извлечение ответа сервера

        token = response.path("token"); //Извлечение значения токена
    }

    //Просмотр карт
    @Test
    void shouldGetCards() {
        // Given - When - Then
        // Предусловия
        given()
                .header("Authorization", "Bearer " + token) //Добавляет заголовок авторизации с помощью токена
                .contentType(ContentType.JSON)
        // Выполняемые действия
                .when()
                .get("/api/cards")
        // Проверки
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)));
    }

    //Перевод с карты на карту (любую)
    @Test
    void shouldTransferMoney() {
        // Given - When - Then
        // Предусловия
        given()
                .header("Authorization", "Bearer " + token) //Добавляет заголовок авторизации с помощью токена
                .contentType(ContentType.JSON)
                .body("{\"from\": \"5559 0000 0000 0002\", \"to\": \"5559 0000 0000 0008\", \"amount\": 5000}")
        // Выполняемые действия
                .when()
                .post("/api/transfer")
        // Проверки
                .then()
                .statusCode(200);
    }
}