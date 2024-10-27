package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class SpendingTable extends BaseComponent<SpendingTable> {
    private static final ElementsCollection timePeriods = $$("[role='option']");

    private static final String deleteConfirmButton
            = ".MuiPaper-root button.MuiButtonBase-root.MuiButton-containedPrimary";
    private static final String spendingRow = "tr";

    private final SearchField searchField = new SearchField();

    public SpendingTable() {
        super($(".MuiTableContainer-root"));
    }

    @Step("Выбор периода для отображения трат: {period}")
    public SpendingTable selectPeriod(String period) {
        self.$("#period").click();
        timePeriods.find(text(period)).click();
        return this;
    }

    @Step("Изменения описания траты на: {spendingDescription}")
    public EditSpendingPage editSpending(String description) {
        self.$$(spendingRow).find(text(description)).$(" [aria-label='Edit spending']").click();
        return new EditSpendingPage();
    }

    @Step("Удаление траты с описанием: {description}")
    public SpendingTable deleteSpending(String description) {
        self.$$(spendingRow).find(text(description)).$$("td").get(1).click();
        self.$("#delete").shouldBe(visible).click();
        $(deleteConfirmButton).shouldBe(visible).click();
        return this;
    }

    @Step("Поиск траты с описанием: {description}")
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Проверка что таблица содержит трату: {expectedSpends}")
    public SpendingTable checkTableContains(String... expectedSpends) {
        self.$$("td:nth-child(4)").shouldHave(textsInAnyOrder(expectedSpends));
        return this;
    }

    @Step("Проверка что количество трат равно: {expectedSize}")
    public SpendingTable checkTableSize(int expectedSize) {
        self.$(spendingRow).$$("tr").shouldHave(size(expectedSize));
        return this;
    }
}