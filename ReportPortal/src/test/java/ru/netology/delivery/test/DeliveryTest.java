package ru.netology.delivery.test;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import data.DataGenerator;
import org.openqa.selenium.Keys;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static data.DataGenerator.generateCity;

import com.epam.reportportal.junit5.ReportPortalExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

//@ExtendWith(ReportPortalExtension.class)
class DeliveryTest {

    private static final Logger LOGGER = LogManager.getLogger(DeliveryTest.class);

    private static void logInfo(String message) {
        LOGGER.info(message);
    }
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);


        open("http://localhost:9999");

        //ищем эелементы
        SelenideElement form = $("form");

        //взаимодействуем с элементами
        form.$("[data-test-id=city] input").setValue(validUser.getCity());
        logInfo("В поле ввода Город введено: " + validUser.getCity());
        form.$("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        logInfo("В поле ввода Дата нажимаем на: " + "(SHIFT+HOME), DELETE");

        form.$("[data-test-id=date] input").setValue(firstMeetingDate);
        logInfo("В поле ввода Дата введено: " + firstMeetingDate);

        form.$("[data-test-id=name] input").setValue(validUser.getName());
        logInfo("В поле ввода Фамилия и имя введено: " + validUser.getName());

        form.$("[data-test-id=phone] input").setValue(validUser.getPhone());
        logInfo("В поле ввода Фамилия и имя введено: " + validUser.getPhone());

        form.$("[data-test-id=agreement]").click();
        logInfo("Клик по чекбоксу");

        form.$(".button").click();
        logInfo("Клик по кнопке Запланированить");

        $("[data-test-id=success-notification]")
                .shouldBe(visible)
                .shouldHave(text(firstMeetingDate));
        logInfo("Встреча успешно запланирована на " + firstMeetingDate);


        form.$("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);

        form.$("[data-test-id=date] input").setValue(secondMeetingDate);
        logInfo("В поле ввода Дата введено: " + secondMeetingDate);

        form.$(".button").click();
        logInfo("Клик по кнопке Запланированить");

        $("[data-test-id=replan-notification]")
                .shouldBe(visible)
                .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $("[data-test-id =\"replan-notification\"] .button").click();

        $("[data-test-id=success-notification]");
        $("[data-test-id=success-notification]")

                .shouldBe(visible)
                .shouldHave(text(secondMeetingDate));
        logInfo("Встреча успешно запланирована на " + secondMeetingDate);

    }
}