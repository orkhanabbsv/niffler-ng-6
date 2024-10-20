package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

  private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
  private final SelenideElement statComponent = $("#stat");
  @Getter
  private final Header header = new Header();
  @Getter
  private final SpendingTable spendingTable = new SpendingTable();

  @Step("Проверка наличия траты: {spendingDescription}")
  public void checkThatTableContainsSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription)).should(visible);
  }

  @Step("Проверка загрузки главной страницы")
  public MainPage checkThatPageLoaded() {
    statComponent.should(visible).shouldHave(text("Statistics"));
    return this;
  }
}
