package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.component.StatComponent;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingWebTest {

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getSpendingTable()
                .editSpending("Обучение Advanced 2.0")
                .setNewSpendingDescription(newDescription)
                .saveSpending();

        new MainPage().getSpendingTable()
                .checkTableContains(newDescription);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Utility",
                            description = "Gas bills",
                            amount = 1200
                    )
            }
    )
    @ApiLogin
    @Test
    void spendingShouldBeVisible(UserJson user) {

        Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .getSpendingTable()
                .checkSpendingRows(user.testData().spends().toArray(SpendJson[]::new));
    }

    @User
    @ApiLogin
    @Test
    void shouldAddNewSpending(UserJson user) {
        String category = "Friends";
        int amount = 100;
        Date currentDate = new Date();
        String description = RandomDataUtils.randomSentence(3);

        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setNewSpendingCategory(category)
                .setNewSpendingAmount(amount)
                .setNewSpendingDate(currentDate)
                .setNewSpendingDescription(description)
                .saveSpending()
                .checkAlertMessage("New spending is successfully created");

        new MainPage().getSpendingTable()
                .checkTableContains(description);
    }

    @User
    @ApiLogin
    @Test
    void shouldNotAddSpendingWithEmptyCategory(UserJson user) {
        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setNewSpendingAmount(100)
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Please choose category");
    }

    @User
    @ApiLogin
    @Test
    void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
        Selenide.open(EditSpendingPage.URL, EditSpendingPage.class)
                .setNewSpendingCategory("Friends")
                .setNewSpendingDate(new Date())
                .saveSpending()
                .checkFormErrorMessage("Amount has to be not less then 0.01");
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void deleteSpendingTest(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .getSpendingTable()
                .deleteSpending("Обучение Advanced 2.0")
                .checkTableSize(0);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        StatComponent statComponent = Selenide.open(MainPage.URL, MainPage.class)
                .getStatComponent();

        Thread.sleep(3000);

        assertFalse(new ScreenDiffResult(
                expected,
                statComponent.chartScreenshot()
        ), "Screen comparison failure");

        statComponent.checkBubbles(Color.yellow);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ApiLogin
    @Test
    void checkStatComponents(UserJson user) throws IOException, InterruptedException {
        StatComponent statComponent = Selenide.open(MainPage.URL, MainPage.class)
                .getStatComponent();

        Thread.sleep(3000);

        statComponent.checkBubblesInExactOrder(new Bubble(
                Color.yellow,
                "Обучение 79990 ₽"
        ));
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Utilities",
                            description = "Gas bills",
                            amount = 1200
                    ),
            }
    )
    @ApiLogin
    @Test
    void checkStatComponentsInAnyOrder(UserJson user) throws InterruptedException {
        StatComponent statComponent = Selenide.open(MainPage.URL, MainPage.class)
                .getStatComponent();

        Thread.sleep(3000);

        Bubble firstBubble = new Bubble(
                Color.yellow,
                "Обучение 79990 ₽"
        );

        Bubble secondBubble = new Bubble(
                Color.green,
                "Utilities 1200 ₽"
        );
        statComponent.checkBubblesInAnyOrder(secondBubble, firstBubble);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Utilities",
                            description = "Gas bills",
                            amount = 1200
                    ),
            }
    )
    @ApiLogin
    @Test
    void checkStatComponentsContains(UserJson user) throws InterruptedException {
        StatComponent statComponent = Selenide.open(MainPage.URL, MainPage.class)
                .getStatComponent();

        Thread.sleep(3000);

        Bubble firstBubble = new Bubble(
                Color.green,
                "Utilities 1200 ₽"
        );
        statComponent.checkBubblesContains(firstBubble);
    }
}

