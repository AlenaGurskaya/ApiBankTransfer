package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.*;

public class ApiBankTransferTest {

    int firstCardBalanceBefore;
    int secondCardBalanceBefore;
    String firstCard;
    String secondCard;

    @BeforeEach
    void setup() {
        //Авторизация
        Authorization.getAuthorizationUser();
        //Верификация
        Verification.getVerificationUser();
        //Получаем данные карт
        firstCard = CardsUser.getFirstCardInfo().getCardNumber();
        secondCard = CardsUser.getSecondCardInfo().getCardNumber();
        //Получаем баланс карт
        firstCardBalanceBefore = getCardBalance(firstCard);
        secondCardBalanceBefore = getCardBalance(secondCard);
    }

    //Перевод с карты на карту
    @Test
    void shouldTransferMoney() {
        //Валидная сумма списания
        var validAmount = validAmount(secondCardBalanceBefore);
        //Перевод
        TransferMoneyCard.getTransferMoneyBetweenCards(secondCard, firstCard, validAmount);
        //Проверить балансы карт
        var firstCardBalanceAfter = getCardBalance(firstCard);
        var secondCardBalanceAfter = getCardBalance(secondCard);
        //Проверка баланса
        assertAll(
                () -> assertEquals(firstCardBalanceBefore + validAmount, firstCardBalanceAfter),
                () -> assertEquals(secondCardBalanceBefore - validAmount, secondCardBalanceAfter)
        );
    }
}