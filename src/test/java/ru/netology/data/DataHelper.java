package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Value;
import java.util.Random;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class DataHelper {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    private DataHelper() {
    }

    //Авторизация
    private static void sendRequestAuthorization(AuthorizationDto user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static class Authorization {
        private Authorization() {
        }

        public static AuthorizationDto getUser() {
            return new AuthorizationDto("vasya", "qwerty123");
        }

        public static AuthorizationDto getAuthorizationUser() {
            AuthorizationDto authorizationUser = getUser();
            sendRequestAuthorization(authorizationUser);
            return authorizationUser;
        }
    }

    //Верификация
    private static String sendRequestVerification(VerificationDto code) {
        Response response = given()  // Сохраняем ответ в переменную
                .spec(requestSpec)
                .body(code)
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract().response();

        return response.path("token");  // Извлекаем и возвращаем токен
    }

    public static class Verification {
        private Verification() {
        }

        public static VerificationDto getVerificationCode() {
            return new VerificationDto("vasya", SQLHelper.getVerificationCode());
        }

        public static VerificationDto getVerificationUser() {
            VerificationDto verificationUser = getVerificationCode();
            sendRequestVerification(verificationUser);
            return verificationUser;
        }
    }

    //Получение карт
    public static Response sendResponseGetCards(String token) {
        return given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/cards")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .extract().response();
    }

    public static class CardsUser {
        private CardsUser() {
        }

        //Получение карты 1
        public static CardUser getFirstCardInfo() {
            return new CardUser("5559 0000 0000 0001", "92df3f1c-a033-48e6-8390-206f6b1f56c0");
        }

        //Получение карты 2
        public static CardUser getSecondCardInfo() {
            return new CardUser("5559 0000 0000 0002", "0f3f5c2a-249e-4c3d-8287-09f7a039391d");
        }

        //Отправка запроса на получение карт
        public static void getCards() {
            String token = sendRequestVerification(Verification.getVerificationCode());
            sendResponseGetCards(token);
        }
    }

    public static void sendResponceTransfer(String token, TransferMoney transferMoney) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(transferMoney)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200)
                .extract().response();
    }

    public static class TransferMoneyCard {
        private TransferMoneyCard() {
        }

        public static TransferMoney transferInfo(String cardFrom, String cardTo, int amount) {
            return new TransferMoney(cardFrom, cardTo, amount);
        }

        public static void getTransferMoneyBetweenCards(String cardFrom, String cardTo, int amount) {
            TransferMoney transferMoney = transferInfo(cardFrom,cardTo, amount);
            String token = sendRequestVerification(Verification.getVerificationCode());
            sendResponceTransfer(token, transferMoney);
        }
    }

    public static int validAmount(int balance) {
        return new Random().nextInt(Math.abs(balance)) + 1;
    }

    @Value
    public static class AuthorizationDto {
        String login;
        String password;
    }

    @Value
    public static class VerificationDto {
        String login;
        String code;
    }

    @Value
    public static class CardUser {
        String cardNumber;
        String cardId;
    }

    @Value
    public static class TransferMoney {
        String from;
        String to;
        int amount;
    }
}
