package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.*;

public class ApiBankTransferTest {

    int secondCardBalanceBefore;
    String secondCard;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
        //Авторизация
        var authorizationUser = Authorization.getAuthorizationUser();
        //Верификация
        var verificationUser = Verification.getVerificationUser();
        //Получаем данные карт
        secondCard = CardsUser.getSecondCardInfo().getCardNumber();
        //Получаем баланс карт
        secondCardBalanceBefore = getCardBalance(secondCard);
    }

    //Перевод с карты на карту (любую)
    @Test
    void shouldTransferMoney() {
        //Валидная сумма списания
        var validAmount = validAmount(secondCardBalanceBefore);
        //Перевод
        TransferMoneyCard.getTransferMoneyBetweenCards(validAmount);
        //Проверить баланс карты
        var secondCardBalanceAfter = getCardBalance(secondCard);
        //Проверка баланса
        assertEquals(secondCardBalanceBefore - validAmount, secondCardBalanceAfter);
    }
}