package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomSentence;

@WebTest
class SpendingWebTest {

    private static final Config CFG = Config.getInstance();
    private final EditSpendingPage editSpendingPage = new EditSpendingPage();

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    )
            }
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(UserJson userJson) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(userJson.username(), userJson.testData().password())
                .getSpendingTable()
                .editSpending(userJson.testData().spendings().getFirst().description())
                .setNewSpendingDescription(newDescription)
                .save();

        new MainPage().checkThatTableContainsSpending(newDescription);
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение-1",
                            description = "Новое обучение-1",
                            amount = 6994
                    )
            }
    )
    @Test
    void searchSpend(UserJson userJson) {

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(userJson.username(), userJson.testData().password())
                .getSpendingTable()
                .selectPeriod("Today")
                .searchSpendingByDescription(userJson.testData().spendings().getFirst().description())
                .checkTableContains(userJson.testData().spendings().getFirst().description());
    }

    @User
    @Test
    void addSpendTest(UserJson user) {
        String category = randomCategoryName();
        String description = randomSentence(2);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .getHeader()
                .addSpendingPage()
                .setSpendingCategory(category)
                .setNewSpendingDescription(description)
                .setSpendingAmount("10")
                .getCalendar()
                .selectDateInCalendar(new Date());

        editSpendingPage.save();

        editSpendingPage.checkAlertMessage("New spending is successfully created");
        new MainPage().checkThatTableContainsSpending(description);
    }
}

