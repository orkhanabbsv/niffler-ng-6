package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

    public SearchField() {
        super($("input[type='text']"));
    }

    public SearchField(SelenideElement self) {
        super(self);
    }

    @Step("Поиск по значению: {value}")
    public SearchField search(String value) {
        self.sendKeys(value);
        self.sendKeys(Keys.ENTER);
        return this;
    }

    @Step("Очистить строку поиска")
    public SearchField clearIfNotEmpty() {
        self.clear();
        return this;
    }
}
